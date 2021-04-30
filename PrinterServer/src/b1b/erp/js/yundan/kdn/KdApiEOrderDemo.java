package b1b.erp.js.yundan.kdn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import b1b.erp.js.yundan.kdn.entity.Commodity;
import b1b.erp.js.yundan.kdn.entity.OrderInfo;
import b1b.erp.js.yundan.kdn.entity.Receiver;
import b1b.erp.js.yundan.kdn.entity.Sender;
import b1b.erp.js.yundan.kdn.entity.ShipCode;
import b1b.erp.js.yundan.sf.entity.SFSender;
import b1b.erp.js.yundan.sf.entity.YundanInput;
import b1b.erp.js.yundan.sf.entity.YundanModel;

/**
 *
 * 快递鸟电子面单接口
 *
 * @技术QQ群: 340378554
 * @see: http://kdniao.com/api-eorder
 * @copyright: 深圳市快金数据技术服务有限公司
 * 
 * ID和Key请到官网申请：http://kdniao.com/reg
 */

public class KdApiEOrderDemo {

	// 电商ID

	// 电商加密私钥，快递鸟提供，注意保管，不要泄漏
	private String AppKey = "0ab09fe6-f5b2-452a-85ee-41b94a24fcbd";
	// 请求url, 正式环境地址：http://api.kdniao.com/api/Eorderservice
	// 测试环境地址：http://testapi.kdniao.com:8081/api/EOrderService
	// private String ReqURL = "http://testapi.kdniao.com:8081/api/Eorderservice";
	private String ReqURL = "http://api.kdniao.com/api/Eorderservice";
	private String EBusinessID = "1641677";
	private String RequestType_Order = "1007";
	// private String EBusinessID = "test1641677";
	// private String ReqURL =
	// "http://sandboxapi.kdniao.com:8080/kdniaosandbox/gateway/exterfaceInvoke.json";
	private String mCharset = "utf-8";

	public static void main(String[] args) {

		KdApiEOrderDemo demo = new KdApiEOrderDemo();
		String code = getRandomCode();
		try {
			String ret = demo.orderOnlineByJson(code, "");
			JSONObject mobj = JSONObject.parseObject(ret);
			String retTempLate = mobj.getString("PrintTemplate");
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("mCode=" + code);
	}

	public String getSF150(String monthCode) {
		String code = getRandomCode();
		String retTempLate = "模版获取异常";
		try {
			String ret = orderOnlineByJson(code, monthCode);
			retTempLate = ret;
			/*
			 * JSONObject mobj = new JSONObject(ret); retTempLate = mobj.getString("PrintTemplate");
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}
		// retTempLate=retTempLate.replaceAll("\\\", "\\\\\\");
		return retTempLate;
	}

	public String getSFOrderWithOption(String monthCode, int pack) {
		String code = getRandomCode();
		String retTempLate = "模版获取异常";
		try {
			String ret = orderOnlineByJson(code, monthCode, pack);
			retTempLate = ret;
			/*
			 * JSONObject mobj = new JSONObject(ret); retTempLate = mobj.getString("PrintTemplate");
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}
		// retTempLate=retTempLate.replaceAll("\\\", "\\\\\\");
		return retTempLate;
	}

	public static String getRandomCode() {
		StringBuilder sb = new StringBuilder();
		String table = "1234567890";
		int len = 10;
		for (int i = 0; i < len; i++) {
			int index = (int) (Math.random() * 10);
			char mChar = table.charAt(index);
			sb.append("" + mChar);
		}
		String code = sb.toString();
		return code;
	}

	public YundanModel startOrder(YundanInput body) throws IOException {
		if ("test".equals(body.printer)) {
		} else {

		}
		YundanModel mModle = new YundanModel();
		try {
			// OrderBody
			SFSender reqParams = body.mSender;
			OrderInfo minfo = new OrderInfo();
			minfo.MemberID = "123456";
			minfo.CustomerName = "admin";
			minfo.CustomerPwd = "kdniao";
			minfo.IsReturnPrintTemplate = 1;
			String code = getRandomCode();
			// minfo.OrderCode = body.mSender.orderID;
			minfo.OrderCode = code;
			minfo.MonthCode = "";
			minfo.TemplateSize = "150";
			// minfo.ShipperCode = ShipCode.shipCode_YZPY;
			minfo.ShipperCode = ShipCode.shipCode_YZBK;
			// minfo.ShipperCode = ShipCode.shipCode_EMS;
			minfo.Quantity = Integer.parseInt(reqParams.bagCounts);

			minfo.Remark = body.tuoji;
			System.out.println("use Test1 remark");
			minfo.Remark = "testKdn";

			minfo.PayType = "1";
			minfo.Commodity = new ArrayList<Commodity>();
			Commodity tCommodity = new Commodity();
			tCommodity.GoodsName = "电子元器件";
			tCommodity.Goodsquantity = 1;
			minfo.Commodity.add(tCommodity);
			// minfo.setMemberID("123456");
			// minfo.setCustomerName("admin");
			// minfo.setIsReturnPrintTemplate(1);
			// minfo.setOrderCode(body.mSender.orderID);
			// minfo.setMonthCode("");
			// minfo.setTemplateSize("");
			// minfo.setShipperCode(ShipCode.shipCode_YZPY);
			// 包裹数
			// minfo.setQuantity(Integer.parseInt(reqParams.bagCounts));
			// 备注
			// minfo.setRemark(body.tuoji);
			String PostCode = "100000";
			// 寄件和
			Sender mSender = new Sender();
			mSender.Company = reqParams.j_company;
			mSender.Address = reqParams.j_address;
			mSender.Name = reqParams.j_name;
			mSender.Tel = reqParams.j_tel;
			mSender.Mobile = reqParams.j_tel;
			mSender.PostCode = PostCode;
			// mSender.ProvinceName="";
			// mSender.CityName="";
			// mSender.ExpAreaName="";

			// 收件信息
			Receiver mReceiver = new Receiver();
			mReceiver.Company = reqParams.d_company;
			mReceiver.Address = reqParams.d_address;
			mReceiver.Name = reqParams.d_name;
			mReceiver.Tel = reqParams.d_tel;
			mReceiver.Mobile = reqParams.d_tel;
			mReceiver.PostCode = PostCode;
			// mReceiver.ProvinceName="";
			// mReceiver.CityName="";
			// mReceiver.ExpAreaName="";

			minfo.Sender = mSender;
			minfo.Receiver = mReceiver;

			// minfo.setSender(mSender);
			// minfo.setReceiver(mReceiver);
			String json = JSONObject.toJSONString(minfo);
			// String testRet = orderOnlineByJson(code, "", 1);
			mModle = startOrderByJson(json);
			System.out.println("kdnapi retJson=" + JSONObject.toJSONString(mModle));
			return mModle;
		} catch (JSONException e) {
			throw new IOException("快递鸟参数格式异常," + e.getMessage(), e);
		} catch (IOException e) {
			throw new IOException("快递鸟io异常," + e.getMessage(), e);
		} catch (Exception e) {
			throw new IOException("快递鸟下单异常" + e.getMessage(), e);
		}
	}

	/**
	 * Json方式 电子面单
	 * @throws Exception 
	 */
	public String orderOnlineByJson(String test, String monthCode) throws Exception {
		// Quantity包裹数量
		int tempLateCode = 15001;
		String payType = "1";
		String requestData = "{\"OrderCode\": \"" + test + "\"," + "\"ShipperCode\":\"SF\","
				+ "\"PayType\":1," + "\"ExpType\":1," + "\"Cost\":1.0," + "\"OtherCost\":1.0,"
				+ "\"TemplateSize\":\"21001\"," + "\"LogisticCode\": \"1234561\"," + "\"Sender\":{"
				+ "\"Company\":\"LV\",\"Name\":\"Taylor\",\"Mobile\":\"15018442396\",\"ProvinceName\":\"上海\",\"CityName\":\"上海\",\"ExpAreaName\":\"青浦区\",\"Address\":\"明珠路73号\"},"
				+ "\"Receiver\":" + "{"
				+ "\"Company\":\"GCCUI\",\"Name\":\"Yann\",\"Mobile\":\"15018442396\",\"ProvinceName\":\"北京\",\"CityName\":\"北京\",\"ExpAreaName\":\"朝阳区\",\"Address\":\"三里屯街道雅秀大厦\"},"
				+ "\"Commodity\":" + "[{"
				+ "\"GoodsName\":\"鞋子\",\"Goodsquantity\":1,\"GoodsWeight\":1.0}],"
				+ "\"Weight\":1.0," + "\"Quantity\":1," + "\"Volume\":0.0," + "\"Remark\":\"小心轻放\","
				+ "\"IsReturnPrintTemplate\":1}";

		if (monthCode == null || "".equals(monthCode.trim())) {

		} else {
			payType = "3";
		}
		String testCode3 = "{\"MemberID\":\"123456\",\"CustomerName\":\"admin\",\"CustomerPwd\":\"kdniao\",\"SendSite\":\"福田保税区网点\",\"ShipperCode\":\"SF\",\"LogisticCode\":\"1234561\",\"ThrOrderCode\":\"1234567890\",\"OrderCode\":\""
				+ test + "\",\"MonthCode\":\"" + monthCode + "\",\"PayType\":\"" + payType
				+ "\",\"ExpType\":\"1\",\"IsReturnSignBill\":0,\"OperateRequire\":\"\",\"Cost\":12,\"OtherCost\":0,\"Receiver\":{\"Company\":\"腾讯科技\",\"Name\":\"张三\",\"Tel\":\"0755-0907283\",\"Mobile\":\"13709076789\",\"PostCode\":\"435100\",\"ProvinceName\":\"广东省\",\"CityName\":\"深圳市\",\"ExpAreaName\":\"福田区\",\"Address\":\"深南大道2009号\"},\"Sender\":{\"Company\":\"快金数据\",\"Name\":\"李四\",\"Tel\":\"0755-1111111\",\"Mobile\":\"13932080778\",\"PostCode\":\"435100\",\"ProvinceName\":\"广东省\",\"CityName\":\"深圳市\",\"ExpAreaName\":\"福田区\",\"Address\":\"福田保税区\"},\"IsNotice\":1,\"StartDate\":\"\",\"EndDate\":\"\",\"Weight\":3,\"Quantity\":1,\"Volume\":2,\"Remark\":\"\",\"AddService\":[],\"Commodity\":[{\"GoodsName\":\"书本\",\"GoodsCode\":\"20398\",\"Goodsquantity\":1,\"GoodsPrice\":100,\"GoodsWeight\":2,\"GoodsVol\":10,\"GoodsDesc\":\"格林童话\"}],\"IsReturnPrintTemplate\":1,\"IsSendMessage\":0,\"TemplateSize\":"
				+ tempLateCode + "}";
		requestData = testCode3;
		String result = testD(requestData);

		System.out.println("---------mOut= " + result);
		// 根据公司业务处理返回的信息......

		return result;
	}

	/**
	 * Json方式 电子面单
	 * @throws Exception 
	 */
	public String orderOnlineByJson(String test, String monthCode, int pack) throws Exception {
		System.out.println("-------------------account=" + monthCode + ",pack=" + pack);
		int tempLateCode = 15001;
		String payType = "1";

		String requestData = "{\"OrderCode\": \"" + test + "\"," + "\"ShipperCode\":\"YZBK\","
				+ "\"PayType\":1," + "\"ExpType\":1," + "\"Cost\":1.0," + "\"OtherCost\":1.0,"
				+ "\"TemplateSize\":\"21001\"," + "\"LogisticCode\": \"1234561\"," + "\"Sender\":{"
				+ "\"Company\":\"LV\",\"Name\":\"Taylor\",\"Mobile\":\"15018442396\",\"ProvinceName\":\"上海\",\"CityName\":\"上海\",\"ExpAreaName\":\"青浦区\",\"Address\":\"明珠路73号\"},"
				+ "\"Receiver\":" + "{"
				+ "\"Company\":\"GCCUI\",\"Name\":\"Yann\",\"Mobile\":\"15018442396\",\"ProvinceName\":\"北京\",\"CityName\":\"北京\",\"ExpAreaName\":\"朝阳区\",\"Address\":\"三里屯街道雅秀大厦\"},"
				+ "\"Commodity\":" + "[{"
				+ "\"GoodsName\":\"鞋子\",\"Goodsquantity\":1,\"GoodsWeight\":1.0}],"
				+ "\"Weight\":1.0," + "\"Quantity\":" + pack + "," + "\"Volume\":0.0,"
				+ "\"Remark\":\"小心轻放\"," + "\"IsReturnPrintTemplate\":1}";

		if (monthCode == null || "".equals(monthCode.trim())) {

		} else {
			payType = "3";
		}
		String testCode3 = "{\"MemberID\":\"123456\",\"CustomerName\":\"admin\",\"CustomerPwd\":\"kdniao\",\"SendSite\":\"福田保税区网点\",\"ShipperCode\":\"YZPY\",\"LogisticCode\":\"1234561\",\"ThrOrderCode\":\"1234567890\""
				+ ",\"OrderCode\":\"" + test + "\",\"MonthCode\":\"" + monthCode
				+ "\",\"PayType\":\"" + payType
				+ "\",\"ExpType\":\"1\",\"IsReturnSignBill\":0,\"OperateRequire\":\"\",\"Cost\":12,\"OtherCost\":0,"
				+ "\"Receiver\":{\"Company\":\"腾讯科技\",\"Name\":\"张三\",\"Tel\":\"0755-0907283\",\"Mobile\":\"13709076789\",\"PostCode\":\"435100\",\"ProvinceName\":\"广东省\",\"CityName\":\"深圳市\",\"ExpAreaName\":\"福田区\",\"Address\":\"深南大道2009号\"},"
				+ "\"Sender\":{\"Company\":\"快金数据\",\"Name\":\"李四\",\"Tel\":\"0755-1111111\",\"Mobile\":\"13932080778\",\"PostCode\":\"435100\",\"ProvinceName\":\"广东省\",\"CityName\":\"深圳市\",\"ExpAreaName\":\"福田区\",\"Address\":\"福田保税区\"}"
				+ ",\"IsNotice\":1,\"StartDate\":\"\",\"EndDate\":\"\",\"Weight\":3,\"Quantity\":"
				+ pack
				+ ",\"Volume\":2,\"Remark\":\"\",\"AddService\":[],\"Commodity\":[{\"GoodsName\":\"书本\",\"GoodsCode\":\"20398\",\"Goodsquantity\":1,\"GoodsPrice\":100,\"GoodsWeight\":2,\"GoodsVol\":10,\"GoodsDesc\":\"格林童话\"}]"
				+ ",\"IsReturnPrintTemplate\":1,\"IsSendMessage\":0,\"TemplateSize\":"
				+ tempLateCode + "}";
		requestData = testCode3;
		String result = testD(requestData);
		System.out.println("mOut= " + result);
		// 根据公司业务处理返回的信息......

		return result;
	}

	private String testD(String requestData) throws UnsupportedEncodingException, Exception {
		String dataSign = encrypt(requestData, AppKey, mCharset);
		String json2 = "{\"MemberID\":\"123456\",\"CustomerName\":\"admin\",\"CustomerPwd\":\"kdniao\",\"SendSite\":\"福田保税区网点\",\"ShipperCode\":\"ZTO\",\"LogisticCode\":\"1234561\",\"ThrOrderCode\":\"1234567890\",\"OrderCode\":\"1234561\",\"MonthCode\":\"\",\"PayType\":\"1\",\"ExpType\":\"1\",\"IsReturnSignBill\":0,\"OperateRequire\":\"\",\"Cost\":12,\"OtherCost\":0,\"Receiver\":{\"Company\":\"腾讯科技\",\"Name\":\"张三\",\"Tel\":\"0755-0907283\",\"Mobile\":\"13709076789\",\"PostCode\":\"435100\",\"ProvinceName\":\"广东省\",\"CityName\":\"深圳市\",\"ExpAreaName\":\"福田区\",\"Address\":\"深南大道2009号\"},\"Sender\":{\"Company\":\"快金数据\",\"Name\":\"李四\",\"Tel\":\"0755-1111111\",\"Mobile\":\"13932080778\",\"PostCode\":\"435100\",\"ProvinceName\":\"广东省\",\"CityName\":\"深圳市\",\"ExpAreaName\":\"福田区\",\"Address\":\"福田保税区\"},\"IsNotice\":1,\"StartDate\":\"\",\"EndDate\":\"\",\"Weight\":3,\"Quantity\":1,\"Volume\":2,\"Remark\":\"\",\"AddService\":[],\"Commodity\":[{\"GoodsName\":\"书本\",\"GoodsCode\":\"20398\",\"Goodsquantity\":1,\"GoodsPrice\":100,\"GoodsWeight\":2,\"GoodsVol\":10,\"GoodsDesc\":\"格林童话\"}],\"IsReturnPrintTemplate\":1,\"IsSendMessage\":0,\"TemplateSize\":180}";
		String tempKey = "36c3ec91-a458-4d86-be60-cd061943087e";
		// String tempSign = encrypt(json2, tempKey, mCharset);
		// System.out.println("tempSign=" + tempSign);
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put("RequestData", urlEncoder(requestData, mCharset));
		params.put("EBusinessID", EBusinessID);
		params.put("RequestType", RequestType_Order);
		String testCode2 = "{\"MemberID\":\"123456\",\"CustomerName\":\"admin\",\"CustomerPwd\":\"kdniao\",\"SendSite\":\"福田保税区网点\",\"ShipperCode\":\"SF\",\"LogisticCode\":\"1234561\",\"ThrOrderCode\":\"1234567890\",\"OrderCode\":\"1234561\",\"MonthCode\":\"\",\"PayType\":\"1\",\"ExpType\":\"1\",\"IsReturnSignBill\":0,\"OperateRequire\":\"\",\"Cost\":12,\"OtherCost\":0,\"Receiver\":{\"Company\":\"腾讯科技\",\"Name\":\"张三\",\"Tel\":\"0755-0907283\",\"Mobile\":\"13709076789\",\"PostCode\":\"435100\",\"ProvinceName\":\"广东省\",\"CityName\":\"深圳市\",\"ExpAreaName\":\"福田区\",\"Address\":\"深南大道2009号\"},\"Sender\":{\"Company\":\"快金数据\",\"Name\":\"李四\",\"Tel\":\"0755-1111111\",\"Mobile\":\"13932080778\",\"PostCode\":\"435100\",\"ProvinceName\":\"广东省\",\"CityName\":\"深圳市\",\"ExpAreaName\":\"福田区\",\"Address\":\"福田保税区\"},\"IsNotice\":1,\"StartDate\":\"\",\"EndDate\":\"\",\"Weight\":3,\"Quantity\":1,\"Volume\":2,\"Remark\":\"\",\"AddService\":[],\"Commodity\":[{\"GoodsName\":\"书本\",\"GoodsCode\":\"20398\",\"Goodsquantity\":1,\"GoodsPrice\":100,\"GoodsWeight\":2,\"GoodsVol\":10,\"GoodsDesc\":\"格林童话\"}],\"IsReturnPrintTemplate\":1,\"IsSendMessage\":0,\"TemplateSize\":\"15001\"}";
		// String tempKey = "36c3ec91-a458-4d86-be60-cd061943087e";
		// String dataSign2 = encrypt(testCode3, tempKey, mCharset);
		// String dataSign3 = encrypt2(testCode3, tempKey, mCharset);
		// System.out.println("------code1=" + dataSign2);
		// System.out.println("------code2=" + dataSign3);
		params.put("DataSign", urlEncoder(dataSign, mCharset));
		params.put("DataType", "2");
		String result = sendPost(ReqURL, params);
		// System.out.println("retResult=" + result);
		return result;
	}

	public YundanModel startOrderByJson(String json) throws IOException {
		try {
			// System.out.println("inputjson=" + json);
			String testD = testD(json);
			JSONObject mobj = JSONObject.parseObject(testD);
			if (mobj.getBooleanValue("Success")) {
				//
				YundanModel mModle = new YundanModel();
				JSONObject orderObj = mobj.getJSONObject("Order");
				mModle.yundanId = orderObj.getString("LogisticCode");
				mModle.huidanId = "";
				mModle.destcode = "";
				String html = mobj.getString("PrintTemplate");
				mModle.htmls.add(html);
				return mModle;
			} else {
				String errmsg = mobj.getString("Reason");
				throw new Exception("快递鸟下单失败：" + errmsg);
			}
		} catch (UnsupportedEncodingException e) {
			throw new IOException(e.getMessage(), e);
		} catch (Exception e) {
			throw new IOException(e.getMessage(), e);
		}
	}

	/**
	 * MD5加密
	 * @param str 内容       
	 * @param charset 编码方式
	 * @throws Exception 
	 */
	private String MD5(String str, String charset) throws Exception {
		byte[] result = MD5Byte(str, charset);
		return byte2Str(result).toLowerCase();
	}

	private byte[] MD5Byte(String str, String charset) throws Exception {
		MessageDigest md = MessageDigest.getInstance("MD5");
		// md.update(str.getBytes(charset));
		md.update(str.getBytes());
		byte[] result = md.digest();
		return result;
	}

	String byte2Str(byte[] result) {
		StringBuffer sb = new StringBuffer(32);
		for (int i = 0; i < result.length; i++) {
			int val = result[i] & 0xff;
			if (val <= 0xf) {
				sb.append("0");
			}
			sb.append(Integer.toHexString(val));
		}
		return sb.toString();
	}

	/**
	 * base64编码
	 * @param str 内容       
	 * @param charset 编码方式
	 * @throws UnsupportedEncodingException 
	 */
	private String base64(String str, String charset) throws UnsupportedEncodingException {
		String encoded = Base64.getEncoder().encodeToString(str.getBytes());
		return encoded;
	}

	private String urlEncoder(String str, String charset) throws UnsupportedEncodingException {
		String result = URLEncoder.encode(str, charset);
		result = result.replaceAll("\\+", "%20");
		return result;
	}

	/**
	 * 电商Sign签名生成
	 * @param content 内容   
	 * @param keyValue Appkey  
	 * @param charset 编码方式
	 * @throws UnsupportedEncodingException ,Exception
	 * @return DataSign签名
	 */
	@SuppressWarnings("unused")
	private String encrypt(String content, String keyValue, String charset)
			throws UnsupportedEncodingException, Exception {
		String md5 = "";
		String input = content;
		if (keyValue != null) {
			input = content + keyValue;
		}
		md5 = MD5(input, charset);
		// System.out.println("input=" + input + "\r\nmMd5=" + md5);
		return base64(md5, charset);
	}

	@SuppressWarnings("unused")
	private String encrypt2(String content, String keyValue, String charset)
			throws UnsupportedEncodingException, Exception {
		String md5 = "";
		String input = content;
		if (keyValue != null) {
			input = content + keyValue;
		}
		byte[] md5data = MD5Byte(input, charset);
		return Base64.getEncoder().encodeToString(md5data);
	}

	/**
	* 向指定 URL 发送POST方法的请求     
	* @param url 发送请求的 URL    
	* @param params 请求的参数集合     
	* @return 远程资源的响应结果
	 * @throws IOException 
	*/
	@SuppressWarnings("unused")
	private String sendPost(String url, Map<String, String> params) throws IOException {
		OutputStreamWriter out = null;
		BufferedReader in = null;
		StringBuilder result = new StringBuilder();
		try {
			URL realUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// POST方法
			conn.setRequestMethod("POST");
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.connect();
			// 获取URLConnection对象对应的输出流
			out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
			// 发送请求参数
			if (params != null) {
				StringBuilder sbParam = new StringBuilder();
				for (Map.Entry<String, String> entry : params.entrySet()) {
					if (sbParam.length() > 0) {
						sbParam.append("&");
					}
					sbParam.append(entry.getKey());
					sbParam.append("=");
					sbParam.append(entry.getValue());
					// System.out.println(entry.getKey() + ":" + entry.getValue());
				}
				// System.out.println("param:" + sbParam.toString());
				out.write(sbParam.toString());
			}
			// flush输出流的缓冲
			out.flush();
			int code = conn.getResponseCode();
			if (code != HttpURLConnection.HTTP_OK) {
				InputStream errorStream = conn.getErrorStream();
				if (errorStream != null) {
					StringBuilder errBuilder = new StringBuilder();
					in = new BufferedReader(new InputStreamReader(errorStream, "UTF-8"));
					String line;
					while ((line = in.readLine()) != null) {
						errBuilder.append(line);
					}
					throw new IOException("hasError," + errBuilder.toString());
				}
			}
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			String line;
			while ((line = in.readLine()) != null) {
				result.append(line);
			}
		} catch (IOException e) {
			throw new IOException("连接网络失败", e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new IOException("下单请求失败", e);
		}
		// 使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result.toString();
	}
}
