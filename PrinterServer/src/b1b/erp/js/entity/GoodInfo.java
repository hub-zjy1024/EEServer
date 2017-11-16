package b1b.erp.js.entity;

public class GoodInfo {
	//型号
	private String partno;
	//品牌
	private String brand;
	public String getPartno() {
		return partno;
	}
	public void setPartno(String partno) {
		this.partno = partno;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getCouts() {
		return couts;
	}
	public void setCouts(String couts) {
		this.couts = couts;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getTotalprice() {
		return totalprice;
	}
	public void setTotalprice(String totalprice) {
		this.totalprice = totalprice;
	}
	public String getMark() {
		return mark;
	}
	public void setMark(String mark) {
		this.mark = mark;
	}
	//数量
	private String couts;
	//价格
	private String price;
	//总金额
	private String totalprice;
	//备注
	private String mark;
	public GoodInfo(String id, String partno, String brand, String couts, String price, String totalprice,
			String mark) {
		super();
		this.partno = partno;
		this.brand = brand;
		this.couts = couts;
		this.price = price;
		this.totalprice = totalprice;
		this.mark = mark;
	}
	
}
