package lmdelivery.longmen.com.android.fragments;


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

import lmdelivery.longmen.com.android.Constant;
import lmdelivery.longmen.com.android.R;
import lmdelivery.longmen.com.android.activity.SelectProductActivity;
import lmdelivery.longmen.com.android.data.RateItem;
import lmdelivery.longmen.com.android.util.DateUtil;
import lmdelivery.longmen.com.android.util.Logger;
import lmdelivery.longmen.com.android.util.Util;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RateItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RateItemFragment extends Fragment {

    RateItemFragment fragment;
    ArrayList<RateItem> rateItemArrayList;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment RateItemFragment.
     */
    public static RateItemFragment newInstance(ArrayList<RateItem> rateItemArrayList) {
        RateItemFragment fragment = new RateItemFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList("rateList", rateItemArrayList);
        fragment.setArguments(args);
        return fragment;
    }

    public RateItemFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.rateItemArrayList = getArguments().getParcelableArrayList("rateList");
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
//        ArrayList<RateItem> rateItems = new ArrayList<>();
//        for(int i = 0; i < 10; i++){
//            RateItem item = new RateItem("http://www.hdicon.com/wp-content/uploads/2010/08/ups_2003.png", "Category 1", "$ 55", "Average 1 - 2 Business day", "ups", "One day express");
//            rateItems.add(item);
//        }
        recyclerView.setAdapter(new RateItemRecyclerViewAdapter(rateItemArrayList));
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
                price = (TextView) view.findViewById(R.id.tv_price);
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

            final RateItem item = mValues.get(position);
            try {
                holder.price.setText("$" + Util.roundTo2(item.getEstimate()));
            }catch (Exception e){
                Logger.e("Failed to parse price");
                holder.price.setText("$-");
            }
            holder.date.setText(getString(R.string.estimate_delivery) + DateUtil.UnixTimeToDateString(item.getEstimatedDelivery()));
            holder.type.setText(Util.toDisplayCase(item.getServiceName()));
            Glide.with(fragment)
                    .load(Constant.ENDPOINT + item.getService_icon_url())
                    .crossFade()
                    .error(R.drawable.logo)
                    .into(holder.icon);


            holder.mView.setOnClickListener(v -> ((SelectProductActivity)getActivity()).showPaymentDialog(item));
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }
    }

}
