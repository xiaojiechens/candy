package cn.xsshome.tess4j;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import net.sourceforge.tess4j.Tesseract;

public class TestOCR {
	public static void main(String[] args) throws FileNotFoundException, IOException {
//		System.out.println(findOCR("./a.png", true));
//		System.out.println(findOCR("./b.jpg", true));
		
		//原始验证码地址
//		String OriginalImg = "./temp2.jpg";
//
//		//        去噪点
//		ImgUtils.removeBackground("./b.jpg", OriginalImg);
//        
//        BufferedImage bufferedImage = ImageIO.read(new FileInputStream(OriginalImg)); 
//        ImageFilter imageFilter = new ImageFilter(bufferedImage);
//        
//        bufferedImage = imageFilter.lineGrey();
//        bufferedImage = imageFilter.grayFilter();
//        bufferedImage = imageFilter.changeGrey();	
//        bufferedImage = imageFilter.median();
//        bufferedImage = imageFilter.sharp();
        
        //识别样本输出地址
//        String ocrResult = "./temp2.jpg";
        
//        ImageIO.write(bufferedImage, "jpg", new File(ocrResult)); 
        //裁剪边角
//        ImgUtils.cuttingImg(ocrResult);
        //OCR识别
		System.out.println(findOCR("./temp.jpg", true));
	}
	public static String findOCR(String filePath,boolean ZH_CN){
		String result = "";
		try {
			double start = System.currentTimeMillis();
			File file = new File(filePath);
			BufferedImage textImage = ImageIO.read(file);
			Tesseract tesseract = new Tesseract();
			
			List<String> configs = new ArrayList<String>();
			configs.add("digits");
			tesseract.setConfigs(configs);
			
			//加载训练字体库文件
			tesseract.setDatapath("E:/Program Files/Tesseract-OCR/tessdata");
			result = tesseract.doOCR(textImage);
			double end = System.currentTimeMillis();
			System.out.println("耗时"+(end-start)/1000+" s");
			return result;
		} catch (Exception e) {
			return null;
		}
	}
}