package b1b.erp.js.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class RestartServlet
 */
@WebServlet("/RestartServlet")
public class RestartServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RestartServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		String flag = request.getParameter("flag");
		BufferedReader breader = null;
		StringBuilder builder = new StringBuilder();
		InputStream result = null;
		Process p = null;
		String realPath = getServletContext().getRealPath("docTemplate/restart.bat");
		if ("kill".equals(flag)) {
			p = Runtime.getRuntime().exec("taskkill /f /t /im soffice.bin");
		} else {
			p = Runtime.getRuntime().exec(realPath);
		}
		result = p.getInputStream();
		breader = new BufferedReader(new InputStreamReader(result, "GBK"));
		String tempStr = breader.readLine();
		while (tempStr != null) {
			builder.append(tempStr);
			tempStr = breader.readLine();
		}
		breader.close();
		try {
			p.destroyForcibly();
			p.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.err.println("killResult:" + builder.toString());
		response.getWriter().append(builder.toString()).close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
