package lmdelivery.longmen.com.android.UIFragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import lmdelivery.longmen.com.android.R;
import lmdelivery.longmen.com.android.UIFragments.bean.MyTime;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnTimeSelectedListener}
 * interface.
 */

public class TimeFragment extends Fragment implements AbsListView.OnItemClickListener {

    private OnTimeSelectedListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private ListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListAdapter mAdapter;


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
        mAdapter = new TimeAdapter(getActivity(),buildAvailableTimeArray());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_time_list, container, false);

        // Set the adapter
        mListView = (ListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
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
            i=0;
        } else if (now.compareTo(startOfSecondInterval) == -1) {
            i=1;
        } else if (now.compareTo(startOfThirdInterval) == -1) {
            i=2;
        } else if (now.compareTo(startOfForthInterval) == -1) {
            i=3;
        } else {
            i=4;
        }

        while(i < 4) {
            MyTime myTime = new MyTime(timeSlots[i], i, true);
            myTimes.add(myTime);
            i++;
        }

        //add tomorrow time slots
        for (int j = 0; j < 4; j++) {
            MyTime myTime = new MyTime(timeSlots[j], j, false);
            myTimes.add(myTime);
        }

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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            view.setSelected(true);
            mListener.onTimeSelected(((MyTime) mAdapter.getItem(position)).getTimeCatergory());
        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
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
    public interface OnTimeSelectedListener {

        void onTimeSelected(int category);
    }

    private class TimeAdapter extends ArrayAdapter<MyTime> {

        private final List<MyTime> list;
        private final Activity context;

        public TimeAdapter(Activity context, List<MyTime> list) {
            super(context, R.layout.time_item_layout, list);
            this.context = context;
            this.list = list;
        }

        private class ViewHolder {
            protected TextView time;
            protected TextView date;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView == null) {
                LayoutInflater inflator = context.getLayoutInflater();
                view = inflator.inflate(R.layout.time_item_layout, null);
                final ViewHolder viewHolder = new ViewHolder();
                viewHolder.date = (TextView) view.findViewById(R.id.tv_date);
                viewHolder.time = (TextView) view.findViewById(R.id.tv_time_interval);
                view.setTag(viewHolder);
            } else {
                view = convertView;
            }
            ViewHolder holder = (ViewHolder) view.getTag();

            holder.date.setText(list.get(position).isToday() ? "Today" : "Tomorrow");
            holder.time.setText(getResources().getStringArray(R.array.time_interval_array)[list.get(position).getTimeCatergory()]);
            return view;
        }
    }


}
