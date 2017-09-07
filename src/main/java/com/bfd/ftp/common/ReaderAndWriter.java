package com.bfd.ftp.common;

import com.bfd.ftp.utils.Constants;
import com.bfd.ftp.utils.ReaderProperties;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.Iterator;
import java.util.List;

/**
 * Created by BFD-593 on 2017/9/5.
 * 获取到文件名后  重写每一个文件
 */
public class ReaderAndWriter {
    private static final Logger LOGGER = Logger.getLogger(ReaderAndWriter.class);
    private static int count = 0 ;
    public static void reload(List<String> filenames,String date,Boolean delFlag) {
        FileInputStream fis = null;
        InputStreamReader isr = null;
        LineNumberReader lnr = null;
        if(filenames!=null&&filenames.size()>0){
            LOGGER.info("csv文件List不为空...准备进行处理");
            Iterator<String> iterator = filenames.iterator();
            while(iterator.hasNext()){
                StringBuffer sb = new StringBuffer();
                String filename = iterator.next();
                try {
                    int sumLineNumber = getLineNumber(filename);
                    if(sumLineNumber>3&&CheckDate.isInputDay(filename,date)){
                        LOGGER.info("csv:"+ filename +"文件总行数:" + sumLineNumber+",且大于等于三行");
                        fis = new FileInputStream(filename);
                        isr = new InputStreamReader(fis, "UTF-8");
                        lnr = new LineNumberReader(isr);
                        LOGGER.info("IO流初始化成功...开始删除不需要的行");
                        String lineStr = "";
                        while ((lineStr = lnr.readLine())!=null){
                            if(lnr.getLineNumber()!=1&&lnr.getLineNumber()!=2&&sumLineNumber!=lnr.getLineNumber()){
                                sb.append(lineStr).append("\n");
                            }
                        }
                        fis.close();
                        isr.close();
                        lnr.close();
                        writer(filename,sb.toString());
                    }else if((sumLineNumber==3||sumLineNumber<3)&&CheckDate.isInputDay(filename,date)&&delFlag){
                        File file = new File(filename);
                        file.delete();
                        LOGGER.info("删除只有表头和系统内容的文件:"+file.getName());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if(fis!=null){
                        fis=null;
                    }
                    if(isr!=null){
                        isr=null;
                    }
                    if(lnr!=null){
                        lnr=null;
                    }
                    LOGGER.info("IO流成功关闭...");
                    LOGGER.info("本处次理一共完成了"+count+"个csv文件");
                }
            }
        }else{
            LOGGER.warn("本地目录满足日期的csv文件为空,共处理0个!!!");
        }
    }

    public static int getLineNumber(String filename) {
        LOGGER.info("获取文件的总行数...filename="+filename);
        int cnt = 0;
        LineNumberReader reader = null;
        try {
            reader = new LineNumberReader(new FileReader(filename));
            String lineRead = "";
            while ((lineRead = reader.readLine()) != null) {
            }
            cnt = reader.getLineNumber();
        } catch (Exception ex) {
            LOGGER.error("获取文件的总行数时出错,filename="+filename+",message:"+ex.toString());
            cnt = -1;
            ex.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return cnt;
    }

    public static void writer(String filename,String content) {
        FileWriter fw = null;
        BufferedWriter bw = null;
        ReaderProperties rp = new ReaderProperties(Constants.FILENAME);
        String localpath = rp.loadProperties(Constants.FTPTOFILEPATHKEY);
        filename = localpath+filename.substring(localpath.endsWith("/") ? filename.lastIndexOf("/") + 1 : filename.lastIndexOf("/"), filename.length());
        LOGGER.info("开始重写csv文件...filename="+filename);
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "UTF-8")));
            out.write(content);
            out.close();
        } catch (Exception e) {
            LOGGER.error("重写文件["+filename+"]时出错error:"+e.toString());
            e.printStackTrace();
        } finally {
            if(bw!=null){
                bw=null;
            }
        }
        count = count+1;
        LOGGER.info("重写文件["+filename+"]成功,处理了第"+count+"个文件");
    }
}
