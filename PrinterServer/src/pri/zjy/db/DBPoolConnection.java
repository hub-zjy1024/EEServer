package pri.zjy.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;
import java.util.Set;

import org.slf4j.LoggerFactory;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.pool.DruidPooledConnection;

public class DBPoolConnection {

	private static DBPoolConnection dbPoolConnection = null;
	private static DruidDataSource druidDataSource = null;

	private static org.slf4j.Logger mLogger = LoggerFactory.getLogger(DBPoolConnection.class);
	static {
		String configName = "db_server.properties";

		if (DebugUtil.isLocal()) {
			configName = "db_server_local.properties";
			mLogger.warn("DBSERVER is DEBUG,user config={}", configName);
		}

		Properties properties = loadPropertiesFile(configName);
		try {
			druidDataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(properties); // DruidDataSrouce工厂模式
		} catch (Exception e) {
			e.printStackTrace();
			mLogger.warn("init DBPool error," + e.getMessage());
		}
	}

	/**
	 * 数据库连接池单例
	 * @return
	 */
	public static synchronized DBPoolConnection getInstance() {
		if (null == dbPoolConnection) {
			dbPoolConnection = new DBPoolConnection();
		}
		return dbPoolConnection;
	}

	/**
	 * 
	 * 不适用于生产环境的参数，连接的关闭应该依赖于程序自己实现<br/>
	 * removeAbandoned=true<br/>
	 * removeAbandonedTimeout=120000<br/>
	 * @return 返回druid数据库连接
	 * @throws SQLException
	 */
	public DruidPooledConnection getConnection() throws SQLException {
		Set<DruidPooledConnection> activeConnections = druidDataSource.getActiveConnections();
		Throwable lastCreateError = druidDataSource.getLastCreateError();
		long errorCount = druidDataSource.getCreateErrorCount();
		Date lastCreateErrorTime = druidDataSource.getLastCreateErrorTime();

		long counts = druidDataSource.getConnectCount();
		long closedCounts = druidDataSource.getCloseCount();
		String log = "[druid] " + "Conn status:active counts=" + activeConnections.size()
				+ " getConnectCount=" + counts + ",closedCounts=" + closedCounts;
		return druidDataSource.getConnection();
	}

	public void excute(SQLWrapper wrapper) throws SQLException {
		Connection connection = null;
		try {
			connection = getConnection();
			wrapper.onConnected(connection);
		} catch (SQLException e) {
			mLogger.warn("DBPool ,SQLException  ", e);
			throw new SQLException("sql执行异常", e);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public abstract static class SQLWrapper {
		public abstract void onConnected(Connection conn) throws SQLException;

		public void run() throws SQLException {
			// String sql = "";
			// PreparedStatement prepareStatement = connection.prepareStatement(sql);
			// ResultSet executeQuery = prepareStatement.executeQuery();
		}
	}

	/**
	 * @param string 配置文件名
	 * @return Properties对象
	 */
	private static Properties loadPropertiesFile(String fullFile) {
		String webRootPath = null;
		if (null == fullFile || fullFile.equals("")) {
			throw new IllegalArgumentException("Properties file path can not be null" + fullFile);
		}
		webRootPath = DBPoolConnection.class.getClassLoader().getResource("").getPath();
		// webRootPath = new File(webRootPath).getParent();
		InputStream inputStream = null;
		Properties p = null;
		try {
			inputStream = new FileInputStream(new File(webRootPath + File.separator + fullFile));
			p = new Properties();
			p.load(inputStream);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != inputStream) {
					inputStream.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return p;
	}
}
