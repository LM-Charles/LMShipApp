package lmdelivery.longmen.com.android.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import lmdelivery.longmen.com.android.AppController;
import lmdelivery.longmen.com.android.Constant;
import lmdelivery.longmen.com.android.R;
import lmdelivery.longmen.com.android.data.RateItem;
import lmdelivery.longmen.com.android.fragments.LoginFragment;
import lmdelivery.longmen.com.android.fragments.RegisterFragment;
import lmdelivery.longmen.com.android.util.DialogUtil;
import lmdelivery.longmen.com.android.util.Logger;
import lmdelivery.longmen.com.android.util.Util;


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

    private JsonObjectRequest getCodeRequest;
    private JsonObjectRequest activateAccountRequest;

    private Context context;
    private RateItem rateItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = this;
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        rootLayout = (CoordinatorLayout) findViewById(R.id.main_content);
        collapsingToolbar.setTitle("龙门镖局");
        loadBackdrop();


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        rateItem = getIntent().getParcelableExtra(Constant.EXTRA_RATE_ITEM);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


        rootLayout.post(() -> viewPager.setMinimumHeight(rootLayout.getHeight()));

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

        if (loginFragment != null) {
            loginFragment.cancelQueueRequest();
        }
        if (registerFragment != null) {
            registerFragment.cancelQueueRequest();
        }

        if (getCodeRequest != null) {
            getCodeRequest.cancel();
            getCodeRequest = null;
        }

        if (activateAccountRequest != null) {
            activateAccountRequest.cancel();
            activateAccountRequest = null;
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

    public void returnLoginSuccessResult() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(Constant.EXTRA_RATE_ITEM, rateItem);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    public void showVerifyPhoneNumberDialog(final String email, final String password) {
        final Dialog dialog = new Dialog(this);

        // Get the layout inflater
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_verify_phone, null);
        final EditText etPhone = (EditText) view.findViewById(R.id.et_email);
        final EditText etCode = (EditText) view.findViewById(R.id.et_code);

        final TextInputLayout tilPhone = (TextInputLayout) view.findViewById(R.id.til_phone);
        final TextInputLayout tilCode = (TextInputLayout) view.findViewById(R.id.til_code);
        final Button btnSave = (Button) view.findViewById(R.id.btn_save);
        final Button btnVerify = (Button) view.findViewById(R.id.btn_verify);
        final Button btnContact = (Button) view.findViewById(R.id.btn_contact);
        final Button btnRequestAgain = (Button) view.findViewById(R.id.btn_request_again);

        final TextView tvNoCode = (TextView) view.findViewById(R.id.tv_no_code);

        btnContact.setOnClickListener(v -> Util.sendSupportEmail(context));

        btnSave.setOnClickListener(v -> {
            if (getCodeRequest != null)
                return;

            final String phone = etPhone.getText().toString();
            if (phone.isEmpty()) {
                tilPhone.setError(getString(R.string.error_field_required));
            } else if (phone.length() < 10) {
                tilPhone.setError(getString(R.string.error_phone_too_short));
            } else if (phone.length() > 10) {
                tilPhone.setError(getString(R.string.error_phone_too_long));
            } else {
                final ProgressDialog pd = new ProgressDialog(context);
                pd.setMessage(getString(R.string.loading));
                pd.show();


                getCodeRequest = new JsonObjectRequest(Request.Method.POST, Constant.REST_URL + "user/activation?phone=1" + phone + "&email=" + email + "&password=" + password, response -> {
                    getCodeRequest = null;
                    Logger.e(TAG, response.toString());
                    pd.dismiss();
                    AppController.getInstance().getDefaultSharePreferences().edit().putString(Constant.SHARE_USER_PHONE, phone).apply();

                    try {
                        String message = response.getString("message");
                        DialogUtil.showMessageDialog(message, context);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    etPhone.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            btnContact.setVisibility(View.GONE);
                            btnVerify.setVisibility(View.GONE);
                            btnSave.setVisibility(View.VISIBLE);
                            tilCode.setVisibility(View.GONE);
                            btnRequestAgain.setVisibility(View.GONE);
                            tvNoCode.setVisibility(View.GONE);
                        }
                    });
                    btnContact.setVisibility(View.VISIBLE);
                    btnVerify.setVisibility(View.VISIBLE);
                    btnSave.setVisibility(View.GONE);
                    btnRequestAgain.setVisibility(View.VISIBLE);
                    tilCode.setVisibility(View.VISIBLE);
                    tvNoCode.setVisibility(View.VISIBLE);
                }, error -> {
                    getCodeRequest = null;
                    pd.dismiss();
                    Util.handleVolleyError(error, context);
                });

                // Adding request to request queue
                AppController.getInstance().addToRequestQueue(getCodeRequest);
            }
        });

        btnRequestAgain.setOnClickListener(v -> sendGetCodeRequest(etPhone, tilPhone));


        btnVerify.setOnClickListener(v -> sendActivateAccountRequest(etCode, tilCode, dialog, email, password));

        // Inflate and set the layout for the dialog
        dialog.setContentView(view);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setTitle(getString(R.string.verify_phone));
        dialog.show();
        etPhone.post(() -> {
            String phoneNumber = Util.getPhoneNumber();
            if (!TextUtils.isEmpty(phoneNumber))
                etPhone.setText(phoneNumber);
        });
    }

    private void sendGetCodeRequest(EditText etPhone, TextInputLayout tilPhone) {
        if (getCodeRequest != null)
            return;

        final String phone = etPhone.getText().toString();
        if (phone.isEmpty()) {
            tilPhone.setError(getString(R.string.error_field_required));
        } else if (phone.length() < 10) {
            tilPhone.setError(getString(R.string.error_phone_too_short));
        } else if (phone.length() > 10) {
            tilPhone.setError(getString(R.string.error_phone_too_long));
        } else {

            final ProgressDialog pd = new ProgressDialog(context);
            pd.setMessage(getString(R.string.loading));
            pd.show();

            getCodeRequest = new JsonObjectRequest(Request.Method.POST, Constant.REST_URL + "user/" + AppController.getInstance().getUserId() + "/activation?phone=" + "1" + phone, response -> {
                getCodeRequest = null;
                Logger.e(TAG, response.toString());
                pd.dismiss();
                AppController.getInstance().getDefaultSharePreferences().edit().putString(Constant.SHARE_USER_PHONE, phone).apply();
                DialogUtil.showMessageDialog(getString(R.string.verify_dialog_text, phone), context);
            }, error -> {
                getCodeRequest = null;
                pd.dismiss();
                Util.handleVolleyError(error, context);
            });

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(getCodeRequest);
        }
    }

    private void sendActivateAccountRequest(EditText etCode, TextInputLayout tilCode, final Dialog dialog, final String email, final String password) {
        if (activateAccountRequest != null) {
            return;
        }

        String code = etCode.getText().toString().trim();
        if (code.isEmpty()) {
            tilCode.setError(getString(R.string.error_field_required));
        } else {
            final ProgressDialog pd = new ProgressDialog(context);
            pd.setMessage(getString(R.string.loading));
            pd.show();


            activateAccountRequest = new JsonObjectRequest(Request.Method.POST, Constant.REST_URL + "user/activation/" + code + "?email=" + AppController.getInstance().getUserEmail(), response -> {
                activateAccountRequest = null;
                Logger.e(TAG, response.toString());
                pd.dismiss();
                dialog.dismiss();
                SharedPreferences sharedPref = context.getSharedPreferences(Constant.SHARE_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(Constant.SHARE_IS_USER_ACTIVATED, true);
                editor.apply();
                showVerifySuccessDialog(email, password);
            }, error -> {
                Logger.e(TAG, error.toString());
                activateAccountRequest = null;
                pd.dismiss();
                Util.handleVolleyError(error, context);
            });

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(activateAccountRequest, "activateAccount");
        }
    }

    private void showVerifySuccessDialog(final String email, final String password) {
        new AlertDialog.Builder(context)
                .setMessage(getString(R.string.account_activated))
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    loginFragment.attemptLogin(email, password);
                    dialog.dismiss();
                })
                .show();
    }


}

