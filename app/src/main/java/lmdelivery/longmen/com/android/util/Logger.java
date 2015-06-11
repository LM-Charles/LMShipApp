package lmdelivery.longmen.com.android.util;

import android.util.Log;

public class Logger {
	private final static boolean IS_OPEN = true;
    private final static String TAG = "LMDelivery";
    
    public static void e(String tag, String log) {
        if(IS_OPEN) {
        	if(log!=null)
        		Log.e(tag, log);
        	else
        		Log.e(tag, "null");
        }
    }
    
    
    public static void e(String log) {
        if(IS_OPEN) {
        	if(log!=null)
        		Log.e(TAG, log);
        	else
        		Log.e(TAG, "null");
        }
    }
    
    public static void i(String tag, String log) {
        if(IS_OPEN) {
        	if(log!=null)
        		Log.i(tag, log);
        	else
        		Log.i(tag, "null");
        }
    }
    
    
    public static void i(String log) {
        if(IS_OPEN) {
        	if(log!=null)
        		Log.i(TAG, log);
        	else
        		Log.i(TAG, "null");
        }
    }
    
    public static void v(String tag, String log) {
        if(IS_OPEN) {
        	if(log!=null)
        		Log.v(tag, log);
        	else
        		Log.v(tag, "null");
        }
    }
    
    
    public static void v(String log) {
        if(IS_OPEN) {
        	if(log!=null)
        		Log.i(TAG, log);
        	else
        		Log.i(TAG, "null");
        }
    }


    public static void d(String tag, String log) {
        if(IS_OPEN) {
        	if(log!=null)
        		Log.d(tag, log);
        	else
        		Log.d(tag, "null");
        }
    }
    
    
    public static void d(String log) {
        if(IS_OPEN) {
        	if(log!=null)
        		Log.d(TAG, log);
        	else
        		Log.d(TAG, "null");
        }
    }
}
