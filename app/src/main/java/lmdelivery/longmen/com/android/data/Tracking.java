package lmdelivery.longmen.com.android.data;

import java.io.Serializable;

/**
 * Created by rufus on 2015-09-22.
 */
public class Tracking implements Serializable{
    String pickupDate;
    String trackingDate;
    String trackingCity;
    String trackingCountry;
    String trackingStatus;
    String trackingURL;

    public String getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(String pickupDate) {
        this.pickupDate = pickupDate;
    }

    public String getTrackingDate() {
        return trackingDate;
    }

    public void setTrackingDate(String trackingDate) {
        this.trackingDate = trackingDate;
    }

    public String getTrackingCity() {
        return trackingCity;
    }

    public void setTrackingCity(String trackingCity) {
        this.trackingCity = trackingCity;
    }

    public String getTrackingCountry() {
        return trackingCountry;
    }

    public void setTrackingCountry(String trackingCountry) {
        this.trackingCountry = trackingCountry;
    }

    public String getTrackingStatus() {
        return trackingStatus;
    }

    public void setTrackingStatus(String trackingStatus) {
        this.trackingStatus = trackingStatus;
    }

    public String getTrackingURL() {
        return trackingURL;
    }

    public void setTrackingURL(String trackingURL) {
        this.trackingURL = trackingURL;
    }

}
