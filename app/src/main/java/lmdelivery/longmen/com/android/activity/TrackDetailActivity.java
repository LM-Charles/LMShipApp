package lmdelivery.longmen.com.android.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import lmdelivery.longmen.com.android.AppController;
import lmdelivery.longmen.com.android.Constant;
import lmdelivery.longmen.com.android.R;
import lmdelivery.longmen.com.android.api.GoogleAPI;
import lmdelivery.longmen.com.android.data.Shipments;
import lmdelivery.longmen.com.android.data.TrackingDetail;
import lmdelivery.longmen.com.android.data.googleAPI.GeocodingResult;
import lmdelivery.longmen.com.android.databinding.TrackDetailActivityBinding;
import lmdelivery.longmen.com.android.util.Util;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class TrackDetailActivity extends AppCompatActivity {

    @Bind(R.id.tv_carrier)
    TextView tvCarrier;
    @Bind(R.id.tv_status)
    TextView tvStatus;
    @Bind(R.id.iv_ship_icon)
    ImageView ivIcon;
    @Bind(R.id.ll_package)
    LinearLayout llPackage;
    @Bind(R.id.tv_id)
    TextView tvId;
    @Bind(R.id.tv_order_date)
    TextView tvOrderDate;
    @Bind(R.id.tv_from)
    TextView tvFrom;
    @Bind(R.id.tv_to)
    TextView tvTo;
    @Bind(R.id.tv_insurance)
    TextView tvInsurace;
    @Bind(R.id.tv_cost)
    TextView tvCost;
    @Bind(R.id.tv_pickup_date)
    TextView tvPickupDate;

    @Inject
    GoogleAPI googleAPI;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TrackDetailActivityBinding binding = DataBindingUtil.setContentView(this, R.layout.track_detail_activity);

        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        AppController.getComponent().inject(this);

        final ActionBar ab = getSupportActionBar();
        if (ab != null)
            ab.setDisplayHomeAsUpEnabled(true);


        context = this;
        TrackingDetail trackingDetail = (TrackingDetail) getIntent().getSerializableExtra(Constant.EXTRA_TRACK_DETAIL);
        binding.setTrackDetail(trackingDetail);

        geocoding(trackingDetail);

        tvCarrier.setText(Util.toDisplayCase(trackingDetail.getCourierServiceType()));
        tvCost.setText("$" + Util.roundTo2(trackingDetail.getFinalCost()));
        tvFrom.setText(trackingDetail.getFromAddress().buildFullAddress());
        tvTo.setText(trackingDetail.getToAddress().buildFullAddress());


        tvInsurace.setText("$" + Util.roundTo2(trackingDetail.getInsuranceValue()));
        tvStatus.setText(Util.toDisplayCase(trackingDetail.getOrderStatusModel().getStatus()));
        int slot;
        try{
            slot =Integer.parseInt(trackingDetail.getAppointmentSlotType().charAt(trackingDetail.getAppointmentSlotType().length()-1) + "");
            tvPickupDate.setText(trackingDetail.getAppointmentDate().toString() + "\n" + getResources().getStringArray(R.array.time_interval_array)[slot-1]);
        }catch (Exception e){
            tvPickupDate.setText(getString(R.string.not_available));
        }



        for(int i = 0; i < trackingDetail.getShipments().length; i++){
            llPackage.addView(addPackageTracking(trackingDetail.getShipments()[i], i));
        }

        setupWindowAnimations();
    }

    private void setupWindowAnimations() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.slide_and_changebounds);
        getWindow().setSharedElementEnterTransition(transition);
        getWindow().setSharedElementExitTransition(transition);
        getWindow().setEnterTransition(new Fade());

    }

    private void geocoding(TrackingDetail trackingDetail){
        googleAPI.geocoding(trackingDetail.getFromAddress().buildFullAddress(), Constant.GOOGLE_PLACE_API_SERVER_KEY).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<GeocodingResult>() {
                    @Override
                    public void onCompleted() {
                        Timber.i("onCompleted");

                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e("onError" + e.toString());

                    }

                    @Override
                    public void onNext(GeocodingResult geocodingResult) {
                        Timber.i("onNext");
                        Timber.i(geocodingResult.toString());
                    }
                });

    }

    @BindingAdapter({"android:src"})
    public static void setImageUrl(ImageView view, String url){
        Glide.with(view.getContext())
                .load(Constant.ENDPOINT + url)
                .error(R.mipmap.logo)
                .centerCrop()
                .crossFade()
                .into(view);
    }


    private View addPackageTracking(final Shipments shipments, int index){
        final Context contextThemeWrapper = new ContextThemeWrapper(context, R.style.Base_Theme_LMTheme);
        LayoutInflater vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = vi.cloneInContext(contextThemeWrapper).inflate(R.layout.include_shipment_status, null);
        TextView tvTrackingNumber = (TextView) v.findViewById(R.id.tv_tracking_number);
        TextView tvLocation = (TextView) v.findViewById(R.id.tv_location);
        TextView tvTrackingStatus = (TextView) v.findViewById(R.id.tv_tracking_status);
        TextView tvPackageTitle = (TextView) v.findViewById(R.id.tv_package_title);
        try {
            if(TextUtils.isEmpty(shipments.getTrackingNumber()))
                throw new NullPointerException();
            tvTrackingNumber.setTextColor(getResources().getColor(R.color.teal));
            SpannableString spanString = new SpannableString(shipments.getTrackingNumber());
            spanString.setSpan(new UnderlineSpan(), 0, spanString.length(), 0);
            tvTrackingNumber.setText(spanString);
            tvTrackingNumber.setOnClickListener(v1 -> {
                try {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(shipments.getTracking().getTrackingURL()));
                    context.startActivity(browserIntent);
                } catch (Exception e) {
                    Toast.makeText(context, R.string.fail_to_open_browser, Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){
            tvTrackingNumber.setText(R.string.not_available);
        }

        try {
            if(TextUtils.isEmpty(shipments.getTracking().getTrackingCity()) || TextUtils.isEmpty(shipments.getTracking().getTrackingCountry()))
                throw new NullPointerException();
            tvLocation.setText(shipments.getTracking().getTrackingCity() + " " + shipments.getTracking().getTrackingCountry());
        }catch (Exception e){
            tvLocation.setText(R.string.not_available);
        }

        try {
            if(TextUtils.isEmpty(shipments.getTracking().getTrackingStatus()))
                throw new NullPointerException();
            tvTrackingStatus.setText(shipments.getTracking().getTrackingStatus());
        }catch (Exception e){
            tvTrackingStatus.setText(R.string.not_available);
        }

        tvPackageTitle.setText(getString(R.string.package_name) + " " + (index + 1));
        return v;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
