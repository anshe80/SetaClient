package com.sys.android.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.seta.android.xmppmanager.XmppConnection;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

/**
 * 工具类
 */
public class Utils {
	/**
	 * 根据jid获取用户名
	 */
	public static String getJidToUsername(String jid){
		if(jid!=null)
			return jid.split("@")[0];
		else
			return null;
	}
	
	public static String getServerNameToUsername(String jid){
		if(jid!=null)
			return jid.substring(jid.indexOf("/") + 1);
		else
			return null;
	}
	
	public static String getUserNameToJid(String username){
		return username + "@" + XmppConnection.SERVER_NAME;
	}
	
	public static int getScreenWidth(Context context) {
		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		return display.getWidth();
	}

	public static int getScreenHeight(Context context) {
		WindowManager manager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		return display.getHeight();
	}

	public static float getScreenDensity(Context context) {
		try {
			DisplayMetrics dm = new DisplayMetrics();
			WindowManager manager = (WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE);
			manager.getDefaultDisplay().getMetrics(dm);
			return dm.density;
		} catch (Exception ex) {

		}
		return 1.0f;
	}
	
	// 判断手机格式是否正确
	public static boolean isMobileNum(String mobiles) {
		Pattern p = Pattern
				.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);

		return m.matches();
	}

	// 判断email格式是否正确
	public static boolean isEmail(String email) {
		String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(email);

		return m.matches();
	}

	// 判断是否全是数字
	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}
	
	/**
	 * 正则表达式-判断第一个字符是否为标点或者空格
	 */
	public static boolean isStartWithPunctuation(String str){ 
	    Pattern pattern = Pattern.compile("[\\s\\p{P}]"); 
	    return pattern.matcher(str.substring(0, 1)).matches();    
	 } 

}
