package lmdelivery.longmen.com.android.bean;

/**
 * Created by rufus on 2015-09-22.
 */
public class TrackingDetail {
    int id;
    int client;
    String orderDate;
    String courierServiceType;
    Shipments[] shipments;
    double estimateCost;
    double finalCost;
    Address fromAddress;
    Address toAddress;
    String handler;
    Double declareValue;
    Double insuranceValue;
    String appointmentDate;
    String appointmentSlotType;
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

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
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

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(String appointmentDate) {
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
