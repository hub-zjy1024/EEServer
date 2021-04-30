<%@page import="b1b.erp.js.bussiness.LodopLoadder"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>顺丰v4打印html</title>
<%
	String url="";
	String sPrinter="";
	String kfName=request.getParameter("kfName");
	LodopLoadder.PrintInfo pInfo=LodopLoadder.readLodopUrlBy(kfName);
	url=pInfo.url;
	sPrinter=pInfo.printerName;
	if(!"".equals(url)&&url!=null){
		%>
		<script type="text/javascript"
				src="<%=url %>"></script><% 
	}
	%>
<script type="text/javascript"
	src="./lodop/LodopFuncs.js?priority=1"></script>
<style type="text/css">
.papers input {
	margin-left: 10px;
	width: 30px;
	height: 30px;
}

.toolbar input, button {
	margin-left: 10px;
	font-size: 25px;
	padding: 2px 10px;
}

#sampleData>div {
	float: left;
}
</style>
</head>
<body>
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
		//print();
	}else{
		needInstallCLodop();
		alert("当前未安装lodop插件");
	}
}
</script>
	<script type="text/javascript" src="./lodop/LodopFuncs.js?priority=1"></script>
	<script type="text/javascript" src="./lodop/jquery-3.3.1.min.js"></script>

	<script type="text/javascript">
		function kuayue(type) {
			var pagerType="150";
			if(type==1){
				pagerType="210";
			}
			 var val=$('input[name="paper"]:checked').val(); 
			 pagerType=val;
			$.ajax({
				url : "./SftestServlet?yundanType="+pagerType+"&data=<%=request.getParameter("data")%>",
				method : "get",
				success : function(data) {
					var jsonData = JSON.parse(data);
					/* 	public int errCode = 1;
						public String errMsg = "未知错误"; */
					if (jsonData.errCode == 0) {
						var htmls = jsonData.data[4];
						//console.log("htmls=" + htmls);
						for (var i = 0; i < htmls.length; i++) {
							$("#sampleData").append(htmls[i]);
						}
					} else {
						var errMsg = jsonData.errMsg;
						alert("获取数据异常：" + errMsg);
					}
				},
				error : function(evt) {
					alert("生成打印模版失败，" + evt.message);
				}
			});
		}
		function startDesign(type) {
			console.log("startDesign");
			var pagerType="150";
			if(type==1){
				pagerType="210";
			}
			 var val=$('input[name="paper"]:checked').val(); 
			 pagerType=val;
			$.ajax({
				url : "./SftestServlet?yundanType="+pagerType+"&data=<%=request.getParameter("data")%>",
				method : "get",
				success : function(data) {
					var jsonData = JSON.parse(data);
					/* 	public int errCode = 1;
						public String errMsg = "未知错误"; */
					if (jsonData.errCode == 0) {
						var htmls = jsonData.data[4];
						//console.log("htmls=" + htmls);
						for (var i = 0; i < htmls.length; i++) {
							$("#sampleData").append(htmls[i]);
						}
					} else {
						var errMsg = jsonData.errMsg;
						alert("获取数据异常：" + errMsg);
					}
				},
				error : function(evt) {
					alert("生成模版失败," + evt.message);
				}
			})
		}
		function startPrint(mode) {
			LODOP.PRINT_INITA(0, 0, 450, 850, "Lodop_SF_V4");
			console.log("startPrint");
			var html = document.getElementById("sampleData").innerHTML;
			//LODOP.SET_PRINTER_INDEX("\\HAOLEI-PC\Microsoft XPS Document Writer");
			//LODOP.SET_PRINT_PAGESIZE(1, '100mm', '210mm',"");
			var datas = $("#sampleData > .item");
			var dataLen = datas.length;
			if (dataLen > 10) {
				LODOP.PRINT_INITA(8, 10, 450, 850, "Lodop_SF_V4_YZPY");
				LODOP.ADD_PRINT_HTM(0, 0, "100%", "100%", html);
			} else if (dataLen == 0) {
				LODOP.PRINT_INITA(5, 5, 450, 850, "Lodop_SF_V4_YZBK");
				LODOP.SET_PRINT_PAGESIZE(1, '100mm', '150mm', "");
				LODOP.ADD_PRINT_HTM(0, 0, "100%", "100%", html);
			} else { 
				
				//LODOP.SET_PRINT_PAGESIZE(1, '100mm', '220mm', "");
				for (var i = 0; i < dataLen; i++) {
					var data = datas[i];
					if (i > 0) {
						LODOP.NEWPAGE();
					}
					//LODOP.ADD_PRINT_HTM(0,0,"100mm","208mm",data.outerHTML);
					//LODOP.ADD_PRINT_HTM(0, 0, "100%", "100%", data.outerHTML);
	//				LODOP.ADD_PRINT_HTM(0, 0, "100mm", "210mm", data.outerHTML);
	//console.log("tempHtml="+data.outerHTML.substr(0,1000));
					LODOP.ADD_PRINT_HTM(0, 0, "100%", "100%", data.outerHTML);
					var code = "{'k1':'010W','k2':'010LC','k3':'042','k4':'T6','k5':'SF1040190348359','k6':'','k7':'729b530c'}";
					//			LODOP.ADD_PRINT_BARCODEA(0,0,100,100,"Code128B",code);
					/* LODOP.ADD_PRINT_BARCODE(471, 14, 100, 100,"QRCode",code);
					LODOP.SET_PRINT_STYLEA(0,"ShowBarText",0);
					 */
				}
			}
			
			LODOP.On_Return=function(data){
				console.log("out msg="+JSON.stringify(data));
			}
			if (mode == 1) {
				LODOP.PRINT()
			} else if (mode == 2) {
				LODOP.PREVIEW()
				//LODOP.PRINT_DESIGN();
			} else if (mode == 3) {
				LODOP.PRINT_DESIGN();
			}
		}
		function testP() {
			var html = "<style> .item{ width:500px;}</style><div class=\"item\" style=\"height: 780px; width: 374px;overflow:hidden;padding: 0px 2px;\">\t<style type=\"text/css\">\t\ttable td{padding:1px 1px;margin:0px 0px} .j_icon{width:23px;padding:0 4px}\t</style><div style=\"height: 32px;padding: 5px;\"><img width=\"85px\" style=\"float: right;\" src=\"http://oa.t996.top:6060/PrinterServer/imgs/sf/%E7%83%AD%E7%BA%BF.png\"></div> \t<table style=\"width: 374px;border-collapse:collapse;border: 1px solid;\">\t\t<tr>\t\t\t<td style=\"font-size: 12px\">zj</td>\t\t\t<td colspan=\"2\" style=\"font-size: 12px\">\t\t\t\t<div id=\"item_time\">2020-10-27 15:04:31</div>\t\t\t</td>\t\t</tr>\t\t<tr>\t\t\t<td>1/1</td>\t\t\t<td style=\"font-size: 12px\">\t\t\t\t<div style=\"width: 254px\">\t\t\t\t\t<img id=\"head_bar0\" height=\"40px\" width=\"200px\" src=\"data:image/png;base64,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\"> </img>\t\t\t\t\t\t\t<div style=\"margin-left: 20px;margin-top: 2px;\">\t\t\t\t\t<span> 母单号&nbsp; </span> SF 104 326 478 8022 \t\t\t\t\t</div>\t\t\t\t\t<div style=\"margin-left: 20px;height: 16px;\">\t\t\t\t\t<div style=\"display: none;\">\t<span> 子单号&nbsp; </span> SF 104 326 478 8022 </div>\t\t\t\t\t</div>\t\t\t\t</div>\t\t\t</td>\t\t\t<td rowspan=\"2\"><img width=\"60px\" src=\"http://oa.t996.top:6060/PrinterServer/imgs/sf/20_20%20T4.png\">\t\t\t</td>\t\t</tr>\t\t<tr>\t\t\t<td></td>\t\t\t<td></td>\t\t</tr>\t</table>\t<table style=\"width: 374px;border-collapse:collapse;margin-top: 2px;border:solid 1px black;\">\t\t<tr>\t\t\t<td colspan=\"5\" style=\"padding:0px 0px;\">\t\t\t\t<div style=\"padding: 1px 5px;width:359px; height: 40px; overflow:hidden; font-size: 37px;\">\t\t\t\t\t512WR-TL-029</div>\t\t\t</td>\t\t</tr>\t\t<tr>\t\t\t<!-- <td style=\"padding:0px 0px; border-bottom:solid 1px black;\">\t\t\t\t<div class=\"j_icon\" style=\"width: 23px; padding: 0 4px;\">\t\t\t\t\t<img width=\"23px\" src=\"http://oa.t996.top:6060/PrinterServer/imgs/sf/%E6%94%B6%EF%BC%887mm%EF%BC%89.png\"> </img>\t\t\t\t</div>\t\t\t</td>\t\t\t<td colspan=\"4\" style=\"padding:0px 0px; border-bottom:solid 1px black;\">\t\t\t\t<div style=\"height: 57px;overflow:hidden;font-size: 12px;line-height: 14px;\">\t\t\t\t\t信经理 199****0261  浙江迈雷科技有限公司 苏州工业园区苏虹中路77号208室西单元，208室中单元-1室</div>\t\t\t</td> -->\t\t\t\t\t\t\t\t <td colspan=\"5\" style=\"padding:0px 0px; border-bottom:solid 1px black;\">\t\t\t\t<div class=\"j_icon\" style=\"margin-left:1px;margin-top:3px; width: 23px; padding: 0 4px;float: left;\">\t\t\t\t\t<img width=\"32px\" src=\"http://oa.t996.top:6060/PrinterServer/imgs/sf/%E6%94%B6%EF%BC%887mm%EF%BC%89.png\"> </img>\t\t\t\t</div>\t\t\t \t<div style=\"word-break: break-word;width: 327px;margin-left:45px;height: 43px;overflow:hidden;font-size: 12px;line-height: 14px;\">\t\t\t\t\t信经理 199****0261  浙江迈雷科技有限公司 苏州工业园区苏虹中路77号208室西单元，208室中单元-1室</div>\t\t\t\t\t<div style=\"font-size: 39px; float: left; margin-top: -50px;margin-left: 129px;\">512WR </div>\t\t\t</td>\t\t</tr>\t\t<tr>\t\t\t<td colspan=\"2\" style=\"border-bottom:solid 1px\">\t\t\t\t<div style=\"width: 100px;height:44px; overflow:hidden;\">寄货方</div>\t\t\t</td>\t\t\t<td id=\"tag_qr\" rowspan=\"3\" style=\"border-left:solid 1px ;text-align: center;\">\t\t\t\t<div>\t\t\t\t<!-- \t<div id=\"tag_qr_div0\" style=\"width:58px;padding: 4px 14px;\">\t\t\t\t\t</div> -->\t\t\t\t\t<img id=\"head2_bar0\" style=\"    padding: 2px;\"  width=\"120px\" src=\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAHoAAAB6CAYAAABwWUfkAAAEq0lEQVR42u2dwZbrKgwE8/8//d7+bhLU1XgM5XNmkVk4mHKE1BLi85/XFdfHKRC0l6C9Xg/68/mM/r7d59///zzAH7/31/9/G8/0eb7Nx6/PVeMh6MtBr4KITcviC/HrhK/e51cw6XhWX7CUh6AFPTN9qclLTfv0RVpdEqame3r/lIegBc06M+37U87MdMlJlwhBC/odpnv6wJQpn4JuhZOpMyZoQT8bXqWmPTWh9IvVnp/XxtGC/uOgU5PnZ/bzn9O6/fwS0FharCRJ0hLi1Bmbhnm7JGVBC5qVLKfhxnRCaJO3S1JdlUTHTpqgLwe9CoISJFpOTEu4aUmnlOQsaEGzaTnKJK1KhLQkSiVRWs7n1/sIWtCR8zV9gDQ8SyeoldShkivbJVBBHwo6NY1peEGV6FDfNw1L06WobroFfSjosYkoF8CnS8Lq0kCZUjrM21bAL+hDQU+dMDr8SSeklUygTTaeLBH0paCp5AQlrdLCSdt5osKs6Qs1LjwQ9OGgW+WnFPBxmAFtk90Vnk3HJWhBd5yDaQFDa9NaGl6lSwYVbi6bfEFfCno60F1JDDrsSsMteh4o51jQgmZMdioZUk5WKjG2wjxKMq1vshP0IaDT5AFdRDdNb9Llya2wZ7rELAtWgr4UdBr2UO2ZaCdlCjoVhiiBKXZKBX056NSEpmEFlUShncunTH4cvgpa0KiQQQkhNOCnSpeo9Gq9K5GgDwMdJ7yhjQDtRnYUaKqtFd0mW9C3gqYLBKimrFMBglqC6Ib1lCCEFx4I+jDQ7QlohVHpOKkXJ30++gUStKD/hgBBhV10YQJdYNFOEwta0J0mMnQBPW06qXRjCo5uyiPo20HTUh+9eY0qNJiGcfRBadtKpgQt6GpYlQonlFDSbjFBHWUYF2YIWtDIRKemut2MldoQkIZv9GGrghY0M8CpSJ+GTVOnkg7bWsWT6Q9L0ILuHD24q2E7PaG0JNsqBqzt1BD0y0HTifzpgFvA2gJNq7XF+Icp6EtBtw8y25UGbZUL001qW8WTghZ0JghQ4c2uMCsFSztXaXJkObwS9CWg40UfFhp2Na1ppzepsAsXTAR9OOj2ISmt9lYUkJaJbSdJlgUTQV8GOjUpU1NOJSlawkbbaUqlzmXTLejDQVMmDEuYw1IndZhqq9SodaiLoAXNHNVHO1ctwaMthdJAseyVoA8HTbdRfipNR4+vLSRRTlmtRaSgXwp6Gk60DyGhTNquNtPtw1hrSQ1BHwq6dWgo9cDt++MTXd5OjHndgj4UdNuZaLWipF8ASmihlphxskbQl4JuCwrpRKZhFbVpsJ3UoZ9L0LeDpg/zoI8ofCososMyaqkY56MFfThoWvp7+v502jH9YVBp0uUXU9CCriQp2kJMe2tM6xjj7cWBgr4E9K42yCmglvOVjjsVUOhmPYIW9DPpOkp4oA9VqTVKL4WXghb07MG3BfqltCItvFCmtiY0CVrQCOjWEQutwz+nJpjaCE8nQwQt6I4z1ZJM6Y3ju55nd3mzoAXNhleUc7P6PbQ0+nhyYmrSBX056DS9Rx8Utut4XqoVBy2VTpM/gr4dtNeZl6AF7SVor9dd/wPZaXMsHooOEAAAAABJRU5ErkJggg==\"> \t\t\t\t</div>\t\t\t</td>\t\t\t<td rowspan=\"3\" style=\"border-left:solid 1px\">\t\t\t\t<div style=\"width: 19px;font-size: 20px;\">已验视</div>\t\t\t</td>\t\t\t<td rowspan=\"1\" style=\"border-bottom:solid 1px;border-left:solid 1px\">\t\t\t\t<div style=\"width: 77px;\">\t\t\t\t\t<img height=\"30px\" src=\"http://oa.t996.top:6060/PrinterServer/imgs/sf/blank.bmp\"> </img>\t\t\t\t</div>\t\t\t</td>\t\t</tr>\t\t<tr>\t\t\t<td rowspan=\"2\" colspan=\"2\" style=\"border-right:solid 1px ;font-size: 28px;\">\t\t\t\t<div style=\"width: 100px;height:70px; overflow:hidden;\">\t\t\t\t\tS7</div>\t\t\t</td>\t\t\t<td rowspan=\"2\" style=\"border-left:solid 1px;font-size: 20px;\">\t\t\t\t<div style=\"height: 41px; overflow:hidden;\">\t\t\t\t\t</div>\t\t\t</td>\t\t</tr>\t</table>\t<table style=\"width: 374px;border-collapse:collapse;margin-top: 5px; border:solid 1px black;\">\t\t<tr>\t\t\t<td style=\"border-bottom:solid 1px;\">\t\t\t\t<div style=\"padding: 0 4px;\">\t\t\t\t\t<img width=\"32px\" src=\"http://oa.t996.top:6060/PrinterServer/imgs/sf/%E5%AF%84%287mm%29.png\"> </img>\t\t\t\t</div>\t\t\t</td>\t\t\t<td style=\"border-bottom: solid 1px;\" colspan=\"4\">\t\t\t\t<div style=\"height: 57px; word-break: break-word;width: 327px;overflow:hidden; font-size: 12px; line-height: 14px;\">\t\t\t\t\t宇晓兵 150****2836  北京北方科讯电子技术有限公司 北京市海淀区彩和坊路10号1号楼503室</div>\t\t\t</td>\t\t</tr>\t\t<tr>\t\t\t<td style=\"border-bottom: solid 1px;\" colspan=\"5\">\t\t\t\t<div style=\"float: right;\"><img width=\"23px\" src=\"http://oa.t996.top:6060/PrinterServer/imgs/sf/blank.bmp\"></div>\t\t\t\t<div style=\" margin:2px;font-size: 12px; line-height: 14px;height: 70px; overflow:hidden;\">\t\t\t\t\t托寄物:重量:0    集成电路 TLP350(TP1,F):350,2020/10/27 15:04:31_1548462_0</div>\t\t\t</td>\t\t</tr>\t\t<tr>\t\t\t<td colspan=\"5\">\t\t\t\t<div style=\"height: 65px; overflow:hidden;\">\t\t\t\t\t<img src=\"http://oa.t996.top:6060/PrinterServer/imgs/sf/blank.bmp\">\t\t\t\t</div>\t\t\t</td>\t\t</tr>\t</table>\t<table style=\"width: 374px;border-collapse:collapse;margin-top: 5px;border:solid 1px black;border-bottom:none;\">\t\t<tr>\t\t\t<td colspan=\"1\">\t\t\t\t<div>\t\t\t\t\t<img width=\"85px\" src=\"http://oa.t996.top:6060/PrinterServer/imgs/sf/logo.png\">\t\t\t\t</div>\t\t\t\t<div>\t\t\t\t\t<img width=\"85px\" src=\"http://oa.t996.top:6060/PrinterServer/imgs/sf/%E7%83%AD%E7%BA%BF.png\">\t\t\t\t</div>\t\t\t</td>\t\t\t<td colspan=\"5\"><img id=\"head2_bar0\" height=\"30px\" width=\"200px\" src=\"data:image/png;base64,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\"> </img>\t\t\t\t<div style=\"    width: 277px;\t\t\t\theight: 32px; overflow:hidden;\">\t\t\t\t\t<ul style=\"margin: 0px;\t\tpadding: 0 5px;\t\t\tfont-size: 12px;\t\t\t\t\tlist-style:none;\">\t\t\t\t\t\t<li>母单号 <span> &nbsp;SF 104 326 478 8022  </span>\t\t\t\t\t\t</li>\t\t\t\t\t\t<li><div style=\"height: 16px;\">\t\t\t\t\t<div style=\"display: none;\">\t<span> 子单号&nbsp; </span> SF 104 326 478 8022 </div>\t\t\t\t\t</div>\t\t\t\t\t\t</li>\t\t\t\t\t</ul>\t\t\t\t</div></td>\t\t</tr>\t</table>\t<table style=\"width: 374px;border-collapse:collapse;border:solid 1px black;\">\t\t<tr>\t\t\t<td style=\"border-bottom:solid 1px;\">\t\t\t\t<div style=\"padding: 0 4px;\">\t\t\t\t\t<img width=\"32px\" src=\"http://oa.t996.top:6060/PrinterServer/imgs/sf/%E5%AF%84%287mm%29.png\"> </img>\t\t\t\t</div>\t\t\t</td>\t\t\t<td style=\"border-bottom: solid 1px;\" colspan=\"4\">\t\t\t\t<div style=\"height: 40px; word-break: break-word;width: 322px;overflow:hidden; font-size: 12px; line-height: 14px;\">\t\t\t\t\t宇晓兵 150****2836  北京北方科讯电子技术有限公司 北京市海淀区彩和坊路10号1号楼503室</div>\t\t\t</td>\t\t</tr>\t\t<tr>\t\t\t<td>\t\t\t\t<div style=\"padding: 0 4px;\">\t\t\t\t\t<img width=\"32px\" src=\"http://oa.t996.top:6060/PrinterServer/imgs/sf/%E6%94%B6%EF%BC%887mm%EF%BC%89.png\"> </img>\t\t\t\t</div>\t\t\t</td>\t\t\t<td colspan=\"4\">\t\t\t\t<div style=\"height: 40px;word-break: break-word;width: 322px; overflow:hidden; font-size: 12px; line-height: 14px;\">\t\t\t\t\t信经理 199****0261  浙江迈雷科技有限公司 苏州工业园区苏虹中路77号208室西单元，208室中单元-1室</div>\t\t\t</td>\t\t</tr>\t</table>\t<table style=\"width: 374px;border-collapse:collapse;margin-top: 1px;border:solid 1px black;border-top:none;\">\t\t<tr>\t\t\t<td style=\"border-bottom: solid 1px\" colspan=\"2\">\t\t\t\t<div style=\"height:15px;overflow:hidden;\"></div>\t\t\t</td>\t\t\t<td style=\"border-left: solid 1px\" rowspan=\"2\" colspan=\"3\">\t\t\t\t<div>\t\t\t\t\t<div style=\"float: left; width:250px;height: 40px; overflow:hidden; font-size: 12px; line-height: 14px;\">\t\t\t\t\t重量:0    集成电路 TLP350(TP1,F):350,2020/10/27 15:04:31_1548462_0</div>\t\t\t\t\t<div\t\t\t\t\tstyle=\"width: 30px; height: 30px; overflow: hidden;\">\t\t\t\t\t<img width=\"23px\" src=\"http://oa.t996.top:6060/PrinterServer/imgs/sf/blank.bmp\">\t\t\t\t</div>\t\t\t\t</div>\t\t\t\t\t\t</td>\t\t</tr>\t\t<tr>\t\t\t<td colspan=\"1\">\t\t\t\t<div style=\"width:76px;overflow:hidden;font-size: 12px;\">\t\t\t\t\t寄货方</div>\t\t\t</td>\t\t</tr>\t</table></div>";
			LODOP.PRINT_INITA(5, 5, 450, 850, "Lodop_SF_V4_YZBK"); 
			LODOP.ADD_PRINT_TEXT(10, 10, 100, 20, "hdhfhsdfsdf");
			LODOP.SET_PRINT_PAGESIZE(1, '100mm', '210mm', "");
			LODOP.ADD_PRINT_HTM(0, 0, "100%", "100%", html);
			LODOP.PRINT_DESIGN();
			//LODOP.PREVIEW();
		}
	</script>
	<div style="position: relative; z-index: 10000;">
		<div class="toolbar">
			<button onclick="startDesign()">生成</button>
			<button onclick="startPrint(2)">预览</button>
			<button onclick="startPrint(1)">打印</button>
				<button onclick="testP()">测试</button>
			<!-- <button onclick="startPrint(3)">设计</button>
		
			<button onclick="kuayue()">测试ky</button> -->
		</div>

		<div class="papers">
			<h2>纸张尺寸</h2>
			<input name="paper" type="radio" value="210" checked="checked">210<input
				name="paper" value="180" type="radio">180<input name="paper"
				value="150" type="radio">150
		</div>
	</div>
	<div id="sampleData" style="margin-top: 20px;"></div>
</body>
</html>