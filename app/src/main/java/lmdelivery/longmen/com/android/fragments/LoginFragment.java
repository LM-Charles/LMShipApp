package lmdelivery.longmen.com.android.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import lmdelivery.longmen.com.android.AppController;
import lmdelivery.longmen.com.android.Constant;
import lmdelivery.longmen.com.android.activity.LoginActivity;
import lmdelivery.longmen.com.android.R;
import lmdelivery.longmen.com.android.util.Logger;
import lmdelivery.longmen.com.android.util.Util;

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

        loginActivity = (LoginActivity) getActivity();

        tilEmail = (TextInputLayout) root.findViewById(R.id.til_email);
        tilPassWord = (TextInputLayout) root.findViewById(R.id.til_password);

        mForgotPW = (TextView) root.findViewById(R.id.tv_forgot_pw);
        mForgotPW.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        mForgotPW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForgotPasswordDialog();
            }
        });

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) root.findViewById(R.id.email);

        mPasswordView = (EditText) root.findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) root.findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });


        // Inflate the layout for this fragment
        return root;
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {

        if (loginRequest != null) {
            return;
        }

        // Reset errors.
        tilPassWord.setError(null);
        tilEmail.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            tilPassWord.setError(getString(R.string.error_invalid_password));
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

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            final ProgressDialog pd = new ProgressDialog(getActivity());
            pd.setMessage("Loading");
            pd.show();

            JSONObject params = new JSONObject();
            try {
                params.put("email", mEmailView.getText().toString());
                params.put("password", mPasswordView.getText().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            loginRequest = new JsonObjectRequest(Request.Method.PUT, Constant.URL + "user", params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Logger.e(TAG, response.toString());
                    pd.dismiss();
                    loginRequest = null;
                    //TODO: save user info
                    loginActivity.finish();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    loginRequest = null;
                    pd.dismiss();
                    if (error != null)
                        Logger.e(TAG, error.toString());

                    if (getActivity() != null)
                        Util.showMessageDialog(getString(R.string.err_connection), getActivity());
                }
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
            //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

            mEmailView.setAdapter(adapter);
        }
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnRegisterListener {
        // TODO: Update argument type and name
        public void OnRegisterInteraction(Uri uri);
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
        final EditText etPhone = (EditText) view.findViewById(R.id.et_phone);
        final EditText etCode = (EditText) view.findViewById(R.id.et_code);
        final EditText etNewPassword = (EditText) view.findViewById(R.id.et_new_password);

        final TextInputLayout tilPhone = (TextInputLayout) view.findViewById(R.id.til_phone);
        final TextInputLayout tilCode = (TextInputLayout) view.findViewById(R.id.til_code);
        final TextInputLayout tilNewPassword = (TextInputLayout) view.findViewById(R.id.til_new_password);

        final Button resetBtn = (Button) view.findViewById(R.id.btn_reset);
        final Button contactBtn = (Button) view.findViewById(R.id.btn_contact);
        final Button getCodeBtn = (Button) view.findViewById(R.id.btn_get_code);

        getCodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getResetCodeReq != null)
                    return;

                final String phone = etPhone.getText().toString();

                if (TextUtils.isEmpty(phone)) {
                    tilPhone.setError(getString(R.string.error_field_required));
                } else {
                    final ProgressDialog pd = new ProgressDialog(getActivity());
                    pd.setMessage(loginActivity.getString(R.string.loading));
                    pd.show();

                    getResetCodeReq = new JsonObjectRequest(Request.Method.POST, Constant.URL + "user/" + AppController.getInstance().getUserId() + "/resetPassword", new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Logger.e(TAG, response.toString());
                            pd.dismiss();
                            getResetCodeReq = null;
                            Util.showMessageDialog(getString(R.string.verify_dialog_text, phone), getActivity());

                            tilCode.setVisibility(View.VISIBLE);
                            tilNewPassword.setVisibility(View.VISIBLE);
                            tilPhone.setVisibility(View.VISIBLE);
                            getCodeBtn.setText(getString(R.string.request_again));
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            getResetCodeReq = null;
                            pd.dismiss();
                            Util.handleVolleyError(error, getActivity());
                        }
                    });

                    // Adding request to request queue
                    AppController.getInstance().addToRequestQueue(getResetCodeReq);
                }
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (resetPasswordReq != null)
                    return;

                String newPassword = etNewPassword.getText().toString();
                String code = etCode.getText().toString();

                if (TextUtils.isEmpty(newPassword)) {
                    tilPassWord.setError(getString(R.string.error_field_required));
                }
                // Check for a valid password, if the user entered one.
                else if (!isPasswordValid(newPassword)) {
                    tilPassWord.setError(getString(R.string.error_invalid_password));
                } else if (TextUtils.isEmpty(code)) {
                    tilCode.setError(getString(R.string.error_field_required));
                } else {
                    final ProgressDialog pd = new ProgressDialog(getActivity());
                    pd.setMessage(loginActivity.getString(R.string.loading));
                    pd.show();

                    resetPasswordReq = new JsonObjectRequest(Request.Method.POST, Constant.URL + "user/" + AppController.getInstance().getUserId() + "/resetPassword/" + code + "&newPassword=" + newPassword,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Logger.e(TAG, response.toString());
                                    pd.dismiss();
                                    resetPasswordReq = null;

                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            resetPasswordReq = null;
                            pd.dismiss();
                            Util.handleVolleyError(error, getActivity());
                        }
                    });

                    // Adding request to request queue
                    AppController.getInstance().addToRequestQueue(resetPasswordReq);
                }
            }
        });

        contactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.sendSupportEmail(getActivity());
            }
        });

        // Inflate and set the layout for the dialog
        dialog.setContentView(view);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setTitle(getActivity().getString(R.string.forgot_password)
        );
        dialog.show();
    }

}
