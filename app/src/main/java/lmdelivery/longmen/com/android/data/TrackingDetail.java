package lmdelivery.longmen.com.android.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by rufus on 2015-09-22.
 */
public class TrackingDetail implements Serializable{
    @SerializedName("id")
    @Expose
    int id;
    @Expose
    int client;
    @Expose
    Date orderDate;
    @Expose
    String courierServiceType;

    public String getService_icon_url() {
        return service_icon_url;
    }

    public void setService_icon_url(String service_icon_url) {
        this.service_icon_url = service_icon_url;
    }

    @Expose
    String service_icon_url;
    @Expose
    Shipments[] shipments;
    @Expose
    double estimateCost;
    @Expose
    double finalCost;
    @Expose
    Address fromAddress;
    @Expose
    Address toAddress;
    @Expose
    String handler;
    @Expose
    Double declareValue;

    @Override
    public String toString() {
        return "TrackingDetail{" +
                "id=" + id +
                ", client=" + client +
                ", orderDate=" + orderDate +
                ", courierServiceType='" + courierServiceType + '\'' +
                ", service_icon_url='" + service_icon_url + '\'' +
                ", shipments=" + Arrays.toString(shipments) +
                ", estimateCost=" + estimateCost +
                ", finalCost=" + finalCost +
                ", fromAddress=" + fromAddress +
                ", toAddress=" + toAddress +
                ", handler='" + handler + '\'' +
                ", declareValue=" + declareValue +
                ", insuranceValue=" + insuranceValue +
                ", appointmentDate=" + appointmentDate +
                ", appointmentSlotType='" + appointmentSlotType + '\'' +
                ", orderStatusModel=" + orderStatusModel +
                '}';
    }

    @Expose
    Double insuranceValue;
    @Expose
    Date appointmentDate;
    @Expose
    String appointmentSlotType;
    @Expose
    OrderStatusModel orderStatusModel;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClient() {
        return client;
    }

    public void setClient(int client) {
        this.client = client;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public String getCourierServiceType() {
        return courierServiceType;
    }

    public void setCourierServiceType(String courierServiceType) {
        this.courierServiceType = courierServiceType;
    }

    public Shipments[] getShipments() {
        return shipments;
    }

    public void setShipments(Shipments[] shipments) {
        this.shipments = shipments;
    }

    public double getEstimateCost() {
        return estimateCost;
    }

    public void setEstimateCost(double estimateCost) {
        this.estimateCost = estimateCost;
    }

    public double getFinalCost() {
        return finalCost;
    }

    public void setFinalCost(double finalCost) {
        this.finalCost = finalCost;
    }

    public Address getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(Address fromAddress) {
        this.fromAddress = fromAddress;
    }

    public Address getToAddress() {
        return toAddress;
    }

    public void setToAddress(Address toAddress) {
        this.toAddress = toAddress;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public Double getDeclareValue() {
        return declareValue;
    }

    public void setDeclareValue(Double declareValue) {
        this.declareValue = declareValue;
    }

    public Double getInsuranceValue() {
        return insuranceValue;
    }

    public void setInsuranceValue(Double insuranceValue) {
        this.insuranceValue = insuranceValue;
    }

    public Date getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(Date appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getAppointmentSlotType() {
        return appointmentSlotType;
    }

    public void setAppointmentSlotType(String appointmentSlotType) {
        this.appointmentSlotType = appointmentSlotType;
    }

    public OrderStatusModel getOrderStatusModel() {
        return orderStatusModel;
    }

    public void setOrderStatusModel(OrderStatusModel orderStatusModel) {
        this.orderStatusModel = orderStatusModel;
    }
}
