package b1b.erp.js.yundan.sf.entity;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
/*import com.b1b.js.erpandroid_kf.BuildConfig;
import com.b1b.js.erpandroid_kf.MyApp;
import com.b1b.js.erpandroid_kf.yundan.sf.sfutils.SFWsUtils;
import com.sun.mail.imap.protocol.UID;*/

import b1b.erp.js.yundan.sf.jee.Log;
import b1b.erp.js.yundan.sf.sfutils.DyjInterface2;
import b1b.erp.js.yundan.sf.sfutils.SFWsUtils;

/**
 * Created by 张建宇 on 2020/4/29.
 */

public class SFPrintWrapper {
    public SFSender mSender;
    public SFWsUtils.OrderResponse response;
    public String goodInfos;
    public String payType;
    public String jComapany;
    public String dCompany;
    public String pid;
    public String uid, uname;

    public List<Cargo> cargos;

    public static String LOCAL_SERVER =  "192.168.10.66";
    public String serverIP;

    public SFPrintWrapper(String serverIP) {
        this.serverIP = serverIP;
    }

    public SFPrintWrapper(String uid, String uname, String serverIP) {
        this.uid = uid;
        this.uname = uname;
        this.serverIP = serverIP;
    }

    public void startOrder( ) throws Exception {
        SFWsUtils.OrderResponse orderResponse = SFWsUtils.getOrderResponseV2(mSender,
                cargos, null);
//        if (BuildConfig.DEBUG) {
//            Log.d("zjy", "SFPrintWrapper->startOrder():retObject=" + JSONObject.toJSONString(orderResponse));
//        }
        //日志记录
        try{
            DyjInterface2.SetRequestLog(pid, uid, uname, orderResponse.inputXml, orderResponse.retXml, "出库");
        }catch (Exception e ){
            e.printStackTrace();
        }
        //日志记录
        if (orderResponse.HK_in == null) {
            orderResponse.HK_in = "";
        }
        if (orderResponse.HK_out == null) {
            orderResponse.HK_out = "";
        }
        if (orderResponse.proCode == null) {
            orderResponse.proCode = "";
        }
        response = orderResponse;
    }


    public void print(String yundanType) throws IOException {
        long time1 = System.currentTimeMillis();
        if (response == null) {
            throw new IOException("请先下单后再进行打印");
        }
        String orderID = response.yundanId;
//        String returnOrder = response.return_tracking_no;
        String ip = "http://" + serverIP + ":8080";
        String urlCoding = "UTF-8";
        String strURL = ip + "/PrinterServer/V2/SFPrintServlet?";
        strURL += "orderID=" + URLEncoder.encode(response.yundanId,
                urlCoding);
        strURL += "&proCode=" + URLEncoder.encode(response.proCode,
                urlCoding);
        strURL += "&HK_in=" + URLEncoder.encode(response.HK_in,
                urlCoding);
        strURL += "&HK_out=" + URLEncoder.encode(response.HK_out,
                urlCoding);
        strURL += "&qr_code=" + URLEncoder.encode(response.qrInfo,
                urlCoding);
        strURL += "&goodinfos=" + URLEncoder.encode(goodInfos,
                urlCoding);

        if(mSender.custid!=null){
            strURL += "&cardID=" + URLEncoder.encode(mSender.custid,
                    urlCoding);
        }
        strURL += "&payType=" + URLEncoder.encode(payType,
                urlCoding);
        //        strURL += "&serverType=" + URLEncoder.encode(serverType,
        //                urlCoding);
        strURL += "&j_name=" + URLEncoder.encode(mSender.j_name,
                urlCoding);
        strURL += "&j_phone=" + URLEncoder.encode(mSender.j_tel,
                urlCoding);
        strURL += "&j_address=" + URLEncoder.encode(mSender.j_address,
                urlCoding);
        strURL += "&j_company=" + URLEncoder.encode(jComapany,
                urlCoding);
        strURL += "&destRouteLable=" + URLEncoder.encode(response.destRouteLable,
                urlCoding);

        strURL += "&d_name=" + URLEncoder.encode(mSender.d_name,
                urlCoding);
        strURL += "&d_phone=" + URLEncoder.encode(mSender.d_tel,
                urlCoding);

        strURL += "&d_address=" + URLEncoder.encode(mSender.d_address,
                urlCoding);

        strURL += "&d_company=" + URLEncoder.encode(dCompany,
                urlCoding);
        strURL += "&pid=" + URLEncoder.encode(pid,
                urlCoding);
        strURL += "&yundanType=" + URLEncoder.encode(yundanType,
                urlCoding);
        if(response.returnResponse==null){

        }else{
//            strURL += "&returnOrder=" + URLEncoder.encode(returnOrder,
//                    urlCoding);
            String mData = JSONObject.toJSONString(response.returnResponse);
//            Log.w("zjy", "SFPrintWrapper->print(): sendReturnData==" +mData );
            strURL += "&returnOrderData=" + URLEncoder.encode(mData,
                    urlCoding);
        }
        Log.d("zjy", "SFPrintWrapper->startPrint(): StrUrl==" + strURL);
        URL url = new URL(strURL);
        HttpURLConnection conn = (HttpURLConnection) url
                .openConnection();
        conn.setConnectTimeout(20 * 1000);
        InputStream in = conn.getInputStream();
        StringBuilder builder = new StringBuilder();
        String s = "";
        BufferedReader reader = new BufferedReader(new
                InputStreamReader(in, "UTF-8"));
        while ((s = reader.readLine()) != null) {
            builder.append(s);
        }
        String res = builder.toString();

        double len = (double) (System.currentTimeMillis() - time1) / 1000;
        Log.d("zjy", "SFPrintWrapper->run(): print_result==" + builder
                .toString());
//        MyApp.myLogger.writeInfo("SF yundan" + orderID + "\ttime:" + len);
        if (res.equals("ok")) {

        } else {
            String[] errors = res.split(":");
            if (errors.length == 2) {
                String errMs = errors[1];
                throw new IOException(errMs);
            } else {
                throw new IOException("返回异常");
            }
        }
    }
}
