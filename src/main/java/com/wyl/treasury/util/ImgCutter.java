/**
 * 
 */
package com.wyl.treasury.util;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

/**
 * @author Administrator
 *
 */
public class ImgCutter {

    /**
     * 按照坐标尺寸截取小图
     * 
     * @param imgUrl
     * @param x
     * @param y
     * @param width
     * @param height
     * @return 小图路径
     */
    public static String cutImg(String imgUrl, int x, int y, int width, int height) {
        final byte[] cuted = cutFromUrl(imgUrl, x, y, width, height);
        String baseStr = Base64Util.encodeToString(cuted);
        return baseStr;
    }

    private static byte[] cutFromUrl(String imageUrl, int x, int y, int width, int height) {
        InputStream is = null;
        ImageInputStream iis = null;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            /** 读取图片 */
            Iterator<ImageReader> it = ImageIO.getImageReadersByFormatName("jpg");
            ImageReader reader = it.next();
            /** 获取图片流 */
            URL url = new URL(imageUrl);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            /** 设置请求方式为"GET" */
            httpConn.setRequestMethod("GET");
            /** 超时响应时间为5秒 */
            httpConn.setConnectTimeout(5 * 1000);
            httpConn.connect();
            is = httpConn.getInputStream();

            iis = ImageIO.createImageInputStream(is);
            reader.setInput(iis, true);

            ImageReadParam param = reader.getDefaultReadParam();
            Rectangle rect = new Rectangle(x, y, width, height);
            param.setSourceRegion(rect);
            BufferedImage bi = reader.read(0, param);

            // ImageIO.write(bi, "jpg", new File(fileNameAndPath));
            ImageIO.write(bi, "jpg", outputStream);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (iis != null) {
                    iis.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return outputStream.toByteArray();
    }

    /**
     * 通过图片的url获取图片的base64字符串
     * 
     * @param imgUrl
     *            图片url
     * @return 返回图片字節碼
     */
    public static byte[] image2Base64(String imgUrl) {
        URL url = null;
        InputStream is = null;
        ByteArrayOutputStream outStream = null;
        HttpURLConnection httpUrl = null;
        try {
            url = new URL(imgUrl);
            httpUrl = (HttpURLConnection) url.openConnection();
            httpUrl.connect();
            httpUrl.getInputStream();
            is = httpUrl.getInputStream();
            outStream = new ByteArrayOutputStream();
            // 创建一个Buffer字符串
            byte[] buffer = new byte[1024];
            // 每次读取的字符串长度，如果为-1，代表全部读取完毕
            int len = 0;
            // 使用一个输入流从buffer里把数据读取出来
            while ((len = is.read(buffer)) != -1) {
                // 用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度
                outStream.write(buffer, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outStream != null) {
                try {
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (httpUrl != null) {
                httpUrl.disconnect();
            }
        }
        if (null == outStream) {
            return new byte[] {};
        }
        return outStream.toByteArray();
    }
}
