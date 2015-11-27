package lmdelivery.longmen.com.android.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import lmdelivery.longmen.com.android.AppController;
import lmdelivery.longmen.com.android.Constant;
import lmdelivery.longmen.com.android.R;
import lmdelivery.longmen.com.android.api.LMXApi;
import lmdelivery.longmen.com.android.data.TrackingDetail;
import lmdelivery.longmen.com.android.data.User;
import lmdelivery.longmen.com.android.util.DialogUtil;
import lmdelivery.longmen.com.android.util.Logger;
import lmdelivery.longmen.com.android.util.RxUtils;
import lmdelivery.longmen.com.android.util.Util;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;


public class MainActivity extends AppCompatActivity {

    private static final java.lang.String TAG = MainActivity.class.getSimpleName();
    //    private DrawerLayout mDrawerLayout;
    private Context context;
    private RecyclerView recyclerView;
    private ShipItemRecyclerViewAdapter adapter;

    private JsonObjectRequest logoutRequest;
    private JsonArrayRequest updateTrackingRequest;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FloatingActionButton fab;
    private CompositeSubscription subscriptions = new CompositeSubscription();

    @Inject
    LMXApi lmxApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        AppController.getComponent().inject(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        final ActionBar ab = getSupportActionBar();
//        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
//        ab.setDisplayHomeAsUpEnabled(true);

//        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        if (navigationView != null) {
//            setupDrawerContent(navigationView);
//        }

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            fab.setEnabled(false);
            Intent intent = new Intent(context, NewBookingActivity.class);
            startActivity(intent);
        });

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        setupRecyclerView();
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(() -> updateShipments());
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

    }

    private void setupRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        ArrayList<TrackingDetail> shipItems = new ArrayList<>();
        adapter = new ShipItemRecyclerViewAdapter(shipItems, context);
        //recyclerView.setItemAnimator(new SlideInUpAnimator());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        fab.setEnabled(true);
        invalidateOptionsMenu();
        mSwipeRefreshLayout.post(() -> updateShipments());
        subscriptions = RxUtils.getNewCompositeSubIfUnsubscribed(subscriptions);
    }


    @Override
    public void onStop() {
        super.onStop();
        RxUtils.unsubscribeIfNotNull(subscriptions);
        if (logoutRequest != null) {
            logoutRequest.cancel();
            logoutRequest = null;
        }
        if (updateTrackingRequest != null) {
            updateTrackingRequest.cancel();
            updateTrackingRequest = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (AppController.getInstance().isUserActivated())
            getMenuInflater().inflate(R.menu.menu_main_logout, menu);
        else
            getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case android.R.id.home:
//                mDrawerLayout.openDrawer(GravityCompat.START);
//                break;
            case R.id.action_login:
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.action_logout:
                new AlertDialog.Builder(context)
                        .setMessage(getString(R.string.logout_confirm, AppController.getInstance().getUserEmail()))
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                            logout();
                            dialog.dismiss();
                        })
                        .setNegativeButton(android.R.string.cancel, (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateShipments() {
        if (!AppController.getInstance().isUserActivated()) {
            adapter.setEmptyView();
            if (mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
            return;
        }

        if (!mSwipeRefreshLayout.isRefreshing())
            mSwipeRefreshLayout.setRefreshing(true);

        int userId = AppController.getInstance().getUserId();

        if(userId!=-1) {
            subscriptions.add(
                    lmxApi.getOrderByUser(AppController.getInstance().getUserId(), AppController.getInstance().getUserToken(), 24, 0)
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Subscriber<ArrayList<TrackingDetail>>() {
                                @Override
                                public void onCompleted() {
                                    Timber.i("onCompleted");
                                }

                                @Override
                                public void onError(Throwable e) {
                                    mSwipeRefreshLayout.setRefreshing(false);
                                    e.printStackTrace();
                                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onNext(ArrayList<TrackingDetail> trackingDetailArrayList) {
                                    mSwipeRefreshLayout.setRefreshing(false);
                                    if (trackingDetailArrayList.size() == 0) {
                                        adapter.setEmptyView();
                                        Toast.makeText(context, R.string.no_tracking_found, Toast.LENGTH_SHORT).show();
                                    } else
                                        adapter.updateValues(trackingDetailArrayList);
                                }
                            }));
        }else{
            Toast.makeText(context, R.string.please_sign_in_first, Toast.LENGTH_SHORT).show();
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private void logout() {
        if (logoutRequest != null) {
            return;
        }

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.loading));
        pd.show();

        logoutRequest = new JsonObjectRequest(Request.Method.DELETE, Constant.REST_URL + "login/" + AppController.getInstance().getUserId() + "?token=" + AppController.getInstance().getUserToken(), response -> {
            Logger.e(TAG, response.toString());
            pd.dismiss();
            logoutRequest = null;
            try {
                SharedPreferences.Editor editor = AppController.getInstance().getDefaultSharePreferences().edit();
                editor.putString(Constant.SHARE_USER_EMAIL, "");
                editor.putInt(Constant.SHARE_USER_ID, -1);
                editor.putString(Constant.SHARE_USER_TOKEN, "");
                editor.putString(Constant.SHARE_USER_PHONE, "");
                editor.putBoolean(Constant.SHARE_IS_USER_ACTIVATED, false);
                editor.apply();
                invalidateOptionsMenu();
                Toast.makeText(context, R.string.logout_success, Toast.LENGTH_LONG).show();
                updateShipments();
            } catch (Exception e) {
                e.printStackTrace();
                DialogUtil.showMessageDialog(getString(R.string.err_connection), context);
            }
        }, error -> {
            logoutRequest = null;
            pd.dismiss();
            Util.handleVolleyError(error, context);
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(logoutRequest, "logout");
    }


//    private void setupDrawerContent(NavigationView navigationView) {
//        navigationView.setNavigationItemSelectedListener(
//                new NavigationView.OnNavigationItemSelectedListener() {
//                    @Override
//                    public boolean onNavigationItemSelected(MenuItem menuItem) {
//                        menuItem.setChecked(true);
//                        mDrawerLayout.closeDrawers();
//                        return true;
//                    }
//                });
//    }

    static private class ShipItemRecyclerViewAdapter extends RecyclerView.Adapter<ShipItemRecyclerViewAdapter.ViewHolder> {

        private final TypedValue mTypedValue = new TypedValue();
        private Context context;
        private int mBackground;
        private List<TrackingDetail> mValues;
        private int selectedPosition;
        private static final int TYPE_EMPTY = 0;
        private static final int TYPE_SHIPMENT = 1;
        private boolean isEmpty = false;
        // Allows to remember the last item shown on screen
        private int lastPosition = -1;

        public ShipItemRecyclerViewAdapter(ArrayList<TrackingDetail> items, Context context) {
            mValues = items;
            this.context = context;
        }

        public void setEmptyView() {
            isEmpty = true;
            TrackingDetail trackingDetail = new TrackingDetail();
            mValues.clear();
            mValues.add(trackingDetail);
            notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final CardView mView;
            public final TextView title;
            public final TextView status;
            public final TextView btnTrack;
            public final ImageView icon;
            public final LinearLayout llCard;

            public ViewHolder(View view) {
                super(view);
                mView = (CardView) view.findViewById(R.id.card);
                title = (TextView) view.findViewById(R.id.tv_title);
                status = (TextView) view.findViewById(R.id.tv_status);
                btnTrack = (TextView) view.findViewById(R.id.btn_track);
                icon = (ImageView) view.findViewById(R.id.iv_ship_icon);
                llCard = (LinearLayout) view.findViewById(R.id.ll_card);
            }

        }

        public TrackingDetail getValueAt(int position) {
            return mValues.get(position);
        }

        public void updateValues(ArrayList<TrackingDetail> shipments) {
            isEmpty = false;
            mValues = shipments;
            lastPosition = -1;
            notifyDataSetChanged();

//            isEmpty = false;
//            mValues.clear();
//            notifyDataSetChanged();
//            int counter = 0;
//            for(TrackingDetail trackingDetail : shipments){
//                mValues.add(trackingDetail);
//                notifyItemInserted(counter);
//                counter++;
//            }
        }

        /**
         * Here is the key method to apply the animation
         */
        private void setAnimation(View viewToAnimate, int position) {
            // If the bound view wasn't previously displayed on screen, it's animated
            if (position > lastPosition && viewToAnimate != null) {
                Animation animation = AnimationUtils.loadAnimation(context, R.anim.abc_slide_in_bottom);
                animation.setStartOffset(position * 80);
                viewToAnimate.startAnimation(animation);
                lastPosition = position;
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (isEmpty)
                return TYPE_EMPTY;
            else
                return TYPE_SHIPMENT;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType == TYPE_EMPTY)
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_no_shipment, parent, false);
            else
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ship_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {
            if (getItemViewType(position) == TYPE_SHIPMENT) {
                final TrackingDetail trackingDetail = mValues.get(position);
                holder.title.setText(Util.toDisplayCase(trackingDetail.getCourierServiceType()));
                //display courier status if exist
                if (trackingDetail.getShipments() != null && trackingDetail.getShipments().length > 0 && trackingDetail.getShipments()[0].getTracking() != null) {
                    holder.status.setText(Util.toDisplayCase(trackingDetail.getShipments()[0].getTracking().getTrackingStatus()));
                } else {
                    //otherwise display LM status
                    holder.status.setText(Util.toDisplayCase(trackingDetail.getOrderStatusModel().getStatus()));
                }

                holder.btnTrack.setOnClickListener(v -> {
                    if (trackingDetail.getShipments() != null && trackingDetail.getShipments().length > 0 && trackingDetail.getShipments()[0].getTracking() != null && trackingDetail.getShipments()[0].getTracking().getTrackingURL() != null) {
                        try {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(trackingDetail.getShipments()[0].getTracking().getTrackingURL()));
                            context.startActivity(browserIntent);
                        } catch (Exception e) {
                            Toast.makeText(context, R.string.no_tracking_number, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, R.string.no_tracking_number, Toast.LENGTH_SHORT).show();
                    }
                });

                Glide.with(context)
                        .load(Constant.ENDPOINT + trackingDetail.getService_icon_url())
                        .error(R.drawable.logo)
                        .centerCrop()
                        .crossFade()
                        .into(holder.icon);


                // Here you apply the animation when the view is bound
//                setAnimation(holder.mView, position);

                holder.llCard.setOnClickListener(v -> {

                    Intent intent = new Intent(context, TrackDetailActivity.class);
                    intent.putExtra(Constant.EXTRA_TRACK_DETAIL, trackingDetail);
                    context.startActivity(intent);
                });
            }
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }
    }
}
