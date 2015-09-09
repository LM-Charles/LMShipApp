package lmdelivery.longmen.com.android.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by rufuszhu on 15-09-07.
 */
public class DateUtil {

    public static String UnixTimeToDateString(String timeInMillis){
        Date date = new java.util.Date(Long.parseLong(timeInMillis));
        SimpleDateFormat fmtOut = new SimpleDateFormat("E, MMM d, yyyy", Locale.US);
        return fmtOut.format(date);

    }
}
