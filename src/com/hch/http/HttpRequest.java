package com.hch.http;

import org.apache.http.HttpHost;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

public class HttpRequest extends AsyncTask<Object, Integer, Void>{
	private String tag = this.getClass().getSimpleName();
	public static final int TYPE_NET_WORK_DISABLED = 0;// 网络不可用
	public static final int TYPE_CM_CU_WAP = 4;// 移动联通wap10.0.0.172
	public static final int TYPE_CT_WAP = 5;// 电信wap 10.0.0.200
	public static final int TYPE_OTHER_NET = 6;// 电信,移动,联通,wifi 等net网络
	private Context context = null;

	DefaultHttpClient client = new DefaultHttpClient(params);
	
	public HttpRequest(Context context){
		this.context = context;
	}
	
	@Override
	protected Void doInBackground(Object... params) {
		if(checkNetworkType()==TYPE_CT_WAP){
    		//电信网络
    		HttpHost proxy = new HttpHost("10.0.0.200", 80);
			client.getHttpClient().getParams().setParameter(ConnRouteParams.DEFAULT_PROXY,
					proxy);
    	}else if(checkNetworkType()==TYPE_CM_CU_WAP){
    		//联通或者移动
    		HttpHost proxy = new HttpHost("10.0.0.172", 80);
			client.getHttpClient().getParams().setParameter(ConnRouteParams.DEFAULT_PROXY,
					proxy);
    	}else{
			//cmwap需要设置代理，而使用wifi和cmnet则不需要，设置后反而读不到数据
			//如果网络从gprs转变成wifi,采用默认的代理
			client.getHttpClient().getParams().setParameter(ConnRouteParams.DEFAULT_PROXY,
					android.net.Proxy.getDefaultHost());
    	}
		
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

				//Log.i("", "=====================>无网络");
				return TYPE_OTHER_NET;
			} else {

				// NetworkInfo不为null开始判断是网络类型

				int netType = mobNetInfoActivity.getType();
				if (netType == ConnectivityManager.TYPE_WIFI) {
					// wifi net处理
					//Log.i("", "=====================>wifi网络");
					return TYPE_OTHER_NET;
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
							Log.i("",
									"=====================>代理："
											+ c.getString(c
													.getColumnIndex("proxy")));
							if (user.startsWith(CTWAP)) {
								Log.i("", "=====================>电信wap网络");
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
					Log.i("", "netMode ================== " + netMode);
					if (netMode != null) {
						// 通过apn名称判断是否是联通和移动wap
						netMode = netMode.toLowerCase();
						if (netMode.equals(CMWAP) || netMode.equals(WAP_3G)
								|| netMode.equals(UNIWAP)) {
							Log.i("", "=====================>移动联通wap网络");
							return TYPE_CM_CU_WAP;
						}

					}

				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			return TYPE_OTHER_NET;
		}

		return TYPE_OTHER_NET;

	}

}
