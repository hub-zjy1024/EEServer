package b1b.erp.js.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 Created by 张建宇 on 2017/2/21.
 主要是一些路径的获取 */

public class UploadUtils {
    public static String KF_DIR = "/Zjy/kf/";
    public static String CG_DIR = "/Zjy/caigou/";

    public static String getPankuRemoteName(String id) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return "a_" + id + "_" + sdf.format(new Date());
    }

    public static String getChukuRemoteName(String id) {
        String flag = String.valueOf(Math.random());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String date = sdf.format(new Date());
        return "and_" + id + "_" + date + "_" + flag.substring(2, 6);
    }
    public static String getTimeYmdhms() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String date = sdf.format(new Date());
        return date;
    }

    public static String getCurrentAtSS() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }

    public static String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        String str = calendar.get(Calendar.YEAR) + "_" + (calendar.get(Calendar.MONTH) + 1) + "_" + calendar.get(Calendar
                .DAY_OF_MONTH);
        return str;
    }

    public static String getCurrentYearAndMonth() {
        Calendar calendar = Calendar.getInstance();
        String str = calendar.get(Calendar.YEAR) + "_" + (calendar.get(Calendar.MONTH) + 1);
        return str;
    }

    public static String getCurrentDay() {
        Calendar calendar = Calendar.getInstance();
        String str = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        return str;
    }

    public static String getCaigouRemoteDir(String fileName) {
        return "/" + getCurrentDate() + "/" + fileName;
    }


    /**
     @param ftpUrl
     @param path
     @return 插入到数据库的图片地址
     */
    public static String createInsertPath(String ftpUrl, String path) {
        StringBuilder builder = new StringBuilder();
        builder.append("ftp://");
        builder.append(ftpUrl);
        builder.append(path);
        return builder.toString();
    }

    /**
     @return 插入到数据库的图片地址
     */
    public static String createSCCGRemoteName(String pid) {
        StringBuilder builder = new StringBuilder();
        builder.append("SCCG_a_");
        builder.append(pid);
        builder.append("_" + System.currentTimeMillis());
        return builder.toString();
    }

}
