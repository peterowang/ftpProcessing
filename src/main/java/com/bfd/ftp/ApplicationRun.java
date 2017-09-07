package com.bfd.ftp;


import com.bfd.ftp.common.FtpUtil;
import com.bfd.ftp.common.GetAllFilePath;
import com.bfd.ftp.common.ReaderAndWriter;
import com.bfd.ftp.utils.Constants;
import com.bfd.ftp.utils.ReaderProperties;
import com.google.common.collect.Lists;
import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.List;

/**
 * Created by BFD-593 on 2017/9/5.
 */
public class ApplicationRun {
    private static final Logger log = Logger.getLogger(ApplicationRun.class);
    public static void main(String[] args) {
        log.info("----------------------------------------开始----------------------------------------");
        ReaderProperties rp = new ReaderProperties(Constants.FILENAME);
        String filepaths = rp.loadProperties(Constants.FTPGETFILEPATHKEY);
        String ftpPath = rp.loadProperties(Constants.FTPPATH);
        String localpath = rp.loadProperties(Constants.FTPTOFILEPATHKEY);
        String[] split = filepaths.split(",");
        List<String> list = Lists.newArrayList(Arrays.asList(split));
        if(args.length==0){
            log.info("下载指定的ftp文件到本地,处理昨天上传的文件...");
            FtpUtil.downloadFtpFileAndreLoad(list,localpath,"");
        } else if (args.length == 1 && args[0].equals("local")){
            log.info("处理本地目录下指定文件中,昨天上传的文件...");
            ReaderAndWriter.reload(list,"",false);
        } else if(args.length == 1 && args[0].equals("allfile")){
            log.info("获取ftp指定路径下所有昨天的csv,进行处理");
            List<String> allfiles = Lists.newArrayList();
            GetAllFilePath.getFileName(allfiles, ftpPath, "");
            FtpUtil.downloadFtpFileAndreLoad(allfiles,localpath,"");
        } else if (args.length == 2 && args[0].equals("local") && args[1].equals("allfile")){
            log.info("获取本地指定路径下所有咋天的csv,进行处理");
            List<String> allfiles = Lists.newArrayList();
            GetAllFilePath.getFileName(allfiles, ftpPath, "");
            ReaderAndWriter.reload(allfiles,"",false);
        } else if (args.length == 2 && args[0].equals("local")) {
            log.info("处理本地目录下指定日期上传的文件:"+list);
            ReaderAndWriter.reload(list,args[1],false);
        } else if (args.length == 2 && args[0].equals("allfile")){
            log.info("获取ftp指定路径下所有指定日期的csv,进行处理");
            List<String> allfiles = Lists.newArrayList();
            GetAllFilePath.getFileName(allfiles, ftpPath, args[1]);
            FtpUtil.downloadFtpFileAndreLoad(allfiles,localpath,args[1]);
        } else if (args.length == 3 && args[0].equals("local") && args[1].equals("allfile")){
            log.info("处理本地目录下所有指定日期上传的文件");
            List<String> allfiles = Lists.newArrayList();
            GetAllFilePath.getFileName(allfiles, ftpPath, args[2]);
            ReaderAndWriter.reload(allfiles,args[2],false);
        } else {
            log.info("下载ftp文件到本地,处理指定日期上传的文件...");
            FtpUtil.downloadFtpFileAndreLoad(list,localpath,args[0]);
        }
        log.info("----------------------------------------结束----------------------------------------");
    }
}
