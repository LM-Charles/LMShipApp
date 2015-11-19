package lmdelivery.longmen.com.android.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import lmdelivery.longmen.com.android.AppController;
import lmdelivery.longmen.com.android.data.*;
import lmdelivery.longmen.com.android.data.Package;
import lmdelivery.longmen.com.android.util.CountryCode;
import lmdelivery.longmen.com.android.util.Logger;

/**
 * Created by rzhu on 8/31/2015.
 */
public class Rate {
    private static final String TAG = Rate.class.getSimpleName();

    public static JSONObject buildEstimateParam(Address pickupAddr, Address dropOffAddr, ArrayList<Package> packageArrayList,
                                                MyTime selectedTime, String declareValue, String insuranceValue){
        JSONObject params = new JSONObject();
        try {
            params.put("client", AppController.getInstance().getUserId());

            JSONObject pickup = new JSONObject();
            pickup.put("address", pickupAddr.getStreetName());
            pickup.put("address2", pickupAddr.getUnitNumber());
            pickup.put("city", pickupAddr.getCity());
            pickup.put("province", pickupAddr.getProvince());
            pickup.put("country", "CA");
            pickup.put("postal", pickupAddr.getPostalCode());

            JSONObject dropOff = new JSONObject();
            dropOff.put("address", dropOffAddr.getStreetName());
            dropOff.put("address2", dropOffAddr.getUnitNumber());
            dropOff.put("city", dropOffAddr.getCity());
            dropOff.put("province", dropOffAddr.getProvince());
            dropOff.put("country", CountryCode.getCode(dropOffAddr.getCountry()));
            dropOff.put("postal", dropOffAddr.getPostalCode());

            params.put("fromAddress", pickup);
            params.put("toAddress", dropOff);

            params.put("shipments", Rate.buildBoxJson(packageArrayList));
            params.put("declareValue", declareValue);
            params.put("insuranceValue", insuranceValue);

            params.put("appointmentDate", selectedTime.getUnixDate());

            params.put("appointmentSlotType", selectedTime.getSlot());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return params;
    }

    public static JSONArray buildBoxJson(ArrayList<Package> packageArrayList){

        JSONArray shipments = new JSONArray();

        for(lmdelivery.longmen.com.android.data.Package aPackage : packageArrayList){

            try {
                JSONObject shipment = new JSONObject();
                if(aPackage.isOwnBox()){
                    shipment.put("height", aPackage.getHeightCM());
                    shipment.put("width", aPackage.getWidthCM());
                    shipment.put("length", aPackage.getLengthCM());
                    shipment.put("shipmentPackageType", "CUSTOM");
                }else{
                    shipment.put("height", "0");
                    shipment.put("width", "0");
                    shipment.put("length", "0");
                    switch (aPackage.getBoxSize()){
                        case Package.BIG_BOX:
                            shipment.put("shipmentPackageType", "LARGE");
                            break;
                        case Package.MED_BOX:
                            shipment.put("shipmentPackageType", "MEDIUM");
                            break;
                        case Package.SMALL_BOX:
                            shipment.put("shipmentPackageType", "SMALL");
                            break;
                    }
                }

                shipment.put("weight", aPackage.getWeightKG());
                shipment.put("goodCategoryType", aPackage.getCategory());
                shipments.put(shipment);
            }catch (Exception e){
                Logger.e(TAG, "Failed to create shipment json");
            }
        }
        return shipments;
    }
}
