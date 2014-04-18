package com.hch.http;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.http.client.methods.HttpGet;

import android.content.Context;

public class HttpRequestController {
	
	private Context context = null;
	HttpRequest request = null;
	
	public HttpRequestController(Context context){
		this.context = context;
		this.request = new HttpRequest(context);
	}

	/**
	 * 发起get请求
	 * @param url   链接地址
	 * @param params	参数
	 */
	public void get(String url,Map<String,String> params){
		HttpGet get = new HttpGet(getStringUrl(url,params));
		//request.e
	}
	
	public static String getStringUrl(String url,Map<String,String> params){
		StringBuffer sb = new StringBuffer(url);
		sb.append("?");
		
		Set<String> keyset = params.keySet();
		Iterator<String> iterator = keyset.iterator();
		while(iterator.hasNext()){
			String key = iterator.next();
			String value = params.get(key);
			sb.append(key).append("=").append(value);
			if(iterator.hasNext())
				sb.append("&");
		}
		return sb.toString();
	}
}
