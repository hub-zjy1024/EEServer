package b1b.erp.js.utils;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

public class SingleActiveXComponent {
	private static ActiveXComponent axc;
	static {
		init();
	}

	private SingleActiveXComponent() {

	}

	public synchronized static ActiveXComponent getApp() {
		Dispatch docs = null;
		try {
			docs = axc.getProperty("Documents").toDispatch();
		} catch (Exception e) {
			System.out.println("lastwindow is Closed:"+e.getMessage());
		}
		try {
			if (docs == null) {
				init();
			}
		} catch (Exception e) {
			e.printStackTrace();
			ByteArrayOutputStream bao = new ByteArrayOutputStream();
			PrintWriter writer = new PrintWriter(bao);
			e.printStackTrace(writer);
			writer.flush();
			try {
				System.out.println("initError-----" + new String(bao.toByteArray(), "utf-8"));
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			writer.close();
		}
		return axc;
	}

	private static void init() {
		axc = new ActiveXComponent("Word.Application");
		axc.setProperty("Visible", new Variant(true));
	}
}
