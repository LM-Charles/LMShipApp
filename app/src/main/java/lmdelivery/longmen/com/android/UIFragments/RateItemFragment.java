package lmdelivery.longmen.com.android.UIFragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import lmdelivery.longmen.com.android.R;
import lmdelivery.longmen.com.android.UIFragments.bean.RateItem;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RateItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RateItemFragment extends Fragment {

    RateItemFragment fragment;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment RateItemFragment.
     */
    public static RateItemFragment newInstance() {
        RateItemFragment fragment = new RateItemFragment();
        return fragment;
    }

    public RateItemFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragment = this;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rate_item, container, false);
        RecyclerView rv = (RecyclerView) view.findViewById(R.id.recyclerview);
        setupRecyclerView(rv);

        return view;
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        ArrayList<RateItem> rateItems = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            RateItem item = new RateItem("1","http://www.hdicon.com/wp-content/uploads/2010/08/ups_2003.png", "Category 1", "$ 55", "Average 1 - 2 Business day", "ups", "One day express");
            rateItems.add(item);
        }
        recyclerView.setAdapter(new RateItemRecyclerViewAdapter(rateItems));
    }

    private class RateItemRecyclerViewAdapter extends RecyclerView.Adapter<RateItemRecyclerViewAdapter.ViewHolder> {

        private final TypedValue mTypedValue = new TypedValue();
        private int mBackground;
        private List<RateItem> mValues;
        private int selectedPosition;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public String mBoundString;

            public final View mView;
            public final TextView date;
            public final TextView type;
            public final TextView price;
            public final ImageView icon;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                type = (TextView) view.findViewById(R.id.tv_carrier_type);
                date = (TextView) view.findViewById(R.id.tv_est_date);
                price = (TextView) view.findViewById(R.id.price);
                icon = (ImageView) view.findViewById(R.id.logo);
            }

//            @Override
//            public String toString() {
//                return super.toString() + " '" + time.getText();
//            }
        }

        public RateItem getValueAt(int position) {
            return mValues.get(position);
        }

        public RateItemRecyclerViewAdapter(List<RateItem> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rate_item_layout, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.price.setText(mValues.get(position).getEstimatePrice());
            holder.date.setText(mValues.get(position).getEstimatedDeliveryDate());
            holder.type.setText(mValues.get(position).getServiceName());
            Glide.with(fragment)
                    .load(mValues.get(position).getServiceIcon())
                    .centerCrop()
                    //.placeholder(R.drawable.loading_spinner)
                    .crossFade()
                    .into(holder.icon);


            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }
    }

}
