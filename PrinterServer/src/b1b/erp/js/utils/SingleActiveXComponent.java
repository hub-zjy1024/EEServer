package b1b.erp.js.utils;

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
			e.printStackTrace();
		}
		if (docs == null) {
			init();
		}
		return axc;
	}

	private static void init() {
		axc = new ActiveXComponent("Word.Application");
		axc.setProperty("Visible", new Variant(true));
	}
}
