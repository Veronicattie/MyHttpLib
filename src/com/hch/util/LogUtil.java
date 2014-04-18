package com.hch.util;

import android.content.Context;
import android.widget.Toast;

/**
 * 这个是日志打印工具类，在项目正式发布时，将isPrint设置为false则所有的日志不会打印在控制台
 * 
 * @author 贺承欢
 * 
 */
public class LogUtil {
	// TODO ***********************SDK发布时请将此变量设置为私有的 **********************************
	private final static boolean isPrint = true;
	// 增加一个test属性用于防止测试代码因疏忽导致没有关闭
	public final static boolean test = isPrint;
	// TODO ***********************SDK发布时请将上面变量设置为私有的 **********************************
	public static void i(String tag, String message) {
		if (isPrint) {
			if (tag != null && message != null && !"".equals(tag.trim())
					&& !"".equals(message.trim())) {
				android.util.Log.i(tag, message);
			}
		}
	}

	public static void d(String tag, String message) {
		if (isPrint) {
			if (tag != null && message != null && !"".equals(tag.trim())
					&& !"".equals(message.trim())) {
				android.util.Log.d(tag, message);
			}
		}
	}

	public static void e(String tag, String message) {
		if (isPrint) {
			if (tag != null && message != null && !"".equals(tag.trim())
					&& !"".equals(message.trim())) {
				android.util.Log.e(tag, message);
			}
		}
	}

	public static void w(String tag, String message) {
		if (isPrint) {
			if (tag != null && message != null && !"".equals(tag.trim())
					&& !"".equals(message.trim())) {
				android.util.Log.w(tag, message);
			}
		}
	}

	public static void e(Exception e) {
		if (isPrint) {
			if (e != null) {
				e.printStackTrace();
			}
		}
	}

	public static void showToast(Context context, String content) {
		if (isPrint) {
			if (context != null && content != null)
				Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
		}
	}

//	public static void testPath(int state) {
//		if (isPrint) {
//			if (state ==0) {
//				// 测试线上的
//				Config.BASE_REQUEST_URL = "http://fw.immob.cn";
//				Config.OTHER_REQUEST_URL = "http://api.immob.cn";
//				Config.ISCOLOSED_REQUEST_URL = "http://adserving.immob.cn";
////				Config.BASE_REQUEST_URL = "http://59.151.106.149:8088";
////				Config.OTHER_REQUEST_URL = "http://59.151.106.149:8088";
////				Config.ISCOLOSED_REQUEST_URL = "http://59.151.106.149:8088";
//			} else if(state==1){
//				// 测试线下的
//				Config.BASE_REQUEST_URL = "http://192.168.1.182:8089";
//				Config.OTHER_REQUEST_URL = "http://192.168.1.182:8089";
//				Config.ISCOLOSED_REQUEST_URL = "http://192.168.1.182:8089";
//			}else if(state==2){
//				// 测试线下的
//				Config.BASE_REQUEST_URL = "http://192.168.1.182:8088";
//				Config.OTHER_REQUEST_URL = "http://192.168.1.182:8088";
//				Config.ISCOLOSED_REQUEST_URL = "http://192.168.1.182:8088";
//			}
//			Config.REQUEST_AD_URL = Config.BASE_REQUEST_URL + "/reqad.im";
//			/** 请求广告framework **/
//			Config.REQUEST_FRAMEWORK_URL = Config.BASE_REQUEST_URL + "/getfw";
//			/** 安装上报 **/
//			Config.REQUEST_RESPORTINSTALL_URL = Config.BASE_REQUEST_URL
//					+ "/score/addscore";
//			/** 查询积分墙是否已关闭 **/
//			Config.REQUEST_REQUEST_TURNOFFAD = Config.ISCOLOSED_REQUEST_URL
//					+ "/querystat";
//			/** 查询积分墙积分 **/
//			Config.REQUEST_QUERY_ADWALL_SCORE = Config.OTHER_REQUEST_URL
//					+ "/score/getscore";
//			/** 减少积分墙积分 **/
//			Config.REQUEST_REDU_ADWALL_SCORE = Config.OTHER_REQUEST_URL
//					+ "/score/reducescore";
//			/** 用于积分类型回调 **/
//			Config.REQUEST_CALLBACKADSCORE = Config.OTHER_REQUEST_URL+"/callbackadscore";
//			/** 用于非积分类型回调 **/
//			Config.REQUEST_CALLBACKACTION = Config.OTHER_REQUEST_URL+"/callbackaction";
//		}
//	}
}
