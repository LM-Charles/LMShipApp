package lmdelivery.longmen.com.android.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import lmdelivery.longmen.com.android.activity.NewBookingActivity;
import lmdelivery.longmen.com.android.R;
import lmdelivery.longmen.com.android.bean.MyTime;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnTimeSelectedListener}
 * interface.
 */

public class TimeFragment extends Fragment {

    private static final java.lang.String TAG = TimeFragment.class.getName();
    private OnTimeSelectedListener mListener;


    public static TimeFragment newInstance() {
        TimeFragment fragment = new TimeFragment();
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TimeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mAdapter = new TimeAdapter(getActivity(),buildAvailableTimeArray());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_time_list, container, false);
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.recyclerview);
        setupRecyclerView(rv);

        return view;
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(new TimeRecyclerViewAdapter(buildAvailableTimeArray()));
    }

    private ArrayList<MyTime> buildAvailableTimeArray() {
        ArrayList<MyTime> myTimes = new ArrayList<>();
        Calendar now = Calendar.getInstance();

        Calendar startOfFirstInterval = Calendar.getInstance();
        startOfFirstInterval.set(Calendar.HOUR_OF_DAY, 9);
        startOfFirstInterval.set(Calendar.MINUTE, 0);
        startOfFirstInterval.set(Calendar.SECOND, 0);
        startOfFirstInterval.set(Calendar.MILLISECOND, 0);

        Calendar startOfSecondInterval = Calendar.getInstance();
        startOfSecondInterval.set(Calendar.HOUR_OF_DAY, 11);
        startOfSecondInterval.set(Calendar.MINUTE, 0);
        startOfSecondInterval.set(Calendar.SECOND, 0);
        startOfSecondInterval.set(Calendar.MILLISECOND, 0);

        Calendar startOfThirdInterval = Calendar.getInstance();
        startOfThirdInterval.set(Calendar.HOUR_OF_DAY, 13);
        startOfThirdInterval.set(Calendar.MINUTE, 0);
        startOfThirdInterval.set(Calendar.SECOND, 0);
        startOfThirdInterval.set(Calendar.MILLISECOND, 0);

        Calendar startOfForthInterval = Calendar.getInstance();
        startOfForthInterval.set(Calendar.HOUR_OF_DAY, 13);
        startOfForthInterval.set(Calendar.MINUTE, 0);
        startOfForthInterval.set(Calendar.SECOND, 0);
        startOfForthInterval.set(Calendar.MILLISECOND, 0);

        String[] timeSlots = getResources().getStringArray(R.array.time_interval_array);
        int i;


        if (now.compareTo(startOfFirstInterval) == -1) {
            i = 0;
        } else if (now.compareTo(startOfSecondInterval) == -1) {
            i = 1;
        } else if (now.compareTo(startOfThirdInterval) == -1) {
            i = 2;
        } else if (now.compareTo(startOfForthInterval) == -1) {
            i = 3;
        } else {
            i = 4;
        }

        while (i < 4) {
            MyTime myTime = new MyTime(timeSlots[i], i, true);
            myTimes.add(myTime);
            i++;
        }

        //add tomorrow time slots
        for (int j = 0; j < 4; j++) {
            MyTime myTime = new MyTime(timeSlots[j], j, false);
            myTimes.add(myTime);
        }

        //fake
//        for (int j = 0; j < 4; j++) {
//            MyTime myTime = new MyTime(timeSlots[j], j, false);
//            myTimes.add(myTime);
//        }for (int j = 0; j < 4; j++) {
//            MyTime myTime = new MyTime(timeSlots[j], j, false);
//            myTimes.add(myTime);
//        }for (int j = 0; j < 4; j++) {
//            MyTime myTime = new MyTime(timeSlots[j], j, false);
//            myTimes.add(myTime);
//        }

        return myTimes;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnTimeSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnTimeSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
    public interface OnTimeSelectedListener {

        void onTimeSelected(MyTime myTime);
    }

    private class TimeRecyclerViewAdapter extends RecyclerView.Adapter<TimeRecyclerViewAdapter.ViewHolder> {

        private final TypedValue mTypedValue = new TypedValue();
        private int mBackground;
        private List<MyTime> mValues;
        private int selectedPosition;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public String mBoundString;

            public final View mView;
            public final TextView date;
            public final TextView time;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                date = (TextView) view.findViewById(R.id.tv_date);
                time = (TextView) view.findViewById(R.id.tv_time_interval);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + time.getText();
            }
        }

        public MyTime getValueAt(int position) {
            return mValues.get(position);
        }

        public TimeRecyclerViewAdapter(List<MyTime> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.time_item_layout, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.time.setText(getResources().getStringArray(R.array.time_interval_array)[mValues.get(position).getTimeCatergory()]);
            holder.date.setText(mValues.get(position).isToday() ? getActivity().getString(R.string.today) : getActivity().getString(R.string.tomorrow));


            if (mValues.get(position).equals(((NewBookingActivity) getActivity()).selectedTime))
                holder.mView.setSelected(true);
            else
                holder.mView.setSelected(false);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        mListener.onTimeSelected(mValues.get(position));
                        holder.mView.setSelected(true);
                        notifyItemRangeChanged(0,mValues.size());
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }
    }


}
