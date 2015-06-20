package lmdelivery.longmen.com.android.UIFragments;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import lmdelivery.longmen.com.android.R;
import lmdelivery.longmen.com.android.UIFragments.bean.MyPackage;
import lmdelivery.longmen.com.android.widget.TypefaceTextView;


public class PackageFragment extends Fragment {

    private PackageRecyclerViewAdapter mAdapter;
    private RecyclerView recyclerView;

    public static PackageFragment newInstance() {
        PackageFragment fragment = new PackageFragment();
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PackageFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_package_list, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        setupRecyclerView();

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAdapter.addPackage();
            }
        });

        return view;
    }

    private void setupRecyclerView() {
        ArrayList<MyPackage> myPackageArrayList = new ArrayList<>();
        myPackageArrayList.add(new MyPackage());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        mAdapter = new PackageRecyclerViewAdapter(myPackageArrayList);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setItemAnimator(new SlideInLeftAnimator());
    }

    private class PackageRecyclerViewAdapter extends RecyclerView.Adapter<PackageRecyclerViewAdapter.ViewHolder> {
        private List<MyPackage> mValues;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView date;
            public final TypefaceTextView closeIcon;
            public final LinearLayout llLmBox;
            public final LinearLayout llOwnBox;
            public final Switch aSwitch;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                date = (TextView) view.findViewById(R.id.tv_date);
                closeIcon = (TypefaceTextView) view.findViewById(R.id.ic_close);
                llLmBox = (LinearLayout) view.findViewById(R.id.ll_lm_box);
                llOwnBox = (LinearLayout) view.findViewById(R.id.ll_own_box);
                aSwitch = (Switch) view.findViewById(R.id.switch1);
            }

//            @Override
//            public String toString() {
//                return super.toString() + " '" + time.getText();
//            }
        }

        public MyPackage getValueAt(int position) {
            return mValues.get(position);
        }

        public void addPackage() {
            mValues.add(new MyPackage());
            notifyItemInserted(mValues.size());
            recyclerView.scrollToPosition(mValues.size() - 1);
        }

        private void removePackage(int position){
            if(mValues.size()==1) {
                //TODO: add a toast
            }else{
                mValues.remove(position);
                notifyItemRemoved(position);
            }
        }

        public PackageRecyclerViewAdapter(List<MyPackage> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.package_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder,final int position) {
            holder.closeIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removePackage(position);
                }
            });

            holder.aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        holder.llOwnBox.setVisibility(View.VISIBLE);
                        holder.llLmBox.setVisibility(View.GONE);
                    }else{
                        holder.llOwnBox.setVisibility(View.GONE);
                        holder.llLmBox.setVisibility(View.VISIBLE);
                    }

                }
            });
//            holder.time.setText(getResources().getStringArray(R.array.time_interval_array)[mValues.get(position).getTimeCatergory()]);
//            holder.date.setText(mValues.get(position).isToday() ? "Today" : "Tomorrow");
//
//            if(mValues.get(position).equals(((NewBookingActivity)getActivity()).selectedTime))
//                holder.mView.setSelected(true);
//            else
//                holder.mView.setSelected(false);
//
//            holder.mView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (null != mListener) {
//                        mListener.onTimeSelected(mValues.get(position));
//                        holder.mView.setSelected(true);
//                        notifyDataSetChanged();
//                    }
//                }
//            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }
    }

}
