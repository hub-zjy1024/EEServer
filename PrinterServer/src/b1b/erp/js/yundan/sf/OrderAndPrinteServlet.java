package b1b.erp.js.yundan.sf;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import b1b.erp.js.bussiness.SFPrinterUtil;
import b1b.erp.js.entity.RetJsonObj;
import b1b.erp.js.utils.StreamRead;
import b1b.erp.js.utils.UploadUtils;
import b1b.erp.js.yundan.sf.bussiness.OrderMgr;
import b1b.erp.js.yundan.sf.entity.Cargo;
import b1b.erp.js.yundan.sf.entity.OrderBody;
import b1b.erp.js.yundan.sf.entity.SFSender;
import b1b.erp.js.yundan.sf.entity.YundanInput;
import b1b.erp.js.yundan.sf.sfutils.DyjInterface2;
import b1b.erp.js.yundan.sf.sfutils.SFWsUtils;

/**
 * Servlet implementation class OrderAndPrinteServlet
 */
@WebServlet("/SFYundan2")
public class OrderAndPrinteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public OrderAndPrinteServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		RetJsonObj retJson = new RetJsonObj();
		String json = "";
		try {
			json = StreamRead.readFrom(request.getInputStream());
			System.out.println("SFYundan2 recJson=" + json);
			OrderBody mobj = JSONObject.parseObject(json, OrderBody.class);
			SFSender mSender = mobj.reqParams;
			List<Cargo> cargos = mobj.cargos;
			String yundanType = mobj.yundanType;
			if(yundanType==null) {
				yundanType="210";
				System.out.println("use debug ydType="+yundanType);

			}
			String printer = mobj.printer;
			int flag = mobj.flag;
			String logType = mobj.logType;
			String pid = mobj.pid;
			String uid = mobj.uid;
			String uname = mobj.uname;
			SFWsUtils.OrderResponse orderResponse = SFWsUtils.getOrderResponseV2(mSender, cargos,
					null);
			//日志存储
			DyjInterface2.SetRequestLog(pid, uid, uname, orderResponse.inputXml,
					orderResponse.retXml, logType);
			//System.out.println("retXml="+	orderResponse.retXml );
			// 删除请求和相应的xml
			orderResponse.inputXml = "";
			orderResponse.retXml = "";
			if(	orderResponse.returnResponse!=null) {
				
			}
			String jsondata = JSONObject.toJSONString(orderResponse);
			String mHostAndPort = request.getRequestURL().substring(0,
					request.getRequestURL().indexOf("/", 8));
			String servletPath = request.getServletContext().getContextPath();
			String url = mHostAndPort + servletPath+"/SFPrintV3.jsp?";
			/*
			 * String url = mHostAndPort + "/Dyj_server/SFPrintV3.jsp?data=" +
			 * URLEncoder.encode(jsondata, "utf-8");
			 */
			YundanInput minput = new YundanInput();
			minput.payType = mobj.payType;
			minput.goodInfos = mobj.goodInfos;
			minput.pid = mobj.pid;
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
			if(note!=null){
				builder=new StringBuilder();
				/*		builder.append(note);
			builder.append("_");
				builder.append(nowTimeStr);
				builder.append("_");
				builder.append(pid);*/
				builder.append(String.format("%s,%s_%s" ,note, nowTimeStr, pid));
				if (flag == 1) {
					// 手机托寄
					builder.append("_and");
				} else {
					builder.append("_"+flag);
					// 其他托寄
				}
			}
			OrderMgr orderManager = new OrderMgr();
			// int flag=minput.flag;
			minput.tuoji = builder.toString();
			if(weight==null){
				weight="";
			}
			minput.weight =weight;
			// minput.tuoji = flag;
			minput.mSender = mobj.reqParams;
			minput.response = orderResponse;
			minput.printer = printer;
			minput.yundanType = yundanType;
			minput.flag=mobj.flag;
			minput.isSpecial = mobj.isSpecial;

			String randowmID = UploadUtils.getRandomWithTime();
			orderManager.insertData(randowmID,pid, orderResponse.yundanId,
					JSONObject.toJSONString(minput));
			url += "data=" + randowmID;
			retJson.errCode = 0;
			retJson.errMsg = "成功";
			retJson.data.add(orderResponse.yundanId);
			retJson.data.add(orderResponse.destcode);
			retJson.data.add(url);
		} catch (JSONException e) {
			e.printStackTrace();
			System.out.println("orderError,json=" + json);
			retJson.errMsg = "输入参数异常," + e.getMessage();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("orderError2,json=" + json);
			retJson.errMsg = "异常," + e.getMessage();
		} catch (Throwable e) {
			e.printStackTrace();
			System.out.println("orderError2,fatel=" + json);
			retJson.errMsg = "fatel," + e.getMessage();
		}
		response.setContentType("text/plain;charset=utf-8");
		response.setCharacterEncoding("utf-8");
		String retJsonStr = JSONObject.toJSONString(retJson);
		response.getWriter().append(retJsonStr);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
