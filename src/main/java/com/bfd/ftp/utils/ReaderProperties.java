package com.bfd.ftp.utils;

import java.io.InputStream;
import java.util.Properties;

/**
 * Created by BFD-593 on 2017/9/5.
 */
public class ReaderProperties {
    String path = "";
    public ReaderProperties(String path){
        this.path = path;
    }
    public String loadProperties(String key) {
        InputStream input = null;
        try {
            input = this.getClass().getResourceAsStream(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
       return printProperties(input,key);
    }

    private String printProperties(InputStream input,String key) {
        try {
            if(input!=null){
                Properties properties = new Properties();
                properties.load(input);
                input.close();
                return properties.getProperty(key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(input!=null){
                input = null;
            }
        }
        return "";
    }
}
