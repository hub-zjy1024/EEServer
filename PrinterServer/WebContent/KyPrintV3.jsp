<%@page import="b1b.erp.js.bussiness.LodopLoadder"%>
<%@page import="com.alibaba.fastjson.JSONObject"%>
<%@page import="b1b.erp.js.yundan.sf.entity.YundanInput"%>
<%@page import="b1b.erp.js.entity.YundanInfo"%>
<%@page import="b1b.erp.js.yundan.sf.bussiness.OrderMgr"%>
<%@page import="java.io.IOException"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html >
<!-- PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd"
<html> -->
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>跨越打印v3</title>
<script type="text/javascript">
if (window.console){
	// console.info("has console");
}else{
	window.console={
			info:function(data){
				
			}
	,log:function(data){
		
	}
	}
}
</script>

<!-- <script type="text/javascript"
	src="http://127.0.0.1:8000/CLodopfuncs.js?priority=1"></script>
 -->

<script type="text/javascript" src="./lodop/jquery-3.3.1.min.js"></script>
<script type="text/javascript" src="./lodop/html2canvas.min.js"></script>

<script type="text/javascript" src="./lodop/JsBarcode.all.min.js"></script>
<script type="text/javascript" src="./lodop/qrcode.min.js"></script>
<!-- <script src="https://cdn.bootcss.com/html2canvas/0.5.0-beta4/html2canvas.js"></script>
 -->
<script src="https://cdn.bootcss.com/jspdf/1.3.4/jspdf.debug.js"></script>

<!-- <script type="text/javascript"
	src="http://192.168.10.66:18000/CLodopfuncs.js?priority=2"></script>
 -->
<!-- <script type="text/javascript"
	src="http://localhost:18000/CLodopfuncs.js"></script>
 -->
<!-- <script type="text/javascript" src="./js/date2str.js"></script>
<script type="text/javascript" src="./js/code128_util.js"></script>
<script type="text/javascript" src="./js/JsBarcode.all.min.js"></script>
<script type="text/javascript" src="./js/jquery-3.3.1.min.js"></script>
<script type="text/javascript" src="./js/qrcode.min.js"></script> -->
</head>
<script type="text/javascript">
window.cLodopLoadEvent=function(evt){
	if(evt.data==0){
		/* */
		 try{
			 if(LODOP==undefined){
				 LODOP=getLodop();
			 }
			}catch(e){
				console.log("getLodop error "+e.message);
			}
		print();
	}else{
		needInstallCLodop();
		alert("当前未安装lodop插件");
	}
}
/* window.addEventListener('cLodopLoadEvent',function(evt){
	if(evt.data==0){
		/* try{
			LODOP=getLodop();
			console.log("getLodop ok ");
		}catch(e){
			console.log("getLodop error "+e.message);
		}  
		print();
	}else{
		needInstallCLodop();
		alert("当前未安装lodop插件");
	}
});  */
</script>
<%
	String url = "";
	String sPrinter = "";
	String kfName = request.getParameter("kfName");
	LodopLoadder.PrintInfo pInfo = LodopLoadder.readLodopUrlBy(kfName);
	url = pInfo.url;
	sPrinter = pInfo.printerName;
	if (!"".equals(url) && url != null) {
%>
<script type="text/javascript" src="<%=url%>"></script>
<%
	}
%>
<script type="text/javascript" src="./lodop/LodopFuncs.js?priority=1"></script>
<style type="text/css">
#print_bar {
	
}

#print_bar button {
	font-size: 16px;
	padding: 3px 10px;
}
/* table td{
border: solid 1px black;
} */
#myExportArea {
	overflow: hidden;
}

#print_bar button:nth-of-type(n+2) {
	margin-left: 12px;
	margin-top: 16px;
}
</style>
<script type="text/javascript">


function isIE() {
    if(!!window.ActiveXObject || "ActiveXObject" in window){
      return true;
    }else{
      return false;
　　 }
}
if(isIE()){
	//alert("use ie");
}

/* console=console;
if(console==undefined){
	console.log=function (){
		
	}
} */
//格式化时间
function dateFtt(fmt,date)   
{ //author: meizz   
  var o = {   
    "M+" : date.getMonth()+1,                 //月份   
    "d+" : date.getDate(),                    //日   
    "h+" : date.getHours(),                   //小时   
    "m+" : date.getMinutes(),                 //分   
    "s+" : date.getSeconds(),                 //秒   
    "q+" : Math.floor((date.getMonth()+3 )/3), //季度   
    "S"  : date.getMilliseconds()             //毫秒   
  };   
  if(/(y+)/.test(fmt))   
    fmt=fmt.replace(RegExp.$1, (date.getFullYear()+"").substr(4 - RegExp.$1.length));   
  for(var k in o)   
    if(new RegExp("("+ k +")").test(fmt))   
  fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));   
  return fmt;   
}
function changeStr(str,index,changeStr){
	var maxLen=str.length;
	var last=index+changeStr.length;
	if(index>maxLen){
		return str;
	}
	var lastStr='';
	if(last<=maxLen ){
		lastStr =str.substr(last);
	}
	 return str.substr(0, index) + changeStr+ lastStr;
}

//电话号码抹去部分
function phoneEncode(phone){
	var str=phone;
	if(phone.length==11){
		str=changeStr(phone,3,"****");
	}else{
		str=changeStr(phone,2,"****");
	}
	return str;
}
function getFormatYunStr(mCode){
	
	var index=[3,3,3,4];
if(mCode<16){
	 index=[3,3,3,4];
	}else{
		 index=[2,3,3,3,4];
	}
	var finalCode="";
	var tIndx=0;
	for(var i=0;i<index.length;i++){
		var tempIndex=index[i];
		if(tIndx+tempIndex>mCode.length){
			break;
		}
		var StrCode=mCode.slice(tIndx,tIndx+tempIndex);
		finalCode+=StrCode;
		finalCode+=" ";
		tIndx+=tempIndex;
	}
	if(mCode<16){
		finalCode="SF "+finalCode;
	}
	return finalCode;
}


function design(){
	print(4)
}
function exportPDF(){
/* 	html2canvas(document.body).then(function(canvas) {
		 document.body.appendChild(canvas);
		 }); */
		 var domObj=$("#myExportArea") ;
		 var width = domObj.offsetWidth; //dom宽
			var height = domObj.offsetHeight; //dom高
			// 解决图片模糊
			var scale = 2;//放大倍数
			var canvas = document.createElement('canvas');
			canvas.width = width * 2;
			canvas.height = height * 2;
			canvas.style.width = width + 'px';
			canvas.style.height = height + 'px';
			var context = canvas.getContext('2d');
			context.scale(scale, scale);
	                //设置context位置，值为相对于视窗的偏移量负值，让图片复位(解决偏移的重点)
	                var rect = domObj.get(0).getBoundingClientRect();//获取元素相对于视察的偏移量
			context.translate(-rect.left, -rect.top);

			 
			var opts = {
			/*  	canvas: canvas,*/
				useCORS: true, // 【重要】开启跨域配置
				scrollY: 0, // 纵向偏移量 写死0 可以避免滚动造成偶尔偏移的现象
				 background: "#fff",
				  //这里给生成的图片默认背景，不然的话，如果你的html根节点没设置背景的话，会用黑色填充。
				  allowTaint: false //避免一些不识别的图片干扰，默认为false，遇到不识别的图片干扰则会停止处理html2canvas
				  ,windowHeight:document.body.scrollHeight
				  ,y:window.pageYOffset
			};
			html2canvas($("#myExportArea"),opts).then(function(canvas) {
				  var imgData = canvas.toDataURL('image/jpeg');
				  /*    var tcanvas=document.createElement("canvas");
				var canvasList = document.getElementById('lineArea');
				  canvasList.appendChild(tcanvas);	
				  var tWidth=canvas.width;
				  var tHeight=canvas.width;
				  tcanvas.width = tHeight* 2; // 关键代码: canvas的width、height属性用于管理画布尺寸
		            tcanvas.height =tHeight * 2; // 关键代码
		 
		            tcanvas.style.width = tWidth + "px"; // 关键代码: canvas的style属性中的width、height正好是显示尺寸,即最终生成到pdf中的尺寸
		            tcanvas.style.height =tHeight + "px";
		            setTimeout(() => {
		            	  var ctx = tcanvas.getContext("2d"), file, reader = new FileReader();
				            ctx.scale(2, 2); // 关键代码: 图片绘制的时候 也放大两倍
				            ctx.drawImage(imgData, 0, 0,tWidth, tHeight);
				            imgData = tcanvas.toDataURL("image/jpeg", quality);
					}, 2*1000);
		           */
		         
				    var img = new Image();
				   
				    //根据图片的尺寸设置pdf的规格，要在图片加载成功时执行，之所以要*0.225是因为比例问题
				    img.onload = function() {
				      //此处需要注意，pdf横置和竖置两个属性，需要根据宽高的比例来调整，不然会出现显示不完全的问题
				      var doc;
				      console.log("mWidth= "+this.width+",height="+this.height);
				      if (this.width > this.height) {
				    	  //横向
				         doc = new jsPDF('l', 'mm', [this.width * 0.225, this.height * 0.225]);
				      } else {
				    	  //纵向
				         doc = new jsPDF('p', 'mm', [this.width * 0.225, this.height * 0.225]);
				      }
				      var pageHeight=780 * 0.26;
				      var pageWidth=378 * 0.26;
				      var realW=this.width * 0.26;
				      var realH=this.height * 0.26;
				      doc = new jsPDF('p', 'mm', [pageWidth,pageHeight]);
				      var leftHeight=realH;
				      if (leftHeight < pageHeight) {
				    	  doc.addImage(imgData, 'jpeg', 0, 0, realW, realH );
				    } else {
				    	var position=0;
				    	while(leftHeight > 0) {
				    		doc.addImage(imgData,  'jpeg', 0, position, realW, realH)
				    	  leftHeight -= pageHeight;
				    	  position -= pageHeight;
				    	  //避免添加空白页
				    	  if(leftHeight > 0) {
				    		  doc.addPage();
				    	  }
				    	  }
				    }
				      //794px
				     // doc.addImage(imgData, 'jpeg', 0, 0, realW,realH);
				      //根据下载保存成不同的文件名
				      var filePath="testjsPDF_"+ new Date().getTime() + '.pdf';
				      doc.save(filePath);
				    }
				    img.src = imgData;
				    document.body.appendChild(img);
			});
/* 	html2canvas($("#myExportArea"), {
		  onrendered: function(canvas) {
		    var imgData = canvas.toDataURL('image/jpeg');
		    var img = new Image();
		   
		    //根据图片的尺寸设置pdf的规格，要在图片加载成功时执行，之所以要*0.225是因为比例问题
		    img.onload = function() {
		      //此处需要注意，pdf横置和竖置两个属性，需要根据宽高的比例来调整，不然会出现显示不完全的问题
		       var doc;
		      console.log("mWidth= "+this.width+",height="+this.height);
		      if (this.width > this.height) {
		    	  //横向
		         doc = new jsPDF('l', 'mm', [this.width * 0.225, this.height * 0.225]);
		      } else {
		    	  //纵向
		         doc = new jsPDF('p', 'mm', [this.width * 0.225, this.height * 0.225]);
		      }
		      doc = new jsPDF('p', 'mm', [this.width * 0.225, this.height * 0.225]);

		      doc.addImage(imgData, 'jpeg', 0, 0, this.width * 0.225, this.height * 0.225);
		      //根据下载保存成不同的文件名
		      var filePath="testjsPDF_"+ new Date().getTime() + '.pdf';
		      doc.save(filePath);
		    }
		    img.src = imgData;
		  },
		  background: "#fff",
		  //这里给生成的图片默认背景，不然的话，如果你的html根节点没设置背景的话，会用黑色填充。
		  allowTaint: true //避免一些不识别的图片干扰，默认为false，遇到不识别的图片干扰则会停止处理html2canvas
		}); */
}

/* $(function() {
	try{
		LODOP=getLodop();
		console.log("getLodop ok ");
	}catch(e){
		console.log("getLodop error "+e.message);
	}
	print();
}); */
if(!needCLodop()){
	LODOP=getLodop();
}
dir="./imgs/sf/";
//打印方法
	function print(printMode) {
		//LODOP=getLodop({},{});
	
		var jdata={};
		 try {
			 <%String poString = request.getParameter("data");
			/* 	 String path=request.getServletContext().getContextPath();
				 System.out.println("sfv3 path="+path); */
			System.out.println("sfv3 data=" + poString);%>
			 //初始化数据
			dataobj = {
					YDCodes:["1231231231234","1112223331234"],	 //多单号
					note:"一个普通的测试打印的备注", //托寄物备注
					payType:"寄付月结",//付款方式
					//寄件 
					j_addr:"寄-xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx", 
					j_name:"寄-xx",
					j_phone:"12312312312",
					j_comp:"j_xxxxxxxxxxx",//寄件公司
					//收件 
					r_addr:"收-xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx",
					r_name:"收-xx",
					r_phone:"31231231231",
					r_comp:"收-xxxxxxxxxxx",//收件公司
					codingMappingOut:"123",//出港信息码
					codingMapping:"456",//进港信息码
					destRouteLabel:"010",//目的地代号
					proCode:"T6",//时效类型
					destTeamCode:"001",//取destTeamCode值，收件地址水印
					abFlag:"A",//根据abFlag值 
					paperType:"210",//根据abFlag值 
					twoDimensionCode:"MMM={'k1':'010WB','k2':'010MC','k3':'060','k4':'T6','k5':'322108530494','k6':'','k7':'ae7cc797'}"
					//二维码信息,接口返回twoDimensionCode字段
					};
			 <%String data2 = "{}";
			if (poString != null) {

				try {
					OrderMgr orderMgr = new OrderMgr();
					YundanInput mData = orderMgr.getData(poString);
					String jsonStr = JSONObject.toJSONString(mData);
					JSONObject tempObj = new JSONObject();
					data2 = String.format("%s", jsonStr);%> <%-- var jdata=JSON.Parse('<%=jsonStr%>'); --%>
					 jdata=<%=data2%>;
					var yundanids=jdata.response.yundanId.split(",");
					dataobj = {
							YDCodes:yundanids,	 //多单号
							note:jdata.tuoji,  
							payType:jdata.payType,
							//付款方式
							//寄件 
					        j_addr:jdata.mSender.j_address,
							j_name:jdata.mSender.j_name,
						 	j_phone:jdata.mSender.j_tel,
						<%-- 		'<%=mData.mSender.j_tel%>', --%>
							j_comp:jdata.mSender.j_company,
							<%-- 	'<%=mData.mSender.j_company%>',//寄件公司 --%>
							//收件
							r_addr:jdata.mSender.d_address,
							<%-- 	'<%=mData.mSender.d_address%>', --%>
							r_name:jdata.mSender.d_name,
						<%-- 		'<%=mData.mSender.d_name%>',
						 --%>	r_phone:jdata.mSender.d_tel,
						<%-- 		'<%=mData.mSender.d_tel%>',
						 --%>	r_comp:jdata.mSender.d_company,
						<%-- 		'<%=mData.mSender.d_company%>',//收件公司
						 --%>	codingMappingOut:jdata.response.HK_out,
							<%-- 	'<%=mData.response.HK_out%>',//出港信息码
							 --%>codingMapping:jdata.response.HK_in,
						<%-- 		'<%=mData.response.HK_in%>',//进港信息码
						 --%>	destRouteLabel:jdata.response.destRouteLable,
							<%-- 	'<%=mData.response.destRouteLable%>',//目的地代号
						 --%>	proCode:jdata.response.proCode,
						<%-- 		'<%=mData.response.proCode%>',//时效类型
						 --%>	destTeamCode:jdata.response.destcode,
					<%-- 			'<%=mData.response.destcode%>',//取destTeamCode值，收件地址水印
					 --%>		abFlag:jdata.response.destcode,
						<%-- 		'<%=mData.response.destcode%>',//根据abFlag值  --%>
							isSpecial:jdata.isSpecial,
							weight:jdata.weight,
							paperType:jdata.yundanType,
							printer:jdata.printer,
							twoDimensionCode:jdata.response.qrInfo
							//二维码信息,接口返回twoDimensionCode字段
							};
					<%} catch (Exception e) {
					throw new IOException("打印异常" + e.getMessage());
				}
			}%>
			 if (printMode == 3) {
				dataobj.YDCodes=["1231231231235"];
			} 
			
			var printer= jdata.printer;
			//所有单号 
			var yundanIds=dataobj.YDCodes;
			//主单号
			var mainId=yundanIds[0];
			//配置图标的父路径
	
			var yundanCount=yundanIds.length;
			
		   // LODOP.SET_PRINTER_INDEX("BTP-L540H(BPLZ)(U)1");
		   try{
				LODOP.SET_LICENSES("", "B92246F50E1B077094435C6955BEE291", "C94CEE276DB2187AE6B65D56B3FC2848", "");
		   }catch (e) {
			   console.log("no SET_LICENSES");
		}
			 LODOP.PRINT_INITA(0,0,450,850,"Lodop_SF_V3"+mainId);
	<%if (!"".equals(sPrinter) && null != sPrinter) {%>
		LODOP.SET_PRINTER_INDEX("<%=sPrinter%>");
		<%}%>	
	
			//LODOP.SET_PRINTER_INDEX("Microsoft XPS Document Writer");
			if(printer!=undefined&&printer!=''){
				LODOP.SET_PRINTER_INDEX(printer);
			}else{
				console.log("useDebug printer");
				//LODOP.SET_PRINTER_INDEX("\\HAOLEI-PC\Microsoft XPS Document Writer");
				LODOP.SET_PRINTER_INDEX("SF");
			}
			//LODOP.SET_PRINTER_INDEX("sf");
		    //LODOP.SET_PRINTER_INDEX("BTP-L540H");
		    //LODOP.SET_PRINTER_INDEX("DP23 Label Printer");
		/*     dataobj.paperType="150";
		    console.log("dataobj.paperType use debug ="+dataobj.paperType);
		    dataobj.weight="20kg";
		    console.log("dataobj.weight use debug ="+dataobj.weight);
			dataobj.isSpecial="1";
			console.log("dataobj.isSpecial use debug"); */
		    try{
			if(dataobj.paperType=='210'){
				LODOP.SET_PRINT_PAGESIZE(1, "100mm", "210mm","");
			}else if(dataobj.paperType=='180'){
				LODOP.SET_PRINT_PAGESIZE(1, "100mm", "180mm","");
			}else{
				LODOP.SET_PRINT_PAGESIZE(1, "100mm", "150mm","");
			}
		    }catch (e) {
		    	console.log("use local lodop error="+e.message);
			}
			var isReturn=jdata.mSender.need_return_tracking_no;
			for(var j=0;j<yundanIds.length;j++){
				var mCode=yundanIds[j];
				var finalCode=getFormatYunStr(mainId);
				var finalCode2=getFormatYunStr(mCode);
				var decodeCode=mCode;
				if(mCode.indexOf("SF")!=0){
					decodeCode="SF"+mCode;
				}
				var mcodeHeight=60;
				var barcodeId="imgcode";
		var qrSize={width:100,height:100};
				/* ;
				makeQr(dataobj.twoDimensionCode,"twoDimensionCode",qrSize); */
			var codingMappingOut=dataobj.codingMappingOut;
			var codingMapping=dataobj.codingMapping;
			var destRouteLabel=dataobj.destRouteLabel;
			var proCodeImgUrl="";
		
			var abFlagImgUrl='';
			
			var imgName="";
			if("A"==dataobj.abFlag ){
				imgName="A标.jpg";
			}else if("B"==dataobj.abFlag ){
				imgName="B标.jpg"
			}else{
				imgName='';
			}
			abFlagImgUrl=dir+imgName;
			 if(''==imgName){
				 abFlagImgUrl='';
			 }
			/* if("T1"==dataobj.proCode ){
				proCodeImgUrl=dir+"20_20 T1.png"
			}else if("T4"==dataobj.proCode ){
				proCodeImgUrl=dir+"20_20 T4.png"
			}else if("T6"==dataobj.proCode ){
				proCodeImgUrl=dir+"20_20 T6.png"
			}else if("T8"==dataobj.proCode ){
				proCodeImgUrl=dir+"20_20 T8.png"
			}else if("T9"==dataobj.proCode ){
				proCodeImgUrl=dir+"资源 26.png"
			} */
			 var proImgName='';
				if("T1"==dataobj.proCode ){
					proImgName="20_20 T1.png"
				}else if("T4"==dataobj.proCode ){
					proImgName="20_20 T4.png"
				}else if("T6"==dataobj.proCode ){
					proImgName="20_20 T6.png"
				}else if("T8"==dataobj.proCode ){
					proImgName="20_20 T8.png"
				}else if("T9"==dataobj.proCode ){
					proImgName="资源 26.png"
				}
				proCodeImgUrl=dir+proImgName;
				if(proImgName=''){
					proCodeImgUrl='';
				}
		var YDCode = dataobj.YDCode;
				var destAreaCode = dataobj.destAreaCode;
				var note = '';
				try {
					if (dataobj.note) {
						note = dataobj.note;
					}
				} catch (e) {
					console.log("not exists 'note' key," + e);
				}
				var payType = dataobj.payType;
				var lx = dataobj.serverType;
				var AccountNo = dataobj.account;
				var ifsign = dataobj.ifsignreturn;
				var from = dataobj.j_name + " " +phoneEncode(dataobj.j_phone)   + "  "
						+ dataobj.j_comp + " " + dataobj.j_addr;
			
				if(dataobj.j_addr.length>40){
					 from = dataobj.j_name + " " +phoneEncode(dataobj.j_phone)   + "  "
						+ dataobj.j_comp + " " + dataobj.j_addr;
				}
				//from+="";
				var zhChars= from.replace(/[\u4e00-\u9fa5]/g,'').length
				var totalChars=from.length;
				var mlen=(totalChars-zhChars)+zhChars*2;
				var index=from.indexOf("市");
				if(mlen>100){
					
				}
				var to = dataobj.r_name + " " + phoneEncode(dataobj.r_phone) + "  "
						+ dataobj.r_comp + "  " + dataobj.r_addr;
				if(dataobj.r_addr.length>40){
					 to = dataobj.r_name + " " + phoneEncode(dataobj.r_phone) + "  "
						+ dataobj.r_comp +" " + dataobj.r_addr;
				}
				var crtTime = new Date();
				var time = dateFtt("yyyy-MM-dd hh:mm:ss", crtTime);
				  LODOP.NEWPAGE();
				//左右边界
				var marginHorizontal=4;
				var marginVetical=8;
				var innerPadding=4;
				//初始高度，顶部还有图标
				var initY=45;
				var y = initY;
				//表格宽度96mm==363px
				var lineWidth=368;
				var dataRight=30;
				//第一行
				LODOP.ADD_PRINT_TEXT(y,marginHorizontal+ 8, 100, 20, "ZJ");
				LODOP.SET_PRINT_STYLEA(0, "FontSize", 6);
				LODOP.ADD_PRINT_TEXT(y, 126, 100, 20, time);
				LODOP.SET_PRINT_STYLEA(0, "FontSize", 6);
				/*var imgcode=document.getElementById(barcodeId);
		 	var codeStr=imgcode.outerHTML;
				console.log("imageCode="+codeStr); */
				y+=20;
				var x1=4;
				var codeY=y-5;
				var proCodeY=y+innerPadding ;
		/* 	var codeImgWidth=imgcode.width;
			var codeImgHeight=mcodeHeight; */
			var tag="slowFunction";
			/* 
			1-已暂停
			2-错误
			4-正删除
			8-进入队列	16-正在打印
			32-脱机
			64-缺纸
			128-打印结束	256-已删除
			512-堵塞
			1024-用户介入
			2048-正在重新启动 */
		
			//	LODOP.ADD_PRINT_IMAGE(codeY, 19, 254, mcodeHeight,"<img width='"+codeImgWidth+"' height='"+codeImgHeight+"' title='条码' src='"+codeImageData+"'>");
			var barX=marginHorizontal+45;
			LODOP.ADD_PRINT_BARCODE(codeY, barX, 254, 50,"128B",decodeCode);
			LODOP.SET_PRINT_STYLEA(0,"ShowBarText",0);
			y+=50;
			var codeBottomHeight=20;
			var yundanIndex= (j+1)+ "/"+yundanCount;
			LODOP.ADD_PRINT_TEXT(y, 17, 52, codeBottomHeight,yundanIndex);
			LODOP.SET_PRINT_STYLEA(0, "FontName", "arial");
			LODOP.ADD_PRINT_TEXT(y, 52, 52, codeBottomHeight, "母单号");
			LODOP.ADD_PRINT_TEXT(y, 113, 128, codeBottomHeight,finalCode );
			LODOP.SET_PRINT_STYLEA(0, "FontName", "arial");
			if(j>=1){
				LODOP.ADD_PRINT_TEXT(y+20, 52, 52, codeBottomHeight, "子单号");
				LODOP.ADD_PRINT_TEXT(y+20, 113, 128, codeBottomHeight, finalCode2);
				LODOP.SET_PRINT_STYLEA(0, "FontName", "arial");
			}
			y+=codeBottomHeight+innerPadding;
			var rectTop1=32;
			var rectTop2=initY+106 ;
			var proCodeWidth=82;
			var proCodeX=marginHorizontal+lineWidth-proCodeWidth- innerPadding;
			var proCodeY=rectTop2-proCodeWidth- innerPadding;
			var proCodeInnerWidth=proCodeWidth-3;
			LODOP.ADD_PRINT_IMAGE(proCodeY, proCodeX,proCodeWidth, proCodeWidth, "<img width='"+proCodeInnerWidth+"' height='"+proCodeInnerWidth+"' title='时效T1/T4/T6/T8' src='"+proCodeImgUrl+"'>");
			
			var r1Height=192;
			var lineBottom=rectTop2+r1Height-1;
			LODOP.ADD_PRINT_RECT(rectTop2,marginHorizontal, lineWidth,r1Height, 0, 1);
			var tempJx1=rectTop2+100;
			//var tempJx1=lineBottom+5;
			LODOP.ADD_PRINT_LINE(tempJx1,marginHorizontal,tempJx1-1,lineWidth+marginHorizontal,0,1);
			 
			var destCodeX=marginHorizontal+11;
			maxW=marginHorizontal+lineWidth-marginHorizontal;
			
			LODOP.ADD_PRINT_TEXT(rectTop2, destCodeX, maxW-destCodeX ,46,destRouteLabel);
			LODOP.SET_PRINT_STYLEA(0, "FontSize", 33);
			LODOP.SET_PRINT_STYLEA(0, "Bold", 1);
		    var x1=marginHorizontal+innerPadding;
		    var x2=x1+32;
		    var w2=249 ;
		    var x3=x2+w2+innerPadding
		    var jTop=rectTop2+52;
		    //180 113;
			LODOP.ADD_PRINT_IMAGE(jTop, x1, "7.01mm", "7.01mm","<img width='23' width='23' src='"+dir+"收（7mm）.png'>");
			LODOP.ADD_PRINT_TEXT(jTop, x2, w2, 44,to );
			LODOP.ADD_PRINT_HTM(jTop+5,x2+63,108,40,"<body  style='margin:0px'><div  style='color: gray;z-index:10000;opacity:0.5;margin:0px;font-size: 37pt;line-height:33px'>"+dataobj.destTeamCode+"</div></body>");
			//COD
			LODOP.ADD_PRINT_TEXT(173,x3,"17.01mm","9mm","");
		
			LODOP.SET_PRINT_STYLEA(0, "FontName", "黑体");
			LODOP.SET_PRINT_STYLEA(0, "FontSize", 30);
			LODOP.SET_PRINT_STYLEA(0, "Bold", 1);
			//
			var c1=marginHorizontal+ 125
			//223
			var top1=226;
			var top1Height1="97mm";
			var top2=top1+60;
			var c2_x2=288;
			var c2_x3=marginHorizontal+125;
			//125
			LODOP.ADD_PRINT_LINE(tempJx1, c2_x3,lineBottom, c2_x3+1, 0, 1);
			var c2_x4=c2_x3+124 ;
			var qrOutWidth= qrSize.width+3;
			var qrOutHeight= qrSize.height+3;
			var left=(c2_x4 -c2_x3- qrOutWidth)/2;
		    
			LODOP.ADD_PRINT_TEXT(tempJx1+2, 8, 100, 20, payType);
			LODOP.ADD_PRINT_LINE(tempJx1+20, marginHorizontal, tempJx1+20+1,c2_x3, 0, 1);
			
			LODOP.ADD_PRINT_TEXT(tempJx1+20, x1, 118, 58, codingMapping);
			LODOP.SET_PRINT_STYLEA(0,"FontName","黑体");
			LODOP.SET_PRINT_STYLEA(0,"FontSize",40);
			LODOP.ADD_PRINT_BARCODE(tempJx1+2, c2_x3+left , qrOutWidth,qrOutHeight,"QRCode",dataobj.twoDimensionCode);
			LODOP.SET_PRINT_STYLEA(0,"ShowBarText",0);
			
			LODOP.ADD_PRINT_LINE(tempJx1, c2_x4,lineBottom, c2_x4+1, 0, 1);
			c2_x4+= 35;
			LODOP.ADD_PRINT_LINE(tempJx1, c2_x4,lineBottom, c2_x4+1, 0, 1);
			var abFlagY=tempJx1+55
			LODOP.ADD_PRINT_LINE(abFlagY,c2_x4,abFlagY-1,c2_x4+79,0,1);
			LODOP.ADD_PRINT_TEXT(tempJx1+2, 254, 36, 88, "已验视");
			LODOP.SET_PRINT_STYLEA(0, "FontName", "黑体");
			LODOP.SET_PRINT_STYLEA(0, "FontSize", 20);
			LODOP.SET_PRINT_STYLEA(0, "Bold", 1);
			LODOP.SET_PRINT_STYLEA(0,"LineSpacing",-9);
			LODOP.ADD_PRINT_IMAGE(tempJx1+2, 301, 52, 43, "<img width='52' height='43' title='取abFlag值' src='"+abFlagImgUrl+"'>");
			var textX=c2_x4+innerPadding; 
			var textY=abFlagY; 
			LODOP.ADD_PRINT_TEXT(textY, textX, 57, 26, codingMappingOut);
			LODOP.SET_PRINT_STYLEA(0,"FontName","黑体");
			LODOP.SET_PRINT_STYLEA(0,"FontSize",20);
			LODOP.SET_PRINT_STYLEA(0,"Bold",1);
//			var returnTop=rectTop2+208;
			var returnTop=lineBottom+5;
			//151
			//380
			var returnTop2=333;
		//	86.62 
		    var y1=returnTop+1;
		    var pxY1 =returnTop+3;
			LODOP.ADD_PRINT_RECT(returnTop, marginHorizontal, 368, "52mm", 0, 1);
			 var jInfo_width=323;
			/* LODOP.ADD_PRINT_IMAGE(y1+"mm", x1, "7.01mm", "7.01mm","<img  width='23' width='23' src='"+dir+"寄(7mm).png'>");
			LODOP.ADD_PRINT_TEXT(y1+"mm", x2, jInfo_width, "10.32mm", to); */
			LODOP.ADD_PRINT_IMAGE(pxY1, x1, "7.01mm", "7.01mm","<img  width='23' width='23' src='"+dir+"寄(7mm).png'>");
			LODOP.ADD_PRINT_TEXT(pxY1, x2, jInfo_width, "10.32mm", from);
			var line2Top=returnTop+12;
			var line2Top2=returnTop;
			var line3Top=line2Top+26;
			var tempX=marginHorizontal+innerPadding ;
			var tempY=line2Top2+41 ;
			var w1=100;
			var minWidht=w1+20;
			var linDur=4;
			var tempH=20;
			LODOP.ADD_PRINT_LINE(tempY, marginHorizontal, tempY-1, marginHorizontal+lineWidth, 0, 1);
			LODOP.ADD_PRINT_TEXT(tempY,tempX, w1, tempH, "增值服务:");
			if(''!=dataobj.weight&&undefined!=dataobj.weight){
				var weightX=tempX+w1+5;
				LODOP.ADD_PRINT_TEXT(tempY,weightX, w1, tempH, "重量:"+dataobj.weight);
			}
			tempY+=tempH;
			//49
			tempH=40;
			w1=lineWidth-innerPadding*2;
			var line3Top2=line2Top2+120;
			//LODOP.ADD_PRINT_TEXT(tempY, tempX, w1, line3Top2-tempY-innerPadding, "托寄物:"+note);
			var iconWidth=35;
		
			if("1"==dataobj.isSpecial){
				var iconleft=marginHorizontal+lineWidth-iconWidth;
				//LODOP.ADD_PRINT_TEXT(returnTop+3,xB2+3,259,51,dataobj.note);
				LODOP.ADD_PRINT_IMAGE(tempY,iconleft, 32, 32,"<img width='30' height='30' title='条码' src='./imgs/TeShu.png'>");
				LODOP.ADD_PRINT_TEXT(tempY,tempX,w1-iconWidth,line3Top2-tempY-innerPadding,dataobj.note);
			}else{
				LODOP.ADD_PRINT_TEXT(tempY,tempX,w1,line3Top2-tempY-innerPadding,dataobj.note);
			}
			w1=100;
			tempY+=tempH+linDur;
			tempH=20;
		/* 	LODOP.ADD_PRINT_TEXT(tempY, tempX, w1, tempH, "计费重量:");
			tempX+=minWidht;
			LODOP.ADD_PRINT_TEXT(tempY,tempX, w1, tempH, "实际重量:");
			tempX+=minWidht;
			LODOP.ADD_PRINT_TEXT(tempY, tempX, w1, tempH, "费用合计:"); */
			LODOP.ADD_PRINT_LINE(line3Top2, marginHorizontal, line3Top2-1, marginHorizontal+lineWidth, 0, 1);
		//	var isReturn=jdata.response.returnResponse.isReturn;
		
			if("1"==isReturn){
				LODOP.ADD_PRINT_IMAGE(line3Top2,marginHorizontal+3, 100, 40,"<img height='35' title='条码' src='"+dir+"POD.jpg'>");
			}
			if(dataobj.paperType=='150'){
				var barX=marginHorizontal+80;
				var tempy150=line3Top2+5;
				LODOP.ADD_PRINT_BARCODE(tempy150, barX, 254, 40,"128B",decodeCode);
				LODOP.SET_PRINT_STYLEA(0,"ShowBarText",0);
				LODOP.ADD_PRINT_TEXT(tempy150+45, barX, 128, codeBottomHeight, finalCode2);
				LODOP.SET_PRINT_STYLEA(0, "FontName", "arial");
			}
			returnTop+=200;
			pxY1 =returnTop+3;
			var lineheight=227;
			//180需要的
			//210需要的
			
			if(dataobj.paperType=='210'){
				
			
			var rect3Bottom=lineheight+returnTop;
			LODOP.ADD_PRINT_RECT(returnTop, marginHorizontal, lineWidth,lineheight, 0, 1);
			var bw1=80;
			var bBarCodeHeight=32;
			var bBarOffx=32;
			LODOP.ADD_PRINT_BARCODE(returnTop+3, marginHorizontal+3+bw1+bBarOffx, 250, bBarCodeHeight,"128B",decodeCode);
			LODOP.SET_PRINT_STYLEA(0,"ShowBarText",0);
			
			
			//热线.png,logo.png
			 var logX1=marginHorizontal+2;
			var logY1=returnTop+3;
			LODOP.ADD_PRINT_IMAGE(logY1, logX1, "0", "34","<img width='75' src='"+dir+"热线.png'>");
			logY1=logY1+32;
			LODOP.ADD_PRINT_IMAGE(logY1, logX1, "0", "34","<img width='75' src='"+dir+"logo.png'>");
		
			var bCodeTextHeight=17;
			
			var bCodeTextY= 5+returnTop+bBarCodeHeight;
		/* 	LODOP.ADD_PRINT_TEXT(bCodeTextY, marginHorizontal+3+bw1 , 250, bCodeTextHeight, decodeCode);
			bCodeTextY+=bCodeTextHeight-5;
			LODOP.ADD_PRINT_TEXT(bCodeTextY, marginHorizontal+3+bw1 , 250,bCodeTextHeight, decodeCode);
			 */
			 var bTag=52;
			 var bbarTextX= marginHorizontal+3+bw1;
			 
			 //序号
			 LODOP.ADD_PRINT_TEXT(bCodeTextY, bbarTextX, 40, bCodeTextHeight,yundanIndex);
			 bbarTextX+=40;
			LODOP.ADD_PRINT_TEXT(bCodeTextY, bbarTextX, bTag, bCodeTextHeight, "母单号");
			LODOP.ADD_PRINT_TEXT(bCodeTextY, bbarTextX+bTag, 128, bCodeTextHeight,finalCode );
			
			LODOP.SET_PRINT_STYLEA(0, "FontName", "arial");
			if(j>=1){
				bCodeTextY+=bCodeTextHeight-5;
				LODOP.ADD_PRINT_TEXT(bCodeTextY, bbarTextX, bTag, bCodeTextHeight, "子单号");
				LODOP.ADD_PRINT_TEXT(bCodeTextY, bbarTextX+bTag, 128, bCodeTextHeight, finalCode2);
				LODOP.SET_PRINT_STYLEA(0, "FontName", "arial");
			}
			var bottomH1=64;
			LODOP.ADD_PRINT_LINE(returnTop, marginHorizontal+bw1, returnTop+bottomH1, marginHorizontal+bw1-1, 0, 1);
			returnTop+=bottomH1;
			
			pxY1 =returnTop+3;
			LODOP.ADD_PRINT_LINE(returnTop, marginHorizontal, returnTop-1, marginHorizontal+lineWidth, 0, 1);

			LODOP.ADD_PRINT_IMAGE(pxY1, x1, "7.01mm", "7.01mm","<img  width='23' width='23' src='"+dir+"寄(7mm).png'>");
			LODOP.ADD_PRINT_TEXT(pxY1, x2, jInfo_width, "10.32mm", from );
			
			returnTop+=44;
			pxY1 =returnTop+3;
			LODOP.ADD_PRINT_LINE(returnTop, marginHorizontal, returnTop-1, marginHorizontal+lineWidth, 0, 1);
			LODOP.ADD_PRINT_IMAGE(pxY1, x1, "7.01mm", "7.01mm","<img  width='23' width='23' src='"+dir+"收（7mm）.png'>");
			LODOP.ADD_PRINT_TEXT(pxY1, x2, jInfo_width, "10.32mm", to);
			returnTop+=44;
			pxY1 =returnTop+3;
			var xB2=marginHorizontal+100;
			//备注
			if("1"==dataobj.isSpecial){
				var iconleft=marginHorizontal+lineWidth-35;
				//LODOP.ADD_PRINT_TEXT(returnTop+3,xB2+3,259,51,dataobj.note);
				LODOP.ADD_PRINT_IMAGE(returnTop+3,iconleft, 32, 32,"<img width='30' height='30' title='条码' src='./imgs/TeShu.png'>");
				LODOP.ADD_PRINT_TEXT(returnTop+3,xB2+3,259-35,51,dataobj.note);
			}else{
				LODOP.ADD_PRINT_TEXT(returnTop+3,xB2+3,259,51,dataobj.note);
			}
			//付款方式
			LODOP.ADD_PRINT_TEXT(returnTop+3,marginHorizontal,100,51,dataobj.payType);
			LODOP.ADD_PRINT_LINE(returnTop, marginHorizontal, returnTop-1, marginHorizontal+lineWidth, 0, 1);
			LODOP.ADD_PRINT_LINE(returnTop, xB2 ,rect3Bottom,xB2-1, 0, 1);
			returnTop+=30;
			LODOP.ADD_PRINT_LINE(returnTop, marginHorizontal, returnTop-1, xB2, 0, 1);
			}
			}
			if(jdata.mSender.need_return_tracking_no=='1'){
				try{
				var returnId=	 jdata.response.returnResponse.yundanId
					addPage(mainId,returnId,jdata,yundanCount ,yundanCount+1);
				}catch(e){
					//alert("add page error,%s,%s",e.stack,e.message);
					alert("add page error,"+e.stack+","+e.message)
				}
			}
			
			console.log("nowMode="+printMode);
			
			if (printMode == 1) {
				LODOP.PREVIEW() 
				//LODOP.PRINT_DESIGN();
			    var fileName= "123.jpg";
			    var filePath="d:/dyj/testYudan.jpg";
	             LODOP.SET_SAVE_MODE("FILE_PROMPT",false);
	         /* 	LODOP.On_Return=function(TaskID,Value){
					console.log("status="+Value);
					if (Value==1){
						
						alert("导出到文件 "+filePath+" 成功！");
					};
				};		 
	             LODOP.SAVE_TO_FILE(filePath);
	             */
			} else if (printMode == 3) {
				//LODOP.PREVIEW() 
				LODOP.PRINT_DESIGN();
			} else if (printMode == 4) {
				LODOP.PRINT_DESIGN() 
			}else {
				LODOP.On_Return=function(TaskID,Value){
					console.log("status="+Value);
					if (Value==1){
					//	alert(TaskID+" "+Value);
						/* document.getElementById('T12B').value="打印成功！";
						clearTimeout(t);
						c=0; */
						alert("打印成功！"+TaskID);
					};
				};		
				LODOP.PRINT();
			}
		} catch (e) {
			alert("打印错误 error:" + e.message+",stack="+e.stack+",name="+e.name);
			if(e.message.indexOf("LODOP is not defined")==0){
				var strCLodopInstall_1 = "<br><font color='#FF00FF'>Web打印服务CLodop未安装启动，点击这里<a href='CLodop_Setup_for_Win32NT.exe' target='_self'>下载执行安装</a>";
			    var strCLodopInstall_3 = "，成功后请刷新本页面。</font>";
			    document.body.innerHTML = strCLodopInstall_1  + strCLodopInstall_3 + document.body.innerHTML;
			}
		}
	}
	
	function preview() {
		print(1);
	}
	function preview2() {
//		var monthCode="7550065565"; 
		var monthCode=""; 
		var pack="2"; 
		//var monthCode=""; 
		$.ajax({
			url:'./KdOrderTest?MonthCode='+monthCode+"&Pack="+pack,
			type:'get',
			success:function(data){
				var beijing=JSON.parse( data);
				var html=beijing.PrintTemplate; 
				var pattern = /<body>.*<\/body>/gi;
				var matches = pattern.exec(data);
				var obj="'"+matches[0]+"'";
var bodyPattern=/<body>(.|\b|\s)*<\/body>/gi;
				var obj2=/<body>(.|\b|\s)*<\/body>/gi.exec(html)[0];
				console.log("mObj="+obj);
				var subs=beijing.SubPrintTemplates;
				$("#kdn_sf_preview" ).html(obj2); 
				
				 LODOP.PRINT_INITA(0,0,450,850," Lodop_SF_V2");
			    LODOP.SET_PRINTER_INDEX("BTP-L540H");
				//LODOP.SET_PRINT_PAGESIZE(1, "100mm", "150mm");
				//LODOP.NEWPAGE();
			//	LODOP.PRINT_INITA(1,1,770,600,"测试预览功能");
				LODOP.ADD_PRINT_TEXT(15,121,100,20,"这是测试快递鸟 ");
				LODOP.ADD_PRINT_HTM(5,5,"100%","100%",obj2);
				if(subs!=undefined){
					var count=subs.length;
					//console.log("subCounts="+count);
				  for(var i=0;i<subs.length;i++){
					  LODOP.NEWPAGE();
					  var temp=subs[i];
					  var tempChild = bodyPattern.exec(temp)[0];
					  LODOP.ADD_PRINT_HTM(5,5,"100%","100%",tempChild);
					 /*  */
					  //LODOP.ADD_PRINT_HTML(5,5,"100%","100%",temp);
					  }
				 }
				// LODOP.PRINT_DESIGN();
				 LODOP.PRINT();
			},
			error:function(error){
				console.log("error kdniao:"+error.message);
			}
		});
	}
	function addPage(mainId,mCode,jdata,j,yundanCount){
		dataobj = {
				//YDCodes:yundanids,	 //多单号,  
				//托寄物备注
				payType:jdata.payType, 
				note:jdata.tuoji,  
				//付款方式
				//寄件 
		        j_addr:jdata.mSender.j_address ,
				j_name:jdata.mSender.j_name,
				j_phone:jdata.mSender.j_tel,
				j_comp:jdata.mSender.j_company,
				//收件
				r_addr:jdata.mSender.d_address ,
				r_name:jdata.mSender.d_name,
				r_phone:jdata.mSender.d_tel ,
				r_comp:jdata.mSender.d_company,//收件公司
				
				codingMappingOut:jdata.response.HK_out,//出港信息码
				codingMapping:jdata.response.HK_in ,//进港信息码
				destRouteLabel:jdata.response.destRouteLable,//目的地代号
				proCode:jdata.response.proCode,//时效类型
				destTeamCode:jdata.response.destcode,//取destTeamCode值，收件地址水印
				abFlag:jdata.response.destcode,//根据abFlag值 
				paperType:jdata.yundanType,
				isSpecial:jdata.isSpecial,
				twoDimensionCode:jdata.response.qrInfo
				//二维码信息,接口返回twoDimensionCode字段
				};
		//var isReturn=jdata.response.returnResponse.isReturn;
		var isReturn=jdata.mSender.need_return_tracking_no;
		if("1"==isReturn){
			console.log("addPage isReturn");
			dataobj.note="回单"+jdata.flag;
			
			dataobj.j_addr=jdata.mSender.d_address;
			dataobj.j_name=jdata.mSender.d_name  ,
			dataobj.j_tel=jdata.mSender.d_tel ,
			dataobj.jComapany=jdata.mSender.d_company;
			
			dataobj.r_addr=jdata.mSender.j_address
			dataobj.r_name=jdata.mSender.j_name 
			dataobj.r_phone=jdata.mSender.j_tel 
			dataobj.r_comp=jdata.mSender.j_company;
			
			dataobj.twoDimensionCode=jdata.response.returnResponse.qrInfo;
			dataobj.destTeamCode=jdata.response.returnResponse.destcode;
			dataobj.codingMappingOut=jdata.response.returnResponse.HK_out;
			dataobj.codingMapping=jdata.response.returnResponse.HK_in;
			dataobj.destRouteLabel=jdata.response.returnResponse.destRouteLable;
			dataobj.proCode=jdata.response.returnResponse.proCode;
		}else{
			console.log("addPage noReturn");
		}
		dataobj.paperType="150";
		console.log("addPage dataobj.paperType use debug ="+dataobj.paperType);
		var finalCode=getFormatYunStr(mainId);
		var finalCode2=getFormatYunStr(mCode);
		var decodeCode=mCode;
		if(mCode.indexOf("SF")!=0){
			decodeCode="SF"+mCode;
		}
		var mcodeHeight=60;
		var barcodeId="imgcode";			
		var qrSize={width:100,height:100};
	/* ;
		makeQr(dataobj.twoDimensionCode,"twoDimensionCode",qrSize); */
	var codingMappingOut=dataobj.codingMappingOut;
	var codingMapping=dataobj.codingMapping;
	var destRouteLabel=dataobj.destRouteLabel;
	var proCodeImgUrl="";

	var abFlagImgUrl='';
	
	var imgName="";
	if("A"==dataobj.abFlag ){
		imgName="A标.jpg";
	}else if("B"==dataobj.abFlag ){
		imgName="B标.jpg"
	}else{
		imgName='';
	}
	abFlagImgUrl=dir+imgName;
	 if(''==imgName){
		 abFlagImgUrl='';
	 }
	 /* if("T1"==dataobj.proCode ){
		proCodeImgUrl=dir+"20_20 T1.png"
	}else if("T4"==dataobj.proCode ){
		proCodeImgUrl=dir+"20_20 T4.png"
	}else if("T6"==dataobj.proCode ){
		proCodeImgUrl=dir+"20_20 T6.png"
	}else if("T8"==dataobj.proCode ){
		proCodeImgUrl=dir+"20_20 T8.png"
	}else if("T9"==dataobj.proCode ){
		proCodeImgUrl=dir+"资源 26.png"
	} */
	 var proImgName='';
		if("T1"==dataobj.proCode ){
			proImgName="20_20 T1.png"
		}else if("T4"==dataobj.proCode ){
			proImgName="20_20 T4.png"
		}else if("T6"==dataobj.proCode ){
			proImgName="20_20 T6.png"
		}else if("T8"==dataobj.proCode ){
			proImgName="20_20 T8.png"
		}else if("T9"==dataobj.proCode ){
			proImgName="资源 26.png"
		}
		proCodeImgUrl=dir+proImgName;
		if(proImgName=''){
			proCodeImgUrl='';
		}
var YDCode = dataobj.YDCode;
		var destAreaCode = dataobj.destAreaCode;
		var note = '';
		try {
			if (dataobj.note) {
				note = dataobj.note;
			}
		} catch (e) {
			console.log("not exists 'note' key," + e);
		}
		var payType = dataobj.payType;
		var lx = dataobj.serverType;
		var AccountNo = dataobj.account;
		var ifsign = dataobj.ifsignreturn;
		var from = dataobj.j_name + " " +phoneEncode(dataobj.j_phone)   + "  "
				+ dataobj.j_comp + "\r\n" + dataobj.j_addr;
		var to = dataobj.r_name + " " + phoneEncode(dataobj.r_phone) + "  "
				+ dataobj.r_comp + "\r\n" + dataobj.r_addr;
		var crtTime = new Date();
		var time = dateFtt("yyyy-MM-dd hh:mm:ss", crtTime);
		  LODOP.NEWPAGE();
		//左右边界
		var marginHorizontal=4;
		var marginVetical=8;
		var innerPadding=4;
		//初始高度，顶部还有图标
		var initY=45;
		var y = initY;
		//表格宽度96mm==363px
		var lineWidth=368;

		//第一行
		LODOP.ADD_PRINT_TEXT(y,marginHorizontal+ 8, 100, 20, "ZJ");
		LODOP.SET_PRINT_STYLEA(0, "FontSize", 6);
		LODOP.ADD_PRINT_TEXT(y, 126, 100, 20, time);
		LODOP.SET_PRINT_STYLEA(0, "FontSize", 6);
		/*var imgcode=document.getElementById(barcodeId);
 	var codeStr=imgcode.outerHTML;
		console.log("imageCode="+codeStr); */
		y+=20;
		var x1=4;
		var codeY=y-5;
		var proCodeY=y+innerPadding ;
/* 	var codeImgWidth=imgcode.width;
	var codeImgHeight=mcodeHeight; */
	var tag="slowFunction";
	/* 
	1-已暂停
	2-错误
	4-正删除
	8-进入队列	16-正在打印
	32-脱机
	64-缺纸
	128-打印结束	256-已删除
	512-堵塞
	1024-用户介入
	2048-正在重新启动 */

	//	LODOP.ADD_PRINT_IMAGE(codeY, 19, 254, mcodeHeight,"<img width='"+codeImgWidth+"' height='"+codeImgHeight+"' title='条码' src='"+codeImageData+"'>");
	var barX=marginHorizontal+45;
	LODOP.ADD_PRINT_BARCODE(codeY, barX, 254, 50,"128B",decodeCode);
	LODOP.SET_PRINT_STYLEA(0,"ShowBarText",0);
	y+=50;
	var codeBottomHeight=20;
	//var yundanIndex= yundanCount-1+ "/"+yundanCount;

	var yundanIndex= (j+1)+ "/"+yundanCount;
	if((j+1)==yundanCount){
		yundanIndex="回单";
	}
	LODOP.ADD_PRINT_TEXT(y, 17, 52, codeBottomHeight,yundanIndex);
	LODOP.SET_PRINT_STYLEA(0, "FontName", "arial");
	LODOP.ADD_PRINT_TEXT(y, 52, 52, codeBottomHeight, "母单号");
	LODOP.ADD_PRINT_TEXT(y, 113, 128, codeBottomHeight,finalCode );
	LODOP.SET_PRINT_STYLEA(0, "FontName", "arial");
	if(j>=1){
		LODOP.ADD_PRINT_TEXT(y+20, 52, 52, codeBottomHeight, "子单号");
		LODOP.ADD_PRINT_TEXT(y+20, 113, 128, codeBottomHeight, finalCode2);
		LODOP.SET_PRINT_STYLEA(0, "FontName", "arial");
	}
	
	y+=codeBottomHeight+innerPadding;
	var rectTop1=32;
	var rectTop2=initY+106 ;
	var proCodeWidth=82;
	var proCodeX=marginHorizontal+lineWidth-proCodeWidth- innerPadding;
	var proCodeY=rectTop2-proCodeWidth- innerPadding;
	var proCodeInnerWidth=proCodeWidth-3;
	LODOP.ADD_PRINT_IMAGE(proCodeY, proCodeX,proCodeWidth, proCodeWidth, "<img width='"+proCodeInnerWidth+"' height='"+proCodeInnerWidth+"' title='时效T1/T4/T6/T8' src='"+proCodeImgUrl+"'>");
	
	var r1Height=192;
	var lineBottom=rectTop2+r1Height-1;
	LODOP.ADD_PRINT_RECT(rectTop2,marginHorizontal, lineWidth,r1Height, 0, 1);
	var tempJx1=rectTop2+100;
	//var tempJx1=lineBottom+5;
	LODOP.ADD_PRINT_LINE(tempJx1,marginHorizontal,tempJx1-1,lineWidth+marginHorizontal,0,1);
	 
	var destCodeX=marginHorizontal+11;
	maxW=marginHorizontal+lineWidth-marginHorizontal;
	
	LODOP.ADD_PRINT_TEXT(rectTop2, destCodeX, maxW-destCodeX ,46,destRouteLabel);
	LODOP.SET_PRINT_STYLEA(0, "FontSize", 33);
	LODOP.SET_PRINT_STYLEA(0, "Bold", 1);
    var x1=marginHorizontal+innerPadding;
    var x2=x1+32;
    var w2=249 ;
    var x3=x2+w2+innerPadding
    var jTop=rectTop2+52;
    //180 113;
	LODOP.ADD_PRINT_IMAGE(jTop, x1, "7.01mm", "7.01mm","<img  width='23' width='23' src='"+dir+"收（7mm）.png'>");
	LODOP.ADD_PRINT_TEXT(jTop, x2, w2, 44,to );
	LODOP.ADD_PRINT_HTM(jTop+5,x2+63,108,40,"<body  style='margin:0px'><div  style='color: gray;z-index:10000;opacity:0.5;margin:0px;font-size: 37pt;line-height:33px'>"+dataobj.destTeamCode+"</div></body>");
	//COD
	LODOP.ADD_PRINT_TEXT(173,x3,"17.01mm","9mm","");

	LODOP.SET_PRINT_STYLEA(0, "FontName", "黑体");
	LODOP.SET_PRINT_STYLEA(0, "FontSize", 30);
	LODOP.SET_PRINT_STYLEA(0, "Bold", 1);
	//
	var c1=marginHorizontal+ 125
	//223
	var top1=226;
	var top1Height1="97mm";
	var top2=top1+60;
	var c2_x2=288;
	var c2_x3=marginHorizontal+125;
	//125
	LODOP.ADD_PRINT_LINE(tempJx1, c2_x3,lineBottom, c2_x3+1, 0, 1);
	var c2_x4=c2_x3+124 ;
	var qrOutWidth= qrSize.width+3;
	var qrOutHeight= qrSize.height+3;
	var left=(c2_x4 -c2_x3- qrOutWidth)/2;
    
	LODOP.ADD_PRINT_TEXT(tempJx1+2, 8, 100, 20, payType);
	LODOP.ADD_PRINT_LINE(tempJx1+20, marginHorizontal, tempJx1+20+1,c2_x3, 0, 1);
	
	LODOP.ADD_PRINT_TEXT(tempJx1+20, x1, 118, 58, codingMapping);
	LODOP.SET_PRINT_STYLEA(0,"FontName","黑体");
	LODOP.SET_PRINT_STYLEA(0,"FontSize",40);
	LODOP.ADD_PRINT_BARCODE(tempJx1+2, c2_x3+left , qrOutWidth,qrOutHeight,"QRCode",dataobj.twoDimensionCode);
	LODOP.SET_PRINT_STYLEA(0,"ShowBarText",0);
	
	LODOP.ADD_PRINT_LINE(tempJx1, c2_x4,lineBottom, c2_x4+1, 0, 1);
	c2_x4+= 35;
	LODOP.ADD_PRINT_LINE(tempJx1, c2_x4,lineBottom, c2_x4+1, 0, 1);
	var abFlagY=tempJx1+55
	LODOP.ADD_PRINT_LINE(abFlagY,c2_x4,abFlagY-1,c2_x4+79,0,1);
	LODOP.ADD_PRINT_TEXT(tempJx1+2, 254, 36, 88, "已验视");
	LODOP.SET_PRINT_STYLEA(0, "FontName", "黑体");
	LODOP.SET_PRINT_STYLEA(0, "FontSize", 20);
	LODOP.SET_PRINT_STYLEA(0, "Bold", 1);
	LODOP.SET_PRINT_STYLEA(0,"LineSpacing",-9);
	LODOP.ADD_PRINT_IMAGE(tempJx1+2, 301, 52, 43, "<img width='52' height='43' title='取abFlag值' src='"+abFlagImgUrl+"'>");
	var textX=c2_x4+innerPadding; 
	var textY=abFlagY; 
	LODOP.ADD_PRINT_TEXT(textY, textX, 57, 26, codingMappingOut);
	LODOP.SET_PRINT_STYLEA(0,"FontName","黑体");
	LODOP.SET_PRINT_STYLEA(0,"FontSize",20);
	LODOP.SET_PRINT_STYLEA(0,"Bold",1);
//	var returnTop=rectTop2+208;
	var returnTop=lineBottom+5;
	//151
	//380
	var returnTop2=333;
//	86.62 
    var y1=returnTop+1;
    var pxY1 =returnTop+3;
	LODOP.ADD_PRINT_RECT(returnTop, marginHorizontal, 368, "52mm", 0, 1);
	 var jInfo_width=323;
	/* LODOP.ADD_PRINT_IMAGE(y1+"mm", x1, "7.01mm", "7.01mm","<img  width='23' width='23' src='"+dir+"寄(7mm).png'>");
	LODOP.ADD_PRINT_TEXT(y1+"mm", x2, jInfo_width, "10.32mm", to); */
	LODOP.ADD_PRINT_IMAGE(pxY1, x1, "7.01mm", "7.01mm","<img  width='23' width='23' src='"+dir+"寄(7mm).png'>");
	LODOP.ADD_PRINT_TEXT(pxY1, x2, jInfo_width, "10.32mm", from);
	var line2Top=returnTop+12;
	var line2Top2=returnTop;
	var line3Top=line2Top+26;
	var tempX=marginHorizontal+innerPadding ;
	var tempY=line2Top2+41 ;
	var w1=100;
	var minWidht=w1+20;
	var linDur=4;
	var tempH=20;
	LODOP.ADD_PRINT_LINE(tempY, marginHorizontal, tempY-1, marginHorizontal+lineWidth, 0, 1);
	LODOP.ADD_PRINT_TEXT(tempY,tempX, w1, tempH, "增值服务:");
	tempY+=tempH;
	//49
	tempH=40;
	w1=lineWidth-innerPadding*2;
	var line3Top2=line2Top2+120;
	LODOP.ADD_PRINT_TEXT(tempY, tempX, w1, line3Top2-tempY-innerPadding, "托寄物:"+note);
	
	w1=100;
	tempY+=tempH+linDur;
	tempH=20;
/* 	LODOP.ADD_PRINT_TEXT(tempY, tempX, w1, tempH, "计费重量:");
	tempX+=minWidht;
	LODOP.ADD_PRINT_TEXT(tempY,tempX, w1, tempH, "实际重量:");
	tempX+=minWidht;
	LODOP.ADD_PRINT_TEXT(tempY, tempX, w1, tempH, "费用合计:"); */
	LODOP.ADD_PRINT_LINE(line3Top2, marginHorizontal, line3Top2-1, marginHorizontal+lineWidth, 0, 1);
	//var isReturn=jdata.response.returnResponse.isReturn;
	//var isReturn=jdata.mSender.need_return_tracking_no;
	if("1"==isReturn){
		LODOP.ADD_PRINT_IMAGE(line3Top2,marginHorizontal+3, 100, 40,"<img height='35' title='条码' src='"+dir+"POD.jpg'>");
	}
	returnTop+=200;
	pxY1 =returnTop+3;
	var lineheight=227;
	if(dataobj.paperType=='150'){
		var barX=marginHorizontal+80;
		var tempy150=returnTop-70;
		LODOP.ADD_PRINT_BARCODE(tempy150, barX, 254, 40,"128B",decodeCode);
		LODOP.SET_PRINT_STYLEA(0,"ShowBarText",0);
		LODOP.ADD_PRINT_TEXT(tempy150+45, barX, 128, codeBottomHeight, finalCode2);
		LODOP.SET_PRINT_STYLEA(0, "FontName", "arial");
	}
	if(dataobj.paperType=='210'){
	//180需要的
	//210需要的
	var rect3Bottom=lineheight+returnTop;
	LODOP.ADD_PRINT_RECT(returnTop, marginHorizontal, lineWidth,lineheight, 0, 1);
	var bw1=80;
	var bBarCodeHeight=32;
	var bBarOffx=32;
	LODOP.ADD_PRINT_BARCODE(returnTop+3, marginHorizontal+3+bw1+bBarOffx, 250, bBarCodeHeight,"128B",decodeCode);
	LODOP.SET_PRINT_STYLEA(0,"ShowBarText",0);
	
	
	//热线.png,logo.png
	 var logX1=marginHorizontal+2;
	var logY1=returnTop+3;
	LODOP.ADD_PRINT_IMAGE(logY1, logX1, "0", "34","<img width='75' src='"+dir+"热线.png'>");
	logY1=logY1+32;
	LODOP.ADD_PRINT_IMAGE(logY1, logX1, "0", "34","<img width='75' src='"+dir+"logo.png'>");

	var bCodeTextHeight=17;
	
	var bCodeTextY= 5+returnTop+bBarCodeHeight;
/* 	LODOP.ADD_PRINT_TEXT(bCodeTextY, marginHorizontal+3+bw1 , 250, bCodeTextHeight, decodeCode);
	bCodeTextY+=bCodeTextHeight-5;
	LODOP.ADD_PRINT_TEXT(bCodeTextY, marginHorizontal+3+bw1 , 250,bCodeTextHeight, decodeCode);
	 */
	 var bTag=52;
	 var bbarTextX= marginHorizontal+3+bw1;
	 
	 //序号
	 LODOP.ADD_PRINT_TEXT(bCodeTextY, bbarTextX, 40, bCodeTextHeight,yundanIndex);
	 bbarTextX+=40;
	LODOP.ADD_PRINT_TEXT(bCodeTextY, bbarTextX, bTag, bCodeTextHeight, "母单号");
	LODOP.ADD_PRINT_TEXT(bCodeTextY, bbarTextX+bTag, 128, bCodeTextHeight,finalCode );
	
	LODOP.SET_PRINT_STYLEA(0, "FontName", "arial");
	if(j>=1){
		bCodeTextY+=bCodeTextHeight-5;
	
		LODOP.ADD_PRINT_TEXT(bCodeTextY, bbarTextX, bTag, bCodeTextHeight, "子单号");
		LODOP.ADD_PRINT_TEXT(bCodeTextY, bbarTextX+bTag, 128, bCodeTextHeight, finalCode2);
		LODOP.SET_PRINT_STYLEA(0, "FontName", "arial");
	}
	var bottomH1=64;
	LODOP.ADD_PRINT_LINE(returnTop, marginHorizontal+bw1, returnTop+bottomH1, marginHorizontal+bw1-1, 0, 1);
	returnTop+=bottomH1;
	
	pxY1 =returnTop+3;
	LODOP.ADD_PRINT_LINE(returnTop, marginHorizontal, returnTop-1, marginHorizontal+lineWidth, 0, 1);

	LODOP.ADD_PRINT_IMAGE(pxY1, x1, "7.01mm", "7.01mm","<img  width='23' width='23' src='"+dir+"寄(7mm).png'>");
	LODOP.ADD_PRINT_TEXT(pxY1, x2, jInfo_width, "10.32mm", from );
	
	returnTop+=44;
	pxY1 =returnTop+3;
	LODOP.ADD_PRINT_LINE(returnTop, marginHorizontal, returnTop-1, marginHorizontal+lineWidth, 0, 1);
	LODOP.ADD_PRINT_IMAGE(pxY1, x1, "7.01mm", "7.01mm","<img  width='23' width='23' src='"+dir+"收（7mm）.png'>");
	LODOP.ADD_PRINT_TEXT(pxY1, x2, jInfo_width, "10.32mm", to);
	


	returnTop+=44;
	pxY1 =returnTop+3;
	var xB2=marginHorizontal+100;
	//备注
	LODOP.ADD_PRINT_TEXT(returnTop+3,xB2+3,259,51,dataobj.note);
	//付款方式
	LODOP.ADD_PRINT_TEXT(returnTop+3,marginHorizontal,100,51,dataobj.payType);

	LODOP.ADD_PRINT_LINE(returnTop, marginHorizontal, returnTop-1, marginHorizontal+lineWidth, 0, 1);
	
	LODOP.ADD_PRINT_LINE(returnTop, xB2 ,rect3Bottom,xB2-1, 0, 1);
	returnTop+=30;
	LODOP.ADD_PRINT_LINE(returnTop, marginHorizontal, returnTop-1, xB2, 0, 1);
	}
	}
	

	//一维码生成
	function makeBarcode(barcodeId,mcodeHeight,decodeCode){
		JsBarcode("#"+barcodeId, decodeCode, {
			  format: "CODE128B",//选择要使用的条形码类型
			  width:1,//设置条之间的宽度
			  height:mcodeHeight,//高度
			  displayValue:false,//是否在条形码下方显示文字
			  text:"456",//覆盖显示的文本
			  fontOptions:"bold italic",//使文字加粗体或变斜体
			  font:"fantasy",
			  textMargin:0//设置文本的字体
			 // textAlign:"left",//设置文本的水平对齐方式
			 // textPosition:"top",//设置文本的垂直位置
			  //textMargin:5,//设置条形码和文本之间的间距
			 // fontSize:15,//设置文本的大小
			 // background:"#eee",//设置条形码的背景
			 // lineColor:"#2196f3",//设置条和文本的颜色。
			//  margin:15//设置条形码周围的空白边距
			 , margin:5
			});
	}
	function makeBarcode2(barcodeId,options,decodeCode){
		JsBarcode("#"+barcodeId, decodeCode, {
			  format: "CODE128B",//选择要使用的条形码类型
			  width:1,//设置条之间的宽度
			  height:options.height,//高度
			  displayValue:false,//是否在条形码下方显示文字
			  text:"456",//覆盖显示的文本
			  fontOptions:"bold italic",//使文字加粗体或变斜体
			  font:"fantasy",
			  textMargin:0//设置文本的字体
			 , margin:options.margin
			});
	}
	//二维码生成
	function makeQr(str,divId,size) {
		/* if (typeof qrcode == "undefined") {
			qrcode = new QRCode(document.getElementById(divId), {
				text : str,
				width :size.width,
				height : size.height,
				colorDark : "#000000",
				colorLight : "#ffffff",
		//	correctLevel : QRCode.CorrectLevel.H
			 	correctLevel: 3 
			});
		}
		qrcode.clear(); // clear the code.
		qrcode.makeCode(str);  */
		qrcode = new QRCode(document.getElementById(divId), {
			text : str,
			width :size.width,
			height : size.height,
			colorDark : "#000000",
			colorLight : "#ffffff",
		/* 	correctLevel : QRCode.CorrectLevel.H
		 */	correctLevel: 3 
		});
		qrcode.makeCode(str); 
	}
	function printHtml(){
//		var html=document.getElementById("myExportArea").outerHTML;
		var html=document.getElementById("myExportArea").outerHTML;
	     LODOP.PRINT_INITA(0,0,450,850,"Lodop_SF_HTML");
//LODOP.SET_PRINTER_INDEX("Microsoft XPS Document Writer");
LODOP.SET_PRINTER_INDEX("\\HAOLEI-PC\Microsoft XPS Document Writer");
	LODOP.SET_PRINT_PAGESIZE(1, '100mm', '210mm',"");
	var datas=$("#myExportArea .item");
	var dataLen=datas.length;
	for(var i=0;i<dataLen;i++){
		var data=datas[i];
		if(i>0){
			LODOP.NEWPAGE();
		}
		//LODOP.ADD_PRINT_HTM(0,0,"100mm","208mm",data.outerHTML);
		LODOP.ADD_PRINT_HTML(0,0,"100%","100%",data.outerHTML);
	}
	LODOP.PRINT_DESIGN();
/* if(dataobj.paperType=='210'){

}else if(dataobj.paperType=='180'){
	LODOP.SET_PRINT_PAGESIZE(1, "100mm", "180mm");
}else{
	LODOP.SET_PRINT_PAGESIZE(1, "100mm", "150mm");
} */
		
	}
</script>
<body>
	<h1>顺丰打印v3</h1>
	<div id="container">
		<div id="print_bar">
			<button onclick="preview()">预览</button>
			<button onclick="print()">打印</button>
			<!-- 	<button onclick="design()">设计</button>
			<button onclick="print(3)">单页</button> -->
			<div style="display: none;">
				<button onclick="exportPDF()">导出pdf</button>
				<button onclick="printHtml()">打印html</button>
			</div>
		</div>
		<div id="myExportArea" style="margin-top: 20px; width: 378px;">

			<!--列表li 模板-->
			<script type="text/html" id="row">
 <%-- {
			note:jdata.tuoji,  
			payType:jdata.payType,
			//付款方式
			//寄件 
	        j_addr:jdata.mSender.j_address,
			j_name:jdata.mSender.j_name,
		 	j_phone:jdata.mSender.j_tel,
				'<%=mData.mSender.j_tel%>',
			j_comp:jdata.mSender.j_company,
				'<%=mData.mSender.j_company%>',//寄件公司
			//收件
			r_addr:jdata.mSender.d_address,
				'<%=mData.mSender.d_address%>',
			r_name:jdata.mSender.d_name,
				'<%=mData.mSender.d_name%>',
			r_phone:jdata.mSender.d_tel,
				'<%=mData.mSender.d_tel%>',
			r_comp:jdata.mSender.d_company,
				'<%=mData.mSender.d_company%>',//收件公司
			codingMappingOut:jdata.response.HK_out,
				'<%=mData.response.HK_out%>',//出港信息码
			codingMapping:jdata.response.HK_in,
				'<%=mData.response.HK_in%>',//进港信息码
			destRouteLabel:jdata.response.destRouteLable,
				'<%=mData.response.destRouteLable%>',//目的地代号
			proCode:jdata.response.proCode,
				'<%=mData.response.proCode%>',//时效类型
			destTeamCode:jdata.response.destcode,
				'<%=mData.response.destcode%>',//取destTeamCode值，收件地址水印
			abFlag:jdata.response.destcode,
				'<%=mData.response.destcode%>',//根据abFlag值 
			isSpecial:jdata.isSpecial,
			paperType:jdata.yundanType,
			twoDimensionCode:jdata.response.qrInfo
			//二维码信息,接口返回twoDimensionCode字段
			}; --%>
		<div class="item" style="height: 780px;overflow: hidden;padding: 0px 2px;" >
		<style type="text/css"> 
		table td{
		padding: 1px 1px;
		margin:0px 0px;
		}

   .j_icon{
width: 23px;
    padding: 0 4px;
}
		</style>
				<table style="width: 374px;border-collapse:collapse;margin-top: 45px;">
					<tr>
						<td style="font-size: 12px">zj</td>
						<td colspan="2" style="font-size: 12px" ><div id="item_time">{{time}}</div></td>
					</tr>
					<tr>
						<td>{{index}}</td>
						<td style="font-size: 12px"><div style="width: 254px">
								<img id="head_bar{{tag}}"></img>
								<div style="margin-left: 20px;">
									<span>母单号&nbsp;</span>{{mainId}}
								</div>
								<div style="margin-left: 20px;">
									<span>子单号&nbsp;</span>{{tempCode}}
								</div>
							</div></td>
						<td rowspan="2"><img width="75px"
							src="{{proCodeImgUrl}}"></td>
					</tr>
					<tr>
						<td></td>
						<td></td>
					</tr>
				</table>
				<table style="width: 374px;border-collapse:collapse;margin-top: 2px;border: solid 1px red;">
					<tr >
						<td colspan="5" style="padding:0px 0px; border-bottom:solid 1px black;"><div
								style="width:359px; height: 46px; overflow: hidden; font-size: 37px;">{{destRouteLabel}}</div></td>
					</tr>
					<tr>
						<td style="padding:0px 0px; border-bottom:solid 1px black;">
<div  class="j_icon"  style="width: 23px; padding: 0 4px;">
<img width="23px" src="./imgs/sf/收（7mm）.png"></img> </div></td>
						<td colspan="4" style="padding:0px 0px; border-bottom:solid 1px black;"><div style="height: 57px;overflow: hidden;font-size: 12px;
    line-height: 14px;">{{to}}</div></td>
					</tr>
					<tr>

						<td colspan="2" style="border-bottom: solid 1px"><div style="width: 80px; overflow: hidden;">{{payType}}</div></td>
						<td id="tag_qr" rowspan="3" style="border-left: solid 1px"><div><div id="tag_qr_div{{tag}}" style="width:58px;padding: 4px 14px;"></div> </div></td>
						<td rowspan="3" style="border-left: solid 1px"><div style="width: 19px;font-size: 20px;">已验视</div></td>
						<td rowspan="1" style="border-bottom: solid 1px;border-left: solid 1px"><div style="width: 77px;"><img height="30px" src="{{abFlagImgUrl}}"></img></div></td>
					</tr>
					
					<tr>
						<td rowspan="2" colspan="2"  style="border-right: solid 1px ;font-size: 28px;" ><div style="height: 41px; overflow: hidden;">{{codingMapping}}</div></td>
						<td rowspan="2" style="border-left: solid 1px;font-size: 20px;"><div style="height: 41px; overflow: hidden;">{{codingMappingOut}}</div></td>
					</tr>
				</table>
				<table style="width: 374px;border-collapse:collapse;margin-top: 2px; border: solid 1px red;">
					<tr >
						<td style="border-bottom: solid 1px;"><div style="width: 23px; padding: 0 4px;">
								<img width="23px" src="./imgs/sf/寄(7mm).png"></img>
							</div></td>
						<td style="border-bottom: solid 1px;" colspan="4"><div
								style="height: 57px; overflow: hidden; font-size: 12px; line-height: 14px;">{{from}}</div></td>
					</tr>
					<tr >
						<td style="border-bottom: solid 1px;" colspan="5"><div style=" font-size: 12px; line-height: 14px;height: 70px; overflow: hidden;">托寄物:{{note}}</div></td>
					</tr>
					<tr >
						<td colspan="5"><div style="height: 65px; overflow: hidden;"><img src="{{podUrl}}"></div></td>
					</tr>

				</table>
				<table style="width: 374px;border-collapse:collapse;margin-top: 1px;border: solid 1px red;border-bottom:none;">
					<tr>
						<td colspan="1"><div>
								<img width="85px" src="./imgs/sf/logo.png">
							</div>
							<div>
							<img width="85px" src="./imgs/sf/热线.png"></div></td>
						<td  colspan="5">
						
						<img id="head2_bar{{tag}}"></img>
							<div style="    width: 277px;
							height: 32px; overflow: hidden;">
								 <ul  style="margin: 0px;
    font-size: 12px;
    list-style: none;"><li>母单号<span>&nbsp;{{mainId}}</span></li>
							<li>子单号<span>&nbsp;{{tempCode}}</span></li></ul></div></td>
					</tr></table>
				<table style="width: 374px;border: solid 1px red;">
					
					<tr>
						<td style="border-bottom: solid 1px;"><div style="width: 23px; padding: 0 4px;">
								<img width="23px" src="./imgs/sf/寄(7mm).png"></img>
							</div></td>
						<td style="border-bottom: solid 1px;" colspan="4"><div
								style="height: 40px; overflow: hidden; font-size: 12px; line-height: 14px;">{{from}}</div></td>
					</tr>
						<tr>
						<td  ><div style="width: 23px; padding: 0 4px;">
								<img width="23px" src="./imgs/sf/收（7mm）.png"></img>
							</div></td>
						<td    colspan="4"><div
								style="height: 40px; overflow: hidden; font-size: 12px; line-height: 14px;">{{to}}</div></td>
					</tr>
			
				</table>
						<table style="width: 374px;margin-top: 1px;border: solid 1px red;border-top: none;">
							<tr>
						<td  style="border-bottom: solid 1px" colspan="2">
<div style="height:15px;overflow: hidden;"></div></td>
						<td style="border-left: solid 1px"  rowspan="2"  colspan="3"><div style="width:283px;height: 40px; overflow: hidden; font-size: 12px; line-height: 14px;">{{note}}</div></td>
					</tr>
					<tr>
					<td  colspan="1"><div style="width:76px;overflow: hidden;font-size: 12px;">{{payType}}</div></td>
					</tr>
					</table>
			</div>
		</div>
</script>
		</div>
		<div id="lineArea"></div>
		<script type="text/javascript">
	</script>
</body>


</html>