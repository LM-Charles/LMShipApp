package lmdelivery.longmen.com.android.util;

/**
 * Created by rzhu on 9/16/2015.
 */
public class NumberUtil {

    public static double toDouble(String str){
        try {
            return Double.parseDouble(str);
        }catch (Exception e){
            return 0.00;
        }

    }
}
