package b1b.erp.js.yundan.sf.sfutils;

import java.io.IOException;
import java.net.SocketException;
import java.net.URLEncoder;
import java.security.PrivilegedActionException;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import b1b.erp.js.yundan.sf.jee.Log;

public class DyjInterface2 {
	public static final String HOST = "http://vpn3.t996.top:810";
	public static final String iKey = "10162105300";

	public static class DyjException extends Exception {
		/**
		 * Constructs a new exception with {@code null} as its detail message.
		 * The cause is not initialized, and may subsequently be initialized by a
		 * call to {@link #initCause}.
		 */
		public DyjException() {
		}

		/**
		 * Constructs a new exception with the specified detail message.  The
		 * cause is not initialized, and may subsequently be initialized by
		 * a call to {@link #initCause}.
		 *
		 * @param message the detail message. The detail message is saved for
		 *                later retrieval by the {@link #getMessage()} method.
		 */
		public DyjException(String message) {
			super(message);
		}

		/**
		 * Constructs a new exception with the specified detail message and
		 * cause.  <p>Note that the detail message associated with
		 * {@code cause} is <i>not</i> automatically incorporated in
		 * this exception's detail message.
		 *
		 * @param message the detail message (which is saved for later retrieval
		 *                by the {@link #getMessage()} method).
		 * @param cause   the cause (which is saved for later retrieval by the
		 *                {@link #getCause()} method).  (A <tt>null</tt> value is
		 *                permitted, and indicates that the cause is nonexistent or
		 *                unknown.)
		 * @since 1.4
		 */
		public DyjException(String message, Throwable cause) {
			super(message, cause);
		}

		/**
		 * Constructs a new exception with the specified cause and a detail
		 * message of <tt>(cause==null ? null : cause.toString())</tt> (which
		 * typically contains the class and detail message of <tt>cause</tt>).
		 * This constructor is useful for exceptions that are little more than
		 * wrappers for other throwables (for example, {@link
		 * PrivilegedActionException}).
		 *
		 * @param cause the cause (which is saved for later retrieval by the
		 *              {@link #getCause()} method).  (A <tt>null</tt> value is
		 *              permitted, and indicates that the cause is nonexistent or
		 *              unknown.)
		 * @since 1.4
		 */
		public DyjException(Throwable cause) {
			super(cause);
		}

	}

	public static void SetRequestLog(String pid, String uid, String uname, String postValue,
			String resultValue, String type) throws IOException, DyjException {
		// public static final String HOST = "http://192.168.10.117:8090/";
		String url = HOST + "/YuChuKu/SetRequestLog?";
		// String url = "http://192.168.10.136:8090/YuChuKu/SetRequestLog?";
		String mName = url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf("?"));
		url = String.format(url, pid, uid, URLEncoder.encode(uname, "utf-8"), iKey);
		String bodyString = "";
		try {
			String reqBody = "";
			JSONObject mObj = new JSONObject();
			mObj.put("uid", uid);
			mObj.put("uName", uname);
			mObj.put("pid", pid);
			mObj.put("postValue", postValue);
			mObj.put("resultValue", resultValue);
			mObj.put("objtype", type);
			mObj.put("objfrom", "手机库房版app");
			mObj.put("key", "");
			reqBody = mObj.toJSONString();
			bodyString =HttpUtils.create(url).post().addReqBody(reqBody).getBodyString();
			// Log.d("zjy", "DyjInterface2->SetRequestLog(): reqBody==" + reqBody);
			// Log.d("zjy", DyjInterface2.class.getClass() + "->SetRequestLog(): ==" + bodyString);
			JSONObject mobj = JSONObject.parseObject(bodyString);
			boolean isSuccess = false;
			// boolean isSuccess = mobj.getBoolean("isSuccess");
			String retCode = mobj.getString("errcode");
			if ("0".equals(retCode)) {
				isSuccess = true;
			}
			if (isSuccess) {

			} else {
				String errrMsg = mobj.getString("errmsg");
				Log.w("zjy", DyjInterface2.class + "->UpdateStoreChekerInfo(): url==" + url);
				throw new DyjException(mName + ",接口异常," + errrMsg);
			}
		} catch (SocketException e) {
			throw new IOException("连接异常," + e.getMessage() + ",url=" + url);
		} catch (IOException e) {
			e.printStackTrace();
			throw new IOException("网络异常," + e.getMessage() + ",url=" + url);
		} catch (JSONException e) {
			throw new IOException("数据格式异常,json=" + bodyString);
		}
	}
}
