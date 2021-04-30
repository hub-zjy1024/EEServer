package b1b.erp.js.yundan.ky.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlpull.v1.XmlPullParserException;

import com.alibaba.fastjson.JSONObject;

import b1b.erp.js.bussiness.SFPrinterUtil;
import b1b.erp.js.entity.RetJsonObj;
import b1b.erp.js.utils.UploadUtils;
import b1b.erp.js.utils.net.SF_Server;
import b1b.erp.js.yundan.ky.entity.BillOrder;
import b1b.erp.js.yundan.ky.entity.BillUserInfo;
import b1b.erp.js.yundan.ky.entity.OrderInfo;
import b1b.erp.js.yundan.ky.entity.OrderRetInfo;
import b1b.erp.js.yundan.sf.bussiness.OrderMgr;
import b1b.erp.js.yundan.sf.entity.OrderBody;
import b1b.erp.js.yundan.sf.entity.YundanInput;
import b1b.erp.js.yundan.sf.entity.YundanModel;
import b1b.erp.js.yundan.sf.jee.Log;
import b1b.erp.js.yundan.sf.sfutils.SFWsUtils.OrderResponse;

public class KyService {

	private OrderBody mBody;
	private HttpServletRequest mRequest;
	static Logger mLogger = LoggerFactory.getLogger(KyService.class);

	public KyService(OrderBody mBody, HttpServletRequest mRequest) {
		super();
		this.mBody = mBody;
		this.mRequest = mRequest;
	}

	/**
	 * @return
	 * @throws Exception
	 */
	public YundanModel startOrderNew() throws Exception {
		 String goodInfos = mBody.goodInfos;
		final String cardID = mBody.reqParams.custid;
		final String payType = mBody.payType;
		final String serverType = mBody.reqParams.expressType;
		final String counts = mBody.reqParams.bagCounts;
		final String printName = mBody.printer;
		String ifSing = mBody.reqParams.need_return_tracking_no;
		String pid = mBody.pid;
		// mBody.reqParams
		// ifSing = "1";
		// if (cboSign.isChecked()) {
		//
		// }
		final String finalIfSing = ifSing;
		OrderInfo orderInfo = new OrderInfo();
		List<BillOrder> mBills = new ArrayList<>();
		BillOrder realBill = new BillOrder();
		// 寄件人信息
		BillUserInfo jUser = new BillUserInfo();
		String jAddress = mBody.reqParams.j_address;
		String jName = mBody.reqParams.j_name;
		String jTel = mBody.reqParams.j_tel;
		String jComapany = mBody.reqParams.j_company;

		String dAddress = mBody.reqParams.d_address;
		String dName = mBody.reqParams.d_name;
		String dTel = mBody.reqParams.d_tel;
		String dCompany = mBody.reqParams.d_company;
		jUser.setAddress(jAddress);
		jUser.setPerson(jName);
		jUser.setMobile(jTel);
		jUser.setCompanyName(jComapany);
		// 收件人信息
		BillUserInfo dUser = new BillUserInfo();
		dUser.setAddress(dAddress);
		dUser.setPerson(dName);
		dUser.setMobile(dTel);
		dUser.setCompanyName(dCompany);

		realBill.setPreWaybillDelivery(jUser);
		realBill.setPreWaybillPickup(dUser);

		// 其他参数
		realBill.setCount(Integer.parseInt(counts));
		// dismantling
		realBill.setDismantling(20);

		// serverType
		// 10-当天达
		int serviceMode = 20;
		switch (serverType) {
		case "当天达":
			serviceMode = 10;
			break;
		case "次日达":
			serviceMode = 20;
			break;
		case "隔日达":
			serviceMode = 30;
			break;
		case "陆运件":
			serviceMode = 40;
			break;
		case "同城次日":
			serviceMode = 50;
			break;
		case "次晨达":
			serviceMode = 50;
			break;
		case "同城即日":
			serviceMode = 70;
			break;
		case "航空件":
			serviceMode = 80;
			break;
		case "早班件":
			serviceMode = 90;
			break;
		case "中班件":
			serviceMode = 100;
			break;
		case "晚班件":
			serviceMode = 110;
			break;
		case "省内次日":
			serviceMode = 160;
			break;
		case "省内即日":
			serviceMode = 170;
			break;
		case "空运":
			serviceMode = 210;
			break;
		case "专运":
			serviceMode = 220;
			break;
		default:
			break;
		}
		realBill.setServiceMode(serviceMode);
		int payMode = 10;
		if ("转第三方付款".equals(payType)) {
			payMode = 30;
		} else if ("到付".equals(payType)) {
			payMode = 20;
		}
		realBill.setPayMode(payMode);
		if(goodInfos==null||"".equals(goodInfos)){
			goodInfos=mBody.note;
		}
		if(goodInfos==null||"".equals(goodInfos)){
			goodInfos="货物";
		}
		 realBill.setGoodsType(goodInfos);
//		realBill.setGoodsType("托寄物");
		String tempOrderId = pid + "_" + UploadUtils.getRandomNumber(6);

		realBill.setOrderId(tempOrderId);
		realBill.setPaymentCustomer(cardID);
		int setReceiptFlag = 20;
		if (finalIfSing.equals("1")) {
			setReceiptFlag = 10;
			// 签回单时必须要指定回单份数
			realBill.setReceiptCount(1);
		}
		realBill.setReceiptFlag(setReceiptFlag);
		realBill.setWaybillRemark("");
		mBills.add(realBill);

		orderInfo.setCustomerCode(cardID);
		orderInfo.setPlatformFlag(KyExpressUtils.platformFlag);
		orderInfo.setOrderInfos(mBills);
		OrderResponse retInfo = null;

		String errMsg = "未知错误";
		int code = 1;
		try {
			retInfo = getOrderRetInfo(orderInfo);
			OrderRetInfo mret = new OrderRetInfo();
			YundanModel insertDb = insertDb(retInfo);
			return insertDb;
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new IOException("缺少必要参数");
		} catch (IOException e) {
			errMsg = e.getMessage();
			e.printStackTrace();
			throw new IOException(errMsg, e);
		}
	}

	public YundanModel startOrder(YundanInput minput) throws IOException {
		YundanModel mModle = new YundanModel();
		KyHtmlModel kyHtmlMaker = new KyHtmlModel(minput, mRequest);
		List<String> onlyHtmls = kyHtmlMaker.getOnlyHtmls();
		mModle.yundanId = minput.response.yundanId;
		mModle.destcode = minput.response.destcode;
		StringBuffer requestURL = mRequest.getRequestURL();
		// minput.code="";
		// minput.code="";
		String data = "debug";
		mModle.url = requestURL.substring(0, requestURL.lastIndexOf("/"))
				+ "/SFPrintV4Html.jsp?data=" + data;
		mModle.htmls.addAll(onlyHtmls);
		return mModle;
	}

	// private YundanModel insertDb(OrderRetInfo kyRetInfo) throws Exception {
	//
	// }

	public RetJsonObj orderAndGetRetobj() {
		RetJsonObj retJson = new RetJsonObj();
		try {
			YundanModel startOrderNew = startOrderNew();
			retJson.errCode = 0;
			retJson.data.add(startOrderNew);
			retJson.errMsg = "下单成功";
		} catch (Exception e) {
			e.printStackTrace();
			retJson.errMsg = e.getMessage();
		}
		return retJson;
	}

	private YundanModel insertDb(OrderResponse kyRetInfo) throws Exception {
		YundanInput minput = new YundanInput();
		OrderBody mobj = mBody;
		String pid = mobj.pid;
		minput.payType = mobj.payType;
		minput.goodInfos = mobj.goodInfos;
		minput.pid = mobj.pid;
		int flag = mobj.flag;
		StringBuilder sbTuiji = new StringBuilder();
		String[] infos;
		StringBuilder builder = new StringBuilder();
		String nowTimeStr = SFPrinterUtil.getCurrentDate();
		String goodinfos = mobj.goodInfos;
		String weight = mobj.weight;
		String note = mobj.note;
		if (goodinfos != null) {
			infos = goodinfos.split("\\$");
			for (int i = 0; i < infos.length; i++) {
				if (i == 3) {
					if (infos.length > 3) {
						builder.append("...");
					}
					break;
				}
				String[] s = infos[i].split("&");
				if (s.length != 1) {
					builder.append(s[0] + ":" + s[1]);
					if (i != 2) {
						builder.append("\n");
					}
				}
			}
			if (flag == 1) {
				// 手机托寄
				builder.append(String.format(",%s_%s_and", nowTimeStr, pid));
			} else {
				// 其他托寄
				builder.append(String.format(",%s_%s_" + flag, nowTimeStr, pid));
			}
		} else {
			builder.append(String.format("无型号信息,%s_%s_and", nowTimeStr, pid));
		}
		if (note != null) {
			builder = new StringBuilder();
			/*
			 * builder.append(note); builder.append("_"); builder.append(nowTimeStr);
			 * builder.append("_"); builder.append(pid);
			 */
			builder.append(String.format("%s,%s_%s", note, nowTimeStr, pid));
			if (flag == 1) {
				// 手机托寄
				builder.append("_and");
			} else {
				builder.append("_" + flag);
				// 其他托寄
			}
		}
		OrderMgr orderManager = new OrderMgr();
		// int flag=minput.flag;
		minput.tuoji = builder.toString();
		if (weight == null) {
			weight = "";
		}
		minput.weight = weight;
		// minput.tuoji = flag;
		minput.mSender = mobj.reqParams;
		minput.response = kyRetInfo;
		// minput.response = orderResponse;
		minput.printer = mobj.printer;
		minput.yundanType = mobj.yundanType;
		minput.flag = mobj.flag;
		minput.isSpecial = mobj.isSpecial;
		minput.exLog = "kdn";
		OrderResponse orderResponse = kyRetInfo;

		String randowmID = UploadUtils.getRandomWithTime();
		HttpServletRequest request = mRequest;
		String mHostAndPort = request.getRequestURL().substring(0,
				request.getRequestURL().indexOf("/", 8));
		String servletPath = request.getServletContext().getContextPath();
		String url = mHostAndPort + servletPath + "/SFPrintV4Html.jsp?";
		String sJson = JSONObject.toJSONString(minput);
		// System.out.println("mSaveJson=" + sJson);
		orderManager.insertData(randowmID, pid, orderResponse.yundanId, sJson);
		url += "data=" + randowmID;

		YundanModel mModle = new YundanModel();
		mModle.yundanId = orderResponse.yundanId;
		mModle.destcode = orderResponse.destcode;
		if ("1".equals(mobj.reqParams.need_return_tracking_no)) {
			mModle.huidanId = orderResponse.returnResponse == null ? ""
					: orderResponse.returnResponse.yundanId;
		} else {
			mModle.huidanId = "";
		}
		mModle.url = url;
		// retJson.data.add(orderResponse.yundanId);
		// retJson.data.add(orderResponse.destcode);
		// retJson.data.add(url);
		// retJson.data.add(onlyHtmls);
		KyHtmlModel model = new KyHtmlModel(minput, request);
		List<String> onlyHtmls = model.getOnlyHtmls();
		mModle.htmls.addAll(onlyHtmls);
		return mModle;
	}

	OrderResponse getOrderRetInfo(OrderInfo orderInfo) throws IOException {
		String errMsg = "";
		String newOrderJson = "";
		try {
			OrderResponse mInfo = new OrderResponse();
			com.alibaba.fastjson.JSONObject mObj = (com.alibaba.fastjson.JSONObject) com.alibaba.fastjson.JSONObject
					.toJSON(orderInfo);
			newOrderJson = mObj.toString();
			Log.d("zjy", getClass() + "->run():newApi json ==" + newOrderJson);
			String newApiRes = SF_Server.PostDataOpenApiInfo(newOrderJson);
//			 System.out.println( "PostDataOpenApiInfo,mjson= " + newOrderJson + ",\r\nret=" +
//			 newApiRes);
			Log.d("zjy", getClass() + "->run():newApi Res ==" + newApiRes);
			com.alibaba.fastjson.JSONObject mresJobj = com.alibaba.fastjson.JSONObject
					.parseObject(newApiRes);
			int code = mresJobj.getIntValue("code");
			String msg = mresJobj.getString("msg");

			if (code == 10000) {
				// System.out.println("newOrderJson ="
				// + newOrderJson
				// + "\r\n"
				// + "ky order ok retData=" + newApiRes+"");
				if (mresJobj.containsKey("data")) {
					com.alibaba.fastjson.JSONObject dataObj = mresJobj.getJSONArray("data")
							.getJSONObject(0);
					OrderRetInfo tRInfo = com.alibaba.fastjson.JSONObject
							.parseObject(dataObj.toJSONString(), OrderRetInfo.class);
					mInfo.yundanId = tRInfo.waybillNumber;
					mInfo.destcode = tRInfo.areaCode;
					// JSONObject mob = new JSONObject.wrap(mInfo);
					return mInfo;
				} else {
					mLogger.warn("noData error={}", newOrderJson);
					throw new Exception("接口返回异常,返回数据为空");
				}
			} else {
				// System.out.println("failed json=" + newOrderJson);
				mLogger.warn("failed json={}", newOrderJson);
				throw new Exception("接口返回异常," + msg);
			}
		} catch (IOException e) {
			e.printStackTrace();
			errMsg = "下单异常," + e.getMessage();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			errMsg = "下单异常," + e.getMessage();
		} catch (com.alibaba.fastjson.JSONException e) {
			e.printStackTrace();
			errMsg = "下单异常," + e.getMessage();
		} catch (Exception e) {
			e.printStackTrace();
			errMsg = e.getMessage();
			mLogger.warn("failed json={}", newOrderJson);
		}
		throw new IOException(errMsg);
	}
}
