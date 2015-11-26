package lmdelivery.longmen.com.android.activity;

import android.app.AlertDialog;
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
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.android.volley.Request;
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

import javax.inject.Inject;

import lmdelivery.longmen.com.android.AppController;
import lmdelivery.longmen.com.android.Constant;
import lmdelivery.longmen.com.android.R;
import lmdelivery.longmen.com.android.api.LMXApi;
import lmdelivery.longmen.com.android.api.Order;
import lmdelivery.longmen.com.android.api.Rate;
import lmdelivery.longmen.com.android.data.Address;
import lmdelivery.longmen.com.android.data.MyTime;
import lmdelivery.longmen.com.android.data.Package;
import lmdelivery.longmen.com.android.data.RateItem;
import lmdelivery.longmen.com.android.data.User;
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

    @Inject
    LMXApi lmxApi;

    /**
     * GoogleApiClient wraps our service connection to Google Play Services and provides access
     * to the user's sign in state as well as the Google's APIs.
     */
    public GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_booking);
        AppController.getComponent().inject(this);
        context = this;
        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            packageArrayList = savedInstanceState.getParcelableArrayList(Constant.EXTRA_PACKAGE);
            pickupAddr = (Address) savedInstanceState.getSerializable(Constant.EXTRA_PICKUP);
            dropOffAddr = (Address) savedInstanceState.getSerializable(Constant.EXTRA_DROPOFF);
            selectedTime = (MyTime) savedInstanceState.getSerializable(Constant.EXTRA_TIME);
            insuranceValue = savedInstanceState.getString(Constant.EXTRA_INSURANCE_ITEM);
            declareValue = savedInstanceState.getString(Constant.EXTRA_ESTIMATE_VALUE);
        } else {
            init();
        }


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        final ActionBar ab = getSupportActionBar();
        if (ab != null)
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
                if (tab.getPosition() == Constant.TAB_TIME) {
                    if (selectedTime == null) {
                        hideFab();
                    }
                } else if (tab.getPosition() == Constant.TAB_SUMMARY) {
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
                        if (pickupFragment != null && pickupFragment.saveAndValidate()) {
                            tabLayout.getTabAt(Constant.TAB_FROM).setIcon(R.drawable.shape_greendot);
                        } else {
                            tabLayout.getTabAt(Constant.TAB_FROM).setIcon(R.drawable.shape_yellowdot);
                        }
                        break;

                    case Constant.TAB_TO:
                        if (dropOffFragment != null && dropOffFragment.saveAndValidate()) {
                            tabLayout.getTabAt(Constant.TAB_TO).setIcon(R.drawable.shape_greendot);
                        } else {
                            tabLayout.getTabAt(Constant.TAB_TO).setIcon(R.drawable.shape_yellowdot);
                        }
                        break;

                    case Constant.TAB_PACKAGE:
                        if (packageFragment != null && packageFragment.validateAllPackage()) {
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
                        } else {
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
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putParcelableArrayList(Constant.EXTRA_PACKAGE, packageArrayList);
        savedInstanceState.putSerializable(Constant.EXTRA_PICKUP, pickupAddr);
        savedInstanceState.putSerializable(Constant.EXTRA_DROPOFF, dropOffAddr);
        savedInstanceState.putSerializable(Constant.EXTRA_TIME, selectedTime);
        savedInstanceState.putString(Constant.EXTRA_INSURANCE_ITEM, insuranceValue);
        savedInstanceState.putString(Constant.EXTRA_ESTIMATE_VALUE, declareValue);
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == PICK_RATE_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.
                RateItem rateItem = data.getParcelableExtra(Constant.EXTRA_RATE_ITEM);
                if (rateItem != null)
                    bookShipment(rateItem);
                //Logger.e(TAG, "selected item: " + ((RateItem) data.getParcelableExtra("selected_rate")).getServiceName());
                Logger.d(TAG, "make order successfully");
                // Do something with the contact here (bigger example below)
            }
        }
    }

    private void hideFab() {
        fab.animate().scaleX(0).scaleY(0).setInterpolator(new AccelerateInterpolator()).setDuration(Constant.FAB_ANIMTION_DURATION).withEndAction(() -> fab.setVisibility(View.GONE)).start();
    }

    private void showFab() {
        if (fab.getVisibility() == View.GONE) {
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
            fab.setOnClickListener(view -> getRate());
        } else {
            hideFab();
        }
    }

    private void setupNextFab() {
        fab.setImageResource(R.drawable.ic_arrow_forward_white_36dp);
        fab.setBackgroundTintList(getResources().getColorStateList(R.color.normal_state_list));
        fab.setOnClickListener(view -> {
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
                if (insuranceFragment.saveAndValidate(insuranceValue, declareValue)) {
                    viewPager.setCurrentItem(Constant.TAB_SUMMARY, true);
                }
            }
        });
        showFab();
    }

    private void init() {

        User user = new Select().from(User.class).where("remote_id = ?", AppController.getInstance().getUserId()).executeSingle();
        if(user!=null && user.address!=null){
            pickupAddr = user.address;
        }else{
            pickupAddr = new Address();
        }
        pickupAddr.setCountry("Canada");
        pickupAddr.setProvince("BC");

        dropOffAddr = new Address();
        packageArrayList = new ArrayList<>();
        packageArrayList.add(new Package());
        insuranceValue = "0";
        declareValue = "0";
    }

    public void scrollTo(int tabPosition) {
        if (tabLayout.getTabAt(tabPosition) != null)
            tabLayout.getTabAt(tabPosition).select();
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new Adapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onTimeSelected(MyTime myTime) {
        selectedTime = myTime;
        if (fab.getVisibility() == View.GONE) {
            setupNextFab();
        }
    }

    class Adapter extends FragmentPagerAdapter {
        private final String[] mFragmentTitles = AppController.getAppContext().getResources().getStringArray(R.array.tabs_title);
        private final int PAGER_SIZE = 6;

        public Adapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {
            Logger.e(TAG, "getItem: " + position);
            switch (position) {
                case Constant.TAB_FROM:
                    if (pickupFragment == null)
                        pickupFragment = PickupFragment.newInstance();
                    return pickupFragment;
                case Constant.TAB_TO:
                    if (dropOffFragment == null)
                        dropOffFragment = DestinationFragment.newInstance();
                    return dropOffFragment;
                case Constant.TAB_PACKAGE:
                    if (packageFragment == null)
                        packageFragment = PackageFragment.newInstance();
                    return packageFragment;
                case Constant.TAB_TIME:
                    if (timeFragment == null)
                        timeFragment = TimeFragment.newInstance();
                    return timeFragment;
                case Constant.TAB_INSURANCE:
                    if (insuranceFragment == null)
                        insuranceFragment = new InsuranceFragment();
                    return insuranceFragment;
                case Constant.TAB_SUMMARY:
                    if (summaryFragment == null)
                        summaryFragment = SummaryFragment.newInstance();
                    return summaryFragment;
                default:
                    if (pickupFragment == null)
                        pickupFragment = PickupFragment.newInstance();
                    return pickupFragment;
            }
        }

        @Override
        public int getCount() {
            return PAGER_SIZE;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles[position];
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
        return pickupFragment.saveAndValidate();
    }

    public boolean validateDropOff() {
        return dropOffFragment.saveAndValidate();
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

    public boolean validateInsurance() {
        return insuranceFragment.saveAndValidate(insuranceValue, declareValue);
    }


    private void getRate() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.loading));
        pd.setOnCancelListener(dialog -> {
            Logger.d(TAG, "getRateRequest canceled");
            if (null != getRateRequest) {
                getRateRequest.cancel();
                getRateRequest = null;
            }
        });
        pd.show();


        JSONObject params = Rate.buildEstimateParam(pickupAddr, dropOffAddr, packageArrayList, selectedTime, declareValue, insuranceValue);

        Logger.d(TAG, "Sending get rate:\n" + params);

        getRateRequest = new JsonObjectRequest(Request.Method.POST, Constant.REST_URL + "courier/rate/", params, response -> {
            Logger.d(TAG, response.toString());
            pd.dismiss();
            getRateRequest = null;

            try {
                Type listType = new TypeToken<ArrayList<RateItem>>() {
                }.getType();
                final ArrayList<RateItem> rateItemList = new Gson().fromJson(response.getJSONArray("courierRates").toString(), listType);
                RateItem insuranceItem = new Gson().fromJson(response.getJSONObject("insuranceRate").toString(), RateItem.class);
                RateItem packageItem = new Gson().fromJson(response.getJSONObject("handlingRate").toString(), RateItem.class);
                if (rateItemList.isEmpty()) {
                    DialogUtil.showMessageDialog(getString(R.string.err_no_estimate), context);
                } else if (rateItemList.size() == 1) {
                    DialogUtil.showSingleEstimateDialog(context, rateItemList.get(0), insuranceItem, packageItem, (dialog, which) -> {
                        if (AppController.getInstance().isUserActivated()) {
                            bookShipment(rateItemList.get(0));
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.register_before_book, Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(context, LoginActivity.class);
                            intent.putExtra(Constant.EXTRA_INSURANCE_ITEM, rateItemList.get(0));
                            startActivityForResult(intent, Constant.LOGIN_REQUEST_CODE);
                        }
                    });
                } else {
                    Intent intent = new Intent(context, SelectProductActivity.class);
                    intent.putExtra(Constant.EXTRA_PICKUP, pickupAddr);
                    intent.putExtra(Constant.EXTRA_DROPOFF, dropOffAddr);
                    intent.putExtra(Constant.EXTRA_TIME, selectedTime);

                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList(Constant.EXTRA_PACKAGE, packageArrayList);
                    bundle.putParcelableArrayList(Constant.EXTRA_RATE_ITEM, rateItemList);
                    bundle.putParcelable(Constant.EXTRA_INSURANCE_ITEM, insuranceItem);
                    bundle.putParcelable(Constant.EXTRA_PACKAGE_ITEM, packageItem);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, PICK_RATE_REQUEST);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            getRateRequest = null;
            pd.dismiss();
            Util.handleVolleyError(error, context);
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(getRateRequest);
    }

    public void bookShipment(RateItem rateItem) {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.loading));
        pd.show();

        bookShipRequest = new JsonObjectRequest(Request.Method.POST, Constant.REST_URL + "order?token=" + AppController.getInstance().getUserToken(),
                Order.buildOrderParam(AppController.getInstance().getUserId(), "", rateItem, pickupAddr, dropOffAddr, packageArrayList, selectedTime, declareValue, insuranceValue),
                response -> {
                    Logger.e(TAG, "book order response: " + response.toString());
                    pd.dismiss();
                    bookShipRequest = null;
                    showBookSuccessDialog();
                    User user = new Select().from(User.class).where("remote_id = ?", AppController.getInstance().getUserId()).executeSingle();
                    pickupAddr.save();
                    user.address = pickupAddr;
                    user.save();
                    lmxApi.updateUser(AppController.getInstance().getUserId(), user);
                }, error -> {
            bookShipRequest = null;
            pd.dismiss();
            Util.handleVolleyError(error, context);
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

        switch (item.getItemId()) {
            case R.id.action_diff_city:
                pickupAddr.setCountry("CA");
                pickupAddr.setCity("Richmond");
                pickupAddr.setProvince("BC");
                pickupAddr.setName("Rufus");
                pickupAddr.setPhone("7788594684");
                pickupAddr.setStreetName("7362 Elmbridge Way");
                pickupAddr.setPostalCode("V6X 0A6");
                pickupAddr.buildFullAddress();

                dropOffAddr.setCountry("Canada");
                dropOffAddr.setProvince("MB");
                dropOffAddr.setCity("Winnipeg");
                dropOffAddr.setName("Rufus");
                dropOffAddr.setPhone("7788594684");
                dropOffAddr.setStreetName("1234 Fairfield Avenue");
                dropOffAddr.setPostalCode("R3T 2R2");
                dropOffAddr.buildFullAddress();

                selectedTime = new MyTime("9am - 11am", 0, true);
                packageArrayList.get(0).setBoxSize(Package.BIG_BOX);
                packageArrayList.get(0).setWeight("5");
                insuranceValue = "100";
                declareValue = "100";
                getRate();
                break;
            case R.id.action_same_city:
                pickupAddr.setCountry("CA");
                pickupAddr.setCity("Richmond");
                pickupAddr.setProvince("BC");
                pickupAddr.setName("Rufus");
                pickupAddr.setPhone("7788594684");
                pickupAddr.setStreetName("7362 Elmbridge Way");
                pickupAddr.setPostalCode("V6X 0A6");
                pickupAddr.buildFullAddress();

                dropOffAddr.setCountry("Canada");
                dropOffAddr.setProvince("BC");
                dropOffAddr.setCity("Vancouver");
                dropOffAddr.setName("Rufus");
                dropOffAddr.setPhone("7788594684");
                dropOffAddr.setStreetName("736 Granville Street");
                dropOffAddr.setPostalCode("V6Z 1E4");
                dropOffAddr.buildFullAddress();

                selectedTime = new MyTime("9am - 11am", 0, true);
                packageArrayList.get(0).setBoxSize(Package.BIG_BOX);
                packageArrayList.get(0).setWeight("5");
                insuranceValue = "100";
                declareValue = "100";
                getRate();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showBookSuccessDialog() {
        new AlertDialog.Builder(context)
                .setMessage(getString(R.string.book_success))
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    dialog.dismiss();
                    finishBooking();
                })
                .show();
    }

    private void finishBooking() {
        finish();
    }
}
