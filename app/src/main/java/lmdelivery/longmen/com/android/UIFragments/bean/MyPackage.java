package lmdelivery.longmen.com.android.UIFragments.bean;

/**
 * Created by Kaiyu on 2015-06-16.
 */

import android.content.res.Resources;

import lmdelivery.longmen.com.android.MyApplication;
import lmdelivery.longmen.com.android.R;
public class MyPackage {
    public static final int SMALL_BOX = 0;
    public static final int MED_BOX = 1;
    public static final int BIG_BOX = 2;
    private String length, width, height, weight;
    private int boxSize;
    private boolean isOwnBox;
    private boolean showValidation;



    public MyPackage() {
        this.length = "";
        this.width = "";
        this.height = "";
        this.weight = "";
        this.boxSize = 0;
        this.isOwnBox = true;
        this.showValidation = false;
    }

    @Override public String toString(){
        if(isOwnBox){
            Resources resources = MyApplication.getAppContext().getResources();
            return resources.getString(R.string.length) + ": " + length + "  " + resources.getString(R.string.width) + ": " + width + "\n" +
                    resources.getString(R.string.height) + ": " + height + "  " + resources.getString(R.string.weight) + ": " + weight;
        }else{
            String strBoxSize = "";
            switch (boxSize){
                case SMALL_BOX:
                    strBoxSize = MyApplication.getAppContext().getString(R.string.small_box);
                    break;
                case MED_BOX:
                    strBoxSize = MyApplication.getAppContext().getString(R.string.med_box);
                    break;
                case BIG_BOX:
                    strBoxSize = MyApplication.getAppContext().getString(R.string.big_box);
                    break;
            }
            return "Longmen " + strBoxSize + " ($5)";
        }
    }

    public boolean isShowValidation() {
        return showValidation;
    }

    public void setShowValidation(boolean showValidation) {
        this.showValidation = showValidation;
    }

    public int getBoxSize() {
        return boxSize;
    }

    public void setBoxSize(int boxSize) {
        this.boxSize = boxSize;
    }

    public boolean isOwnBox() {
        return isOwnBox;
    }

    public void setIsOwnBox(boolean isOwnBox) {
        this.isOwnBox = isOwnBox;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

}
