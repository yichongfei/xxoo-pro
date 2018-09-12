package com.xxss.util;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.UUID;

import com.xxss.config.AccountConfig;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class ImgUtil {
	// base64字符串转化成图片
	public static String GenerateImage(String imgStr) {
		
		imgStr = imgStr.split(",")[1];
		
		// 对字节数组字符串进行Base64解码并生成图片
		if (imgStr == null) // 图像数据为空
			return null;

		BASE64Decoder decoder = new BASE64Decoder();
		try {

			String uuidpath = UUID.randomUUID().toString().replaceAll("-", "")+".jpg";
			String imgFilePath = AccountConfig.PHOTO_PATH + uuidpath;// 新生成的图片

			boolean result = base64ToFile(imgStr, imgFilePath);

			return imgFilePath;
		} catch (Exception e) {
			return null;
		}
	}

	public static boolean base64ToFile(String base64, String path) {
		byte[] buffer;
		try {
			buffer = new BASE64Decoder().decodeBuffer(base64);
			FileOutputStream out = new FileOutputStream(path);
			out.write(buffer);
			out.close();
			return true;
		} catch (Exception e) {
			throw new RuntimeException("base64字符串异常或地址异常\n" + e.getMessage());
		}
	}

	public static void main(String[] args) {
		GenerateImage("data:image/png;base64,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");
	}
}