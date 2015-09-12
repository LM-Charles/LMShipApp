package lmdelivery.longmen.com.android.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lmdelivery.longmen.com.android.AppController;
import lmdelivery.longmen.com.android.Constant;
import lmdelivery.longmen.com.android.R;
import lmdelivery.longmen.com.android.api.Rate;
import lmdelivery.longmen.com.android.bean.Address;
import lmdelivery.longmen.com.android.bean.MyTime;
import lmdelivery.longmen.com.android.bean.Package;
import lmdelivery.longmen.com.android.bean.RateItem;
import lmdelivery.longmen.com.android.fragments.DestinationFragment;
import lmdelivery.longmen.com.android.fragments.InsuranceFragment;
import lmdelivery.longmen.com.android.fragments.PackageFragment;
import lmdelivery.longmen.com.android.fragments.PickupFragment;
import lmdelivery.longmen.com.android.fragments.SummaryFragment;
import lmdelivery.longmen.com.android.fragments.TimeFragment;
import lmdelivery.longmen.com.android.util.DialogUtil;
import lmdelivery.longmen.com.android.util.Logger;
import lmdelivery.longmen.com.android.util.Util;


public class NewBookingActivity extends AppCompatActivity implements TimeFragment.OnTimeSelectedListener, GoogleApiClient.OnConnectionFailedListener {
    private static final java.lang.String TAG = NewBookingActivity.class.getName();
    static final int PICK_RATE_REQUEST = 1;

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
    public String declareValue;
    public String insuranceValue;

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
                if(tab.getPosition() == Constant.TAB_TIME){
                    if(selectedTime==null){
                        hideFab();
                    }
                }
                else if (tab.getPosition() == Constant.TAB_SUMMARY) {
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
                        if (!TextUtils.isEmpty(declareValue)) {
                            tabLayout.getTabAt(Constant.TAB_INSURANCE).setIcon(R.drawable.shape_greendot);
                        }else {
                            tabLayout.getTabAt(Constant.TAB_INSURANCE).setIcon(R.drawable.shape_yellowdot);
                        }

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == PICK_RATE_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.

                Logger.e(TAG, "selected item: " + ((RateItem) data.getParcelableExtra("selected_rate")).getServiceName());
                // Do something with the contact here (bigger example below)
            }
        }
    }

    private void hideFab(){
        fab.animate().scaleX(0).scaleY(0).setInterpolator(new AccelerateInterpolator()).setDuration(Constant.FAB_ANIMTION_DURATION).withEndAction(new Runnable() {
            @Override
            public void run() {
                fab.setVisibility(View.GONE);
            }
        }).start();
    }

    private void showFab(){
        if(fab.getVisibility() == View.GONE){
            fab.setVisibility(View.VISIBLE);
            fab.setScaleX(0);
            fab.setScaleY(0);
            fab.animate().scaleX(1).scaleY(1).setInterpolator(new OvershootInterpolator()).setDuration(Constant.FAB_ANIMTION_DURATION).start();
        }
    }

    private void setupDoneFab() {
        if (summaryFragment.setupView()) {
            fab.setImageResource(R.drawable.ic_done);
            fab.setBackgroundTintList(getResources().getColorStateList(R.color.done_state_list));
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    if(!TextUtils.isEmpty(AppController.getInstance().getUserId())){
                        getRate(null);
//                    }

                }
            });
        } else {
            hideFab();
        }
    }

    private void setupNextFab() {
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
                    if (insuranceFragment.saveAndValidate(insuranceValue)) {
                        viewPager.setCurrentItem(Constant.TAB_SUMMARY, true);
                    }
                }
            }
        });
        showFab();
    }

    private void init() {
        context = this;
        pickupAddr = new Address();
        pickupAddr.setCountry("Canada");
        pickupAddr.setProvince("BC");
        dropOffAddr = new Address();
        packageArrayList = new ArrayList<>();
        packageArrayList.add(new Package());
        insuranceValue = "0";
        declareValue = "0";
    }

    public void scrollTo(int tabPosition) {
        if(tabLayout.getTabAt(tabPosition)!=null)
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
    }

    @Override
    public void onTimeSelected(MyTime myTime) {
        selectedTime = myTime;
        if(fab.getVisibility() == View.GONE){
            setupNextFab();
        }
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

    public boolean validatePickup() {
//        return pickupAddr != null && !pickupAddr.getCity().isEmpty() && !pickupAddr.getCountry().isEmpty() && !pickupAddr.getProvince().isEmpty()
//                && !pickupAddr.getStreetName().isEmpty() && validateCanadaPostalCode(pickupAddr.getPostalCode());
        return pickupFragment.saveAndValidate();
    }

    public boolean validateDropOff() {
//        return dropOffAddr != null && !dropOffAddr.getCity().isEmpty() && !dropOffAddr.getCountry().isEmpty() && !dropOffAddr.getProvince().isEmpty()
//                && !dropOffAddr.getStreetName().isEmpty() && validateCanadaChinaPostalCode(dropOffAddr.getPostalCode());
        return dropOffFragment.saveAndValidate();
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

    public boolean validateInsurance(){
//        return !TextUtils.isEmpty(declareValue);
        //everything optional
        return insuranceFragment.saveAndValidate(insuranceValue);
    }


    private void getRate(JSONObject params){
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.loading));
        pd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Logger.d(TAG, "getRateRequest canceled");
                if (null != getRateRequest) {
                    getRateRequest.cancel();
                    getRateRequest = null;
                }
            }
        });
        pd.show();

        if (params==null)
            params = Rate.buildEstimateParam(pickupAddr,dropOffAddr,packageArrayList,selectedTime,declareValue,insuranceValue);

        Logger.d(TAG,"Sending get rate:\n" + params);

        getRateRequest = new JsonObjectRequest(Request.Method.POST, Constant.REST_URL + "courier/rate/", params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Logger.d(TAG, response.toString());
                pd.dismiss();
                getRateRequest = null;

                try {
                    Type listType = new TypeToken<ArrayList<RateItem>>() {}.getType();
                    ArrayList<RateItem> rateItemList = new Gson().fromJson(response.getJSONArray("courierRates").toString(), listType);
                    if(rateItemList.isEmpty()){
                        DialogUtil.showMessageDialog(getString(R.string.err_no_estimate), context);
                    }else if(rateItemList.size()==1){
                        DialogUtil.showSingleEstimateDialog(context,rateItemList.get(0));
                    }else{
                        Intent intent = new Intent(context, SelectProductActivity.class);
                        intent.putExtra(Constant.EXTRA_PICKUP, pickupAddr);
                        intent.putExtra(Constant.EXTRA_DROPOFF, dropOffAddr);
                        intent.putExtra(Constant.EXTRA_TIME, selectedTime);

                        Bundle bundle = new Bundle();
                        bundle.putParcelableArrayList(Constant.EXTRA_PACKAGE, packageArrayList);
                        bundle.putParcelableArrayList(Constant.EXTRA_RATE_ITEM, rateItemList);
                        intent.putExtras(bundle);
                        startActivityForResult(intent,PICK_RATE_REQUEST);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

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

        bookShipRequest = new JsonObjectRequest(Request.Method.POST, Constant.REST_URL + "order?token=" + AppController.getInstance().getUserToken(), params, new Response.Listener<JSONObject>() {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_booking, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId()) {
            case R.id.action_diff_city:
                try {
                    getRate(new JSONObject("{\n" +
                            "    \"client\" : \"2\",\n" +
                            "    \"fromAddress\" : {\n" +
                            "        \"address\" : \"8000 Delsom Way\",\n" +
                            "        \"address2\" : \"unit 10\",\n" +
                            "        \"city\" : \"Delta\",\n" +
                            "        \"province\" : \"BC\",\n" +
                            "        \"postal\" : \"V4C0A9\",\n" +
                            "        \"country\" : \"CA\"\n" +
                            "    },\n" +
                            "    \"toAddress\" : {\n" +
                            "        \"address\" : \"812 Wharf Street\",\n" +
                            "        \"city\" : \"Victoria\",\n" +
                            "        \"province\" : \"BC\",\n" +
                            "        \"postal\" : \"V8W1T3\",\n" +
                            "        \"country\" : \"CA\"\n" +
                            "    },\n" +
                            "    \"shipments\" : [\n" +
                            "        {\n" +
                            "            \"height\" : 10,\n" +
                            "            \"width\" : 10,\n" +
                            "            \"length\" : 10,\n" +
                            "            \"weight\" : 1,\n" +
                            "            \"shipmentPackageType\" : \"CUSTOM\",\n" +
                            "            \"goodCategoryType\" : \"REGULAR\"\n" +
                            "\n" +
                            "        }\n" +
                            "    ],\n" +
                            "    \"handler\" : \"optional_handler\",\n" +
                            "    \"declareValue\" : 100,\n" +
                            "    \"insuranceValue\" : 10,\n" +
                            "    \"appointmentDate\" : 1263110400000,\n" +
                            "    \"appointmentSlotType\" : \"SLOT_1\"\n" +
                            "}"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.action_same_city:
                try {
                    getRate(new JSONObject("{\n" +
                            "    \"client\" : \"2\",\n" +
                            "    \"fromAddress\" : {\n" +
                            "        \"address\" : \"8000 Delsom Way\",\n" +
                            "        \"address2\" : \"unit 10\",\n" +
                            "        \"city\" : \"Delta\",\n" +
                            "        \"province\" : \"BC\",\n" +
                            "        \"postal\" : \"V4C0A9\",\n" +
                            "        \"country\" : \"CA\"\n" +
                            "    },\n" +
                            "    \"toAddress\" : {\n" +
                            "        \"address\" : \"9188 Hemlock Drive\",\n" +
                            "        \"city\" : \"Richmond\",\n" +
                            "        \"province\" : \"BC\",\n" +
                            "        \"postal\" : \"V7C2X4\",\n" +
                            "        \"country\" : \"CA\"\n" +
                            "    },\n" +
                            "    \"shipments\" : [\n" +
                            "        {\n" +
                            "            \"height\" : 10,\n" +
                            "            \"width\" : 10,\n" +
                            "            \"length\" : 10,\n" +
                            "            \"weight\" : 1,\n" +
                            "            \"shipmentPackageType\" : \"CUSTOM\",\n" +
                            "            \"goodCategoryType\" : \"COSMETICS\"\n" +
                            "\n" +
                            "        }\n" +
                            "    ],\n" +
                            "    \"handler\" : \"optional_handler\",\n" +
                            "    \"declareValue\" : 100,\n" +
                            "    \"insuranceValue\" : 10,\n" +
                            "    \"appointmentDate\" : 1263110400000,\n" +
                            "    \"appointmentSlotType\" : \"SLOT_1\"\n" +
                            "}"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
