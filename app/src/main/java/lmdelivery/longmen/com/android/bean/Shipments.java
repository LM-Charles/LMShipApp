package lmdelivery.longmen.com.android.bean;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.List;

/**
 * Created by rzhu on 7/24/2015.
 */
@Table(name = "Shipments")
public class Shipments extends Model {
    int id;
    double height;
    double width;
    double length;
    double weight;
    String trackingNumber;
    String trackingDocumentType;
    String shipmentPackageType;
    Tracking tracking;
    String displayLengthPreference;
    String displayWeightPreference;

    public int getShipmentId() {
        return id;
    }

    public void setShipmentId(int id) {
        this.id = id;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public String getTrackingDocumentType() {
        return trackingDocumentType;
    }

    public void setTrackingDocumentType(String trackingDocumentType) {
        this.trackingDocumentType = trackingDocumentType;
    }

    public String getShipmentPackageType() {
        return shipmentPackageType;
    }

    public void setShipmentPackageType(String shipmentPackageType) {
        this.shipmentPackageType = shipmentPackageType;
    }

    public Tracking getTracking() {
        return tracking;
    }

    public void setTracking(Tracking tracking) {
        this.tracking = tracking;
    }

    public String getDisplayLengthPreference() {
        return displayLengthPreference;
    }

    public void setDisplayLengthPreference(String displayLengthPreference) {
        this.displayLengthPreference = displayLengthPreference;
    }

    public String getDisplayWeightPreference() {
        return displayWeightPreference;
    }

    public void setDisplayWeightPreference(String displayWeightPreference) {
        this.displayWeightPreference = displayWeightPreference;
    }
}
