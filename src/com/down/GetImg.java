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
	 * �ٶ�ͼƬAPI���ο���https://blog.csdn.net/yuanwofei/article/details/16343743��
	 * ����һ��ҳ�棺http://image.baidu.com/i?tn=resultjsonavstar&ie=utf-8&word=���»�&pn=0&rn=60
	 ***********************************************************************************************************
	 * ����json����������˵����pn:�ڼ�ҳ��rn:һҳ���ٸ�
	 *         �ض������棺tag1=����|tag2=С��| e:&tag1=��Ů&tag2=ȫ��|
	 *                http://image.baidu.com/channel/listjson?pn=0&rn=30&tag1=��Ů&tag2=ȫ��&ftags=С����&ie=utf8
	 *        
	 *         ͨ�ò����棺tn=resultjson_com|ipn=rj|&1527302998449=(�����)��&ct=201326592��ü���
	 *                https://image.baidu.com/search/acjson?tn=resultjson_com&ipn=rj&pn=0&rn=1&word=��ֽ+��ͬ���+ˮī��
	 *         ͨ�ð�ȫ��������
	 *                  https://image.baidu.com/search/acjson?tn=resultjson_com&ipn=rj&ct=201326592&is=&fp=result
	 *                  &queryWord=��ֽ+��ͬ���+ˮī��&cl=2&lm=-1&ie=utf-8&oe=utf-8&adpicid=&st=-1&z=&ic=0&word=��ֽ+��ͬ���+ˮī��
	 *                  &s=&se=&tab=&width=&height=&face=0&istype=2&qc=&nc=1&fr=&pn=30&rn=30&itg=1&gsm=1e&1527295672647=
	 ************************************************************************************************************/

	public static void main(String[] args) { 
		
		long t1 = System.currentTimeMillis();
		
		//����������Ҫ��URLEncoder.encode()����ת�룬��Ȼ�޷���ȷ�����������ע����������֧�ֵĸ�ʽ��baidu��google�Ķ���utf-8
		String word = "�����";  //����������������������Կո����
		String path = "D:\\DownImg\\" + getWordPath(word); //ͼƬ�����ַ
		//������Щ������Ҫ���ã���Ȼ���ص�ͼƬ�����ظ���
		int pn = 0; //Ҫ��rn��������
		int rn = 50; //һ�η��ض����ţ����60
		int total = 300;//�������������������50�ı������������ص������������ظ�������̫�ࣩ
		int count = 0;//����ʵ��������
		
		int frequency = total/rn;  //ѭ������
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
						
						//ע��url��ͬ�����ص�jsonҲ��ͬ������Ҳ��ͬ���ض��������ȡid��image_url����������ͨ�ò������ȡdi��thumbURL��������
						//����image_url��thumbURL���صĶ���СͼƬ������ܣ�������ͼƬ��ַ��objURL�������Ǿ������ܵģ���Ҫת��
						
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
	    
	    System.err.println("����������"+count);
	    System.err.println("����ʱ��:"+(t2-t1)/1000+"s");
    } 

	//ͨ��URL��ȡjson���� 
	public static String loadJson(String url) {
		StringBuilder json = new StringBuilder();
		try {
			URL urlObject = new URL(url);
			URLConnection uc = urlObject.openConnection();
			//uc.setConnectTimeout(1000);//�������ӳ�ʱʱ��1s
			// ����Ϊutf-8�ı��� �Ų�����������
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
        System.out.println("�������:"+ name + type);
	}
	
	//����������ƴ�ӳ�"+"��ʽ
	public static String getSearchWord(String word){
		return word.trim().replace(" ", "+");
	}
	
	//����������ת�����ļ���
	public static String  getWordPath(String word) {
		return word.replace(" ", "\\");
	}
	
	//����url��ȡ�����ͼƬ����
	private static String getType(String imgUrl) {
		if (imgUrl.indexOf(".png") != -1) {
			return ".png";
		}else if (imgUrl.indexOf(".gif") != -1) {
			return ".gif";
		}
		return ".jpg";//Ĭ�Ϸ��صĸ�ʽ
	}
	
	/** 
	 * �����ٶ�ͼƬ��ַ 
	 * @param url �����ͼƬ������ַ��������ippr��ͷ����_z&e3B3r2��β 
	 *        ���磺ippr_z2C$qAzdH3FAzdH3Frtv8_z&e3Botg9aaa_z&e3Bv54AzdH3Fowssrwrj6AzdH3FdAzdH3Fc0llmn8l9m9lv_z&e3B3r2
	 * @return ������ʵ������ͼƬ��ַ 
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