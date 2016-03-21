package lmdelivery.longmen.com.android;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Kaiyu on 2015-06-10.
 */
public class Constant {

//    public static final String REST_URL = "http://lmshipservice-devo.elasticbeanstalk.com/rest/";
//    public static final String ENDPOINT = "http://lmshipservice-devo.elasticbeanstalk.com";
    public static final String ENDPOINT = "https://lmshipservice-prod.longmenservice.com";
    public static final String REST_URL = ENDPOINT + "/rest/";
    public static final String ENDPOINT_GOOGLE = "https://maps.googleapis.com";
    public static final String GOOGLE_PLACE_API_SERVER_KEY = "AIzaSyCeiPMf730xLzLtE6OJ4wslNCvtSrpVPlA";

    public static final String EXTRA_PICKUP = "EXTRA_PICKUP";
    public static final String EXTRA_DROPOFF = "EXTRA_DROPOFF";
    public static final String EXTRA_PACKAGE = "EXTRA_PACKAGE";
    public static final String EXTRA_TIME = "EXTRA_TIME";
    public static final String EXTRA_RATE_ITEM = "EXTRA_RATE_ITEM";
    public static final String EXTRA_INSURANCE_VALUE = "EXTRA_INSURANCE_VALUE";
    public static final String EXTRA_ESTIMATE_VALUE = "EXTRA_ESTIMATE_VALUE";
    public static final String EXTRA_INSURANCE_ITEM = "EXTRA_INSURANCE_ITEM";
    public static final String EXTRA_PACKAGE_ITEM = "EXTRA_PACKAGE_ITEM";
    public static final String EXTRA_TRACK_DETAIL = "EXTRA_TRACK_DETAIL";

    public static final int FAB_ANIMTION_DURATION = 500;
    public static final int MAX_CITY_LENGTH = 15;
    public static final int MAX_PROVINCE_LENGTH = 15;
    public static final int PASSWORD_MIN_LENGTH = 6;

    public static final String SHARE_NAME = "LMSHARE";
    public static final String SHARE_USER_EMAIL = "SHARE_USER_EMAIL";
    public static final String SHARE_USER_TOKEN = "SHARE_USER_TOKEN";
    public static final String SHARE_USER_PHONE = "SHARE_USER_PHONE";
    public static final String SHARE_IS_USER_ACTIVATED = "SHARE_IS_USER_ACTIVATED";
    public static final String SHARE_USER_ID = "SHARE_USER_ID";
    public static final String SHARE_WEIGHT_UNIT = "SHARE_WEIGHT_UNIT";
    public static final String SHARE_LENGTH_UNIT = "SHARE_LENGTH_UNIT";

    public static final int TAB_FROM = 0;
    public static final int TAB_TO = 1;
    public static final int TAB_PACKAGE = 2;
    public static final int TAB_TIME = 3;
    public static final int TAB_INSURANCE = 4;
    public static final int TAB_SUMMARY = 5;


    public static final int LOGIN_REQUEST_CODE = 0;


    public static final String[] CATEGORY_ARR = {"REGULAR", "LUXURY", "COSMETICS", "DOCUMENT"};

    public static final Set<String> citiesInVan = new HashSet<>();
    public static final int MAX_INSURANCE_VALUE = 1000;
    public static final int MAX_DECLARE_VALUE = 100000;
    static {
        citiesInVan.add("VANCOUVER");
        citiesInVan.add("NORTH VANCOUVER");
        citiesInVan.add("WEST VANCOUVER");
        citiesInVan.add("RICHMOND");
        citiesInVan.add("SURREY");
        citiesInVan.add("DELTA");
        citiesInVan.add("BURNABY");
        citiesInVan.add("COQUITLAM");
    }

}
