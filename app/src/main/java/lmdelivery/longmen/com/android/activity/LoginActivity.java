package lmdelivery.longmen.com.android.activity;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import lmdelivery.longmen.com.android.R;
import lmdelivery.longmen.com.android.fragments.LoginFragment;
import lmdelivery.longmen.com.android.fragments.RegisterFragment;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends LoginBaseActivity implements LoaderManager.LoaderCallbacks<Cursor>, LoginFragment.OnLoginListener, RegisterFragment.OnRegisterListener {

    private static final java.lang.String TAG = LoginActivity.class.getName();

    // UI references.
    private AppBarLayout appBarLayout;
    private CollapsingToolbarLayout collapsingToolbar;
    private CoordinatorLayout rootLayout;
    public LoginFragment loginFragment;
    public RegisterFragment registerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        rootLayout = (CoordinatorLayout) findViewById(R.id.main_content);
        collapsingToolbar.setTitle("龙门镖局");
        loadBackdrop();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


        rootLayout.post(new Runnable() {
            @Override
            public void run() {
                viewPager.setMinimumHeight(rootLayout.getHeight());
            }
        });

        populateAutoComplete();

        //showVerifyPhoneNumberDialog();
//        mProgressView = findViewById(R.id.login_progress);
    }

    @Override
    protected void onShowKeyboard(int keyboardHeight) {
        // do things when keyboard is shown
        toggleToolbar();
    }

    @Override
    protected void onHideKeyboard() {
        // do things when keyboard is hidden
        //expandToolbar();

    }

    private void setupViewPager(ViewPager viewPager) {
        if (loginFragment == null)
            loginFragment = new LoginFragment();
        if (registerFragment == null)
            registerFragment = new RegisterFragment();
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(registerFragment, "Register");
        adapter.addFragment(loginFragment, "Login");
        viewPager.setAdapter(adapter);
    }

    public void toggleToolbar() {
        if (loginFragment != null && registerFragment != null && (loginFragment.hasFocus() || registerFragment.hasFocus())) {
            collapseToolbar();
        } else {
            expandToolbar();
        }
    }

    public void collapseToolbar() {
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
        if (behavior != null) {
            behavior.onNestedFling(rootLayout, appBarLayout, null, 0, 10000, true);
        }
    }

    public void expandToolbar() {

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
        if (behavior != null) {
            behavior.setTopAndBottomOffset(0);
            behavior.onNestedPreScroll(rootLayout, appBarLayout, null, 0, 1, new int[2]);
        }

    }

    private void loadBackdrop() {
        final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
        Glide.with(this).load(R.drawable.background).centerCrop().into(imageView);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE + " = ?", new String[]{ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        List<String> emails = new ArrayList<String>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        registerFragment.addEmailsToAutoComplete(emails);
        loginFragment.addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    @Override
    public void onLoginClicked(Uri uri) {

    }

    @Override
    public void onRegisterClicked(Uri uri) {

    }

    @Override
    public void onResume() {
        super.onResume();
        attachKeyboardListeners();

    }

    @Override
    public void onStop() {
        super.onStop();

        if(loginFragment!=null){
            loginFragment.cancelQueueRequest();
        }
        if(registerFragment!=null){
            registerFragment.cancelQueueRequest();
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


}

