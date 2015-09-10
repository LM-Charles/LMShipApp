package lmdelivery.longmen.com.android.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import lmdelivery.longmen.com.android.Constant;
import lmdelivery.longmen.com.android.activity.NewBookingActivity;
import lmdelivery.longmen.com.android.R;
import lmdelivery.longmen.com.android.bean.Package;
import lmdelivery.longmen.com.android.util.Util;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SummaryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SummaryFragment extends Fragment {

    private TextView tvPickup;
    private TextView tvDropoff;
    private TextView tvTime;
    private TextView tvPackage;
    private TextView tvInsurance;

    private CardView cardPackage;
    private CardView cardFrom;
    private CardView cardTo;
    private CardView cardTime;
    private CardView cardInsurance;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SummaryFragment.
     */
    public static SummaryFragment newInstance() {
        SummaryFragment fragment = new SummaryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public SummaryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public boolean setupView(){
        final NewBookingActivity newBookingActivity = ((NewBookingActivity) getActivity());
        if(newBookingActivity==null)
            return false;

        boolean result = true;


        if (newBookingActivity.validatePickup()) {
            tvPickup.setText(newBookingActivity.pickupAddr.buildFullAddress());
            tvPickup.setTextColor(getResources().getColor(R.color.white_text));
        } else {
            tvPickup.setText(getString(R.string.invalid_pickup));
            tvPickup.setTextColor(getResources().getColor(R.color.red));
            result = false;
        }
        cardFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newBookingActivity.scrollTo(Constant.TAB_FROM);
            }
        });

        if (newBookingActivity.validateDropOff()) {
            tvDropoff.setText(newBookingActivity.dropOffAddr.buildFullAddress());
            tvDropoff.setTextColor(getResources().getColor(R.color.white_text));
        } else {
            tvDropoff.setText(getString(R.string.invalid_dropoff));
            tvDropoff.setTextColor(getResources().getColor(R.color.red));
            result = false;
        }
        cardTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newBookingActivity.scrollTo(Constant.TAB_TO);
            }
        });

        if(newBookingActivity.validateAllPackage()){
            tvPackage.setText(buildPackageString());
            tvPackage.setTextColor(getResources().getColor(R.color.white_text));
        }else{
            tvPackage.setText(getString(R.string.invalid_package));
            tvPackage.setTextColor(getResources().getColor(R.color.red));
            result = false;
        }
        cardPackage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newBookingActivity.scrollTo(Constant.TAB_PACKAGE);
            }
        });

        if (newBookingActivity.selectedTime != null) {
            String time = newBookingActivity.selectedTime.isToday() ? getString(R.string.today) : getString(R.string.tomorrow);
            time += " " + newBookingActivity.selectedTime.getTimeString();
            tvTime.setText(time);
            tvTime.setTextColor(getResources().getColor(R.color.white_text));
        } else {
            tvTime.setText(getString(R.string.no_pickup_time));
            tvTime.setTextColor(getResources().getColor(R.color.red));
            result = false;
        }
        cardTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newBookingActivity.scrollTo(Constant.TAB_TIME);
            }
        });

        if (newBookingActivity.validateInsurance()) {
            String text = getString(R.string.estimate_value) + newBookingActivity.declareValue + "\n";

            if(!TextUtils.isEmpty(newBookingActivity.insuranceValue)){
                text += getString(R.string.insurance_value) + newBookingActivity.insuranceValue + "\n";
                int value = Integer.parseInt(newBookingActivity.insuranceValue);

                text += getString(R.string.insurance_cost) + Util.roundTo2((double) value * 0.03);
            }else{
                text += getString(R.string.insurance_decline);
            }
            tvInsurance.setText(text);
            tvInsurance.setTextColor(getResources().getColor(R.color.white_text));
        } else {
            tvInsurance.setText(getString(R.string.no_est_value));
            tvInsurance.setTextColor(getResources().getColor(R.color.red));
            result = false;
        }
        cardInsurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newBookingActivity.scrollTo(Constant.TAB_INSURANCE);
            }
        });
        return  result;
    }

    private String buildPackageString(){
        ArrayList<Package> packageArrayList = ((NewBookingActivity) getActivity()).packageArrayList;
        String result = "";
        int size = packageArrayList.size();
        for(int i = 0; i < size; i++){
            if(!result.isEmpty())
                result += "\n\n";
            result += getString(R.string.box) + (i+1) + ":\n" + packageArrayList.get(i).toString();
        }
        return result;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_summary, container, false);
        tvPickup = (TextView) view.findViewById(R.id.tv_pickup_addr);
        tvDropoff = (TextView) view.findViewById(R.id.tv_dropoff_addr);
        tvTime = (TextView) view.findViewById(R.id.tv_pickup_time);
        tvPackage = (TextView) view.findViewById(R.id.tv_package_info);
        tvInsurance = (TextView) view.findViewById(R.id.tv_insurance);
        cardPackage = (CardView) view.findViewById(R.id.card_package);
        cardFrom = (CardView) view.findViewById(R.id.card_from);
        cardTo = (CardView) view.findViewById(R.id.card_to);
        cardTime = (CardView) view.findViewById(R.id.card_time);
        cardInsurance = (CardView) view.findViewById(R.id.card_insurance);
        return view;
    }


}
