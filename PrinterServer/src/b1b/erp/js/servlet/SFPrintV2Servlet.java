package b1b.erp.js.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import b1b.erp.js.bussiness.SFPrinterUtil;
import b1b.erp.js.bussiness.SFPrinterV2;
import b1b.erp.js.entity.YundanInfo;

/**
 * Servlet implementation class SFPrintV2Servlet
 */
@WebServlet("/V2/SFPrintServlet")
public class SFPrintV2Servlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SFPrintV2Servlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		SFPrinterV2 printerV2 = new SFPrinterV2(request);
		response.setContentType("text/html;charset=utf-8");
		response.setCharacterEncoding("utf-8");

		String res = "error:";
		try {
			YundanInfo realInfo = new YundanInfo();
			String orderID = request.getParameter("orderID");
			String goodinfos = request.getParameter("goodinfos");
			String price = request.getParameter("baojiaprice");
			String cardID = request.getParameter("cardID");
			String destcode = request.getParameter("destcode");
			String yundanType = request.getParameter("yundanType");
			String printer = request.getParameter("printer");
			String hasE = request.getParameter("hasE");
			String jCompany = request.getParameter("j_company");
			String dCompany = request.getParameter("d_company");
			if (printer == null) {
				printer = "";
			}
			printer = getServletContext().getInitParameter("printer1");
			String pid = request.getParameter("pid");
			if (jCompany == null) {
				jCompany = "";
			}
			if (dCompany == null) {
				dCompany = "";
			}
			if (pid == null) {
				pid = "";
			}
			String[] orders = orderID.split(",");
			if (destcode == null) {
				destcode = "1024";
			}
			// 寄方，到方，第三方
			String payType = request.getParameter("payType");
			String payPerson = request.getParameter("payPerson");
			if (price != null) {
				if (price.equals("-1.0")) {
					price = "";
				}
			} else {
				price = "";
			}
			String serverType = request.getParameter("serverType");
			String jName = request.getParameter("j_name");
			String jPhone = request.getParameter("j_phone");
			String jAddress = request.getParameter("j_address");
			String dName = request.getParameter("d_name");
			String dPhone = request.getParameter("d_phone");
			String dAddress = request.getParameter("d_address");

			// 新增
			String destRouteLable = request.getParameter("destRouteLable");
			String HK_in = request.getParameter("HK_in");
			String HK_out = request.getParameter("HK_out");
			String proCode = request.getParameter("proCode");
			String qr_code = request.getParameter("qr_code");

			String[] infos;
			StringBuilder builder = new StringBuilder();
			String nowTimeStr = SFPrinterUtil.getCurrentDate();
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
				builder.append(String.format(",%s_%s_and", nowTimeStr, pid));
			} else {
				// infos = new String[] { "test1&1000", "test3&1024", "test2&500", "test" };
				builder.append(String.format("无型号信息,%s_%s_and", nowTimeStr, pid));
			}
			String tuoji = builder.toString();

			realInfo.yundans = orders;
			realInfo.destRouteLable = destRouteLable;
			realInfo.pay_type = payType;
			realInfo.proCode = proCode;
			realInfo.pid = pid;
			realInfo.qr_code = qr_code;
			realInfo.HK_in = "";
			realInfo.HK_out = "";
			realInfo.tuoji = tuoji;

			if (HK_in != null) {
				realInfo.HK_in = HK_in;
			}
			if (HK_out != null) {
				realInfo.HK_out = HK_out;
			}
			int maxPhoneLength = 12;
			if (jPhone.length() > maxPhoneLength) {
				jPhone = jPhone.substring(0, maxPhoneLength);
			}
			if (dPhone.length() > maxPhoneLength) {
				dPhone = dPhone.substring(0, maxPhoneLength);
			}
			// 寄件人信息
			realInfo.j_name = jName;
			realInfo.j_phone = jPhone;
			realInfo.j_comp = jCompany;
			realInfo.j_addr = jAddress;
			// 收件人信息
			realInfo.d_name = dName;
			realInfo.d_phone = dPhone;
			realInfo.d_comp = dCompany;
			realInfo.d_addr = dAddress;
			// 其他
			realInfo.print_time = nowTimeStr;
			realInfo.note = "";
			printerV2.Print(realInfo);
			res = "ok";
			// printerV2.testApi();
		} catch (NullPointerException e) {
			e.printStackTrace();
			res += "缺少必要参数";
		} catch (Exception e) {
			e.printStackTrace();
			res += "打印失败," + e.getMessage();
		}
		response.getWriter().append(res);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
