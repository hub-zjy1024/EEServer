package pri.zjy.db;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrestatmentUtil {

	static Logger mLogger = LoggerFactory.getLogger(PrestatmentUtil.class);

	public static void fillSql(PreparedStatement psts, List<Object> args) throws SQLException {
		if (args != null) {
			for (int i = 0; i < args.size(); i++) {
				Object data = args.get(i);
				int realIndex = i + 1;
				if (data instanceof Integer) {
					psts.setObject(realIndex, data, Types.INTEGER);
				} else if (data instanceof Long) {
					psts.setLong(realIndex, (long) data);
				} else if (data instanceof String) {
					// psts.setObject(i, data, Types.VARCHAR);
					psts.setString(realIndex, (String) data);
				} else if (data instanceof Timestamp) {
//					psts.setTimestamp(realIndex,data);
					psts.setObject(realIndex, data, Types.TIMESTAMP);
				} else if (data == null) {
					psts.setObject(realIndex, data, Types.NULL);
				} else {
					mLogger.error("--------------不支持" + data);
				}
			}
		}
	}
}
