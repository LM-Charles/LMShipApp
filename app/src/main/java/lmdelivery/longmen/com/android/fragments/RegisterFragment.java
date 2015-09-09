package lmdelivery.longmen.com.android.fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import lmdelivery.longmen.com.android.AppController;
import lmdelivery.longmen.com.android.Constant;
import lmdelivery.longmen.com.android.R;
import lmdelivery.longmen.com.android.activity.LoginActivity;
import lmdelivery.longmen.com.android.util.Logger;
import lmdelivery.longmen.com.android.util.Util;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {

    private static final java.lang.String TAG = RegisterFragment.class.getName();
    // UI references.
    private AutoCompleteTextView mEmailView;
    private LoginActivity loginActivity;
    private EditText mPasswordView;
    private TextInputLayout tilPassWord;
    private TextInputLayout tilEmail;

    private JsonObjectRequest registerRequest;
    private JsonObjectRequest getCodeRequest;
    private JsonObjectRequest activateAccountRequest;


    private OnRegisterListener mListener;

    public RegisterFragment() {
        // Required empty public constructor
    }

    public void cancelQueueRequest() {

        if (registerRequest != null) {
            registerRequest.cancel();
            registerRequest = null;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_register, container, false);

        loginActivity = (LoginActivity) getActivity();
        // Inflate the layout for this fragment

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) root.findViewById(R.id.email);
        tilEmail = (TextInputLayout) root.findViewById(R.id.til_email);
        tilPassWord = (TextInputLayout) root.findViewById(R.id.til_password);

        mPasswordView = (EditText) root.findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptRegister();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) root.findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptRegister();
            }
        });


        TextView link = (TextView) root.findViewById(R.id.link);
        link.setText(Html.fromHtml("By Creating an account, you are agreeing to our" + "<br>" +
                "<a href=\"http://zoroapp.com/EULA\">Terms and Conditions</a> "));
        link.setMovementMethod(LinkMovementMethod.getInstance());

        return root;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onRegisterClicked(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnRegisterListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnRegisterListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public boolean hasFocus() {
        return (mEmailView != null && mPasswordView != null) && (mEmailView.isFocused() || mPasswordView.isFocused());
    }

    private void attemptRegister() {

        if (registerRequest != null)
            return;

        // Reset errors.
        tilPassWord.setError(null);
        tilEmail.setError(null);

        // Store values at the time of the login attempt.
        final String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(password)) {
            tilPassWord.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }
        // Check for a valid password, if the user entered one.
        if (password.length() < Constant.PASSWORD_MIN_LENGTH) {
            tilPassWord.setError(getString(R.string.error_password_too_short, Constant.PASSWORD_MIN_LENGTH));
            focusView = mPasswordView;
            cancel = true;
        } else if (!isPasswordValid(password)) {
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
            pd.setMessage(getString(R.string.loading));
            pd.show();

            JSONObject params = new JSONObject();
            // JSONArray jsArray = new JSONArray();
            try {
                params.put("email", email);
                params.put("password", password);
                params.put("phone", "");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            registerRequest = new JsonObjectRequest(Request.Method.PUT, Constant.URL + "user", params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Logger.e(TAG, response.toString());
                    pd.dismiss();
                    registerRequest = null;

                    try {
                        String id = response.getString("id");
                        SharedPreferences sharedPref = getActivity().getSharedPreferences(Constant.SHARE_NAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString(Constant.SHARE_USER_EMAIL, email);
                        editor.putString(Constant.SHARE_USER_ID, id);
                        editor.apply();
                        Toast.makeText(getActivity(), getString(R.string.register_successful), Toast.LENGTH_LONG).show();
                        showVerifyPhoneNumberDialog();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Util.showMessageDialog(getString(R.string.err_connection), getActivity());
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    pd.dismiss();
                    Util.handleVolleyError(error, getActivity());
                    registerRequest = null;
                }
            });

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(registerRequest);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //contain both number and letter
        return password.matches("^(?=.*[a-z])(?=.*[0-9])[a-zA-Z0-9]+$");
    }

    private boolean isPhoneValid(String phone) {
        //Only support Canada number for now
        return phone.length() == 10;
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
        public void onRegisterClicked(Uri uri);
    }


    public void showVerifyPhoneNumberDialog() {
        final Dialog dialog = new Dialog(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
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

        btnContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.sendSupportEmail(getActivity());
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                    final ProgressDialog pd = new ProgressDialog(getActivity());
                    pd.setMessage(getString(R.string.loading));
                    pd.show();

                    getCodeRequest = new JsonObjectRequest(Request.Method.POST, Constant.URL + "user/activation?phone=1"+ phone + "&email=" + mEmailView.getText().toString() + "&password=" + mPasswordView.getText().toString(), new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            getCodeRequest = null;
                            Logger.e(TAG, response.toString());
                            pd.dismiss();
                            AppController.getInstance().getDefaultSharePreferences().edit().putString(Constant.SHARE_USER_PHONE, phone).apply();
                            try {
                                String message = response.getString("message");
                                Util.showMessageDialog(message, getActivity());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            etPhone.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {}

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
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            getCodeRequest = null;
                            pd.dismiss();
                            Util.handleVolleyError(error, getActivity());
                        }
                    });

                    // Adding request to request queue
                    AppController.getInstance().addToRequestQueue(getCodeRequest);
                }
            }
        });

        btnRequestAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendGetCodeRequest(etPhone, tilPhone);
            }
        });


        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendActivateAccountRequest(etCode, tilCode, dialog);
            }
        });

        // Inflate and set the layout for the dialog
        dialog.setContentView(view);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setTitle(getString(R.string.verify_phone));
        dialog.show();
        etPhone.post(new Runnable() {
            @Override
            public void run() {
                String phoneNumber = Util.getPhoneNumber();
                if (!TextUtils.isEmpty(phoneNumber))
                    etPhone.setText(phoneNumber);
            }
        });
    }

    public void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        if (getActivity() != null) {
            //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

            mEmailView.setAdapter(adapter);
        }
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

            final ProgressDialog pd = new ProgressDialog(getActivity());
            pd.setMessage(getString(R.string.loading));
            pd.show();

            getCodeRequest = new JsonObjectRequest(Request.Method.POST, Constant.URL + "user/" + AppController.getInstance().getUserId() + "/activation?phone=" + "1" + phone, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    getCodeRequest = null;
                    Logger.e(TAG, response.toString());
                    pd.dismiss();
                    AppController.getInstance().getDefaultSharePreferences().edit().putString(Constant.SHARE_USER_PHONE, phone).apply();
                    Util.showMessageDialog(getString(R.string.verify_dialog_text, phone), getActivity());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    getCodeRequest = null;
                    pd.dismiss();
                    Util.handleVolleyError(error, getActivity());
                }
            });

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(getCodeRequest);
        }
    }

    private void sendActivateAccountRequest(EditText etCode, TextInputLayout tilCode,final Dialog dialog) {
        if (activateAccountRequest != null) {
            return;
        }

        String code = etCode.getText().toString().trim();
        if (code.isEmpty()) {
            tilCode.setError(getString(R.string.error_field_required));
        } else {
            final ProgressDialog pd = new ProgressDialog(getActivity());
            pd.setMessage(getString(R.string.loading));
            pd.show();

            activateAccountRequest = new JsonObjectRequest(Request.Method.POST, Constant.URL + "user/activation/" + code + "?email=" + AppController.getInstance().getUserEmail(), new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    activateAccountRequest = null;
                    Logger.e(TAG, response.toString());
                    pd.dismiss();
                    dialog.dismiss();
                    showVerifySuccessDialog();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Logger.e(TAG, error.toString());
                    activateAccountRequest = null;
                    pd.dismiss();
                    Util.handleVolleyError(error, getActivity());
                }
            });

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(activateAccountRequest, "activateAccount");
        }
    }

    private void showVerifySuccessDialog() {
        new AlertDialog.Builder(getActivity())
                .setMessage(getString(R.string.account_activated))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        loginActivity.finish();
                        dialog.dismiss();
                    }
                })
                .show();
    }
}
