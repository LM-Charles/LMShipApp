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

import java.util.ArrayList;
import java.util.List;

import lmdelivery.longmen.com.android.UIFragments.RateItemFragment;
import lmdelivery.longmen.com.android.UIFragments.bean.MyAddress;
import lmdelivery.longmen.com.android.UIFragments.bean.MyPackage;
import lmdelivery.longmen.com.android.UIFragments.bean.MyTime;


public class SelectProductActivity extends AppCompatActivity {

    private static final java.lang.String TAG = SelectProductActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_product);

        Bundle bundle = getIntent().getExtras();
        ArrayList<MyPackage> packageList = bundle.getParcelableArrayList(Constant.EXTRA_PACKAGE);
        MyAddress pickup = (MyAddress) getIntent().getSerializableExtra(Constant.EXTRA_PICKUP);
        MyAddress dropoff = (MyAddress) getIntent().getSerializableExtra(Constant.EXTRA_DROPOFF);
        MyTime time = (MyTime) getIntent().getSerializableExtra(Constant.EXTRA_TIME);

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
}
