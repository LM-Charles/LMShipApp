package lmdelivery.longmen.com.android.fragments.bean;

/**
 * Created by rzhu on 6/30/2015.
 */
public class RateItem {

    String courierServiceId, serviceIcon, lmCategory, estimatePrice, estimatedDeliveryDate, courierName, serviceName;

    public RateItem(String courierServiceId, String serviceIcon, String lmCategory, String estimatePrice, String estimatedDeliveryDate, String courierName, String serviceName) {
        this.courierServiceId = courierServiceId;
        this.serviceIcon = serviceIcon;
        this.lmCategory = lmCategory;
        this.estimatePrice = estimatePrice;
        this.estimatedDeliveryDate = estimatedDeliveryDate;
        this.courierName = courierName;
        this.serviceName = serviceName;
    }

    public String getCourierServiceId() {
        return courierServiceId;
    }

    public void setCourierServiceId(String courierServiceId) {
        this.courierServiceId = courierServiceId;
    }

    public String getServiceIcon() {
        return serviceIcon;
    }

    public void setServiceIcon(String serviceIcon) {
        this.serviceIcon = serviceIcon;
    }

    public String getLmCategory() {
        return lmCategory;
    }

    public void setLmCategory(String lmCategory) {
        this.lmCategory = lmCategory;
    }

    public String getEstimatePrice() {
        return estimatePrice;
    }

    public void setEstimatePrice(String estimatePrice) {
        this.estimatePrice = estimatePrice;
    }

    public String getEstimatedDeliveryDate() {
        return estimatedDeliveryDate;
    }

    public void setEstimatedDeliveryDate(String estimatedDeliveryDate) {
        this.estimatedDeliveryDate = estimatedDeliveryDate;
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
}
