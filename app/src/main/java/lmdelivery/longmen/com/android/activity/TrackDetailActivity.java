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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

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
import lmdelivery.longmen.com.android.data.googleAPI.Location;
import lmdelivery.longmen.com.android.databinding.TrackDetailActivityBinding;
import lmdelivery.longmen.com.android.ui.PackageItemView;
import lmdelivery.longmen.com.android.util.Util;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class TrackDetailActivity extends AppCompatActivity {

    private GoogleMap mMap;

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
    TrackingDetail trackingDetail;
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
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setIcon(null);
            ab.setDisplayShowTitleEnabled(false);
        }

        context = this;
        trackingDetail = (TrackingDetail) getIntent().getSerializableExtra(Constant.EXTRA_TRACK_DETAIL);
        binding.setTrackDetail(trackingDetail);
        setUpMapIfNeeded(trackingDetail);

        tvCarrier.setText(Util.toDisplayCase(trackingDetail.getCourierServiceType()));
        tvCost.setText("$" + Util.roundTo2(trackingDetail.getFinalCost()));
//        tvFrom.setText(trackingDetail.getFromAddress().buildFullAddress());
//        tvTo.setText(trackingDetail.getToAddress().buildFullAddress());


        tvInsurace.setText("$" + Util.roundTo2(trackingDetail.getInsuranceValue()));
        tvStatus.setText(Util.toDisplayCase(trackingDetail.getOrderStatusModel().getStatus()));
        int slot;
        try {
            slot = Integer.parseInt(trackingDetail.getAppointmentSlotType().charAt(trackingDetail.getAppointmentSlotType().length() - 1) + "");
            tvPickupDate.setText(trackingDetail.getAppointmentDate().toString() + "\n" + getResources().getStringArray(R.array.time_interval_array)[slot - 1]);
        } catch (Exception e) {
            tvPickupDate.setText(getString(R.string.not_available));
        }


        for (int i = 0; i < trackingDetail.getShipments().length; i++) {
            llPackage.addView(addPackageTracking(trackingDetail.getShipments()[i]));
        }

        setupWindowAnimations();


    }

    private void setUpMapIfNeeded(TrackingDetail trackingDetail) {
        final SupportMapFragment mMapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
        // Try to obtain the map from the SupportMapFragment.
        mMapFragment.getMapAsync(
                googleMap -> {
                    mMap = googleMap;
                    geocoding(trackingDetail);
                });

    }

    private void setupWindowAnimations() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.slide_and_changebounds);
        getWindow().setSharedElementEnterTransition(transition);
        getWindow().setSharedElementExitTransition(transition);
        getWindow().setEnterTransition(new Fade());

    }

    private void geocoding(TrackingDetail trackingDetail) {
        googleAPI.geocoding(trackingDetail.getFromAddress().buildFullAddress(), Constant.GOOGLE_PLACE_API_SERVER_KEY)
                .zipWith(googleAPI.geocoding(trackingDetail.getToAddress().buildFullAddress(), Constant.GOOGLE_PLACE_API_SERVER_KEY), (geocodingResult, geocodingResult2) -> {
                    return createLatLngBound(geocodingResult, geocodingResult2);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<LatLng[]>() {
                    @Override
                    public void onCompleted() {
                        Timber.i("onCompleted");

                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e("onError" + e.toString());
                        e.printStackTrace();

                    }

                    @Override
                    public void onNext(LatLng latLng[]) {
                        if (latLng != null && latLng.length > 1) {
                            LatLng fromLatLng = latLng[0];
                            LatLng toLatLng = latLng[1];
                            if (fromLatLng.latitude < toLatLng.latitude) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(fromLatLng, toLatLng), 60));
                            } else {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(toLatLng, fromLatLng), 60));
                            }
                            mMap.addMarker(new MarkerOptions().position(fromLatLng));
                            mMap.addMarker(new MarkerOptions().position(toLatLng));
                        }
                    }
                });

    }

    private LatLng[] createLatLngBound(GeocodingResult geocodingResult, GeocodingResult geocodingResult2) {
        try {
            Location from = geocodingResult.getResults().get(0).getGeometry().getLocation();
            Location to = geocodingResult2.getResults().get(0).getGeometry().getLocation();
            LatLng fromLatLng = new LatLng(from.getLat(), from.getLng());
            LatLng toLatLng = new LatLng(to.getLat(), to.getLng());
            return new LatLng[]{fromLatLng, toLatLng};
        } catch (NullPointerException e) {

        }
        return null;
    }




    private PackageItemView addPackageTracking(final Shipments shipments) {
        PackageItemView itemView = new PackageItemView(context);
        itemView.bindPackageItem(shipments,trackingDetail.getOrderStatusModel().getStatus());
        return itemView;
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
