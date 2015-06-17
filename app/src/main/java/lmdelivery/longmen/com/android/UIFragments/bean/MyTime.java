package lmdelivery.longmen.com.android.UIFragments.bean;

public class MyTime {

    private String timeString;
    private boolean isToday;
    private int timeCatergory;
    private static final int TIME_0 = 0;// 9am-11am,
    private static final int TIME_1 = 1;
    private static final int TIME_2 = 2;
    private static final int TIME_3 = 3;


    public MyTime(String timeString, int timeCatergory, boolean isToday) {
        this.timeString = timeString;
        this.timeCatergory = timeCatergory;
        this.isToday = isToday;
    }

    public String getTimeString() {
        return timeString;
    }

    public void setTimeString(String timeString) {
        this.timeString = timeString;
    }

    public boolean isToday() {
        return isToday;
    }

    public void setIsToday(boolean isToday) {
        this.isToday = isToday;
    }

    public int getTimeCatergory() {
        return timeCatergory;
    }

    public void setTimeCatergory(int timeCatergory) {
        this.timeCatergory = timeCatergory;
    }
}
