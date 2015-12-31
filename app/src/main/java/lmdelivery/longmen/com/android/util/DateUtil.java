package lmdelivery.longmen.com.android.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import lmdelivery.longmen.com.android.AppController;
import lmdelivery.longmen.com.android.R;

/**
 * Created by rufuszhu on 15-09-07.
 */
public class DateUtil {

    public static String UnixTimeToDateString(String timeInMillis){
        try {
            Date date = new java.util.Date(Long.parseLong(timeInMillis));
            SimpleDateFormat fmtOut = new SimpleDateFormat("E, MMM d, yyyy", Locale.US);
            return fmtOut.format(date);
        }catch (Exception e){
            return AppController.getAppContext().getString(R.string.unknown);
        }
    }

    public static String DateToString(Date date){
        try {
            SimpleDateFormat fmtOut = new SimpleDateFormat("MMM d, yyyy", Locale.US);
            return fmtOut.format(date);
        }catch (Exception e){
            return AppController.getAppContext().getString(R.string.unknown);
        }
    }

}
