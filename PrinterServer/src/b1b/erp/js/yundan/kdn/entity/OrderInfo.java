package b1b.erp.js.yundan.kdn.entity;

import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

public class OrderInfo {
	@JSONField(name = "MemberID")
	public String MemberID;
	@JSONField(name = "CustomerName")

	public String CustomerName = "";
	@JSONField(name = "CustomerPwd")

	public String CustomerPwd = "";
	@JSONField(name = "MonthCode")

	public String MonthCode = "";
	@JSONField(name = "SendSite")

	public String SendSite = "";
	@JSONField(name = "SendStaff")

	public String SendStaff = "";
	/**
	 * EMS,1标准快递，8到付</br>   邮政国内标快(YZBK) 只有1</br>  邮政快递包裹(YZPY) 1</br>
	 */
	@JSONField(name = "ExpType")
	public String ExpType = "1";
	// CustomerName
	// CustomerPwd
	// MonthCode
	// SendSite
	// SendStaff
	@JSONField(name = "ShipperCode")

	public String ShipperCode;
	@JSONField(name = "LogisticCode")

	public String LogisticCode;
	@JSONField(name = "ThrOrderCode")

	public String ThrOrderCode;
	@JSONField(name = "OrderCode")
	public String OrderCode;
	@JSONField(name = "PayType")
	public String PayType;
	@JSONField(name = "IsReturnSignBill")
	public int IsReturnSignBill;
	@JSONField(name = "OperateRequire")
	public String OperateRequire;
	@JSONField(name = "Cost")
	public int Cost;
	@JSONField(name = "OtherCost")
	public int OtherCost;

	public Receiver Receiver;

	public Sender Sender;
	@JSONField(name = "IsNotice")
	public int IsNotice;
	@JSONField(name = "StartDate")
	public String StartDate;
	@JSONField(name = "EndDate")
	public String EndDate;
	@JSONField(name = "Weight")
	public int Weight;
	@JSONField(name = "Quantity")
	public int Quantity;
	@JSONField(name = "Volume")
	public int Volume;

	@JSONField(name = "Remark")
	public String Remark;

	public List<String> AddService;

	public List<Commodity> Commodity;

	/**
	 * 是否返回打印模版1 需要，0不需要
	 */
	@JSONField(name = "IsReturnPrintTemplate")
	public int IsReturnPrintTemplate;

	@JSONField(name = "IsSendMessage")
	public int IsSendMessage;

	/** 
	 * SF模版15001 150新  ，180 180新 ，  21001 210新</br>
	 * YZBK 默认180，不传,YZPY，不传默认180， 传180为新版180
	 */
	@JSONField(name = "TemplateSize")
	public String TemplateSize;

}
