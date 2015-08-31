package lmdelivery.longmen.com.android.bean;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

@Table(name = "MyTime")
public class MyTime implements Serializable{
    @Column(name = "TimeString")
    private String timeString;
    @Column(name = "IsToday")
    private boolean isToday;
    @Column(name = "TimeCatergory")
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

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MyTime))
            return false;
        if (obj == this)
            return true;

        MyTime rhs = (MyTime) obj;
        return timeCatergory == rhs.getTimeCatergory() && (isToday==rhs.isToday);
    }

    public long getUnixDate(){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        Date today = cal.getTime();
        if(isToday){
            return today.getTime()/1000L;
        }else{
            cal.add(Calendar.DAY_OF_YEAR, 1);
            Date tomorrow = cal.getTime();
            return tomorrow.getTime()/1000L;
        }
    }
    public String getSlot(){
        return "SLOT_" + (++timeCatergory);
    }
}
