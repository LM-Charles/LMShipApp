package lmdelivery.longmen.com.android.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator;
import lmdelivery.longmen.com.android.activity.NewBookingActivity;
import lmdelivery.longmen.com.android.R;
import lmdelivery.longmen.com.android.bean.Package;
import lmdelivery.longmen.com.android.widget.TypefaceTextView;


public class PackageFragment extends Fragment {

    private static final java.lang.String TAG = PackageFragment.class.toString();
    private PackageRecyclerViewAdapter mAdapter;
    private RecyclerView recyclerView;
    private ArrayList<Package> packageArrayList;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
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
        recyclerView.setHasFixedSize(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        packageArrayList = ((NewBookingActivity)getActivity()).packageArrayList;
        mAdapter = new PackageRecyclerViewAdapter(getActivity(), packageArrayList, recyclerView);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setItemAnimator(new SlideInLeftAnimator());
    }

    public boolean validateAllPackage(){
        if(getActivity()== null || packageArrayList ==null){
            return false;
        }

        ArrayList<Package> aPackages = packageArrayList;
        boolean result = true;
        for(int i = 0; i < aPackages.size(); i++){
            Package aPackage = aPackages.get(i);
            if(aPackage.isOwnBox() && (aPackage.getHeight().isEmpty() || aPackage.getLength().isEmpty() || aPackage.getWeight().isEmpty() || aPackage.getWidth().isEmpty())) {
                aPackage.setShowValidation(true);
                mAdapter.notifyDataSetChanged();
                result = false;
            }else if(!aPackage.isOwnBox() && aPackage.getWeight().isEmpty()){
                aPackage.setShowValidation(true);
                mAdapter.notifyDataSetChanged();
                result = false;
            }
        }
        return result;
    }

    static private class PackageRecyclerViewAdapter extends RecyclerView.Adapter<PackageRecyclerViewAdapter.ViewHolder> {

        private Context context;
        private List<Package> packageArrayList;
        private RecyclerView recyclerView;
        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView date;
            public final TextView price;
            public final TypefaceTextView closeIcon;
            public final LinearLayout llLmBox;
            public final LinearLayout llOwnBox;
            public final RelativeLayout rlSmallBox, rlMedBox, rlBigBox;
            public final CheckBox checkBox;
            public final RadioButton rbSmall, rbMed, rbBig;
            public final EditText etLength, etWidth, etHeight, etWeight;
            public final Spinner spinner;


            public ViewHolder(View view) {
                super(view);
                mView = view;
                date = (TextView) view.findViewById(R.id.tv_date);
                price = (TextView) view.findViewById(R.id.tv_price);
                closeIcon = (TypefaceTextView) view.findViewById(R.id.ic_close);
                llLmBox = (LinearLayout) view.findViewById(R.id.ll_lm_box);
                llOwnBox = (LinearLayout) view.findViewById(R.id.ll_own_box);
                rlSmallBox = (RelativeLayout) view.findViewById(R.id.rl_small_box);
                rlMedBox = (RelativeLayout) view.findViewById(R.id.rl_med_box);
                rlBigBox = (RelativeLayout) view.findViewById(R.id.rl_big_box);
                
                checkBox = (CheckBox) view.findViewById(R.id.checkbox);

                rbSmall = (RadioButton) view.findViewById(R.id.rb_small_box);
                rbMed = (RadioButton) view.findViewById(R.id.rb_med_box);
                rbBig = (RadioButton) view.findViewById(R.id.rb_big_box);

                etLength = (EditText) view.findViewById(R.id.et_length);
                etWidth = (EditText) view.findViewById(R.id.et_width);
                etHeight = (EditText) view.findViewById(R.id.et_height);
                etWeight = (EditText) view.findViewById(R.id.et_weight);

                spinner = (Spinner) view.findViewById(R.id.spinner_package_type);
            }

//            @Override
//            public String toString() {
//                return super.toString() + " '" + time.getText();
//            }
        }

        public Package getValueAt(int position) {
            return packageArrayList.get(position);
        }

        public void addPackage() {
                packageArrayList.add(new Package());
                notifyItemInserted(packageArrayList.size());
                //this is to notify first item to show the close icon
                notifyItemChanged(0);
                recyclerView.scrollToPosition(packageArrayList.size() - 1);
        }

        private void removePackage(int position){
            if(((NewBookingActivity) context).packageArrayList.size()==1) {
                //TODO: add a toast
            }else{
                packageArrayList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, packageArrayList.size());
                //this is to notify first item to hide the close icon
                notifyItemChanged(0);
            }
        }

        private void showEditTextValidation(Package aPackage, PackageRecyclerViewAdapter.ViewHolder viewHolder){
            String weight = viewHolder.etWeight.getText().toString();
            if(aPackage.isOwnBox()){
                String height = viewHolder.etHeight.getText().toString();
                String length = viewHolder.etLength.getText().toString();
                String width = viewHolder.etWidth.getText().toString();
                if(height.isEmpty())
                    ((TextInputLayout)viewHolder.etHeight.getParent()).setError(context.getString(R.string.required));
                else
                    ((TextInputLayout)viewHolder.etHeight.getParent()).setErrorEnabled(false);

                if(length.isEmpty())
                    ((TextInputLayout)viewHolder.etLength.getParent()).setError(context.getString(R.string.required));
                else
                    ((TextInputLayout)viewHolder.etLength.getParent()).setErrorEnabled(false);

                if(width.isEmpty())
                    ((TextInputLayout)viewHolder.etWidth.getParent()).setError(context.getString(R.string.required));
                else
                    ((TextInputLayout)viewHolder.etWidth.getParent()).setErrorEnabled(false);

            }

            if(weight.isEmpty())
                ((TextInputLayout)viewHolder.etWeight.getParent()).setError(context.getString(R.string.required));
            else
                ((TextInputLayout)viewHolder.etWeight.getParent()).setErrorEnabled(false);
        }

        public PackageRecyclerViewAdapter(Context context, ArrayList<Package> aPackages, RecyclerView recyclerView) {
            this.context = context;
            this.packageArrayList = aPackages;
            this.recyclerView = recyclerView;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.package_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder,final int position) {
            final Package aPackage = packageArrayList.get(position);

            //update view
            if(aPackage.isOwnBox()){
                holder.checkBox.setChecked(true);
            }else{
                holder.checkBox.setChecked(false);
            }

            holder.etHeight.setText(aPackage.getHeight());
            holder.etLength.setText(aPackage.getLength());
            holder.etWeight.setText(aPackage.getWeight());
            holder.etWidth.setText(aPackage.getWidth());
            holder.rbSmall.setChecked(false);
            holder.rbMed.setChecked(false);
            holder.rbBig.setChecked(false);


            switch (aPackage.getBoxSize()){
                case Package.BIG_BOX:
                    holder.rbBig.setChecked(true);
                    break;
                case Package.MED_BOX:
                    holder.rbMed.setChecked(true);
                    break;
                case Package.SMALL_BOX:
                    holder.rbSmall.setChecked(true);
                    break;
            }

            if (aPackage.isOwnBox()) {
                holder.price.setText(context.getString(R.string.free));
                holder.llOwnBox.setVisibility(View.VISIBLE);
                holder.llLmBox.setVisibility(View.GONE);
            } else {
                holder.price.setText(context.getString(R.string.five_bucks));
                holder.llOwnBox.setVisibility(View.GONE);
                holder.llLmBox.setVisibility(View.VISIBLE);
            }

            if(aPackage.isShowValidation()){
                showEditTextValidation(aPackage,holder);
            }

            if(position==0 && packageArrayList.size()==1){
                holder.closeIcon.setVisibility(View.GONE);
            }else {
                holder.closeIcon.setVisibility(View.VISIBLE);
            }

            holder.closeIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removePackage(position);
                }
            });

            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        aPackage.setIsOwnBox(true);
                        holder.price.setText(context.getString(R.string.free));
                        holder.llOwnBox.setVisibility(View.VISIBLE);
                        holder.llLmBox.setVisibility(View.GONE);
                    } else {
                        holder.price.setText(context.getString(R.string.five_bucks));
                        aPackage.setIsOwnBox(false);
                        holder.llOwnBox.setVisibility(View.GONE);
                        holder.llLmBox.setVisibility(View.VISIBLE);
                    }
                }
            });

            holder.rbBig.setClickable(false);
            holder.rbMed.setClickable(false);
            holder.rbSmall.setClickable(false);

            holder.rlBigBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    aPackage.setBoxSize(Package.BIG_BOX);
                    holder.rbBig.setChecked(true);
                    holder.rbMed.setChecked(false);
                    holder.rbSmall.setChecked(false);
                }
            });

            holder.rlMedBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    aPackage.setBoxSize(Package.MED_BOX);
                    holder.rbBig.setChecked(false);
                    holder.rbMed.setChecked(true);
                    holder.rbSmall.setChecked(false);
                }
            });

            holder.rlSmallBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    aPackage.setBoxSize(Package.SMALL_BOX);
                    holder.rbBig.setChecked(false);
                    holder.rbMed.setChecked(false);
                    holder.rbSmall.setChecked(true);
                }
            });

            holder.etHeight.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if(!holder.etHeight.getText().toString().isEmpty()) {
                        ((TextInputLayout) holder.etHeight.getParent()).setErrorEnabled(false);
                    }
                    aPackage.setHeight(holder.etHeight.getText().toString());
                }
            });

            holder.etLength.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    if(!holder.etLength.getText().toString().isEmpty()) {
                        ((TextInputLayout) holder.etLength.getParent()).setErrorEnabled(false);
                    }
                    aPackage.setLength(holder.etLength.getText().toString());

                }
            });

            holder.etWeight.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    if(!holder.etWeight.getText().toString().isEmpty()) {
                        ((TextInputLayout) holder.etWeight.getParent()).setErrorEnabled(false);
                    }
                    aPackage.setWeight(holder.etWeight.getText().toString());
                }
            });

            holder.etWidth.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    if(!holder.etWidth.getText().toString().isEmpty()) {
                        ((TextInputLayout) holder.etWidth.getParent()).setErrorEnabled(false);
                    }
                    aPackage.setWidth(holder.etWidth.getText().toString());
                }
            });

        }

        @Override
        public int getItemCount() {
            return packageArrayList.size();
        }
    }

}
