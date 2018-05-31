package com.down;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class RRLCoderr {
	
	public static void main(String[] args) throws UnsupportedEncodingException {
		
		System.out.println("解码结果："+URLDecoder.decode("http%3A%2F%2Fwww.deskier.com%2Fuploads%2Fallimg%2F160820%2F1-160R0105G0.jpg","utf-8"));
		System.out.println("解码结果："+URLDecoder.decode("http%3A%2F%2Fimg3.imgtn.bdimg.com%2Fit%2Fu%3D252457620%2C3400876947%26fm%3D27%26gp%3D0.jpg","utf-8"));
		
		System.out.println("编码结果："+URLEncoder.encode("","utf-8"));
	}

}
