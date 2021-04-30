package b1b.erp.js.yundan.sf.bussiness;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.chainsaw.Main;

import com.alibaba.fastjson.JSONObject;

import b1b.erp.js.bussiness.SFPrinterUtil;
import b1b.erp.js.utils.Myuuid;
import b1b.erp.js.utils.StreamRead;
import b1b.erp.js.utils.TestBarcode4j;
import b1b.erp.js.utils.UploadUtils;
import b1b.erp.js.yundan.SFv4Model;
import b1b.erp.js.yundan.sf.entity.YundanInput;
import b1b.erp.js.yundan.sf.sfutils.SFWsUtils.OrderResponse;

public class HtmlModelMaker {
	String templatePath;
	String dir = "";
	String imgPath = "";
	static final int returnID = -1;

	private String yundanType;
	String resRoot = "";

	static int v_1=0;
	static int v_2=1;
	static int v_3=2;
	int version=v_3;
	
	
	public HtmlModelMaker(HttpServletRequest request, String yundanType) {
		this.yundanType = yundanType;
		//System.out.println("yundanType  type  " + yundanType);

		if ("210".equals(yundanType)) {
			templatePath = request.getServletContext().getRealPath("/docTemplate/sf_html_210.html");
			if(version==v_1){
				templatePath = request.getServletContext().getRealPath("/docTemplate/sf_html_210_2020.html");
			}else if(version==v_3){
				templatePath = request.getServletContext().getRealPath("/docTemplate/sf_html_210_old.html");
			}
		} else if ("180".equals(yundanType)) {
			templatePath = request.getServletContext().getRealPath("/docTemplate/sf_html_180.html");
		} else if ("150".equals(yundanType)) {
			templatePath = request.getServletContext().getRealPath("/docTemplate/sf_html_150.html");
		} else {
			System.out.println("not found template3 type  " + yundanType);
			templatePath = request.getServletContext().getRealPath("/docTemplate/sf_html_210.html");
			if(version==v_1){
				templatePath = request.getServletContext().getRealPath("/docTemplate/sf_html_210_2020.html");
			}else if(version==v_3){
				templatePath = request.getServletContext().getRealPath("/docTemplate/sf_html_210_old.html");
			}
		}
		String totalUrl = request.getRequestURL().toString();
		// dir = totalUrl.substring(0, totalUrl.lastIndexOf("/"))+"/imgs/sf/";
		// dir = "http://" + request.getRemoteHost() + ":" + request.getRemotePort()
		// + request.getContextPath() + "/imgs/sf/";
		// String preUrl = "http://" + request.getLocalAddr() + ":8080";
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

	public List<String> getHtmls(YundanInput orderResponse) throws IOException {
	
		String fileStr="";
		try {
			FileInputStream fis = new FileInputStream(templatePath);
			 fileStr = StreamRead.readFrom(fis);
		} catch (FileNotFoundException e) {
//			e.printStackTrace();
			throw new IOException("模板文件不存在,"+e.getMessage());
		} catch (Exception e) {
//			e.printStackTrace();
			throw new IOException("模板文件读取失败,"+e.getMessage());
		}
	
		String[] yundanIds = orderResponse.response.yundanId.split(",");
		boolean isReturn = "1".equals(orderResponse.mSender.need_return_tracking_no);
		List<String> arrList = new ArrayList<>();
		Date mDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
		String time = sdf.format(mDate);
		for (int i = 0; i < yundanIds.length; i++) {
			String parseData = parseData(i, time, yundanIds, orderResponse, fileStr, isReturn);
			arrList.add(parseData);
		}
		if (isReturn) {
			String parseData = parseData(-1, time, yundanIds, orderResponse, fileStr, isReturn);
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
			// System.out.println("mfileStr=" + result);

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
			String qrInfo = info.response.qrInfo;

			if (index == returnID) {
				String temp = from;
				from = to;
				to = temp;
				String before = JSONObject.toJSONString(info.response);
				OrderResponse res = info.response.returnResponse;
				info.response = res;
				String after = JSONObject.toJSONString(info.response);
				// System.out.println("src data"+before+"\tafter="+after);
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
			if (index == 0) {
				showChild = "none";
			} else {
				qrInfo = qrInfo.replace(mainId, tempCode);
			}
			mobj.put("display", showChild);
			mobj.put("from", from);
			mobj.put("to", to);
			mobj.put("tag", index);
			String podUrl = dir + "blank.bmp";
			mobj.put("index", showIndex);
			if (isReturn) {
				podUrl = dir + "POD.jpg";
			}
			String destRouteLabel = info.response.destRouteLable;
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
				proCodeImgUrl = dir + "blank.bmp";
			}

			String dImg = dir + encodeUrl("收（7mm）.png");
			String jImg = dir + encodeUrl("寄(7mm).png");
			String logo = dir + encodeUrl("logo.png");
			String rexian = dir + encodeUrl("热线.png");
			String abFlagImgUrl = dir + "blank.bmp";

			mobj.put("abFlagImgUrl", abFlagImgUrl);
			mobj.put("dImg", dImg);
			mobj.put("jImg", jImg);

			mobj.put("logo", logo);
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
			
			String pid = info.pid;
			String sampleImgName = UploadUtils.getCurrentDay() + "-" + pid + "_" + tempCode + "_"
					+ Myuuid.createRandom(4);
			String name = sampleImgName + ".png";
			String qrName = sampleImgName + "_qr" + ".png";
			String path = imgPath + name;
			String qrPath = imgPath + qrName;

			int height = 50;
			// Code128CCreator mCreater=new Code128CCreator();
			// String mCode=mCreater.getCodeA(tempCode, 1);
			// mCreater.kiCode128C(mCode, 2, height, path);
		
			//SFPrinterUtil.makeCode128B(tempCode, height, path);
			// SFPrinterUtil.makeCode128Thin(tempCode, height, path);
			if(version==v_1){
//				SFPrinterUtil.makeCode128Thin(tempCode, height, path);
				TestBarcode4j.saveCode(tempCode, path);
			}else if(version==v_3){
				mobj.put("codeW", "200px");
				SFPrinterUtil.makeCode128Thin(tempCode, height, path);
			}
			//
			String codeImg = "data:image/png;base64," + SFv4Model.readFileBase64(path);
			mobj.put("codeImg", codeImg);
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
			SFPrinterUtil.makeQrFile(qrInfo, 130, qrPath);
			// System.out.println("mqr=" + info.response.qrInfo);
			// (tempCode, height, path);
			String codeQrImg = "data:image/png;base64," + SFv4Model.readFileBase64(qrPath);
			mobj.put("qrImg", codeQrImg);
			mobj.put("dstcode", info.response.destcode);

			int limitLen = 6;
			if (pCode != null && pCode.length() > limitLen) {
				pCode = pCode.substring(0, limitLen);
			}
			mobj.put("proCode", pCode);
			
			mobj.put("bagCount", "" + info.mSender.bagCounts);

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
		String finalCode = "";
		int[] index = new int[] { 3, 3, 3, 4 };
		if (mCode.length() == 15) {
			index = new int[] { 2, 3, 3, 3, 4 };
		}
		int tIndx = 0;
		for (int i = 0; i < index.length; i++) {
			int tempIndex = index[i];
			int maxIndex = tempIndex + tIndx;
			if (maxIndex > mCode.length()) {
				break;
			}
			String StrCode = mCode.substring(tIndx, maxIndex);
			finalCode += StrCode;
			finalCode += " ";
			tIndx += tempIndex;
		}
		if (mCode.length() < 15) {
			finalCode = "SF " + finalCode;
		}
		return finalCode;
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
