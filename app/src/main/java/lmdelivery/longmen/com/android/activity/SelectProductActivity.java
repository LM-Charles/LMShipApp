package lmdelivery.longmen.com.android.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import lmdelivery.longmen.com.android.AppController;
import lmdelivery.longmen.com.android.Constant;
import lmdelivery.longmen.com.android.R;
import lmdelivery.longmen.com.android.api.Order;
import lmdelivery.longmen.com.android.bean.Address;
import lmdelivery.longmen.com.android.bean.MyTime;
import lmdelivery.longmen.com.android.bean.Package;
import lmdelivery.longmen.com.android.bean.RateItem;
import lmdelivery.longmen.com.android.bean.RateItemPriceComparator;
import lmdelivery.longmen.com.android.fragments.RateItemFragment;
import lmdelivery.longmen.com.android.swipeback.SwipeBackActivity;
import lmdelivery.longmen.com.android.swipeback.SwipeBackLayout;
import lmdelivery.longmen.com.android.util.DialogUtil;
import lmdelivery.longmen.com.android.util.Logger;
import lmdelivery.longmen.com.android.util.Util;


public class SelectProductActivity extends SwipeBackActivity {

    private static final java.lang.String TAG = SelectProductActivity.class.getName();
    private SwipeBackLayout mSwipeBackLayout;
    private JsonObjectRequest bookShipRequest;
    private Address mPickupAddr;
    private Address mDropoffAddr;
    private MyTime mTime;
    private ArrayList<Package> mPackageList;
    private ArrayList<RateItem> mFastestList;
    private ArrayList<RateItem> mMediumList;
    private ArrayList<RateItem> mEconomyList;
    private ArrayList<RateItem> mRateList;
    private RateItem mPackageRate;
    private RateItem mInsuranceRate;
    private String mInsuranceValue;
    private String mEstValue;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_product);
        mSwipeBackLayout = getSwipeBackLayout();
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        context = this;
        if(savedInstanceState!=null){
            mRateList = savedInstanceState.getParcelableArrayList(Constant.EXTRA_RATE_ITEM);
        }else {
            Bundle bundle = getIntent().getExtras();
            try {
//            mPackageList = bundle.getParcelableArrayList(Constant.EXTRA_PACKAGE);
//            mPickupAddr = (Address) getIntent().getSerializableExtra(Constant.EXTRA_PICKUP);
//            mDropoffAddr = (Address) getIntent().getSerializableExtra(Constant.EXTRA_DROPOFF);
//            mTime = (MyTime) getIntent().getSerializableExtra(Constant.EXTRA_TIME);
                mRateList = bundle.getParcelableArrayList(Constant.EXTRA_RATE_ITEM);
//            mPackageRate = bundle.getParcelable(Constant.EXTRA_PACKAGE_ITEM);
//            mInsuranceRate = bundle.getParcelable(Constant.EXTRA_INSURANCE_ITEM);
//            mInsuranceValue = getIntent().getStringExtra(Constant.EXTRA_INSURANCE_VALUE);
//            mEstValue = getIntent().getStringExtra(Constant.EXTRA_ESTIMATE_VALUE);
            } catch (Exception e) {

            }
        }

//        Logger.e(TAG, pickup.getFullAddress());
//        Logger.e(TAG, dropoff.getFullAddress());
//        Logger.e(TAG, time.getTimeString());
//        Logger.e(TAG, packageList.size()+"");
        parseCategory();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        if(ab!=null)
            ab.setDisplayHomeAsUpEnabled(true);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putParcelableArrayList(Constant.EXTRA_RATE_ITEM, mRateList);
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }


    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(RateItemFragment.newInstance(mFastestList), getString(R.string.fastest));
        adapter.addFragment(RateItemFragment.newInstance(mMediumList), getString(R.string.medium));
        adapter.addFragment(RateItemFragment.newInstance(mEconomyList), getString(R.string.economy));
        viewPager.setAdapter(adapter);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void returnSelectedRate(){
        Intent returnIntent = new Intent();
//        returnIntent.putExtra("selected_rate",item);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    public void showPaymentDialog(final RateItem item){
        DialogUtil.showSingleEstimateDialog(this, item, mInsuranceRate, mPackageRate, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                bookShipment(item);
            }
        });
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

    private void parseCategory(){
        mEconomyList = new ArrayList<>();
        mFastestList = new ArrayList<>();
        mMediumList = new ArrayList<>();
        for(RateItem rateItem:mRateList){
            switch (rateItem.getCategory()){
                case ("FASTEST_COURIER"):
                    mFastestList.add(rateItem);
                    break;
                case ("MEDIUM_COURIER"):
                    mMediumList.add(rateItem);
                    break;
                case ("ECONOMY_COURIER"):
                    mEconomyList.add(rateItem);
                    break;
            }
        }
        Collections.sort(mFastestList, new RateItemPriceComparator());
        Collections.sort(mMediumList, new RateItemPriceComparator());
        Collections.sort(mEconomyList, new RateItemPriceComparator());
    }

    public void bookShipment(RateItem rateItem){
        Intent returnIntent = new Intent();
        returnIntent.putExtra(Constant.EXTRA_RATE_ITEM, rateItem);
        setResult(RESULT_OK, returnIntent);
        finish();
//        final ProgressDialog pd = new ProgressDialog(this);
//        pd.setMessage(getString(R.string.loading));
//        pd.show();
//
//        bookShipRequest = new JsonObjectRequest(Request.Method.POST, Constant.REST_URL + "order?token=" + "userToken",
//                Order.buildOrderParam(AppController.getInstance().getUserId(), "",rateItem,mPickupAddr,mDropoffAddr,mPackageList,mTime,mEstValue,mInsuranceValue)
//                , new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                Logger.e(TAG, response.toString());
//                pd.dismiss();
//                bookShipRequest = null;
//                returnSelectedRate();
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                bookShipRequest = null;
//                pd.dismiss();
//                Util.handleVolleyError(error, context);
//            }
//        });
//        // Adding request to request queue
//        AppController.getInstance().addToRequestQueue(bookShipRequest);
    }
}
