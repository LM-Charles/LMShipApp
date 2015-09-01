package lmdelivery.longmen.com.android.api;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import lmdelivery.longmen.com.android.bean.*;
import lmdelivery.longmen.com.android.bean.Package;
import lmdelivery.longmen.com.android.util.Logger;

/**
 * Created by rzhu on 8/31/2015.
 */
public class Rate {
    private static final String TAG = Rate.class.getSimpleName();

    public static JSONArray buildBoxJson(ArrayList<Package> packageArrayList){

        JSONArray shipments = new JSONArray();

        for(lmdelivery.longmen.com.android.bean.Package aPackage : packageArrayList){
            try {
                JSONObject shipment = new JSONObject();
                if(aPackage.isOwnBox()){
                    shipment.put("height", aPackage.getHeight());
                    shipment.put("width", aPackage.getWidth());
                    shipment.put("length", aPackage.getLength());
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
                shipment.put("weight", aPackage.getWeight());
                shipment.put("goodCategoryType", aPackage.getCategory());
                //TODO: add unit after desmond provide it in the api

                shipments.put(shipment);
            }catch (Exception e){
                Logger.e(TAG, "Failed to create shipment json");
            }
        }
        return shipments;
    }
}
