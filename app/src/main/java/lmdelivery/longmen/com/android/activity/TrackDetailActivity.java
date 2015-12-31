package lmdelivery.longmen.com.android.activity;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

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
import lmdelivery.longmen.com.android.util.DateUtil;
import lmdelivery.longmen.com.android.util.Util;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class TrackDetailActivity extends AppCompatActivity {

    private GoogleMap mMap;

    private static int MAP_ICON_PADDING = 5;
    @Bind(R.id.tv_carrier)
    TextView tvCarrier;
    @Bind(R.id.tv_status)
    TextView tvStatus;
    @Bind(R.id.ll_package)
    LinearLayout llPackage;
    @Bind(R.id.tv_from)
    TextView tvFrom;
    @Bind(R.id.tv_to)
    TextView tvTo;
    @Bind(R.id.tv_package_cost)
    TextView tvPackageCost;
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
//        tvFrom.setText(trackingDetail.getFromAddress().buildFullAddress());
//        tvTo.setText(trackingDetail.getToAddress().buildFullAddress());



        tvStatus.setText(Util.toDisplayCase(trackingDetail.getOrderStatusModel().getStatus()));
        int slot;
        try {
            slot = Integer.parseInt(trackingDetail.getAppointmentSlotType().charAt(trackingDetail.getAppointmentSlotType().length() - 1) + "");
            tvPickupDate.setText(String.format("%s %s", getResources().getStringArray(R.array.time_interval_array)[slot - 1], DateUtil.DateToString(trackingDetail.getAppointmentDate())));
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
                                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(fromLatLng, toLatLng), MAP_ICON_PADDING));
                            } else {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(toLatLng, fromLatLng), MAP_ICON_PADDING));
                            }

//                            mMap.addMarker(new MarkerOptions().position(fromLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.home_big)));
//                            mMap.addMarker(new MarkerOptions().position(toLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.flag_variant_big)));
                            mMap.addMarker(new MarkerOptions().position(fromLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_home_grey600_24dp)));
                            mMap.addMarker(new MarkerOptions().position(toLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_flag_variant_grey600_24dp)));
                            Polyline line = mMap.addPolyline(new PolylineOptions()
                                    .add(fromLatLng, toLatLng)
                                    .width(8)
                                    .geodesic(true)
                                    .color(getResources().getColor(R.color.red)));
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
