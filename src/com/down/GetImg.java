package com.down;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class GetImg {
	
	/***********************************************************************************************************
	 * 百度图片API（参考：https://blog.csdn.net/yuanwofei/article/details/16343743）
	 * 返回一个页面：http://image.baidu.com/i?tn=resultjsonavstar&ie=utf-8&word=刘德华&pn=0&rn=60
	 ***********************************************************************************************************
	 * 返回json：公共参数说明：pn:第几页；rn:一页多少个
	 *         特定参数版：tag1=大类|tag2=小类| e:&tag1=美女&tag2=全部|
	 *                http://image.baidu.com/channel/listjson?pn=0&rn=30&tag1=美女&tag2=全部&ftags=小清新&ie=utf8
	 *        
	 *         通用参数版：tn=resultjson_com|ipn=rj|&1527302998449=(随机数)和&ct=201326592最好加上
	 *                https://image.baidu.com/search/acjson?tn=resultjson_com&ipn=rj&pn=0&rn=1&word=壁纸+不同风格+水墨画
	 *         通用版全部参数：
	 *                  https://image.baidu.com/search/acjson?tn=resultjson_com&ipn=rj&ct=201326592&is=&fp=result
	 *                  &queryWord=壁纸+不同风格+水墨画&cl=2&lm=-1&ie=utf-8&oe=utf-8&adpicid=&st=-1&z=&ic=0&word=壁纸+不同风格+水墨画
	 *                  &s=&se=&tab=&width=&height=&face=0&istype=2&qc=&nc=1&fr=&pn=30&rn=30&itg=1&gsm=1e&1527295672647=
	 ************************************************************************************************************/

	public static void main(String[] args) { 
		
		long t1 = System.currentTimeMillis();
		
		//搜索参数需要用URLEncoder.encode()方法转码，不然无法正确搜索出结果，注意搜索引擎支持的格式，baidu和google的都是utf-8
		String word = "刘亦菲";  //搜索条件，多个搜索条件以空格隔开
		String path = "D:\\DownImg\\" + getWordPath(word); //图片保存地址
		//下面这些参数需要配置，不然下载的图片都是重复的
		int pn = 0; //要是rn的整数倍
		int rn = 50; //一次返回多少张，最多60
		int total = 300;//下载总数量（最好输入50的倍数，这样下载的数量不会因重复而减少太多）
		int count = 0;//计算实际下载量
		
		int frequency = total/rn;  //循环数量
	    try {
	    	for(int i=0;i<=frequency;i++){
	    		pn = i*rn;
	    		if(i == frequency){
	    			pn=total-pn;
	    			rn=pn;
	    		}
				String jsonUrl = "https://image.baidu.com/search/acjson?tn=resultjson_com&ipn=rj&ct=201326592&1527302998449=&pn="+pn+"&rn="+rn+"&word="+URLEncoder.encode(getSearchWord(word), "utf-8");
				
				//System.out.println(loadJson(jsonUrl));
				JSONObject obj = JSONObject.fromObject(loadJson(jsonUrl));
				JSONArray data = obj.getJSONArray("data");
				if(data.size()>0){
					for(int j=0;j<data.size();j++){
						JSONObject imgJson = data.getJSONObject(j);
						
						//注意url不同，返回的json也不同，参数也不同，特定参数版获取id和image_url两个参数，通用参数版获取di和thumbURL两个参数
						//其中image_url和thumbURL返回的都是小图片无需解密，真正的图片地址是objURL，但是是经过加密的，需要转换
						
						if(imgJson.has("objURL")){
							count++;
							String imgUrl = imgJson.getString("objURL");
							String name = imgJson.getString("di");
							//System.out.println(decode(imgUrl));
							downImg(decode(imgUrl),path,getType(imgUrl),name);
						}
					}
				}
	    	}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	    long t2 = System.currentTimeMillis();
	    
	    System.err.println("下载数量："+count);
	    System.err.println("下载时间:"+(t2-t1)/1000+"s");
    } 

	//通过URL获取json数据 
	public static String loadJson(String url) {
		StringBuilder json = new StringBuilder();
		try {
			URL urlObject = new URL(url);
			URLConnection uc = urlObject.openConnection();
			//uc.setConnectTimeout(1000);//设置连接超时时间1s
			// 设置为utf-8的编码 才不会中文乱码
			BufferedReader in = new BufferedReader(new InputStreamReader(uc
									.getInputStream(), "utf-8"));
			String inputLine = null;
			while ((inputLine = in.readLine()) != null) {
				json.append(inputLine);
			}
			in.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return json.toString();
	}
	
	public static void downImg(String url, String path,String type,String name){
		
		BufferedImage image = null;    
        try { 
            image = ImageIO.read(new URL(url)); 
            File fileDir = new File(path);
			FileUtils.forceMkdir(fileDir);
			if(null != image){
				ImageIO.write(image, type.substring(1, type.length()), new File(fileDir +  "\\" + name + type));    
			}
        } catch (IOException e) {    
            e.printStackTrace();    
        }    
        System.out.println("完成下载:"+ name + type);
	}
	
	//将搜索条件拼接成"+"形式
	public static String getSearchWord(String word){
		return word.trim().replace(" ", "+");
	}
	
	//将搜索条件转换成文件夹
	public static String  getWordPath(String word) {
		return word.replace(" ", "\\");
	}
	
	//根据url获取保存的图片类型
	private static String getType(String imgUrl) {
		if (imgUrl.indexOf(".png") != -1) {
			return ".png";
		}else if (imgUrl.indexOf(".gif") != -1) {
			return ".gif";
		}
		return ".jpg";//默认返回的格式
	}
	
	/** 
	 * 解析百度图片网址 
	 * @param url 传入的图片加密网址，参数以ippr开头，以_z&e3B3r2结尾 
	 *        例如：ippr_z2C$qAzdH3FAzdH3Frtv8_z&e3Botg9aaa_z&e3Bv54AzdH3Fowssrwrj6AzdH3FdAzdH3Fc0llmn8l9m9lv_z&e3B3r2
	 * @return 返回真实的网络图片地址 
	 */  
	private static String decode(String url){
	    String myUrl = "";  
	    myUrl = url.replace("ippr", "http");  
	    myUrl = myUrl.replace("_z2C$q", ":");  
	    myUrl = myUrl.replace("AzdH3F", "/");  
	    myUrl = myUrl.replace("_z&e3B", ".");  
	    //myUrl = myUrl.toLowerCase();  
	    myUrl = myUrl.substring(4);  
	    char[] arr = myUrl.toCharArray();  
	    myUrl = "";  
	    for(char c : arr){  
			switch(c){  
				case 'w': myUrl += "a";break;  
				case 'k': myUrl += "b";break;  
				case 'v': myUrl += "c";break;  
				case '1': myUrl += "d";break;  
				case 'j': myUrl += "e";break;  
				case 'u': myUrl += "f";break;  
				case '2': myUrl += "g";break;  
				case 'i': myUrl += "h";break;  
				case 't': myUrl += "i";break;  
				case '3': myUrl += "j";break;  
				case 'h': myUrl += "k";break;  
				case 's': myUrl += "l";break;  
				case '4': myUrl += "m";break;  
				case 'g': myUrl += "n";break;  
				case '5': myUrl += "o";break;  
				case 'r': myUrl += "p";break;  
				case 'q': myUrl += "q";break;  
				case '6': myUrl += "r";break;  
				case 'f': myUrl += "s";break;  
				case 'p': myUrl += "t";break;  
				case '7': myUrl += "u";break;  
				case 'e': myUrl += "v";break;  
				case 'o': myUrl += "w";break;  
				case '8': myUrl += "1";break;  
				case 'd': myUrl += "2";break;  
				case 'n': myUrl += "3";break;  
				case '9': myUrl += "4";break;  
				case 'c': myUrl += "5";break;  
				case 'm': myUrl += "6";break;  
				case '0': myUrl += "7";break;  
				case 'b': myUrl += "8";break;  
				case 'l': myUrl += "9";break;  
				case 'a': myUrl += "0";break;  
				case 'A': myUrl += "A";break;  
				case 'B': myUrl += "B";break;  
				case 'C': myUrl += "C";break;  
				case 'D': myUrl += "D";break;  
				case 'E': myUrl += "E";break;  
				case 'F': myUrl += "F";break;  
				case 'G': myUrl += "G";break;  
				case 'H': myUrl += "H";break;  
				case 'I': myUrl += "I";break;  
				case 'J': myUrl += "J";break;  
				case 'K': myUrl += "K";break;  
				case 'L': myUrl += "L";break;  
				case 'M': myUrl += "M";break;  
				case 'N': myUrl += "N";break;  
				case 'O': myUrl += "O";break;  
				case 'P': myUrl += "P";break;  
				case 'Q': myUrl += "Q";break;  
				case 'R': myUrl += "R";break;  
				case 'S': myUrl += "S";break;  
				case 'T': myUrl += "T";break;  
				case 'U': myUrl += "U";break;  
				case 'V': myUrl += "V";break;  
				case 'W': myUrl += "W";break;  
				case 'X': myUrl += "X";break;  
				case 'Y': myUrl += "Y";break;  
				case 'Z': myUrl += "Z";break;  
				default : myUrl += c;break;
			}  
	    }  
	    return "http"+myUrl;  
	}  
	
}