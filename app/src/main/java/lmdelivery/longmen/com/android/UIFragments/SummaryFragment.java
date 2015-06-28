package lmdelivery.longmen.com.android.UIFragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

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
        NewBookingActivity newBookingActivity = ((NewBookingActivity) getActivity());
        if (newBookingActivity.pickupFragment.saveAndValidate()) {
            tvPickup.setText(newBookingActivity.pickupAddr.buildFullAddress());
        } else {
            tvPickup.setText("Pick up address invalid");
            result = false;
        }

        if (newBookingActivity.dropOffFragment.saveAndValidate()) {
            tvDropoff.setText(newBookingActivity.dropOffAddr.buildFullAddress());
        } else {
            tvDropoff.setText("Drop off address invalid");
            result = false;
        }

        if(newBookingActivity.packageFragment.validateAllPackage()){
            tvPackage.setText(buildPackageString());
        }else{
            tvPackage.setText("Package info is invalid");
            result = false;
        }

        if (newBookingActivity.selectedTime != null) {
            tvTime.setText(newBookingActivity.selectedTime.isToday() ? "Today" : "Tomorrow" + " " + getResources().getStringArray(R.array.time_interval_array)[newBookingActivity.selectedTime.getTimeCatergory()]);
        } else {
            tvTime.setText("Pickup time not selected");
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


        return view;
    }


}
