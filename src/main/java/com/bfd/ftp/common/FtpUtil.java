package com.bfd.ftp.common;

import com.bfd.ftp.model.FtpDomain;
import com.bfd.ftp.utils.Constants;
import com.bfd.ftp.utils.ReaderProperties;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.*;


/**
 * Created by BFD-593 on 2017/9/5.
 */
public class FtpUtil {
    private static final Logger LOGGER = Logger.getLogger(FtpUtil.class);

    private FtpUtil() {
    }

    private static FtpDomain getFtpFile() {
        ReaderProperties rp = new ReaderProperties(Constants.FILENAME);
        String host = rp.loadProperties(Constants.FTPADDR);
        String port = rp.loadProperties(Constants.FTPPORT);
        String username = rp.loadProperties(Constants.FTPUSERNAME);
        String password = rp.loadProperties(Constants.FTPPWD);
        FtpDomain ftp = new FtpDomain(host, port, username, password);
        return ftp;
    }

    /*获取ftpClient连接实例*/
    private static FTPClient connect() {
        FtpDomain ftp = getFtpFile();
        LOGGER.info("开始连接ftp服务器...");
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(ftp.getHost(), Integer.parseInt(ftp.getPort()));
            ftpClient.login(ftp.getUsername(), ftp.getPassword());
            if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                LOGGER.warn("未连接到FTP,用户名或密码错误.");
                ftpClient.disconnect();
            } else {
                LOGGER.info("FTP连接成功!");
            }
        } catch (SocketException e) {
            LOGGER.error("FTP的IP地址可能错误,请正确配置.message:" + e.toString());
            e.printStackTrace();
        } catch (IOException e) {
            LOGGER.error("FTP的端口错误,请正确配置.message:" + e.toString());
            e.printStackTrace();
        }
        return ftpClient;
    }

    public static void downloadFtpFileAndreLoad(List<String> ftpPaths,String localpath,String date) {
        FTPClient ftpClient = connect();
        if (ftpPaths==null||ftpPaths.size()<=0) {
            LOGGER.warn("获取要处理的文件为空!");
            return;
        }

        if (FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
            try {
                //设置编码
                ftpClient.setControlEncoding("UTF-8");
                //设置文件类型
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                /*
                * 这他妈的是个大坑
                * 这个方法的意思就是每次数据连接之前，
                * ftp client告诉ftp server开通一个端口来传输数据。
                * 为什么要这样做呢，因为ftp server可能每次开启不同的端口来传输数据，
                * 但是在linux上或者其他服务器上面，由于安全限制，
                * 可能某些端口没有开启，所以就出现阻塞。解决ftp.listFiles();为空的问题。
                */
                ftpClient.enterLocalPassiveMode();
                for(String path : ftpPaths){
                    if(!path.startsWith("/")){
                        path = "/" + path;
                    }
                    String dic = path.substring(0, path.lastIndexOf("/"));
                    String fi = path.substring(path.lastIndexOf("/")+1, path.length());
                    //设置ftp工作目录
                    ftpClient.changeWorkingDirectory(dic);
                    LOGGER.info("当前切换到的ftp目录是:" + dic);
                    FTPFile[] ftpFiles = ftpClient.listFiles();
                    for (FTPFile ff : ftpFiles) {
                        if (ff.isFile() && ff.getName().endsWith(".csv") &&
                                (ff.getName().contains(fi)||ff.getName().equals(fi)||fi.contains(ff.getName()))) {
                            LOGGER.info("下载ftp文件:" + ff.getName());
                            File localFile = new File(localpath.endsWith("/") ?
                                    localpath + ff.getName() :
                                    localpath + "/" + ff.getName());
                            if (!localFile.exists()) {
                                localFile.getParentFile().mkdirs();
                                localFile.createNewFile();
                            }
                            OutputStream out = new FileOutputStream(localFile);
                            ftpClient.retrieveFile(ff.getName(), out);
                            out.close();
                            LOGGER.info("下载成功,文件为:" + ff.getName());
                        }
                    }
                }
                ftpClient.logout();
                ftpClient.disconnect();
                localFileReload(localpath, date);
            } catch (IOException e) {
                LOGGER.error("下载文件失败,原因可能是不能切换到ftp目录!");
                e.printStackTrace();
            }
        }
    }
    public static void localFileReload(String localpath,String date){
        List<String> listPaths = Lists.newArrayList();
        GetAllFilePath.getFileName(listPaths, localpath, date);
        ReaderAndWriter.reload(listPaths,date,true);
    }
}
