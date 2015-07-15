package lmdelivery.longmen.com.android;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Kaiyu on 2015-06-10.
 */
public class Constant {
    public static final String URL = "http://lmshipservice-devo.elasticbeanstalk.com/rest/";
    public static final String GOOGLE_PLACE_API_SERVER_KEY = "AIzaSyCeiPMf730xLzLtE6OJ4wslNCvtSrpVPlA";

    public static final String EXTRA_PICKUP = "EXTRA_PICKUP";
    public static final String EXTRA_DROPOFF = "EXTRA_DROPOFF";
    public static final String EXTRA_PACKAGE = "EXTRA_PACKAGE";
    public static final String EXTRA_TIME = "EXTRA_TIME";

    public static final int FAB_ANIMTION_DURATION = 1000;
    public static final int MAX_CITY_LENGTH = 15;
    public static final int MAX_PROVINCE_LENGTH = 15;

    public static final String SHARE_USER_EMAIL = "SHARE_USER_EMAIL";
    public static final String SHARE_USER_TOKEN = "SHARE_USER_TOKEN";
    public static final String SHARE_USER_PHONE = "SHARE_USER_PHONE";
    public static final String SHARE_USER_ID = "SHARE_USER_ID";

    public static final int TAB_FROM = 0;
    public static final int TAB_TO = 1;
    public static final int TAB_PACKAGE = 2;
    public static final int TAB_TIME = 3;
    public static final int TAB_SUMMARY = 4;

    public static final Set<String> citiesInVan = new HashSet<>();

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
