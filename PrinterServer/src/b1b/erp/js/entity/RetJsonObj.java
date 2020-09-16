package b1b.erp.js.entity;

import java.util.ArrayList;
import java.util.List;

public class RetJsonObj {
	public int errCode = 1;
	public String errMsg = "未知错误";
	//	public String data = "[]";
	public List<Object> data = new ArrayList<>();

	public int getErrCode() {
		return errCode;
	}

	public void setErrCode(int errCode) {
		this.errCode = errCode;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	public List<Object> getData() {
		return data;
	}

	public void setData(List<Object> data) {
		this.data = data;
	}

}
