package lmdelivery.longmen.com.android.bean;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.List;

/**
 * Created by rzhu on 7/24/2015.
 */
@Table(name = "Shipments")
public class Shipment extends Model {
    //private String date, fromAddress, fromCity, fromProvince, fromCode, fromCountry, toAddress, toCity, toProvince, toCode, toCountry, courierServiceId;
    @Column(name = "PickupAddress")
    public Address pickupAddr;
    @Column(name = "DropoffAddress")
    public Address dropoffAddr;
    @Column(name = "courierServiceId")
    public String courierServiceId;

    public List<Package> packages(){
        return getMany(Package.class, "Package");
    }

    @Column(name = "Time")
    public MyTime time;

    @Column(name = "OrderId")
    public int orderId;

    @Column(name = "Status")
    public String status;

    @Column(name = "ServiceIconURL")
    public String serviceIconUrl;

    @Column(name = "Type")
    public String type;

    @Column(name = "NickName")
    public String nickName;

    public boolean isEmpty;

    public Shipment(String courierServiceId, Address pickup, Address dropoff, MyTime time) {
        super();
        this.pickupAddr = pickup;
        this.dropoffAddr = dropoff;
        this.time = time;
        this.courierServiceId = courierServiceId;
        isEmpty = false;
    }

    /**
     * Use this factory method to create a new instance of
     * empty shipment
     *
     * @return A new instance of fragment RateItemFragment.
     */
    public static Shipment newEmptyShipmentInstance() {
        Shipment shipment = new Shipment();
        shipment.isEmpty = true;
        return shipment;
    }

    public static Shipment newFakeShipmentInstance() {
        Shipment shipment = new Shipment();
        shipment.nickName = "Books";
        shipment.serviceIconUrl = "https://pbs.twimg.com/profile_images/605379702564331521/1Nq_eDu4.jpg";
        shipment.status = "Delivered";
        return shipment;
    }

    public Shipment(){

    }

}
