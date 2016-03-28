package lmdelivery.longmen.com.android.util;

/**
 * Created by rzhu on 3/2/2016.
 */
public class StringUtil {

    public static String getFirstName(String name){
        String names[] = name.split(" ");
        if (names.length>1){
            return names[0];
        }
        return name;
    }

    public static String getLastName(String name){
        String names[] = name.split(" ");
        if (names.length>1){
            return names[1];
        }
        return "";
    }
}
