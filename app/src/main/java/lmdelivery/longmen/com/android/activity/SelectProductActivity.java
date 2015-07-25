package lmdelivery.longmen.com.android.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import lmdelivery.longmen.com.android.AppController;
import lmdelivery.longmen.com.android.Constant;
import lmdelivery.longmen.com.android.R;
import lmdelivery.longmen.com.android.fragments.RateItemFragment;
import lmdelivery.longmen.com.android.bean.Address;
import lmdelivery.longmen.com.android.bean.Package;
import lmdelivery.longmen.com.android.bean.MyTime;
import lmdelivery.longmen.com.android.util.Logger;
import lmdelivery.longmen.com.android.util.Util;


public class SelectProductActivity extends AppCompatActivity {

    private static final java.lang.String TAG = SelectProductActivity.class.getName();

    private JsonObjectRequest bookShipRequest;
    private Address mPickupAddr;
    private Address mDropoffAddr;
    private MyTime mTime;
    private ArrayList<Package> mPackageList;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_product);
        context = this;
        Bundle bundle = getIntent().getExtras();
        mPackageList = bundle.getParcelableArrayList(Constant.EXTRA_PACKAGE);
        mPickupAddr = (Address) getIntent().getSerializableExtra(Constant.EXTRA_PICKUP);
        mDropoffAddr = (Address) getIntent().getSerializableExtra(Constant.EXTRA_DROPOFF);
        mTime = (MyTime) getIntent().getSerializableExtra(Constant.EXTRA_TIME);

//        Logger.e(TAG, pickup.getFullAddress());
//        Logger.e(TAG, dropoff.getFullAddress());
//        Logger.e(TAG, time.getTimeString());
//        Logger.e(TAG, packageList.size()+"");

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

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new RateItemFragment(), "Category 1");
        adapter.addFragment(new RateItemFragment(), "Category 2");
        adapter.addFragment(new RateItemFragment(), "Category 3");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_product, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

    private void bookShipment(){
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.loading));
        pd.show();

        JSONObject params = new JSONObject();
        try {
            params.put("userId", AppController.getInstance().getUserId());

            params.put("fromAddress", mPickupAddr.getStreetName());
            params.put("fromCity", mPickupAddr.getCity());
            params.put("fromProvince", mPickupAddr.getProvince());
            params.put("fromCountry", mPickupAddr.getCountry());
            params.put("fromPostal", mPickupAddr.getPostalCode());

            params.put("toAddress", mDropoffAddr.getStreetName());
            params.put("toCity", mDropoffAddr.getCity());
            params.put("toProvince", mDropoffAddr.getProvince());
            params.put("toCountry", mDropoffAddr.getCountry());
            params.put("toPostal", mDropoffAddr.getPostalCode());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        bookShipRequest = new JsonObjectRequest(Request.Method.POST, Constant.URL + "order?token=" + "userToken", params, new Response.Listener<JSONObject>() {
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
                Util.handleVolleyError(error, context);
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(bookShipRequest);
    }
}
