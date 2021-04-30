package b1b.erp.js.yundan.sf.bussiness;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.mysql.cj.jdbc.exceptions.MysqlDataTruncation;

import b1b.erp.js.yundan.sf.entity.YundanInput;
import pri.zjy.db.DBPoolConnection;
import pri.zjy.db.PrestatmentUtil;

public class OrderMgr {

	public YundanInput getData(String id) throws Exception {
		YundanInput mInput = new YundanInput();
		Connection mConn = null;
		try {
			if (id == null) {
				throw new IOException("参数不能为空");
			}
			mConn = DBPoolConnection.getInstance().getConnection();
			String sql = "select *from  yuninfos where mId=?";
			PreparedStatement prepareStatement = mConn.prepareStatement(sql);
			List<Object> args = new ArrayList<Object>();
			args.add(id);
			PrestatmentUtil.fillSql(prepareStatement, args);
			ResultSet executeQuery = prepareStatement.executeQuery();
			String json = "";
			while (executeQuery.next()) {
				json = executeQuery.getString("json");
			}
			mInput = JSONObject.parseObject(json, YundanInput.class);
		} catch (JSONException e) {
			throw new SQLException("查询不到运单数据");
		} catch (SQLException e) {
			e.printStackTrace();
			throw new SQLException("连接数据库失败");
		} catch (Exception e) {
			throw e;
		} finally {
			if (mConn != null) {
				mConn.close();
			}
		}
		return mInput;
	}

	public void insertData(String mId, String pid, String yundanId, String json) throws Exception {
		String mData = "";
		Connection mConn = null;
		try {
			mConn = DBPoolConnection.getInstance().getConnection();
			String sql = "insert into yuninfos  (mId,pid,yundanId,json) values(?,?,?,?)";
			PreparedStatement prepareStatement = mConn.prepareStatement(sql);
			List<Object> args = new ArrayList<Object>();
			args.add(mId);
			args.add(pid);
			args.add(yundanId);
			args.add(json);
			PrestatmentUtil.fillSql(prepareStatement, args);
			int executeUpdate = prepareStatement.executeUpdate();
			if (executeUpdate == 1) {

			} else {
				throw new IOException("新增数据失败," + executeUpdate);
			}
		} catch (JSONException e) {
			throw new SQLException("查询不到运单数据");
		} catch (MysqlDataTruncation e) {
			throw new SQLException("字段内容过长, "+e.getMessage());
		} catch (SQLException e) {
			e.printStackTrace();
			throw new SQLException("连接数据库失败,"+e.getMessage());
		} catch (Exception e) {
			throw e;
		} finally {
			if (mConn != null) {
				mConn.close();
			}
		}
	}
}
