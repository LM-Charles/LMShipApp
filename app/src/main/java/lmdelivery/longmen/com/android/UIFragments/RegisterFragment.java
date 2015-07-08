package lmdelivery.longmen.com.android.UIFragments;


import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import lmdelivery.longmen.com.android.LoginActivity;
import lmdelivery.longmen.com.android.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {

    // UI references.
    private AutoCompleteTextView mEmailView;
    private LoginActivity loginActivity;
    private EditText mPasswordView;
    private View mProgressView;
    private LinearLayout mLoginFormView;
    private AppBarLayout appBarLayout;
    private CollapsingToolbarLayout collapsingToolbar;
    private CoordinatorLayout rootLayout;

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

        mPasswordView = (EditText) root.findViewById(R.id.password);

        TextView link = (TextView) root.findViewById(R.id.link);
        link.setText(Html.fromHtml("By Creating an account, you are agreeing to our" + "<br>"+
                "<a href=\"http://zoroapp.com/EULA\">Terms and Conditions</a> "));
        link.setMovementMethod(LinkMovementMethod.getInstance());
        return root ;
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
            throw new ClassCastException(activity.toString()+ " must implement OnRegisterListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public boolean hasFocus(){
        return (mEmailView!=null && mPasswordView !=null) && (mEmailView.isFocused() || mPasswordView.isFocused());
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

    public void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_dropdown_item_1line, emailAddressCollection);
//        mEmailView.setAdapter(adapter);
    }

}
