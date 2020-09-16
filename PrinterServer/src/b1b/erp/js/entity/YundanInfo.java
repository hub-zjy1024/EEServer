package b1b.erp.js.entity;

public class YundanInfo {
	public String[] yundans;
	public String print_time;
	public String pid;
	public String destRouteLable;
	// 收件信息
	public String d_name;
	public String d_phone;
	public String d_comp;
	public String d_addr;
	// 寄件信息
	public String j_name;
	public String j_phone;
	public String j_comp;
	public String j_addr;
	// 其他
	public String qr_code;
	public String timeType;
	public String pay_type;
	public String typeA;
	public String proCode;
	public String HK_in;
	public String HK_out;
	public String tuoji;
	public String note;
	public String isSpecial;
	// 回单号
	public String returnOrder;
	public YundanInfo returnInfo;
	// print_time
	// destRouteLable

	@Override
	public Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		YundanInfo mInfo = new YundanInfo();
		mInfo.d_addr = d_addr;
		mInfo.yundans = yundans;
		mInfo.yundans = yundans;
		mInfo.print_time = print_time;
		mInfo.pid = pid;
		mInfo.destRouteLable = destRouteLable;
		mInfo.d_name = d_name;
		mInfo.d_phone = d_phone;
		mInfo.d_comp = d_comp;
		mInfo.d_addr = d_addr;
		mInfo.j_name = j_name;
		mInfo.j_phone = j_phone;
		mInfo.j_comp = j_comp;
		mInfo.j_addr = j_addr;
		mInfo.qr_code = qr_code;
		mInfo.timeType = timeType;
		mInfo.pay_type = pay_type;
		mInfo.typeA = typeA;
		mInfo.proCode = proCode;
		mInfo.HK_in = HK_in;
		mInfo.HK_out = HK_out;
		mInfo.tuoji = tuoji;
		mInfo.note = note;
		mInfo.returnOrder = returnOrder;
		mInfo.isSpecial = isSpecial;
		return mInfo;
	}
	// dname
	// d_phone
	// d_comp
	// d_addr
	// pay_type
	// qr_code
	// typeA
	// HK_in
	// HK_out
	// j_name
	// j_phone
	// j_comp
	// j_addr
	// tuoji
	// note

	public YundanInfo(String[] yundans, String print_time, String pid, String destRouteLable,
			String d_name, String d_phone, String d_comp, String d_addr, String j_name,
			String j_phone, String j_comp, String j_addr, String qr_code, String timeType,
			String pay_type, String typeA, String proCode, String hK_in, String hK_out,
			String tuoji, String note, String returnOrder) {
		super();
		this.yundans = yundans;
		this.print_time = print_time;
		this.pid = pid;
		this.destRouteLable = destRouteLable;
		this.d_name = d_name;
		this.d_phone = d_phone;
		this.d_comp = d_comp;
		this.d_addr = d_addr;
		this.j_name = j_name;
		this.j_phone = j_phone;
		this.j_comp = j_comp;
		this.j_addr = j_addr;
		this.qr_code = qr_code;
		this.timeType = timeType;
		this.pay_type = pay_type;
		this.typeA = typeA;
		this.proCode = proCode;
		HK_in = hK_in;
		HK_out = hK_out;
		this.tuoji = tuoji;
		this.note = note;
		this.returnOrder = returnOrder;
	}

	public YundanInfo() {
		super();
	}
}
