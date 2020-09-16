package b1b.erp.js.yundan.sf.entity;

import java.io.Serializable;
import java.util.List;

public class OrderBody implements Serializable {
    public String payType;
    public String goodInfos;
    public String weight;
	public String pid;
	public String uid;
	public String uname;
	public String yundanType;
	public String printer;
	public String logType;
	public String isSpecial;
	public String note;
	public int flag;
	public List<Cargo> cargos;
	public SFSender reqParams;
}