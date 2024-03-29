package lmdelivery.longmen.com.android.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import javax.inject.Inject;

import lmdelivery.longmen.com.android.AppController;
import lmdelivery.longmen.com.android.Constant;
import lmdelivery.longmen.com.android.R;
import lmdelivery.longmen.com.android.activity.LoginActivity;
import lmdelivery.longmen.com.android.api.LMXApi;
import lmdelivery.longmen.com.android.data.User;
import lmdelivery.longmen.com.android.util.DialogUtil;
import lmdelivery.longmen.com.android.util.Logger;
import lmdelivery.longmen.com.android.util.RxUtils;
import lmdelivery.longmen.com.android.util.Util;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFragment.OnLoginListener} interface
 * to handle interaction events.
 */
public class LoginFragment extends Fragment {
    private static final java.lang.String TAG = LoginFragment.class.getSimpleName();
    private JsonObjectRequest loginRequest;
    private JsonObjectRequest getResetCodeReq;
    private JsonObjectRequest resetPasswordReq;


    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private TextInputLayout tilPassWord;
    private TextInputLayout tilEmail;
    private TextView mForgotPW;
    private LoginActivity loginActivity;

    private OnLoginListener mListener;

    @Inject
    LMXApi lmxApi;
    private CompositeSubscription subscriptions = new CompositeSubscription();

    public LoginFragment() {
        // Required empty public constructor
    }

    public void cancelQueueRequest() {

        if (loginRequest != null) {
            loginRequest.cancel();
            loginRequest = null;
        }

        if (getResetCodeReq != null) {
            getResetCodeReq.cancel();
            getResetCodeReq = null;
        }

        if (resetPasswordReq != null) {
            resetPasswordReq.cancel();
            resetPasswordReq = null;
        }

    }

    public boolean hasFocus() {
        return (mEmailView != null && mPasswordView != null) && (mEmailView.isFocused() || mPasswordView.isFocused());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_login, container, false);

        AppController.getComponent().inject(this);

        loginActivity = (LoginActivity) getActivity();

        tilEmail = (TextInputLayout) root.findViewById(R.id.til_email);
        tilPassWord = (TextInputLayout) root.findViewById(R.id.til_password);

        mForgotPW = (TextView) root.findViewById(R.id.tv_forgot_pw);
        mForgotPW.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        mForgotPW.setOnClickListener(v -> showForgotPasswordDialog());

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) root.findViewById(R.id.email);

        mPasswordView = (EditText) root.findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == R.id.login || id == EditorInfo.IME_NULL) {
                //empty in order to use the value in the ui form
                attemptLogin("", "");
                return true;
            }
            return false;
        });

        Button mEmailSignInButton = (Button) root.findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(view -> {
            //empty in order to use the value in the ui form
            attemptLogin("", "");
        });


        // Inflate the layout for this fragment
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        subscriptions = RxUtils.getNewCompositeSubIfUnsubscribed(subscriptions);
    }

    @Override
    public void onPause() {
        super.onPause();

        RxUtils.unsubscribeIfNotNull(subscriptions);
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.matches("^(?=.*[a-z])(?=.*[0-9])[a-zA-Z0-9]+$");
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin(String email, String password) {
        if (loginRequest != null) {
            return;
        }

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            // Reset errors.
            tilPassWord.setError(null);
            tilEmail.setError(null);

            // Store values at the time of the login attempt.
            email = mEmailView.getText().toString();
            password = mPasswordView.getText().toString();

            if (TextUtils.isEmpty(password)) {
                tilPassWord.setError(getString(R.string.error_field_required));
                focusView = mPasswordView;
                cancel = true;
            }

            // Check for a valid email address.
            if (TextUtils.isEmpty(email)) {
                tilEmail.setError(getString(R.string.error_field_required));
                focusView = mEmailView;
                cancel = true;
            } else if (!isEmailValid(email)) {
                tilEmail.setError(getString(R.string.error_invalid_email));
                focusView = mEmailView;
                cancel = true;
            }
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            final ProgressDialog pd = new ProgressDialog(getActivity());
            pd.setMessage(getString(R.string.loading));
            pd.show();

            JSONObject params = new JSONObject();
            try {
                params.put("email", email);
                params.put("password", password);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            final String finalEmail = email;
            final String finalPassword = password;
            loginRequest = new JsonObjectRequest(Request.Method.POST, Constant.REST_URL + "login?email=" + email + "&password=" + password, params, response -> {
                Timber.i(response.toString());
                pd.dismiss();
                loginRequest = null;
                try {
                    int id = response.getInt("id");
                    String token = response.getString("apiToken");
                    String status = response.getString("status");
                    SharedPreferences sharedPref = getActivity().getSharedPreferences(Constant.SHARE_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(Constant.SHARE_USER_EMAIL, finalEmail);
                    editor.putInt(Constant.SHARE_USER_ID, id);
                    editor.putString(Constant.SHARE_USER_TOKEN, token);
                    editor.apply();

                    Timber.i("starting");
                    subscriptions.add(
                            lmxApi.getUser(id,id,token)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Subscriber<User>() {
                                        @Override
                                        public void onCompleted() {
                                            Timber.i("onCompleted");
                                            if (!status.equals("ACTIVE")) {
                                                Toast.makeText(getActivity(), R.string.verify_again, Toast.LENGTH_LONG).show();
                                                ((LoginActivity) getActivity()).showVerifyPhoneNumberDialog(finalEmail, finalPassword);
                                            } else {
                                                editor.putBoolean(Constant.SHARE_IS_USER_ACTIVATED, true);
                                                editor.apply();
                                                ((LoginActivity) getActivity()).returnLoginSuccessResult();
                                                Toast.makeText(getActivity(), R.string.login_success, Toast.LENGTH_LONG).show();
                                            }
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            Timber.e("onError" + e.toString());
                                            if (!status.equals("ACTIVE")) {
                                                Toast.makeText(getActivity(), R.string.verify_again, Toast.LENGTH_LONG).show();
                                                ((LoginActivity) getActivity()).showVerifyPhoneNumberDialog(finalEmail, finalPassword);
                                            } else {
                                                editor.putBoolean(Constant.SHARE_IS_USER_ACTIVATED, true);
                                                editor.apply();
                                                ((LoginActivity) getActivity()).returnLoginSuccessResult();
                                                Toast.makeText(getActivity(), R.string.login_success, Toast.LENGTH_LONG).show();
                                            }
                                        }

                                        @Override
                                        public void onNext(User user) {
                                            if(user.address!=null){
                                                user.address.save();
                                            }

                                            user.save();
                                        }
                                    }));
                } catch (Exception e) {
                    e.printStackTrace();
                    DialogUtil.showMessageDialog(getString(R.string.err_connection), getActivity());
                }
            }, error -> {
                loginRequest = null;
                pd.dismiss();
                Util.handleVolleyError(error, getActivity());
            });
            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(loginRequest, "login");
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onLoginClicked(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnLoginListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnLoginListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        if (getActivity() != null) {
            if(emailAddressCollection.size()>0) {
                mEmailView.setText(emailAddressCollection.get(0));
            }
            //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

            mEmailView.setAdapter(adapter);
        }
    }


    public interface OnLoginListener {
        // TODO: Update argument type and name
        public void onLoginClicked(Uri uri);
    }


    private void showForgotPasswordDialog() {
        final Dialog dialog = new Dialog(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_forgot_pw, null);
        final EditText etEmail = (EditText) view.findViewById(R.id.et_email);
        final EditText etCode = (EditText) view.findViewById(R.id.et_code);
        final EditText etNewPassword = (EditText) view.findViewById(R.id.et_new_password);
        final TextView tvNoCode = (TextView) view.findViewById(R.id.tv_no_code);


        final TextInputLayout tilEmail = (TextInputLayout) view.findViewById(R.id.til_phone);
        final TextInputLayout tilCode = (TextInputLayout) view.findViewById(R.id.til_code);
        final TextInputLayout tilNewPassword = (TextInputLayout) view.findViewById(R.id.til_new_password);

        final Button resetBtn = (Button) view.findViewById(R.id.btn_reset);
        final Button contactBtn = (Button) view.findViewById(R.id.btn_contact);
        final Button getCodeBtn = (Button) view.findViewById(R.id.btn_get_code);

        contactBtn.setOnClickListener(v -> Util.sendSupportEmail(getActivity()));

        getCodeBtn.setOnClickListener(v -> {
            if (getResetCodeReq != null)
                return;

            final String email = etEmail.getText().toString();

            if (TextUtils.isEmpty(email)) {
                tilEmail.setError(getString(R.string.error_field_required));
            } else {
                final ProgressDialog pd = new ProgressDialog(getActivity());
                pd.setMessage(loginActivity.getString(R.string.loading));
                pd.show();

                getResetCodeReq = new JsonObjectRequest(Request.Method.POST, Constant.REST_URL + "user/resetPassword?email=" + email, response -> {
                    Logger.e(TAG, response.toString());
                    pd.dismiss();
                    getResetCodeReq = null;

                    try {
                        String message = response.getString("message");
                        DialogUtil.showMessageDialog(message, getActivity());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    tilCode.setVisibility(View.VISIBLE);
                    tilNewPassword.setVisibility(View.VISIBLE);
                    tilEmail.setVisibility(View.VISIBLE);
                    tilEmail.setFocusable(false);
                    getCodeBtn.setText(getString(R.string.request_again));
                    resetBtn.setVisibility(View.VISIBLE);
                    contactBtn.setVisibility(View.VISIBLE);
                    tvNoCode.setVisibility(View.VISIBLE);
                }, error -> {
                    getResetCodeReq = null;
                    pd.dismiss();
                    Util.handleVolleyError(error, getActivity());
                });

                // Adding request to request queue
                AppController.getInstance().addToRequestQueue(getResetCodeReq);
            }
        });

        resetBtn.setOnClickListener(v -> {
            if (resetPasswordReq != null)
                return;

            String newPassword = etNewPassword.getText().toString();
            String code = etCode.getText().toString();

            if (TextUtils.isEmpty(newPassword)) {
                tilNewPassword.setError(getString(R.string.error_field_required));
            } else if (newPassword.length() < Constant.PASSWORD_MIN_LENGTH) {
                tilNewPassword.setError(getString(R.string.error_password_too_short, Constant.PASSWORD_MIN_LENGTH));
            }
            // Check for a valid password, if the user entered one.
            else if (!isPasswordValid(newPassword)) {
                tilNewPassword.setError(getString(R.string.error_invalid_password));
            } else if (TextUtils.isEmpty(code)) {
                tilCode.setError(getString(R.string.error_field_required));
            } else {
                final ProgressDialog pd = new ProgressDialog(getActivity());
                pd.setMessage(loginActivity.getString(R.string.loading));
                pd.show();

                resetPasswordReq = new JsonObjectRequest(Request.Method.POST, Constant.REST_URL + "user/resetPassword/" + code + "?email=" + etEmail.getText().toString() + "&newPassword=" + newPassword,

                        response -> {
                            Logger.e(TAG, response.toString());
                            pd.dismiss();
                            resetPasswordReq = null;
                            Toast.makeText(getActivity(), R.string.passwrod_update_success, Toast.LENGTH_LONG).show();
                            mEmailView.setText(etEmail.getText().toString());
                            dialog.dismiss();
                        }, error -> {
                    resetPasswordReq = null;
                    pd.dismiss();
                    Util.handleVolleyError(error, getActivity());
                });

                // Adding request to request queue
                AppController.getInstance().addToRequestQueue(resetPasswordReq);
            }
        });

        contactBtn.setOnClickListener(v -> Util.sendSupportEmail(getActivity()));

        // Inflate and set the layout for the dialog
        dialog.setContentView(view);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setTitle(getActivity().getString(R.string.forgot_password)
        );
        dialog.show();
    }
}
