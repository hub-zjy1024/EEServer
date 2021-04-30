package b1b.erp.js.yundan.ky;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.sun.org.apache.regexp.internal.REUtil;

import b1b.erp.js.entity.RetJsonObj;
import b1b.erp.js.utils.StreamRead;
import b1b.erp.js.yundan.ky.util.KyService;
import b1b.erp.js.yundan.sf.entity.OrderBody;

/**
 * Servlet implementation class KyOrderServlet
 */
@WebServlet("/KyOrderServlet")
public class KyOrderServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public KyOrderServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub

		request.setCharacterEncoding("utf-8");
		RetJsonObj retJson = new RetJsonObj();
		String json = "";
		try {
			json = StreamRead.readFrom(request.getInputStream());
			// System.out.println("SFYundan2 recJson=" + json);
			OrderBody mobj = JSONObject.parseObject(json, OrderBody.class);
			String yundanType = mobj.yundanType;
			if (yundanType == null) {
				yundanType = "210";
				System.out.println("use debug ydType=" + yundanType);
			}
			String printer = mobj.printer;
			int flag = mobj.flag;
			String logType = mobj.logType;
			String pid = mobj.pid;
			String uid = mobj.uid;
			String uname = mobj.uname;
			KyService mserv = new KyService(mobj, request);
			RetJsonObj orderAndGetRetobj = mserv.orderAndGetRetobj();
			retJson = orderAndGetRetobj;
		} catch (JSONException e) {
			e.printStackTrace();
			System.out.println("ky orderError,json=" + json);
			retJson.errMsg = "输入参数异常,请检查json字符串格式" ;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("ky orderError2,json=" + json);
			retJson.errMsg = "异常," + e.getMessage();
		} catch (Throwable e) {
			e.printStackTrace();
			System.out.println("ky orderError2,fatel=" + json);
			retJson.errMsg = "fatel," + e.getMessage();
		}
		response.setContentType("text/plain;charset=utf-8");
		response.setCharacterEncoding("utf-8");
		response.setHeader("Access-Control-Allow-Origin", "*");

		String retJsonStr = JSONObject.toJSONString(retJson);
		response.getWriter().append(retJsonStr);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("UTF-8");
		response.setHeader("Access-Control-Allow-Origin", "*");
		doGet(request, response);
	}

}
