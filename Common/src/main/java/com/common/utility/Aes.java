package com.common.utility;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;

/**
 * author:tonyjarjar
 * time:2017-08-25 09:58
 * describe: Aes加密解密，密钥长度是32位，使用这个加密算法前，先查看这里
 * <a href="http://314649444.iteye.com/blog/2083610">AES</a>
 * 根据C#采集框架的Aes加密解密翻译成java版本
 */
public class Aes {


    static public String encrypt(String text, String password){
        return aes(Cipher.ENCRYPT_MODE, text, password);
    }

    public static  String decrypt(String text, String password){
        return aes(Cipher.DECRYPT_MODE, text, password);
    }


    static public String aes(int mode, String text, String password) {
        try {
            byte[] md5 = getMd5(password);
            byte[] keys = byteArrayToHexString(md5).getBytes();
            byte[] iv = getIv(md5);
            byte[] txt;
            if (mode == Cipher.DECRYPT_MODE){
                txt = new BASE64Decoder().decodeBuffer(text);
            } else {
                txt = text.getBytes();
            }
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            //KeyGenerator 生成aes算法密钥
            SecretKey secretKey = new SecretKeySpec(keys, "AES");
            cipher.init(mode, secretKey, new IvParameterSpec(iv));//使用加密模式初始化 密钥
            byte[] crypt = cipher.doFinal(txt, 0, txt.length);
            String result;
            if (mode == Cipher.DECRYPT_MODE){
                result = new String(crypt);
            } else {
                result = new BASE64Encoder().encode(crypt);            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }



    static private byte[] getIv(byte[] md5) {
        byte[] iv = new byte[16];
        iv[0] = md5[3];
        iv[1] = md5[2];
        iv[2] = md5[1];
        iv[3] = md5[0];
        iv[4] = md5[5];
        iv[5] = md5[4];
        iv[6] = md5[7];
        iv[7] = md5[6];
        iv[8] = md5[8];
        iv[9] = md5[9];
        iv[10] = md5[10];
        iv[11] = md5[11];
        iv[12] = md5[12];
        iv[13] = md5[13];
        iv[14] = md5[14];
        iv[15] = md5[15];
        return iv;
    }


    private static byte []getMd5(String txt){
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            return messageDigest.digest(txt.getBytes("utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    /**
     * 将一个字节转化成十六进制形式的字符串
     */
    private static String byteToHexString(byte b) {
        String[] hexDigits = {"0", "1", "2", "3", "4",
                "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};
        int n = b;
        if (n < 0)
            n = 256 + n;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    /**
     * 转化为字符串
     *
     * @param b
     * @return
     */
    private static String byteArrayToHexString(byte[] b) {
        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            resultSb.append(byteToHexString(b[i]));
        }
        return resultSb.toString();
    }

}
