package b1b.erp.js.yundan.sf;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.sun.javafx.image.impl.ByteIndexed.Getter;

import b1b.erp.js.entity.RetJsonObj;
import b1b.erp.js.yundan.SFv4Model;
import b1b.erp.js.yundan.sf.entity.YundanInput;

/**
 * Servlet implementation class SftestServlet
 */
@WebServlet("/SftestServlet")
//@WebServlet("/reprint")
public class SftestServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SftestServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		SFv4Model mmodle = new SFv4Model(null, request);
		String data=request.getParameter("data");
		String data2=request.getParameter("data");
		String retJson = mmodle.getData(data);
//		System.out.println("mdataLen=" + retJson.length());
		response.setContentType("text/plain;charset=utf-8");
		response.setCharacterEncoding("utf-8");
		response.getWriter().append(retJson);
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
