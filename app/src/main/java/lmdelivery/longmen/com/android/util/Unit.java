package lmdelivery.longmen.com.android.util;

/**
 * Created by rzhu on 8/21/2015.
 */
public class Unit {

    public static final int CM = 0;
    public static final int INCH = 1;
    public static final int LB = 2;
    public static final int KG = 3;

    public static double kgTolb(double kg){return kg * 2.20462;}
    public static double lbTokg(double lb){
        return lb * 0.453592;
    }
    public static double cmToinch(double cm){
        return cm * 0.393701;
    }
    public static double inchTocm(double inch){
        return inch * 2.54;
    }

    public static String getUnitString(int type){
        switch (type){
            case CM:
                return "CM";
            case INCH:
                return "INCH";
            case LB:
                return "LB";
            case KG:
                return "KG";
            default:
                return "";
        }
    }
}
