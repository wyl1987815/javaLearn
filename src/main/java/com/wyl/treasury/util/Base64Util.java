package com.wyl.treasury.util;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.imageio.stream.FileImageOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Base64;

public class Base64Util {

    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    private static final Base64.Decoder decoder = Base64.getDecoder();
    private static final Base64.Encoder encoder = Base64.getEncoder();

    public static void main(String[] args) throws Exception {

        String url = "https://jxsr-oss1.antelopecloud.cn/files2/538378381/5b850a172017008d0410b6de?access_token=538378381_0_1566992195_d3769b88cadb2366b4e434c535174a54&key=%2Fiermu%2Fai%2F137898525675_538378381_1535445526128_1535445527765884139.jpg";
        // byte[] b =
        // ImgCutter.image2Base64("https://jxsr-oss1.antelopecloud.cn/files?obj_id=5b71489480004002041083a1&access_token=2147500034_3356491776_1565686804_7779becd44fd1f3d9ccd939139880cee");

        String text = Base64Util.encodeToString(ImgCutter.image2Base64(url));
        final byte[] b = decodeStr(text);

        // Base64Util.encodeToString(ImgCutter.image2Base64("https://jxsr-oss1.antelopecloud.cn/files?obj_id=5b71489480004002041083a1&access_token=2147500034_3356491776_1565686804_7779becd44fd1f3d9ccd939139880cee"));

        FileImageOutputStream imageOutput = new FileImageOutputStream(new File("d://222.jpg"));
        imageOutput.write(b, 0, b.length);
        imageOutput.close();

        String s = "a123456";
        System.out.println("原字符串：" + s);
        String encryptString = encryptBASE64(s);
        System.out.println("加密后：" + encryptString);
        System.out.println("解密后：" + decryptBASE64(encryptString));
    }

    /**
     * BASE64解密
     *
     * @param key
     * @return
     * @throws Exception
     */
    public static String decryptBASE64(String key) {
        byte[] bt;
        try {
            bt = (new BASE64Decoder()).decodeBuffer(key);
            //如果出现乱码可以改成： String(bt, "utf-8")或 gbk
            return new String(bt, DEFAULT_CHARSET);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static byte[] decode(String str) {
        //return decoder.decode(str);
        byte[] bt;
        try {
            bt = (new BASE64Decoder()).decodeBuffer(str);
            //如果出现乱码可以改成： String(bt, "utf-8")或 gbk
            return bt;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String encode(byte[] bytes) {
        return (new BASE64Encoder()).encodeBuffer(bytes);
    }

    /**
     * BASE64加密
     *
     * @param str
     * @return
     * @throws Exception
     */
    public static String encryptBASE64(String str) {
        byte[] b = null;
        String s = null;
        try {
            b = str.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (b != null) {
            s = new BASE64Encoder().encode(b);
        }
        return s;
    }

    public static String encodeToString(byte[] bytea) {
        return encoder.encodeToString(bytea);
    }

    public static byte[] decodeStr(String str) {
        return decoder.decode(str);
    }
}
