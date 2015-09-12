package lmdelivery.longmen.com.android.bean;

/**
 * Created by Kaiyu on 2015-06-16.
 */

import android.content.res.Resources;
import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import lmdelivery.longmen.com.android.AppController;
import lmdelivery.longmen.com.android.Constant;
import lmdelivery.longmen.com.android.R;
import lmdelivery.longmen.com.android.util.Unit;
import lmdelivery.longmen.com.android.util.Util;

@Table(name = "Packages")
public class Package extends Model implements Parcelable {
    public static final int SMALL_BOX = 0;
    public static final int MED_BOX = 1;
    public static final int BIG_BOX = 2;

    public static final String SMALL_HEIGHT = "10";
    public static final String SMALL_WIDTH = "10";
    public static final String SMALL_LENGTH = "10";
    public static final String MED_HEIGHT = "10";
    public static final String MED_WIDTH = "10";
    public static final String MED_LENGTH = "10";
    public static final String BIG_HEIGHT = "10";
    public static final String BIG_WIDTH = "10";
    public static final String BIG_LENGTH = "10";

    @Column(name = "Length")
    private String length;
    @Column(name = "Width")
    private String width;
    @Column(name = "Height")
    private String height;
    @Column(name = "Weight")
    private String weight;
    @Column(name = "BoxSize")
    private int boxSize;
    @Column(name = "isOwnBox")
    private boolean isOwnBox;
    private boolean showValidation;
    @Column(name = "weightUnit")
    private int weightUnit;
    @Column(name = "distanceUnit")
    private int distanceUnit;
    @Column(name = "category")
    private String category;


    public Package() {
        this.length = "";
        this.width = "";
        this.height = "";
        this.weight = "";
        this.boxSize = 0;
        this.isOwnBox = false;
        this.showValidation = false;
        this.weightUnit = Unit.KG;
        this.distanceUnit = Unit.CM;
        this.category = Constant.CATEGORY_ARR[0];

    }

    @Override
    public String toString() {
        if (isOwnBox) {
            Resources resources = AppController.getAppContext().getResources();
            return resources.getString(R.string.length) + ": " + length + "  " + resources.getString(R.string.width) + ": " + width + "\n" +
                    resources.getString(R.string.height) + ": " + height + "  " + resources.getString(R.string.weight) + ": " + weight;
        } else {
            String strBoxSize = "";
            switch (boxSize) {
                case SMALL_BOX:
                    strBoxSize = AppController.getAppContext().getString(R.string.small_box);
                    break;
                case MED_BOX:
                    strBoxSize = AppController.getAppContext().getString(R.string.med_box);
                    break;
                case BIG_BOX:
                    strBoxSize = AppController.getAppContext().getString(R.string.big_box);
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

    public int getWeightUnit() {
        return weightUnit;
    }

    public String getWeightKG(){
        if (weightUnit==Unit.LB){
            return Util.roundTo2(Unit.lbTokg(Double.parseDouble(weight)));
        }else
            return String.valueOf(weight);
    }
    public String getHeightCM(){
        if (distanceUnit==Unit.INCH){
            return Util.roundTo2(Unit.inchTocm(Double.parseDouble(height)));
        }else
            return String.valueOf(height);
    }
    public String getWidthCM(){
        if (distanceUnit==Unit.INCH){
            return Util.roundTo2(Unit.inchTocm(Double.parseDouble(width)));
        }else
            return String.valueOf(width);
    }
    public String getLengthCM(){
        if (distanceUnit==Unit.INCH){
            return Util.roundTo2(Unit.inchTocm(Double.parseDouble(length)));
        }else
            return String.valueOf(length);
    }

    public void setWeightUnit(int weightUnit) {
        this.weightUnit = weightUnit;
    }

    public int getDistanceUnit() {
        return distanceUnit;
    }

    public void setDistanceUnit(int distanceUnit) {
        this.distanceUnit = distanceUnit;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(boxSize);
        dest.writeString(length);
        dest.writeString(width);
        dest.writeString(height);
        dest.writeString(weight);
        if (isOwnBox)
            dest.writeInt(1);
        else
            dest.writeInt(0);

        if (showValidation)
            dest.writeInt(1);
        else
            dest.writeInt(0);

        dest.writeInt(weightUnit);
        dest.writeInt(distanceUnit);
    }

    public Package(Parcel in) {
        boxSize = in.readInt();
        length = in.readString();
        width = in.readString();
        height = in.readString();
        weight = in.readString();
        isOwnBox = in.readInt() != 0;
        showValidation = in.readInt() != 0;
        weightUnit = in.readInt();
        distanceUnit = in.readInt();
    }

    public static final Parcelable.Creator<Package> CREATOR = new Parcelable.Creator<Package>() {
        public Package createFromParcel(Parcel in) {
            return new Package(in);
        }

        public Package[] newArray(int size) {
            return new Package[size];
        }
    };
}
