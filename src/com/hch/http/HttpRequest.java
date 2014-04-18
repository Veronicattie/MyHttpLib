package com.hch.http;

import org.apache.http.HttpHost;
import org.apache.http.HttpVersion;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import com.hch.util.LogUtil;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;

public class HttpRequest extends AsyncTask<Object, Integer, Void>{
	private String tag = this.getClass().getSimpleName();
	
	public static Uri PREFERRED_APN_URI = Uri
			.parse("content://telephony/carriers/preferapn");
	public static final String CTWAP = "ctwap";
	public static final String CMWAP = "cmwap";
	public static final String WAP_3G = "3gwap";
	public static final String UNIWAP = "uniwap";
	
	/** 网络不可用**/
	public static final int TYPE_NET_WORK_DISABLED = 0;
	/**移动联通wap10.0.0.172**/
	public static final int TYPE_CM_CU_WAP = 4;
	/**电信wap 10.0.0.200**/
	public static final int TYPE_CT_WAP = 5;
	/**电信,移动,联通等net网络**/
	public static final int TYPE_NET = 6;
	/**WIFI网络**/
	public static final int TYPE_WIFI = 7;
	private Context context = null;

	DefaultHttpClient client = null;
	
	public HttpRequest(Context context){
		this.context = context;
		if (client == null) {
	        HttpParams params = new BasicHttpParams();
	        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
	        HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);
	        HttpProtocolParams.setUseExpectContinue(params, true);
	        HttpProtocolParams.setUserAgent(params,
"Mozilla/5.0 (Linux; U; Android 2.2.1; en-us; Nexus One Build/FRG83) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1"
            );

	        ConnManagerParams.setTimeout(params, 1000);

	        HttpConnectionParams.setConnectionTimeout(params, 5000);
	        HttpConnectionParams.setSoTimeout(params, 10000);

	        SchemeRegistry schReg = new SchemeRegistry();
	        schReg.register(new Scheme("http", 
	                        PlainSocketFactory.getSocketFactory(), 80));
	        schReg.register(new Scheme("https", 
	                        SSLSocketFactory.getSocketFactory(), 443));
	        ClientConnectionManager conMgr = new 
	                        ThreadSafeClientConnManager(params,schReg);

	        client = new DefaultHttpClient(conMgr, params);
		}
	}
	
	
	
	@Override
	protected Void doInBackground(Object... params) {
		if(checkNetworkType()==TYPE_CT_WAP){
    		//电信网络
    		HttpHost proxy = new HttpHost("10.0.0.200", 80);
			client.getParams().setParameter(ConnRouteParams.DEFAULT_PROXY,
					proxy);
    	}else if(checkNetworkType()==TYPE_CM_CU_WAP){
    		//联通或者移动
    		HttpHost proxy = new HttpHost("10.0.0.172", 80);
			client.getParams().setParameter(ConnRouteParams.DEFAULT_PROXY,
					proxy);
    	}else{
			//cmwap需要设置代理，而使用wifi和cmnet则不需要，设置后反而读不到数据
			//如果网络从gprs转变成wifi,采用默认的代理
			client.getParams().setParameter(ConnRouteParams.DEFAULT_PROXY,
					android.net.Proxy.getDefaultHost());
    	}
		//client.e
		return null;
	}
	
	/***
	 * 判断Network具体类型（联通移动wap，电信wap，其他net）
	 * 
	 * */
	public int checkNetworkType() {
		try {
			final ConnectivityManager connectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			final NetworkInfo mobNetInfoActivity = connectivityManager
					.getActiveNetworkInfo();
			if (mobNetInfoActivity == null || !mobNetInfoActivity.isAvailable()) {

				// 注意一：
				// NetworkInfo 为空或者不可以用的时候正常情况应该是当前没有可用网络，
				// 但是有些电信机器，仍可以正常联网，
				// 所以当成net网络处理依然尝试连接网络。
				// （然后在socket中捕捉异常，进行二次判断与用户提示）。

				LogUtil.i("", "=====================>无网络");
				return TYPE_NET;
			} else {

				// NetworkInfo不为null开始判断是网络类型

				int netType = mobNetInfoActivity.getType();
				if (netType == ConnectivityManager.TYPE_WIFI) {
					// wifi net处理
					LogUtil.i("", "=====================>wifi网络");
					return TYPE_WIFI;
				} else if (netType == ConnectivityManager.TYPE_MOBILE) {

					// 注意二：
					// 判断是否电信wap:
					// 不要通过getExtraInfo获取接入点名称来判断类型，
					// 因为通过目前电信多种机型测试发现接入点名称大都为#777或者null，
					// 电信机器wap接入点中要比移动联通wap接入点多设置一个用户名和密码,
					// 所以可以通过这个进行判断！

					final Cursor c = context.getContentResolver().query(
							PREFERRED_APN_URI, null, null, null, null);
					if (c != null) {
						c.moveToFirst();
						final String user = c.getString(c
								.getColumnIndex("user"));
						if (!TextUtils.isEmpty(user)) {
							LogUtil.i("",
									"=====================>代理："
											+ c.getString(c
													.getColumnIndex("proxy")));
							if (user.startsWith(CTWAP)) {
								LogUtil.i("", "=====================>电信wap网络");
								return TYPE_CT_WAP;
							}
						}
					}
					c.close();

					// 注意三：
					// 判断是移动联通wap:
					// 其实还有一种方法通过getString(c.getColumnIndex("proxy")获取代理ip
					// 来判断接入点，10.0.0.172就是移动联通wap，10.0.0.200就是电信wap，但在
					// 实际开发中并不是所有机器都能获取到接入点代理信息，例如魅族M9 （2.2）等...
					// 所以采用getExtraInfo获取接入点名字进行判断

					String netMode = mobNetInfoActivity.getExtraInfo();
					LogUtil.i("", "netMode ================== " + netMode);
					if (netMode != null) {
						// 通过apn名称判断是否是联通和移动wap
						netMode = netMode.toLowerCase();
						if (netMode.equals(CMWAP) || netMode.equals(WAP_3G)
								|| netMode.equals(UNIWAP)) {
							LogUtil.i("", "=====================>移动联通wap网络");
							return TYPE_CM_CU_WAP;
						}

					}

				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return TYPE_NET;
		}

		return TYPE_NET;

	}

}
