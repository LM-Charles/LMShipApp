package lmdelivery.longmen.com.android;

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
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;

import java.util.ArrayList;
import java.util.List;

import lmdelivery.longmen.com.android.UIFragments.DestinationFragment;
import lmdelivery.longmen.com.android.UIFragments.PackageFragment;
import lmdelivery.longmen.com.android.UIFragments.PickupFragment;
import lmdelivery.longmen.com.android.UIFragments.TimeFragment;
import lmdelivery.longmen.com.android.UIFragments.bean.MyTime;
import lmdelivery.longmen.com.android.util.Logger;


public class NewBookingActivity extends AppCompatActivity implements TimeFragment.OnTimeSelectedListener, GoogleApiClient.OnConnectionFailedListener {
    private static final java.lang.String TAG = NewBookingActivity.class.getName();
    private String pickupStreet, pickupCity, pickupPostal, pickupUnit;
    private String dropoffStreet, dropoffCity, dropoffPostal, dropoffUnit;
    private TabLayout tabLayout;

    public MyTime selectedTime;


    /**
     * GoogleApiClient wraps our service connection to Google Play Services and provides access
     * to the user's sign in state as well as the Google's APIs.
     */
    public GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_booking);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.getTabAt(0).setIcon(R.drawable.shape_greendot);
        tabLayout.getTabAt(1).setIcon(R.drawable.shape_greendot);
    }

    public void scrollTo(int tabPosition){
        tabLayout.getTabAt(tabPosition).select();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(PickupFragment.newInstance(), getString(R.string.tab_title_from));
        adapter.addFragment(DestinationFragment.newInstance(), getString(R.string.tab_title_to));
        adapter.addFragment(PackageFragment.newInstance(), getString(R.string.tab_title_package));
        adapter.addFragment(TimeFragment.newInstance(), getString(R.string.tab_title_time));
        adapter.addFragment(new TextFragment(), getString(R.string.tab_title_quote));
        viewPager.setAdapter(adapter);
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

//    private EditText.OnFocusChangeListener notEmptyFocusChangeListener = new View.OnFocusChangeListener() {
//        @Override
//        public void onFocusChange(View v, boolean hasFocus) {
//            if(!hasFocus){
//                switch (v.getId()){
//                    case R.id.et_city:
//                    case R.id.et_to_city:
//                        if()
//                        ((EditText) v).setError("City cannot be empty");
//
//
//                }
//            }
//        }
//    };


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


    public String getPickupStreet() {
        return pickupStreet;
    }

    public void setPickupStreet(String pickupStreet) {
        this.pickupStreet = pickupStreet;
    }

    public String getPickupCity() {
        return pickupCity;
    }

    public void setPickupCity(String pickupCity) {
        this.pickupCity = pickupCity;
    }

    public String getPickupPostal() {
        return pickupPostal;
    }

    public void setPickupPostal(String pickupPostal) {
        this.pickupPostal = pickupPostal;
    }

    public String getPickupUnit() {
        return pickupUnit;
    }

    public void setPickupUnit(String pickupUnit) {
        this.pickupUnit = pickupUnit;
    }

    public String getDropoffStreet() {
        return dropoffStreet;
    }

    public void setDropoffStreet(String dropoffStreet) {
        this.dropoffStreet = dropoffStreet;
    }

    public String getDropoffCity() {
        return dropoffCity;
    }

    public void setDropoffCity(String dropoffCity) {
        this.dropoffCity = dropoffCity;
    }

    public String getDropoffPostal() {
        return dropoffPostal;
    }

    public void setDropoffPostal(String dropoffPostal) {
        this.dropoffPostal = dropoffPostal;
    }

    public String getDropoffUnit() {
        return dropoffUnit;
    }

    public void setDropoffUnit(String dropoffUnit) {
        this.dropoffUnit = dropoffUnit;
    }
}
