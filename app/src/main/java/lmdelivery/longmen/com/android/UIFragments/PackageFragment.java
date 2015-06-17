package lmdelivery.longmen.com.android.UIFragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import lmdelivery.longmen.com.android.R;
import lmdelivery.longmen.com.android.UIFragments.bean.MyPackage;


public class PackageFragment extends ListFragment {

    /**
     * The fragment's ListView/GridView.
     */
    private ListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListAdapter mAdapter;


    public static PackageFragment newInstance() {
        PackageFragment fragment = new PackageFragment();
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PackageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayList<MyPackage> myPackageArrayList = new ArrayList<>();
        myPackageArrayList.add(new MyPackage());
        myPackageArrayList.add(new MyPackage());
        mAdapter = new PackageAdapter(getActivity(), myPackageArrayList);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_package_list, container, false);

        // Set the adapter
        mListView = (ListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(mAdapter);

        return view;
    }

    private class PackageAdapter extends ArrayAdapter<MyPackage> {

        private final List<MyPackage> list;
        private final Activity context;

        public PackageAdapter(Activity context, List<MyPackage> list) {
            super(context, R.layout.package_item, list);
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
                view = inflator.inflate(R.layout.package_item, null);
                final ViewHolder viewHolder = new ViewHolder();
//                viewHolder.date = (TextView) view.findViewById(R.id.tv_date);
//                viewHolder.time = (TextView) view.findViewById(R.id.tv_time_interval);
                view.setTag(viewHolder);
            } else {
                view = convertView;
            }
            ViewHolder holder = (ViewHolder) view.getTag();

//            holder.date.setText(list.get(position).isToday() ? "Today" : "Tomorrow");
//            holder.time.setText(getResources().getStringArray(R.array.time_interval_array)[list.get(position).getTimeCatergory()]);
            return view;
        }
    }
}
