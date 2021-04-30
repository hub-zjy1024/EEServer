package b1b.erp.js.yundan.sf.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class YundanModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3120234345154101741L;
	public List<String> htmls;
	public String yundanId;
	public String destcode;
	public String url;
	public String huidanId;

	public YundanModel() {
		super();
		htmls = new ArrayList<String>();
	}

}
