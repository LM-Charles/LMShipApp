package lmdelivery.longmen.com.android.bean;

/**
 * Created by rzhu on 6/30/2015.
 */
public class RateItem {

    private String courierServiceId, service_icon_url, lmCategory, estimate, estimatedDelivery, courierName, serviceName;

    public RateItem(String courierServiceId, String service_icon_url, String lmCategory, String estimate, String estimatedDelivery, String courierName, String serviceName) {
        this.courierServiceId = courierServiceId;
        this.service_icon_url = service_icon_url;
        this.lmCategory = lmCategory;
        this.estimate = estimate;
        this.estimatedDelivery = estimatedDelivery;
        this.courierName = courierName;
        this.serviceName = serviceName;
    }

    public String getCourierServiceId() {
        return courierServiceId;
    }

    public void setCourierServiceId(String courierServiceId) {
        this.courierServiceId = courierServiceId;
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
}
