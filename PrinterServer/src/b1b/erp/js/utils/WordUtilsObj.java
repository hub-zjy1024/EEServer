package b1b.erp.js.utils;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;

public class WordUtilsObj {
	private ActiveXComponent app;
	private Dispatch docs;

	/**
	 * @param isVisible
	 *            是否显示界面
	 */
	public WordUtilsObj(boolean isVisible) {
		ComThread.InitMTA();
		app = new ActiveXComponent("Word.Application");
		app.setProperty("Visible", isVisible);
	}

	/**
	 * 默认不显示界面
	 */
	public WordUtilsObj() {
		this(false);
	}

	public Dispatch openDoc(String docPath) {
		Dispatch doc = Dispatch.call(docs, "Open", docPath).toDispatch();
		return doc;
	}

	public void replaceBookmark(Dispatch doc, HashMap<String, String> bookAndValue) {
		for (Entry<String, String> e : bookAndValue.entrySet()) {
			Dispatch dispatch = getBookmark(e.getKey(), doc);
			if (dispatch != null) {
				if (e.getValue() != null) {
					String value = e.getValue();
					Dispatch.put(dispatch, "Text", value);
				}
			}
		}
	}

	/**
	 * 获取书签的位置
	 * 
	 * @param bookmarkName
	 * @return 书签的位置
	 */
	public static Dispatch getBookmark(String bookmarkName, Dispatch doc) {
		try {
			Dispatch bookmark = Dispatch.call(doc, "Bookmarks", bookmarkName).toDispatch();
			return Dispatch.get(bookmark, "Range").toDispatch();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void showBookMarks(Dispatch doc) {
		Dispatch bookMarks = Dispatch.call(doc, "Bookmarks").toDispatch();
		ArrayList<Map<String, String>> lstBookMarks = new ArrayList<>();
		int bCount = Dispatch.get(bookMarks, "Count").getInt();
		for (int i = 1; i <= bCount; i++) {
			Map<String, String> bookMark = new HashMap<>();
			Dispatch item = Dispatch.call(bookMarks, "Item", i).toDispatch();
			String item_name = Dispatch.get(item, "Name").getString().replaceAll("null", ""); // 读取书签命名
			Dispatch range = Dispatch.get(item, "Range").toDispatch();
			String item_value =Dispatch.get(range, "Text").getString().replaceAll("null", ""); // 读取书签文本

			if (!item_name.equals("")) {
				bookMark.put("NAME", item_name);
				bookMark.put("TEXT", item_value);
				lstBookMarks.add(bookMark);
			}
		}
	}

}
