package lmdelivery.longmen.com.android.fragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import lmdelivery.longmen.com.android.Constant;
import lmdelivery.longmen.com.android.R;
import lmdelivery.longmen.com.android.activity.MainActivity;
import lmdelivery.longmen.com.android.activity.NewBookingActivity;
import lmdelivery.longmen.com.android.util.Util;

/**
 * A simple {@link Fragment} subclass.
 */
public class InsuranceFragment extends Fragment {

    private LinearLayout llInsurance;
    private EditText insuranceValue,estimateValue;
    private TextView tvInsurace;

    public InsuranceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_insurance, container, false);

        estimateValue = (EditText) rootView.findViewById(R.id.et_est_value);
        insuranceValue = (EditText) rootView.findViewById(R.id.et_insurance_value);
        tvInsurace = (TextView) rootView.findViewById(R.id.tv_insurance);
        llInsurance = (LinearLayout) rootView.findViewById(R.id.ll_insurance);

        estimateValue.setText(((NewBookingActivity) getActivity()).declareValue);
        insuranceValue.setText(((NewBookingActivity) getActivity()).insuranceValue);


        estimateValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                saveAndValidate(insuranceValue.getText().toString(),estimateValue.getText().toString());
            }
        });

        insuranceValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                saveAndValidate(insuranceValue.getText().toString(), estimateValue.getText().toString());
            }
        });

        Switch insuranceSwitch = (Switch) rootView.findViewById(R.id.switch1);
        insuranceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    revealInsurance();
                } else {
                    hideInsurance();
                    insuranceValue.setText("");
                    tvInsurace.setText("$ 0");
                    ((NewBookingActivity) getActivity()).insuranceValue = "";
                }
            }
        });

        try {
            if (Integer.parseInt(((NewBookingActivity) getActivity()).insuranceValue)>0) {
                insuranceSwitch.setChecked(true);
            } else {
                insuranceSwitch.setChecked(false);
            }
        }catch (Exception e){
            insuranceSwitch.setChecked(false);
        }

        return rootView;
    }

    public boolean saveAndValidate(String insValue, String decValue){
        if(isAdded()){
            ((NewBookingActivity) getActivity()).insuranceValue = insValue.isEmpty()?"0":insValue;
            ((NewBookingActivity) getActivity()).declareValue = decValue.isEmpty()?"0":decValue;
        }

        if(!TextUtils.isEmpty(insValue)) {
            try {
                int insurance = Integer.parseInt(insValue);
                if(insurance < 0)  {
                    insuranceValue.setError(getString(R.string.cannot_be_negative));
                    return false;
                }
                if(insurance> Constant.MAX_INSURANCE_VALUE)  {
                    insuranceValue.setError(getString(R.string.max_1000));
                    return false;
                }else{
                    tvInsurace.setText("$ " + Util.roundTo2((double) insurance * 0.03));
                    insuranceValue.setError(null);
                    return true;
                }
            } catch (Exception e) {
                insuranceValue.setError(getString(R.string.invalid_number));
                return false;
            }
        }

        if(!TextUtils.isEmpty(decValue)) {
            try {
                int declare = Integer.parseInt(decValue);
                if(declare < 0)  {
                    estimateValue.setError(getString(R.string.cannot_be_negative));
                    return false;
                }else if(declare > Constant.MAX_DECLARE_VALUE)  {
                    estimateValue.setError(getString(R.string.declare_value_too_big));
                    return false;
                }else{
                    estimateValue.setError(null);
                    return true;
                }
            } catch (Exception e) {
                estimateValue.setError(getString(R.string.invalid_number));
                return false;
            }
        }

            return true;

    }

    private void revealInsurance() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // get the center for the clipping circle
            int cx = (llInsurance.getLeft() + llInsurance.getRight()) / 2;
            int cy = (llInsurance.getTop() + llInsurance.getBottom()) / 2;

            // get the final radius for the clipping circle
            int finalRadius = Math.max(llInsurance.getWidth(), llInsurance.getHeight());

            // create the animator for this view (the start radius is zero)
            Animator anim = ViewAnimationUtils.createCircularReveal(llInsurance, cx, cy, 0, finalRadius);
            // make the view visible and start the animation
            llInsurance.setVisibility(View.VISIBLE);
            anim.start();
        } else {
            llInsurance.setVisibility(View.VISIBLE);
            llInsurance.setAlpha(0);
            llInsurance.animate().alpha(1).setDuration(500).start();
        }
    }

    private void hideInsurance() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            // get the center for the clipping circle
            int cx = llInsurance.getRight();
            int cy = (llInsurance.getTop() + llInsurance.getBottom()) / 2;

            // get the initial radius for the clipping circle
            int initialRadius = llInsurance.getWidth();

            // create the animation (the final radius is zero)
            Animator anim = ViewAnimationUtils.createCircularReveal(llInsurance, cx, cy, initialRadius, 0);

            // make the view invisible when the animation is done
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    llInsurance.setVisibility(View.INVISIBLE);
                }
            });
            // start the animation
            anim.start();
        } else {
            llInsurance.animate().alpha(0).setDuration(500).withEndAction(new Runnable() {
                @Override
                public void run() {
                    llInsurance.setVisibility(View.GONE);
                }
            }).start();
        }
    }

}
