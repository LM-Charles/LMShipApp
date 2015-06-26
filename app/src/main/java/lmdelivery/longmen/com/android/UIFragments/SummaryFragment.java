package lmdelivery.longmen.com.android.UIFragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import lmdelivery.longmen.com.android.NewBookingActivity;
import lmdelivery.longmen.com.android.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SummaryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SummaryFragment extends Fragment {

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_summary, container, false);
        TextView tvPickup = (TextView) view.findViewById(R.id.tv_pickup_addr);
        TextView tvDropoff = (TextView) view.findViewById(R.id.tv_dropoff_addr);
        TextView tvTime = (TextView) view.findViewById(R.id.tv_pickup_time);
        TextView tvPackage = (TextView) view.findViewById(R.id.tv_package_info);
        NewBookingActivity newBookingActivity = ((NewBookingActivity) getActivity());

        if(newBookingActivity.pickupFragment.saveAndValidate()){
            tvPickup.setText(newBookingActivity.pickupAddr.buildFullAddress());
        }else{
            tvPickup.setText("Pick up address invalid");
        }

        if(newBookingActivity.dropOffFragment.saveAndValidate()){
            tvDropoff.setText(newBookingActivity.dropOffAddr.buildFullAddress());
        }else{
            tvDropoff.setText("Drop off address invalid");
        }

        if(newBookingActivity.selectedTime!=null){
            tvTime.setText(newBookingActivity.selectedTime.isToday() ? "Today" : "Tomorrow" + " " + getResources().getStringArray(R.array.time_interval_array)[newBookingActivity.selectedTime.getTimeCatergory()]);
        }else{
            tvTime.setText("Pickup time not selected");
        }

        return view;
    }


}
