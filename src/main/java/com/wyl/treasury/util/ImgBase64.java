package com.wyl.treasury.util;

//import java.awt.AlphaComposite;

import sun.misc.BASE64Encoder;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

//import java.awt.Graphics2D;
//import java.awt.Image;
//import java.awt.RenderingHints;
//import java.awt.image.BufferedImage;

public final class ImgBase64 {

	// 水印透明度
	private static float alpha = 0.5f;
	//字体大小
	private static int size = 36;
	// 水印文字字体
	private static Font font = new Font("宋体", Font.BOLD, size);
	// 水印文字颜色
	private static Color color = new Color(130,100,100);
	// 水印旋转角度
	private static int degree = 45;

    /**
     * @Description: 根据图片地址转换为base64编码字符串
     * @Author:
     * @CreateTime:
     * @return
     */
    public static String getImageBase64Str(String imgFile) {
        InputStream inputStream = null;
        byte[] data = null;
        try {
            inputStream = new FileInputStream(imgFile);
            data = new byte[inputStream.available()];
            inputStream.read(data);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 加密
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);
    }

    /**
     * @Description: 根据图片地址转换为base64编码字符串
     * @Author:
     * @CreateTime:
     * @return
     */
    public static byte[] getImageBase64(String imgUrl) throws Exception{
        InputStream inputStream;
        // 构造URL
        URL url = new URL(imgUrl);
        // 打开连接
        URLConnection con = url.openConnection();
        //设置请求超时为5s
        con.setConnectTimeout(5*1000);
        inputStream = con.getInputStream();
        byte[] btImg = ImgBase64.readInputStream(inputStream);//得到图片的二进制数据
        return btImg;
    }

    /**
     * 从输入流中获取数据
     * @param inStream 输入流
     * @return
     * @throws Exception
     */
    public static byte[] readInputStream(InputStream inStream) throws Exception{
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while( (len=inStream.read(buffer)) != -1 ){
            outStream.write(buffer, 0, len);
        }
        inStream.close();
        return outStream.toByteArray();
    }


//	public static void main(String[] args) {
//		String logoText = "管理员按时大大大神带";
//		System.out.println("给图片添加水印文字开始...");
//		// 给图片添加水印文字,水印文字旋转-45
//		String imgUrl = "https://jxsr-oss1.antelopecloud.cn/files?access_token=2147500032_0_1553226734_100c6f794e3ac9c7037f7e569489c16c&obj_id=5b4aabd7800040000410ccdf";
//		String imgStr = markImageByText(logoText, imgUrl);
//		System.out.println("给图片添加水印文字结束...");
//		System.out.println("base64转图片开始...");
//		GenerateImage(imgStr);
//		System.out.println("base64转图片结束...");
//	}
//
//	/**
//	 *
//	 * @param logoText
//	 *            水印文字
//	 * @param imgUrl
//	 *            图片路径
//	 */
//	public static String markImageByText(String logoText, String imgUrl) {
//		String imgStr = "";
//		InputStream is = null;
//		OutputStream os = null;
//		try {
//			// 1、源图片
//			URL url = new URL(imgUrl);
//			Image srcImg = ImageIO.read(url);
//			BufferedImage buffImg = new BufferedImage(srcImg.getWidth(null), srcImg.getHeight(null),
//					BufferedImage.TYPE_INT_RGB);
//			//读取图片的宽
//			int imgWidth = buffImg.getWidth();
//			//读取图片的高
//			int imgHeight = buffImg.getHeight();
//
//			// 2、得到画笔对象
//			Graphics2D g = buffImg.createGraphics();
//			// 3、设置对线段的锯齿状边缘处理
//			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
//			g.drawImage(srcImg.getScaledInstance(srcImg.getWidth(null), srcImg.getHeight(null), Image.SCALE_SMOOTH), 0,
//					0, null);
//			// 4、设置水印旋转
//			g.rotate(Math.toRadians(degree), (double) imgWidth / 2, (double) imgHeight / 2);
//			// 5、设置水印文字颜色
//			g.setColor(color);
//			// 6、设置水印文字Font
//			g.setFont(font);
//			// 7、设置水印文字透明度
//			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
//			// 8、第一参数->设置的内容，后面两个参数->文字在图片上的坐标位置(x,y)
//			int x = imgWidth / 2 - getLogoTextLength(logoText, g) / 2;
//			int y = imgHeight / 2 + size / 2;
//			g.drawString(logoText, x, y);
//			// 9、释放资源
//			g.dispose();
//
//			//图片转换base64字符串
//			BASE64Encoder base64en = new BASE64Encoder();
//			ByteArrayOutputStream bot = new ByteArrayOutputStream();
//			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(bot);
//			encoder.encode(buffImg);
//			byte [] rstByte = bot.toByteArray();
//			imgStr = base64en.encode(rstByte);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				if (null != is)
//					is.close();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			try {
//				if (null != os)
//					os.close();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		return imgStr;
//	}
//
//	public static int getLogoTextLength(String logoText, Graphics2D g) {
//		return g.getFontMetrics(g.getFont()).charsWidth(logoText.toCharArray(), 0, logoText.length());
//	}
//
//	// base64字符串转化成图片
//	public static boolean GenerateImage(String imgStr) { // 对字节数组字符串进行Base64解码并生成图片
//		if (imgStr == null) // 图像数据为空
//			return false;
//		BASE64Decoder decoder = new BASE64Decoder();
//		try {
//			// Base64解码
//			byte[] b = decoder.decodeBuffer(imgStr);
//			for (int i = 0; i < b.length; ++i) {
//				if (b[i] < 0) {// 调整异常数据
//					b[i] += 256;
//				}
//			}
//			// 生成jpeg图片
//			String imgFilePath = "E:\\duanwuCopy.png";// 新生成的图片
//			OutputStream out = new FileOutputStream(imgFilePath);
//			out.write(b);
//			out.flush();
//			out.close();
//			return true;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return false;
//		}
//	}

    public static void main(String[] args) {
        try {
            String s = ImgBase64.getImageBase64Str("C:\\Users\\Public\\Pictures\\Sample Pictures\\timg.jpg");
            System.out.println(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
