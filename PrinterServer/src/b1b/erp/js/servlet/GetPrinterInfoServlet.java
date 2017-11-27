package b1b.erp.js.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.print.DocFlavor;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class GetPrinterInfoServlet
 */
@WebServlet("/GetPrinterInfoServlet")
public class GetPrinterInfoServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetPrinterInfoServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html; charset=utf-8");
		HashPrintRequestAttributeSet requestAttrs = new HashPrintRequestAttributeSet();
		DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE;
		PrintService[] totalService = PrintServiceLookup.lookupPrintServices(flavor, requestAttrs);
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < totalService.length; i++) {
			builder.append(totalService[i].getName() + ",");
		}
		String printers = builder.toString();
		if (!printers.equals("")) {
			builder.deleteCharAt(builder.toString().length() - 1);
			printers = builder.toString();
		}
		PrintWriter writer = response.getWriter();
		writer.append(printers);
		writer.flush();
		writer.close();

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		// doGet(request, response);
		
	}

}
