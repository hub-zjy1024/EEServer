package b1b.erp.js;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class Review {
	public static void main(String[] args) {
		String fullName = "北京供货商";
		String proShortName = "bjgh";
		String goodInfos = "bjgh";
		String proPhone = "12345634562";
		String proAddress = "北京市海淀区中关村";
		String proReceiveMan = "称称";
		String createDate = "2017-07-14";
		String hetongID = "102457";
		String strUrl = "http://localhost:8080/PrinterServer/HetongServlet?";
		// pid=1024521?proFullName=北京市供货商&hetongID=101110&proShortName=bjgh&goodInfos=name1,name2,name3,&proPhone=13452525623&proAddress=北京市海淀区中关村&proReceiveMan=晨晨&createDate=2017-07-13
		try {
			strUrl += "proFullName=" + URLEncoder.encode(fullName, "UTF-8");
			strUrl += "&proShortName=" + URLEncoder.encode(proShortName, "UTF-8"); 
			strUrl += "&goodInfos=" + URLEncoder.encode(goodInfos, "UTF-8"); 
			strUrl += "&proPhone=" + URLEncoder.encode(proPhone, "UTF-8"); 
			strUrl += "&proAddress=" + URLEncoder.encode(proAddress, "UTF-8"); 
			strUrl += "&proReceiveMan=" + URLEncoder.encode(proReceiveMan, "UTF-8"); 
			strUrl += "&createDate=" + URLEncoder.encode(createDate, "UTF-8"); 
			strUrl += "&hetongID=" + URLEncoder.encode(hetongID, "UTF-8"); 
			URL url = new URL(strUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			InputStream lin = conn.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(lin, "UTF-8"));
			System.out.println("response:" + reader.readLine());
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
