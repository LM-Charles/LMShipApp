package lmdelivery.longmen.com.android.UIFragments.bean;

/**
 * Created by Kaiyu on 2015-06-16.
 */
public class MyPackage {
    private String length, width, height, weight;
    private int boxType;

    public MyPackage(String length, String width, String height, String weight, int boxType) {
        this.length = length;
        this.width = width;
        this.height = height;
        this.weight = weight;
        this.boxType = boxType;
    }

    public MyPackage() {
        this.length = "";
        this.width = "";
        this.height = "";
        this.weight = "";
        this.boxType = 0;
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

    public int getBoxType() {
        return boxType;
    }

    public void setBoxType(int boxType) {
        this.boxType = boxType;
    }
}
