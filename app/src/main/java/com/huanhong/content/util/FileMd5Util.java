package com.huanhong.content.util;

import android.support.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * Created by Administrator on 2017/1/12.
 */

public class FileMd5Util {
        @Nullable  public static String getFileMD5(File file) {
        if (!file.exists() || !file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }

    /***
     * Get MD5 of one file！test ok!
     *
     * @param filepath
     * @return
     */
    public static String getFileMD5(String filepath) {
        File file = new File(filepath);
        return getFileMD5(file);
    }

    /**
     * MD5 encrypt,test ok
     *
     * @param data
     * @return byte[]
     * @throws Exception
     */
    public static byte[] encryptMD5(byte[] data) throws Exception {

        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(data);
        return md5.digest();
    }

 /*   public static byte[] encryptMD5(String data) throws Exception {
        return encryptMD5(data.getBytes(CHARSET_ISO88591));
    }*/
    /***
     * compare two file by Md5
     *
     * @param file1
     * @param file2
     * @return
     */
    public static boolean isSameMd5(File file1, File file2){
        String md5_1=getFileMD5(file1);
        String md5_2=getFileMD5(file2);
        return md5_1.equals(md5_2);
    }
    /***
     * compare two file by Md5
     *
     * @param filepath1
     * @param filepath2
     * @return
     */
    public static boolean isSameMd5(String filepath1,String filepath2){
        File file1=new File(filepath1);
        File file2=new File(filepath2);
        return isSameMd5(file1, file2);
    }
}
