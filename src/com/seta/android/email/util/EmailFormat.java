package com.seta.android.email.util;

import java.io.IOException;  
import java.util.regex.Matcher;  
import java.util.regex.Pattern;  
import org.apache.log4j.Logger;

public class EmailFormat {
	
	private static final Logger logger = Logger.getLogger(EmailFormat.class);
    public static boolean isMobileNum(String mobiles){     
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");     
        Matcher m = p.matcher(mobiles);     
        logger.info(m.matches()+"---");     
        return m.matches();     
    } 
    
    public static boolean isEmail(String email){     
     String str="^([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)*@([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+[\\.][A-Za-z]{2,3}([\\.][A-Za-z]{2})?$";
        Pattern p = Pattern.compile(str);     
        Matcher m = p.matcher(email);     
        logger.info(m.matches()+"---");     
        return m.matches();     
    } 

}
