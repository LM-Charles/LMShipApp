package lmdelivery.longmen.com.android.UIFragments;


import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import lmdelivery.longmen.com.android.AppController;
import lmdelivery.longmen.com.android.Constant;
import lmdelivery.longmen.com.android.LoginActivity;
import lmdelivery.longmen.com.android.R;
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

    private OnRegisterListener mListener;

    public RegisterFragment() {
        // Required empty public constructor
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
            // JSONArray jsArray = new JSONArray();
            try {
                params.put("email", mEmailView.getText().toString());
                params.put("password", mPasswordView.getText().toString());
                params.put("phone", "7788594684");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            registerRequest = new JsonObjectRequest(Request.Method.PUT, Constant.URL + "user", params, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Logger.e(TAG, response.toString());
                    pd.dismiss();
                    registerRequest = null;
                    //TODO: save user info
                    showVerifyPhoneNumberDialog();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error != null)
                        Logger.e(TAG, error.toString());
                        pd.dismiss();
                    if (getActivity() != null)
                        Util.showMessageDialog(getString(R.string.err_connection), getActivity());
                    registerRequest = null;
                }
            });

            // Adding request to request queue
            AppController.getInstance().addToRequestQueue(registerRequest, "login");
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
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
        final EditText etPhone = (EditText) view.findViewById(R.id.et_phone);
        final EditText etCode = (EditText) view.findViewById(R.id.et_code);

        final TextInputLayout tilPhone = (TextInputLayout) view.findViewById(R.id.til_phone);
        final TextInputLayout tilCode = (TextInputLayout) view.findViewById(R.id.til_code);
        final Button btnSave = (Button) view.findViewById(R.id.btn_save);
        final Button btnVerify = (Button) view.findViewById(R.id.btn_verify);
        final Button btnContact = (Button) view.findViewById(R.id.btn_contact);

        final TextView tvNoCode = (TextView) view.findViewById(R.id.tv_no_code);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String phone = etPhone.getText().toString();
                if (phone.isEmpty()) {
                    tilPhone.setError(getString(R.string.error_field_required));
                } else if (isPhoneValid(phone)) {
                    tilPhone.setError(getString(R.string.error_phone_too_short));
                } else {
                    final ProgressDialog pd = new ProgressDialog(getActivity());
                    pd.setMessage("Loading");
                    pd.show();

                    JSONObject params = new JSONObject();
                    try {
                        params.put("phone", phone);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JsonObjectRequest getCodeRequest = new JsonObjectRequest(Request.Method.POST, Constant.URL + "user/" + "user_id" + "/activation", params, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Logger.e(TAG, response.toString());
                            pd.dismiss();
                            //TODO: save user phone number
                            //TODO: disable phone number editText
                            btnContact.setVisibility(View.VISIBLE);
                            btnVerify.setVisibility(View.VISIBLE);
                            btnSave.setVisibility(View.GONE);
                            tvNoCode.setVisibility(View.VISIBLE);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if (error != null)
                                Logger.e(TAG, error.toString());
                            pd.dismiss();
                            Util.showMessageDialog(getString(R.string.err_connection), getActivity());
                        }
                    });

                    // Adding request to request queue
                    AppController.getInstance().addToRequestQueue(getCodeRequest, "getActivationCode");
                }
            }
        });


        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String code = etCode.getText().toString();
                if (code.isEmpty()) {
                    tilCode.setError(getString(R.string.error_field_required));
                }  else {
                    final ProgressDialog pd = new ProgressDialog(getActivity());
                    pd.setMessage("Loading");
                    pd.show();

                    JSONObject params = new JSONObject();
                    try {
                        params.put("code", code);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JsonObjectRequest activateAccountRequest = new JsonObjectRequest(Request.Method.POST, Constant.URL + "user/" + "user_id" + "/activation", params, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Logger.e(TAG, response.toString());
                            pd.dismiss();
                            //TODO: show activation success or fail message

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if (error != null)
                                Logger.e(TAG, error.toString());
                            pd.dismiss();
                            Util.showMessageDialog(getString(R.string.err_connection), getActivity());
                        }
                    });

                    // Adding request to request queue
                    AppController.getInstance().addToRequestQueue(activateAccountRequest, "activateAccount");

                }
            }
        });

        // Inflate and set the layout for the dialog
        dialog.setContentView(view);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setTitle(getString(R.string.verify_phone));
        dialog.show();
    }

    public void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        if (getActivity() != null) {
            //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

            mEmailView.setAdapter(adapter);
        }
    }
}
