package b1b.erp.js.bussiness;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import b1b.erp.js.yundan.sf.sfutils.HttpUtils;

public class LodopLoadder {

	static Logger mLogger=LoggerFactory.getLogger(LodopLoadder.class);
	public static class PrintInfo {
		public String printerName;
		public String url;
	}

	public static PrintInfo readLodopUrlBy(String name) {
		String url = "";
		String configUrl = "http://172.16.6.160:8006/DownLoad/dyj_kf/config.txt";
		PrintInfo minfo=new PrintInfo();
		try {
			String bodyString = HttpUtils.create(configUrl).getBodyString();
			JSONArray mObj = JSONArray.parseArray(bodyString);
			mLogger.info("online info={} key={}",JSONObject.toJSONString(bodyString),name);
			
			for (int i = 0; i < mObj.size(); i++) {
				JSONObject jsonObject = mObj.getJSONObject(i);
				String key = jsonObject.getString("kfName");
				String printerName = jsonObject.getString("printerName");
				String tUrl = jsonObject.getString("lodopUrl");
				if (key.equals(name)) {
					minfo.url=tUrl;
					minfo.printerName=printerName;
					mLogger.info("match info={}",JSONObject.toJSONString( minfo));
					return minfo;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return minfo;
	}
}
