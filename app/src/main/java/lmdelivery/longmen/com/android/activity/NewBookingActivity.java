package lmdelivery.longmen.com.android.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lmdelivery.longmen.com.android.AppController;
import lmdelivery.longmen.com.android.Constant;
import lmdelivery.longmen.com.android.R;
import lmdelivery.longmen.com.android.bean.Address;
import lmdelivery.longmen.com.android.bean.MyTime;
import lmdelivery.longmen.com.android.bean.Package;
import lmdelivery.longmen.com.android.fragments.DestinationFragment;
import lmdelivery.longmen.com.android.fragments.InsuranceFragment;
import lmdelivery.longmen.com.android.fragments.PackageFragment;
import lmdelivery.longmen.com.android.fragments.PickupFragment;
import lmdelivery.longmen.com.android.fragments.SummaryFragment;
import lmdelivery.longmen.com.android.fragments.TimeFragment;
import lmdelivery.longmen.com.android.util.Logger;
import lmdelivery.longmen.com.android.util.Util;


public class NewBookingActivity extends AppCompatActivity implements TimeFragment.OnTimeSelectedListener, GoogleApiClient.OnConnectionFailedListener {
    private static final java.lang.String TAG = NewBookingActivity.class.getName();
    private JsonObjectRequest getRateRequest;
    private JsonObjectRequest bookShipRequest;

    private TabLayout tabLayout;
    public PickupFragment pickupFragment;
    public DestinationFragment dropOffFragment;
    public PackageFragment packageFragment;
    public InsuranceFragment insuranceFragment;
    public TimeFragment timeFragment;
    public SummaryFragment summaryFragment;


    public FloatingActionButton fab;
    private ViewPager viewPager;
    public ArrayList<Package> packageArrayList;

    public MyTime selectedTime;
    public Address pickupAddr;
    public Address dropOffAddr;
    private Adapter adapter;

    private Context context;

    /**
     * GoogleApiClient wraps our service connection to Google Play Services and provides access
     * to the user's sign in state as well as the Google's APIs.
     */
    public GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_booking);

        init();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        final ActionBar ab = getSupportActionBar();
        if(ab!=null)
            ab.setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.getTabAt(Constant.TAB_FROM).setIcon(R.drawable.shape_trans_dot);
        tabLayout.getTabAt(Constant.TAB_PACKAGE).setIcon(R.drawable.shape_trans_dot);
        tabLayout.getTabAt(Constant.TAB_TIME).setIcon(R.drawable.shape_trans_dot);
        tabLayout.getTabAt(Constant.TAB_TO).setIcon(R.drawable.shape_trans_dot);
        tabLayout.getTabAt(Constant.TAB_INSURANCE).setIcon(R.drawable.shape_trans_dot);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == Constant.TAB_SUMMARY) {
                    setupDoneFab();
                } else {
                    setupNextFab();
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int currentTab = tab.getPosition();
                switch (currentTab) {
                    case Constant.TAB_FROM:
                        if (pickupFragment.saveAndValidate()) {
                            tabLayout.getTabAt(Constant.TAB_FROM).setIcon(R.drawable.shape_greendot);
                        } else {
                            tabLayout.getTabAt(Constant.TAB_FROM).setIcon(R.drawable.shape_yellowdot);
                        }
                        break;

                    case Constant.TAB_TO:
                        if (dropOffFragment.saveAndValidate()) {
                            tabLayout.getTabAt(Constant.TAB_TO).setIcon(R.drawable.shape_greendot);
                        } else {
                            tabLayout.getTabAt(Constant.TAB_TO).setIcon(R.drawable.shape_yellowdot);
                        }
                        break;

                    case Constant.TAB_PACKAGE:
                        if (packageFragment.validateAllPackage()) {
                            tabLayout.getTabAt(Constant.TAB_PACKAGE).setIcon(R.drawable.shape_greendot);
                        } else {
                            tabLayout.getTabAt(Constant.TAB_PACKAGE).setIcon(R.drawable.shape_yellowdot);
                        }
                        break;

                    case Constant.TAB_TIME:
                        if (selectedTime != null) {
                            tabLayout.getTabAt(Constant.TAB_TIME).setIcon(R.drawable.shape_greendot);
                        } else {
                            tabLayout.getTabAt(Constant.TAB_TIME).setIcon(R.drawable.shape_yellowdot);
                        }
                        break;

                    case Constant.TAB_INSURANCE:
                        tabLayout.getTabAt(Constant.TAB_INSURANCE).setIcon(R.drawable.shape_greendot);

                        break;
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        fab = (FloatingActionButton) findViewById(R.id.fab);
        setupNextFab();

    }

    private void setupDoneFab() {

        if (summaryFragment.setupView()) {
            fab.setImageResource(R.drawable.ic_done);
            fab.setBackgroundTintList(getResources().getColorStateList(R.color.done_state_list));
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, SelectProductActivity.class);
                    intent.putExtra(Constant.EXTRA_PICKUP, pickupAddr);
                    intent.putExtra(Constant.EXTRA_DROPOFF, dropOffAddr);
                    intent.putExtra(Constant.EXTRA_TIME, selectedTime);
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList(Constant.EXTRA_PACKAGE, packageArrayList);
                    intent.putExtras(bundle);
                    startActivity(intent);

                }
            });
        } else {
            fab.setVisibility(View.GONE);
        }
    }

    private void setupNextFab() {

        fab.setVisibility(View.VISIBLE);
        fab.setImageResource(R.drawable.ic_arrow_forward_white_36dp);
        fab.setBackgroundTintList(getResources().getColorStateList(R.color.normal_state_list));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int currentTab = viewPager.getCurrentItem();
                if (currentTab == Constant.TAB_FROM) {
                    if (pickupFragment.saveAndValidate()) {
                        viewPager.setCurrentItem(Constant.TAB_TO, true);
                    }
                } else if (currentTab == Constant.TAB_TO) {
                    if (dropOffFragment.saveAndValidate()) {
                        viewPager.setCurrentItem(Constant.TAB_PACKAGE, true);
                    }
                } else if (currentTab == Constant.TAB_PACKAGE) {
                    if (packageFragment.validateAllPackage()) {
                        viewPager.setCurrentItem(Constant.TAB_TIME, true);
                    }
                    for (int i = 0; i < packageArrayList.size(); i++) {
                        Logger.e(TAG, packageArrayList.get(i).toString());
                    }
                } else if (currentTab == Constant.TAB_TIME) {
                    if (selectedTime != null) {
                        viewPager.setCurrentItem(Constant.TAB_INSURANCE, true);
                    }
                } else if (currentTab == Constant.TAB_INSURANCE) {
                    viewPager.setCurrentItem(Constant.TAB_SUMMARY, true);
                }
            }
        });
    }

    private void init() {
        context = this;
        pickupAddr = new Address();
        pickupAddr.setCountry("Canada");
        pickupAddr.setProvince("BC");
        dropOffAddr = new Address();
        packageArrayList = new ArrayList<>();
        packageArrayList.add(new Package());
    }

    public void scrollTo(int tabPosition) {
        tabLayout.getTabAt(tabPosition).select();
    }


    private void setupViewPager(ViewPager viewPager) {
        adapter = new Adapter(getSupportFragmentManager());

        if (pickupFragment == null)
            pickupFragment = PickupFragment.newInstance();
        if (dropOffFragment == null)
            dropOffFragment = DestinationFragment.newInstance();
        if (packageFragment == null)
            packageFragment = PackageFragment.newInstance();
        if (timeFragment == null)
            timeFragment = TimeFragment.newInstance();
        if(insuranceFragment == null)
            insuranceFragment = new InsuranceFragment();
        if (summaryFragment == null)
            summaryFragment = SummaryFragment.newInstance();

        adapter.addFragment(pickupFragment, getString(R.string.tab_title_from));
        adapter.addFragment(dropOffFragment, getString(R.string.tab_title_to));
        adapter.addFragment(packageFragment, getString(R.string.tab_title_package));
        adapter.addFragment(timeFragment, getString(R.string.tab_title_time));
        adapter.addFragment(insuranceFragment, getString(R.string.tab_title_insurance));
        adapter.addFragment(summaryFragment, getString(R.string.tab_title_summary));
        viewPager.setAdapter(adapter);
//        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//            }
//        });

    }

    @Override
    public void onTimeSelected(MyTime myTime) {
        selectedTime = myTime;
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }


    /**
     * Called when the Activity could not connect to Google Play services and the auto manager
     * could resolve the error automatically.
     * In this case the API is not available and notify the user.
     *
     * @param connectionResult can be inspected to determine the cause of the failure
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Logger.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(this,
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }

//    private List<String> validatePickup() {
//        ArrayList<String> errList = new ArrayList<>();
//
//        //validate street
//        String street = pickupAddr.getStreetName();
//        if (street.isEmpty()) {
//            errList.add(getString(R.string.err_street_empty));
//        }
//
//        //validate city
//        String city = pickupAddr.getCity();
//        if (city.isEmpty()) {
//            errList.add(getString(R.string.err_city_empty));
//        } else if (!Constant.citiesInVan.contains(city.toUpperCase())) {
//            errList.add(city + getString(R.string.err_not_in_van));
//        }
//
//        //validate postal
//        String zip = pickupAddr.getPostalCode();
//        String regex = "^(?!.*[DFIOQU])[A-VXY][0-9][A-Z] ?[0-9][A-Z][0-9]$";
//        Pattern pattern = Pattern.compile(regex);
//        Matcher matcher = pattern.matcher(zip);
//        if (zip.isEmpty())
//            errList.add(getString(R.string.err_post_empty));
//        else if (!matcher.matches())
//            errList.add(getString(R.string.err_post_wrong_format));
//
//        printErrList(errList);
//        return errList;
//    }

    private void printErrList(ArrayList<String> errList) {
        String out = "";
        for (int i = 0; i < errList.size(); i++) {
            out += errList.get(i) + "\n";
        }
        Logger.e(TAG, "error: " + out);
    }


    public boolean validatePickup() {
        return pickupAddr != null && !pickupAddr.getCity().isEmpty() && !pickupAddr.getCountry().isEmpty() && !pickupAddr.getProvince().isEmpty()
                && !pickupAddr.getStreetName().isEmpty() && validateCanadaPostalCode(pickupAddr.getPostalCode());
    }

    public boolean validateDropOff() {
        return dropOffAddr != null && !dropOffAddr.getCity().isEmpty() && !dropOffAddr.getCountry().isEmpty() && !dropOffAddr.getProvince().isEmpty()
                && !dropOffAddr.getStreetName().isEmpty() && validateCanadaChinaPostalCode(dropOffAddr.getPostalCode());
    }

    private boolean validateCanadaPostalCode(String zip) {
        if (zip.isEmpty()) {
            return false;
        }

        String regex = "^(?!.*[DFIOQU])[A-VXY][0-9][A-Z] ?[0-9][A-Z][0-9]$";

        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(zip);

        return matcher.matches();
    }

    private boolean validateCanadaChinaPostalCode(String zip) {
        if (zip.isEmpty()) {
            return false;
        }

        String cadRex = "^(?!.*[DFIOQU])[A-VXY][0-9][A-Z] ?[0-9][A-Z][0-9]$";
        String chnRex = "^([0-9]){6}$";
        Pattern pattern = Pattern.compile(cadRex, Pattern.CASE_INSENSITIVE);
        Pattern pattern2 = Pattern.compile(chnRex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(zip);
        Matcher matcher2 = pattern2.matcher(zip);
        return matcher.matches() || matcher2.matches();
    }

    public boolean validateAllPackage() {
        ArrayList<Package> aPackages = packageArrayList;
        boolean result = true;
        for (int i = 0; i < aPackages.size(); i++) {
            Package aPackage = aPackages.get(i);
            if (aPackage.isOwnBox() && (aPackage.getHeight().isEmpty() || aPackage.getLength().isEmpty() || aPackage.getWeight().isEmpty() || aPackage.getWidth().isEmpty())) {
                result = false;
            } else if (!aPackage.isOwnBox() && aPackage.getWeight().isEmpty()) {
                result = false;
            }
        }
        return result;
    }

    private void getRate(){
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.loading));
        pd.show();

        JSONObject params = new JSONObject();
        try {
            JSONObject pickup = new JSONObject();
            pickup.put("address", pickupAddr.getStreetName());
            pickup.put("city", pickupAddr.getCity());
            pickup.put("province", pickupAddr.getProvince());
            pickup.put("country", pickupAddr.getCountry());
            pickup.put("postal", pickupAddr.getPostalCode());
            pickup.put("residential", true);

            JSONObject dropOff = new JSONObject();
            dropOff.put("address", dropOffAddr.getStreetName());
            dropOff.put("city", dropOffAddr.getCity());
            dropOff.put("province", dropOffAddr.getProvince());
            dropOff.put("country", dropOffAddr.getCountry());
            dropOff.put("postal", dropOffAddr.getPostalCode());
            dropOff.put("residential", true);

            params.put("fromAddress", pickup);
            params.put("toAddress", dropOff);

            params.put("shipments", buildBoxJson());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        getRateRequest = new JsonObjectRequest(Request.Method.POST, Constant.URL + "rate/", params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Logger.d(TAG, response.toString());
                pd.dismiss();
                getRateRequest = null;

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                getRateRequest = null;
                pd.dismiss();
                Util.handleVolleyError(error,context);
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(getRateRequest);
    }

    private JSONArray buildBoxJson(){
        JSONArray shipments = new JSONArray();

        for(Package aPackage : packageArrayList){
            try {
                JSONObject shipment = new JSONObject();
                if(aPackage.isOwnBox()){
                    shipment.put("height", aPackage.getHeight());
                    shipment.put("width", aPackage.getWidth());
                    shipment.put("length", aPackage.getLength());
                    shipment.put("weight", aPackage.getWeight());
                }else{
                    switch (aPackage.getBoxSize()){
                        case Package.BIG_BOX:
                            shipment.put("height", Package.BIG_HEIGHT);
                            shipment.put("width", Package.BIG_WIDTH);
                            shipment.put("length", Package.BIG_LENGTH);
                            break;
                        case Package.MED_BOX:
                            shipment.put("height", Package.MED_HEIGHT);
                            shipment.put("width", Package.MED_WIDTH);
                            shipment.put("length", Package.MED_LENGTH);
                            break;
                        case Package.SMALL_BOX:
                            shipment.put("height", Package.SMALL_HEIGHT);
                            shipment.put("width", Package.SMALL_WIDTH);
                            shipment.put("length", Package.SMALL_LENGTH);
                            break;
                    }
                    shipment.put("weight", aPackage.getWeight());
                }
                shipments.put(shipment);
            }catch (Exception e){

            }
        }
        return shipments;
    }

    private void bookShipment(){
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.loading));
        pd.show();

        JSONObject params = new JSONObject();
        try {
            params.put("userId", AppController.getInstance().getUserId());

            params.put("fromAddress", pickupAddr.getStreetName());
            params.put("fromCity", pickupAddr.getCity());
            params.put("fromProvince", pickupAddr.getProvince());
            params.put("fromCountry", pickupAddr.getCountry());
            params.put("fromPostal", pickupAddr.getPostalCode());

            params.put("toAddress", dropOffAddr.getStreetName());
            params.put("toCity", dropOffAddr.getCity());
            params.put("toProvince", dropOffAddr.getProvince());
            params.put("toCountry", dropOffAddr.getCountry());
            params.put("toPostal", dropOffAddr.getPostalCode());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        bookShipRequest = new JsonObjectRequest(Request.Method.POST, Constant.URL + "order?token=" + AppController.getInstance().getUserToken(), params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Logger.e(TAG, response.toString());
                pd.dismiss();
                bookShipRequest = null;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                bookShipRequest = null;
                pd.dismiss();
                Util.handleVolleyError(error,context);
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(bookShipRequest);
    }
}
