package b1b.erp.js.yundan.ky.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONObject;
import com.sun.swing.internal.plaf.basic.resources.basic;

import b1b.erp.js.bussiness.SFPrinterUtil;
import b1b.erp.js.utils.Myuuid;
import b1b.erp.js.utils.StreamRead;
import b1b.erp.js.utils.TestBarcode4j;
import b1b.erp.js.utils.UploadUtils;
import b1b.erp.js.yundan.SFv4Model;
import b1b.erp.js.yundan.sf.entity.YundanInput;
import b1b.erp.js.yundan.sf.sfutils.SFWsUtils.OrderResponse;

public class KyHtmlModel {
	String templatePath;
	String dir = "";
	String imgPath = "";
	static final int returnID = -1;

	private String yundanType;
	String resRoot = "";

	public KyHtmlModel(HttpServletRequest request, String yundanType) {
		this.yundanType = yundanType;
		if ("210".equals(yundanType)) {
			templatePath = request.getServletContext().getRealPath("/docTemplate/ky_html_210.html");
		} else {
			System.out.println("ky not found template type " + yundanType);
			templatePath = request.getServletContext().getRealPath("/docTemplate/ky_html_210.html");
		}
		String totalUrl = request.getRequestURL().toString();
		// dir = totalUrl.substring(0, totalUrl.lastIndexOf("/"))+"/imgs/sf/";
		// dir = "http://" + request.getRemoteHost() + ":" + request.getRemotePort()
		// + request.getContextPath() + "/imgs/sf/";
		// String preUrl = "http://" + request.getLocalAddr() + ":8080";
//		String preUrl = "http://oa.wl.net.cn:6060";
		String preUrl = "http://oa.t996.top:6060";
		dir = preUrl + request.getContextPath() + "/imgs/sf/";
		resRoot = preUrl + request.getContextPath() + "/imgs/";
		// System.out.println("mdir=" + dir);
		String wordDir = request.getServletContext().getInitParameter("dyjDir");
		String savePath = wordDir + "SFv4/" + UploadUtils.getCurrentYearAndMonth() + "/";
		imgPath = savePath + "codeImg/";
		// File file = new File(savePath);
		// if (!file.exists()) {
		// file.mkdirs();
		// }
		File imgDir = new File(imgPath);
		if (!imgDir.exists()) {
			imgDir.mkdirs();
		}

	}

	YundanInput minput;
	HttpServletRequest req;

	public KyHtmlModel(YundanInput minput, HttpServletRequest req) {
		this(req, "210");
		this.minput = minput;
		this.req = req;
	}

	public List<String> getOnlyHtmls() throws IOException {
		YundanInput orderResponse = minput;
		// HtmlModelMaker mMaker = new HtmlModelMaker(req, orderResponse.yundanType);
		// // yundanType
		// // 生成html模版
		// List<String> htmls = mMaker.getHtmls(orderResponse);
		List<String> htmls = getHtmls(orderResponse);
		return htmls;
	}

	public static String objToJsonStr(Object obj) {
		return JSONObject.toJSONString(obj);
	}

	public String getRandom(String data) {
		SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMddHHmmss");
		String realData = String.valueOf(Math.random());
		return realData;
	}

	public static void main(String[] args) throws Exception {
		String fileName = "D:\\dyj\\save\\itxPdfTest.pdf";
		// test(fileName);
	}

	public static String readFileBase64(String file) throws IOException {
		File mfile = new File(file);
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		FileInputStream fis = new FileInputStream(mfile);

		byte[] buffer = new byte[1024 * 1000];
		int len = 0;
		while ((len = fis.read(buffer)) != -1) {
			bao.write(buffer, 0, len);
		}
		byte[] mData = bao.toByteArray();
		bao.close();
		fis.close();
		String data = Base64.getEncoder().encodeToString(mData);
		return data;
	}

	public List<String> getHtmls(YundanInput orderResponse) throws IOException {
		FileInputStream fis = new FileInputStream(templatePath);
		String fileStr = StreamRead.readFrom(fis);
		String[] yundanIds = orderResponse.response.yundanId.split(",");
		boolean isReturn = "1".equals(orderResponse.mSender.need_return_tracking_no)
				&& orderResponse.response.returnResponse != null;
		List<String> arrList = new ArrayList<>();
		Date mDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
		String time = sdf.format(mDate);
		for (int i = 0; i < yundanIds.length; i++) {
			String parseData = parseData(i, time, yundanIds, orderResponse, fileStr, isReturn);
			arrList.add(parseData);
		}
		if (isReturn) {
			String parseData = parseData(returnID, time, yundanIds, orderResponse, fileStr,
					isReturn);
			arrList.add(parseData);
		}
		return arrList;
	}

	String phoneEncode(String phone) {
		String str = phone;
		if (phone.length() == 11) {
			str = changeStr(phone, 3, "****");
		} else {
			str = changeStr(phone, 2, "****");
		}
		return str;
	}

	String changeStr(String src, int fIndex, String changeStr) {
		int maxLen = src.length();
		int last = fIndex + changeStr.length();
		if (last > maxLen) {
			return src;
		}
		String lastStr = "";
		if (last <= maxLen) {
			lastStr = src.substring(last);
		}
		return src.substring(0, fIndex) + changeStr + lastStr;
	}

	public String parseData(int index, String time, String[] yundanIds, YundanInput info,
			String fileStr, boolean isReturn) throws IOException {
		try {
			String result = fileStr;
			
			 //System.out.println("mfileStr=" + result);

			JSONObject mobj = (JSONObject) JSONObject.toJSON(info);

			String from = info.mSender.j_name + " " + phoneEncode(info.mSender.j_tel) + "  "
					+ info.mSender.j_company + " " + info.mSender.j_address;
			String to = info.mSender.d_name + " " + phoneEncode(info.mSender.d_tel) + "  "
					+ info.mSender.d_company + " " + info.mSender.d_address;
			String mainId = yundanIds[0];
			mainId = getFormatYunStr(mainId);
			String tempCode = "";
			String showChild = "block";
			String showIndex = (index + 1) + "/" + yundanIds.length;
			if (index == 0) {
				showChild = "none";
			}
			if (index == returnID) {
				String temp = from;
				from = to;
				to = temp;
				String before = JSONObject.toJSONString(info.response);
				OrderResponse res = info.response.returnResponse;
				info.response = res;
				String after = JSONObject.toJSONString(info.response);
				System.out.println(getClass() + " src data" + before + "\tafter=" + after);
				tempCode = res.yundanId;
				info.tuoji = "回单_" + info.pid + "," + info.flag;
				// showChild="none";
				showIndex = "回单";
			} else {
				if (info.weight != null) {
					info.tuoji = "重量:" + info.weight + "    " + info.tuoji;
				}
				tempCode = yundanIds[index];
			}
			mobj.put("display", showChild);
			mobj.put("from", from);
			mobj.put("to", to);
			mobj.put("tag", index);
			String podUrl = "";
			mobj.put("index", showIndex);
			if (isReturn) {
				podUrl = dir + "POD.jpg";
			}
			String destRouteLabel = info.response.destcode;
			String proImgName = "";
			String pCode = info.response.proCode;
			if ("T1".equals(pCode)) {
				proImgName = "20_20 T1.png";
			} else if ("T4".equals(pCode)) {
				proImgName = "20_20 T4.png";
			} else if ("T6".equals(pCode)) {
				proImgName = "20_20 T6.png";
			} else if ("T8".equals(pCode)) {
				proImgName = "20_20 T8.png";
			} else if ("T9".equals(pCode)) {
				proImgName = "资源 26.png";
			}
			String proCodeImgUrl = dir + encodeUrl(proImgName);
			if ("".equals(proImgName)) {
				proCodeImgUrl = "";
			}

			String dImg = dir + encodeUrl("收（7mm）.png");
			String jImg = dir + encodeUrl("寄(7mm).png");
			String logo = dir + encodeUrl("logo.png");
			String rexian = dir + encodeUrl("热线.png");
			String abFlagImgUrl = "";

			mobj.put("abFlagImgUrl", abFlagImgUrl);
			mobj.put("dImg", dImg);
			mobj.put("jImg", jImg);
			mobj.put("logo", logo);

			if ("1".equals(info.mSender.need_return_tracking_no)) {
				mobj.put("tag_sign", "签回单");
			} else {
				mobj.put("tag_sign", "");
			}
			mobj.put("rexian", rexian);
			mobj.put("mainId", mainId);

			if (yundanIds.length == 1 || index == 0) {
				mobj.put("tempCode", "");
			}
			mobj.put("time", time);
			mobj.put("note", info.tuoji);

			mobj.put("proCodeImgUrl", proCodeImgUrl);
			mobj.put("destRouteLabel", destRouteLabel);
			mobj.put("codingMapping", info.response.HK_in);
			mobj.put("codingMappingOut", info.response.HK_out);
			mobj.put("podUrl", podUrl);
			// 跨越新增
			mobj.put("serverType", info.mSender.expressType);
			mobj.put("bagCount", info.mSender.bagCounts);
			mobj.put("card", info.mSender.custid);

			String pid = info.pid;
			String sampleImgName = UploadUtils.getCurrentDay() + "-" + pid + "_" + tempCode + "_"
					+ Myuuid.createRandom(4);
			String name = sampleImgName + ".png";
			String qrName = sampleImgName + "_qr" + ".png";
			String path = imgPath + name;
			String path2 = imgPath + sampleImgName + "_2.png";
			String qrPath = imgPath + qrName;

			int height = 40;
			// Code128CCreator mCreater=new Code128CCreator();
			// String mCode=mCreater.getCodeA(tempCode, 1);
			// mCreater.kiCode128C(mCode, 2, height, path);
			// SFPrinterUtil.makeCode128B(tempCode, height, path);
			// SFPrinterUtil.makeCode128Thin(tempCode, height, path);

			TestBarcode4j.saveCodeBKy(tempCode, path);
			TestBarcode4j.saveCodeBKy2(tempCode, path2);

//			TestBarcode4j.saveCode(tempCode, path);

			String codeImg = "data:image/png;base64," + SFv4Model.readFileBase64(path);
			//二三联的条码
			String codeImg2 = "data:image/png;base64," + SFv4Model.readFileBase64(path2);
			mobj.put("codeImg", codeImg);
			mobj.put("codeImg2", codeImg2);
			String specImg = dir + "blank.bmp";
			if ("1".equals(info.isSpecial)) {
				 specImg = resRoot + "TeShu.png";
			} 
			mobj.put("speIcon", specImg);
			tempCode = getFormatYunStr(tempCode);
			mobj.put("tempCode", tempCode);
			if ("150".equals(yundanType)) {
				mobj.put("spe150Code", codeImg);
			}
			// SFPrinterUtil.makeQrFile(info.response.qrInfo, 100, qrPath);
			mobj.put("dstcode", info.response.destcode);

			Set<String> keySet = mobj.keySet();
			Iterator<String> iterator = keySet.iterator();

			while (iterator.hasNext()) {
				String next = iterator.next();
				String mData = String.format("\\{\\{%s\\}\\}", next);
				// System.out.println("repKey=" + mData);
				Object mVal = mobj.get(next);
				String value = "";
				if (mVal != null) {
					value = mVal.toString();
				} else {
					System.out.println("replace template Key=" + next + "==null");
				}
				result = result.replaceAll(mData, value);
			}
			return result;
		} catch (Exception e) {
			throw new IOException("解析html模版异常", e);
		}
	}

	String encodeUrl(String rexian) {
		String charset = "utf-8";
		try {
			return URLEncoder.encode(rexian, charset).replaceAll("\\+", "%20");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return rexian;
		}
	}

	public String getFormatYunStr(String mCode) {
		// String finalCode = "";
		// int[] index = new int[] { 3, 3, 3, 4 };
		// if (mCode.length() == 15) {
		// index = new int[] { 2, 3, 3, 3, 4 };
		// }
		// int tIndx = 0;
		// for (int i = 0; i < index.length; i++) {
		// int tempIndex = index[i];
		// int maxIndex = tempIndex + tIndx;
		// if (maxIndex > mCode.length()) {
		// break;
		// }
		// String StrCode = mCode.substring(tIndx, maxIndex);
		// finalCode += StrCode;
		// finalCode += " ";
		// tIndx += tempIndex;
		// }
		// if (mCode.length() < 15) {
		//// finalCode = "SF " + finalCode;
		// }
		// return finalCode;
		return mCode;
	}

	/*
	 * function getFormatYunStr(mCode){
	 * 
	 * var index=[3,3,3,4]; if(mCode<16){ index=[3,3,3,4]; }else{ index=[2,3,3,3,4]; } var
	 * finalCode=""; var tIndx=0; for(var i=0;i<index.length;i++){ var tempIndex=index[i];
	 * if(tIndx+tempIndex>mCode.length){ break; } var StrCode=mCode.slice(tIndx,tIndx+tempIndex);
	 * finalCode+=StrCode; finalCode+=" "; tIndx+=tempIndex; } if(mCode<16){
	 * finalCode="SF "+finalCode; } return finalCode; }
	 */

}
