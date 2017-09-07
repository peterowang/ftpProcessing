package com.bfd.ftp.common;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.List;

/**
 * Created by BFD-593 on 2017/9/5.
 * 传入本地路径获取所有满足要求的文件名
 */
public class GetAllFilePath {
    private static final Logger LOGGER = Logger.getLogger(GetAllFilePath.class);
    /*
    * 获取所有满足条件的文件名
    * */
    public static void getFileName(List<String> filenames,String filepath,String dateStr) {
        if(StringUtils.isEmpty(filepath)||!filepath.contains("/")){
            LOGGER.warn("文件路径 [" + filepath + "] 无效!");
            return;
        }
        if(!filepath.endsWith("/")){
            filepath = filepath + "/";
        }
        try {
            File file = new File(filepath);
            File[] tempList = file.listFiles();
            LOGGER.info("该目录下文件个数："+tempList.length);
            for (int i = 0; i < tempList.length; i++) {
                if (tempList[i].isFile()&&tempList[i].getName().endsWith(".csv")&&
                        CheckDate.isInputDay(tempList[i].getName(),dateStr)) {
                    filenames.add(filepath+tempList[i].getName());
                    LOGGER.info("成功添加csv文件:" + tempList[i].getName());
                }else if(tempList[i].isDirectory()){
                    String p = filepath+tempList[i].getName();
                    LOGGER.info("获取目录,等待继续扫描csv文件:" + p);
                    getFileName(filenames,p, dateStr);
                }
            }
            return;
        } catch (Exception e) {
            LOGGER.error("文件路径不存在或获取到的csv文件为空!message:" + e.toString());
            e.printStackTrace();
        }
    }

}
