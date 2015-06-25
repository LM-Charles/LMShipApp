package lmdelivery.longmen.com.android;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lmdelivery.longmen.com.android.UIFragments.DestinationFragment;
import lmdelivery.longmen.com.android.UIFragments.PackageFragment;
import lmdelivery.longmen.com.android.UIFragments.PickupFragment;
import lmdelivery.longmen.com.android.UIFragments.TimeFragment;
import lmdelivery.longmen.com.android.UIFragments.bean.MyAddress;
import lmdelivery.longmen.com.android.UIFragments.bean.MyPackage;
import lmdelivery.longmen.com.android.UIFragments.bean.MyTime;
import lmdelivery.longmen.com.android.util.Logger;


public class NewBookingActivity extends AppCompatActivity implements TimeFragment.OnTimeSelectedListener, GoogleApiClient.OnConnectionFailedListener {
    private static final java.lang.String TAG = NewBookingActivity.class.getName();
    private TabLayout tabLayout;
    private PickupFragment pickupFragment;
    private DestinationFragment dropOffFragment;
    private PackageFragment packageFragment;
    private TimeFragment timeFragment;

    public ArrayList<MyPackage> myPackageArrayList;

    public MyTime selectedTime;
    public MyAddress pickupAddr;
    public MyAddress dropOffAddr;
    private Adapter adapter;
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

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }


        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int currentTab = tab.getPosition();
                switch (currentTab) {
                    case Constant.TAB_FROM:
                        if (pickupFragment.saveAndValidate()) {
                            tabLayout.getTabAt(Constant.TAB_FROM).setIcon(R.drawable.shape_greendot);
                        } else {
                            tabLayout.getTabAt(Constant.TAB_FROM).setIcon(R.drawable.shape_reddot);
                        }
                        break;

                    case Constant.TAB_TO:
                        if (dropOffFragment.saveAndValidate()) {
                            tabLayout.getTabAt(Constant.TAB_TO).setIcon(R.drawable.shape_greendot);
                        } else {
                            tabLayout.getTabAt(Constant.TAB_TO).setIcon(R.drawable.shape_reddot);
                        }
                        break;

                    case Constant.TAB_PACKAGE:
                        if (packageFragment.validateAllPackage()) {
                            tabLayout.getTabAt(Constant.TAB_PACKAGE).setIcon(R.drawable.shape_greendot);
                        } else {
                            tabLayout.getTabAt(Constant.TAB_PACKAGE).setIcon(R.drawable.shape_reddot);
                        }

                        break;
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
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
                    for(int i = 0 ; i < myPackageArrayList.size(); i++){
                        Logger.e(TAG,myPackageArrayList.get(i).toString());
                    }
                }
            }
        });

        init();
    }

    private void init() {
        pickupAddr = new MyAddress();
        dropOffAddr = new MyAddress();
        myPackageArrayList = new ArrayList<>();
        myPackageArrayList.add(new MyPackage());
    }

    public void scrollTo(int tabPosition) {
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
        adapter = new Adapter(getSupportFragmentManager());

        if (pickupFragment == null)
            pickupFragment = PickupFragment.newInstance();
        if (dropOffFragment == null)
            dropOffFragment = DestinationFragment.newInstance();
        if (packageFragment == null)
            packageFragment = PackageFragment.newInstance();
        if (timeFragment == null)
            timeFragment = TimeFragment.newInstance();

        adapter.addFragment(pickupFragment, getString(R.string.tab_title_from));
        adapter.addFragment(dropOffFragment, getString(R.string.tab_title_to));
        adapter.addFragment(packageFragment, getString(R.string.tab_title_package));
        adapter.addFragment(timeFragment, getString(R.string.tab_title_time));
        adapter.addFragment(new TextFragment(), getString(R.string.tab_title_summary));
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

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

    private List<String> validatePickup() {
        ArrayList<String> errList = new ArrayList<>();

        //validate street
        String street = pickupAddr.getStreetName();
        if (street.isEmpty()) {
            errList.add(getString(R.string.err_street_empty));
        }

        //validate city
        String city = pickupAddr.getCity();
        if (city.isEmpty()) {
            errList.add(getString(R.string.err_city_empty));
        } else if (!Constant.citiesInVan.contains(city.toUpperCase())) {
            errList.add(city + getString(R.string.err_not_in_van));
        }

        //validate postal
        String zip = pickupAddr.getPostalCode();
        String regex = "^(?!.*[DFIOQU])[A-VXY][0-9][A-Z] ?[0-9][A-Z][0-9]$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(zip);
        if (zip.isEmpty())
            errList.add(getString(R.string.err_post_empty));
        else if (!matcher.matches())
            errList.add(getString(R.string.err_post_wrong_format));

        printErrList(errList);
        return errList;
    }

    private void printErrList(ArrayList<String> errList) {
        String out = "";
        for (int i = 0; i < errList.size(); i++) {
            out += errList.get(i) + "\n";
        }
        Logger.e(TAG, "error: " + out);
    }


    public void updatePickupAddress(String unitNumber, String streetName, String city, String post) {
        pickupAddr.setUnitNumber(unitNumber);
        pickupAddr.setStreetName(streetName);
        pickupAddr.setPostalCode(post);
        pickupAddr.setCity(city);
    }

    public void updateDropOffAddress(String unitNumber, String streetName, String city, String province, String country, String post) {
        dropOffAddr.setUnitNumber(unitNumber);
        dropOffAddr.setStreetName(streetName);
        dropOffAddr.setCity(city);
        dropOffAddr.setPostalCode(post);
        dropOffAddr.setProvince(province);
        dropOffAddr.setCountry(country);
    }

}
