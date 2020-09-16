package pri.zjy.db;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class DebugUtil {

	public static boolean isLocal() {
		String mName = System.getProperty("user.name");
		if ("js".equals(mName)) {
			return true;
		}
		return false;
	}

	public static boolean isDebug2() {
		try {
			InetAddress addr = InetAddress.getLocalHost();
			String ip = addr.getHostAddress().toString(); // 获取本机ip
			String hostName = addr.getHostName().toString(); // 获取本机计算机名称
			if ("js-PC".equals(hostName)) {
				return true;
			}
		} catch (UnknownHostException e) {
			// e.printStackTrace();
		}
		return false;
	}
}
