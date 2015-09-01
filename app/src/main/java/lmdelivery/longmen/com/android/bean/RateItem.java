package lmdelivery.longmen.com.android.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by rzhu on 6/30/2015.
 */
public class RateItem implements Parcelable {

    private String service_icon_url, lmCategory, estimate, estimatedDelivery, courierName, serviceName;

    public RateItem( String service_icon_url, String lmCategory, String estimate, String estimatedDelivery, String courierName, String serviceName) {
        this.service_icon_url = service_icon_url;
        this.lmCategory = lmCategory;
        this.estimate = estimate;
        this.estimatedDelivery = estimatedDelivery;
        this.courierName = courierName;
        this.serviceName = serviceName;
    }

    public String getService_icon_url() {
        return service_icon_url;
    }

    public void setService_icon_url(String service_icon_url) {
        this.service_icon_url = service_icon_url;
    }

    public String getLmCategory() {
        return lmCategory;
    }

    public void setLmCategory(String lmCategory) {
        this.lmCategory = lmCategory;
    }

    public String getEstimate() {
        return estimate;
    }

    public void setEstimate(String estimate) {
        this.estimate = estimate;
    }

    public String getEstimatedDelivery() {
        return estimatedDelivery;
    }

    public void setEstimatedDelivery(String estimatedDelivery) {
        this.estimatedDelivery = estimatedDelivery;
    }

    public String getCourierName() {
        return courierName;
    }

    public void setCourierName(String courierName) {
        this.courierName = courierName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(service_icon_url);
        dest.writeString(lmCategory);
        dest.writeString(estimate);
        dest.writeString(estimatedDelivery);
        dest.writeString(courierName);
        dest.writeString(serviceName);
    }

    public RateItem(Parcel in) {
        service_icon_url = in.readString();
        lmCategory = in.readString();
        estimate = in.readString();
        estimatedDelivery = in.readString();
        courierName = in.readString();
        serviceName = in.readString();
    }

    public static final Parcelable.Creator<RateItem> CREATOR = new Parcelable.Creator<RateItem>() {
        public RateItem createFromParcel(Parcel in) {
            return new RateItem(in);
        }

        public RateItem[] newArray(int size) {
            return new RateItem[size];
        }
    };
}
