package lmdelivery.longmen.com.android.UIFragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import lmdelivery.longmen.com.android.Constant;
import lmdelivery.longmen.com.android.NewBookingActivity;
import lmdelivery.longmen.com.android.R;
import lmdelivery.longmen.com.android.UIFragments.bean.MyPackage;

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

    private CardView cardPackage;
    private CardView cardFrom;
    private CardView cardTo;
    private CardView cardTime;
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
        boolean result = true;

        final NewBookingActivity newBookingActivity = ((NewBookingActivity) getActivity());
        if (newBookingActivity.validatePickup()) {
            tvPickup.setText(newBookingActivity.pickupAddr.buildFullAddress());
            cardFrom.setOnClickListener(null);
        } else {
            tvPickup.setText("Pick up address invalid");
            cardFrom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    newBookingActivity.scrollTo(Constant.TAB_FROM);
                }
            });
            result = false;
        }

        if (newBookingActivity.validateDropOff()) {
            tvDropoff.setText(newBookingActivity.dropOffAddr.buildFullAddress());
            cardTo.setOnClickListener(null);
        } else {
            tvDropoff.setText("Drop off address invalid");
            cardTo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    newBookingActivity.scrollTo(Constant.TAB_TO);
                }
            });
            result = false;
        }

        if(newBookingActivity.validateAllPackage()){
            tvPackage.setText(buildPackageString());
            cardPackage.setOnClickListener(null);
        }else{
            tvPackage.setText("Package info is invalid");
            cardPackage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    newBookingActivity.scrollTo(Constant.TAB_PACKAGE);
                }
            });
            result = false;
        }

        if (newBookingActivity.selectedTime != null) {
            tvTime.setText(newBookingActivity.selectedTime.isToday() ? "Today" : "Tomorrow" + " " + getResources().getStringArray(R.array.time_interval_array)[newBookingActivity.selectedTime.getTimeCatergory()]);
            cardTime.setOnClickListener(null);
        } else {
            tvTime.setText("Pickup time not selected");
            cardTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    newBookingActivity.scrollTo(Constant.TAB_TIME);
                }
            });
            result = false;
        }
        return  result;
    }

    private String buildPackageString(){
        ArrayList<MyPackage> myPackageArrayList = ((NewBookingActivity) getActivity()).myPackageArrayList;
        String result = "";
        for(int i = 0; i < myPackageArrayList.size(); i++){
            if(!result.isEmpty())
                result += "\n\n";
            result += "Box " + (i+1) + ":\n" + myPackageArrayList.get(i).toString();
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
        cardPackage = (CardView) view.findViewById(R.id.card_package);
        cardFrom = (CardView) view.findViewById(R.id.card_from);
        cardTo = (CardView) view.findViewById(R.id.card_to);
        cardTime = (CardView) view.findViewById(R.id.card_time);
        return view;
    }


}
