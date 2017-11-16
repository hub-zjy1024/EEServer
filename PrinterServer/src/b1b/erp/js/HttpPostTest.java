package b1b.erp.js;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpPostTest {
	public static void main(String[] args) {
		try {
			readContentFromPost();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void readContentFromPost() throws IOException {
		// Post请求的url，与get不同的是不需要带参数
		URL postUrl = new URL("http://192.168.10.65:8080/PrinterServer/TestServlet");
		// 打开连接
		HttpURLConnection connection = (HttpURLConnection) postUrl.openConnection();
		// 设置是否向connection输出，因为这个是post请求，参数要放在
		// http正文内，因此需要设为true
		connection.setDoOutput(true);
		// Read from the connection. Default is true.
		connection.setDoInput(true);
		// 默认是 GET方式
		connection.setRequestMethod("POST");
		// Post 请求不能使用缓存
		connection.setUseCaches(false);
		// 配置本次连接的Content-type，配置为application/x-www-form-urlencoded的
		connection.setRequestProperty("Content-Type", "multipart/form-data");
		OutputStream out = connection.getOutputStream();
		// 正文，正文内容其实跟get的URL中 '? '后的参数字符串一致
		String divider="=======================";
		String content = "account=" + "一个大肥人\n";
		content += "pswd=" + "两个个大肥人\n";
		content += "beijin=" + "q个个大肥人\n";
		content += "pswd=" + "e个大肥人\n";
		content+=divider+"\n";
		content+="names=a"+"\n";
		// DataOutputStream.writeBytes将字符串中的16位的unicode字符以8位的字符形式写到流里面
		out.write(content.getBytes("utf-8"));
		out.flush();
		out.close();
		InputStream in = connection.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in, "utf-8"));
		String line;
		while ((line = reader.readLine()) != null) {
			System.out.println(line);
		}
		//
		// reader.close();
	}

}
