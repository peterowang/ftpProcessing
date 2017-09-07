package com.bfd.ftp.common;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by BFD-593 on 2017/9/5.
 */
public class CheckDate {
    private static final Logger LOGGER = Logger.getLogger(CheckDate.class);
    /*判断文件名中的日期是否是输入日期*/
    public static boolean isInputDay(String filename,String dateStr){
        Date date1 = split(filename);
        if(dateStr.equals("all")){
            return true;
        }
        if(StringUtils.isEmpty(dateStr)){
            LOGGER.info("判断文件日期是否为当天的文件,filename=" + filename);
            return isOrNot(date1, null,filename);
        }
        if(!dateStr.matches("^[0-9]{8}$")){
            LOGGER.warn("输入的日期参数不合法,请输入如:20170906");
            return false;
        }
        Date date2 = format(dateStr);
        LOGGER.info("判断文件日期是否为指定日期的文件,filename=" + filename);
        return isOrNot(date1, date2, filename);
    }
    /*获取文件中的日期字符串*/
    private static Date split(String filename){
        if(StringUtils.isEmpty(filename)){
            LOGGER.warn("要格式化日期的文件名为空:filename="+filename);
            return null;
        }
        int i = filename.lastIndexOf("_");
        String str = filename.substring(i + 1, filename.length()).substring(0, 8);
        return format(str);
    }
    /*字符串转日期*/
    private static Date format(String dateStr){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Date date = null;
        try {
            date = format.parse(dateStr);
        } catch (ParseException e) {
            LOGGER.error("转化日期格式出错:" + dateStr + ",message:" + e.toString());
            e.printStackTrace();
        }
        return date;
    }

    /*判断date1是否等于date2*/
    private static boolean isOrNot(Date date1, Date date2, String filename) {
        if (date1 == null) {
            LOGGER.warn("找不到文件名中的日期:" + filename);
            return false;
        }
        Calendar c1 = Calendar.getInstance();
        c1.setTime(date1);
        int year1 = c1.get(Calendar.YEAR);
        int month1 = c1.get(Calendar.MONTH);
        int day1 = c1.get(Calendar.DAY_OF_MONTH);
        Calendar c2 = Calendar.getInstance();
        int year2,month2,day2;
        if(date2==null){
            c2.setTime(new Date());
            year2 = c2.get(Calendar.YEAR);
            month2 = c2.get(Calendar.MONTH);
            day2 = c2.get(Calendar.DAY_OF_MONTH)-1;
        }else{
            c2.setTime(date2);
            year2 = c2.get(Calendar.YEAR);
            month2 = c2.get(Calendar.MONTH);
            day2 = c2.get(Calendar.DAY_OF_MONTH);
        }
        return ((year1 == year2) && (month1 == month2) && (day1 == day2)) ? true : false;
    }

}
