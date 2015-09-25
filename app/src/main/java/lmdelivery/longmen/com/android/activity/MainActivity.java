package lmdelivery.longmen.com.android.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import lmdelivery.longmen.com.android.AppController;
import lmdelivery.longmen.com.android.Constant;
import lmdelivery.longmen.com.android.R;
import lmdelivery.longmen.com.android.bean.RateItem;
import lmdelivery.longmen.com.android.bean.Shipments;
import lmdelivery.longmen.com.android.bean.TrackingDetail;
import lmdelivery.longmen.com.android.util.DialogUtil;
import lmdelivery.longmen.com.android.util.Logger;
import lmdelivery.longmen.com.android.util.Util;


public class MainActivity extends AppCompatActivity {

    private static final java.lang.String TAG = MainActivity.class.getSimpleName();
    private DrawerLayout mDrawerLayout;
    private Context context;
    private RecyclerView recyclerView;
    private ShipItemRecyclerViewAdapter adapter;

    private JsonObjectRequest logoutRequest;
    private JsonArrayRequest updateTrackingRequest;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, NewBookingActivity.class);
                startActivity(intent);
            }
        });

//        CardView cardView = (CardView) findViewById(R.id.welcome_card);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);

//        rv.setVisibility(View.GONE);
        setupRecyclerView();
//
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateShipments();
//                Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    public void run() {
//                        ArrayList<Shipments> shipItems = new ArrayList<>();
//                        for (int i = 0; i < 10; i++) {
//                            shipItems.add(Shipments.newFakeShipmentInstance());
//                        }
//                        adapter.updateValues(shipItems);
//                        mSwipeRefreshLayout.setRefreshing(false);
//                    }
//                }, 2000);
            }
        });
    }

    private void setupRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        ArrayList<Shipments> shipItems = new ArrayList<>();
        Shipments empty = new Shipments();
        shipItems.add(empty);
//        for(int i = 0; i < 10; i++){
//            RateItem item = new RateItem("1","http://www.hdicon.com/wp-content/uploads/2010/08/ups_2003.png", "Category 1", "$ 55", "Average 1 - 2 Business day", "ups", "One day express");
//            rateItems.add(item);
//        }
        adapter = new ShipItemRecyclerViewAdapter(shipItems, context);
        recyclerView.setItemAnimator(new SlideInUpAnimator());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        invalidateOptionsMenu();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (logoutRequest != null)
            logoutRequest.cancel();
        if (updateTrackingRequest != null)
            updateTrackingRequest.cancel();
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.action_login:
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.action_logout:
                new AlertDialog.Builder(context)
                        .setMessage(getString(R.string.logout_confirm, AppController.getInstance().getUserEmail()))
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                logout();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateShipments() {
        if (updateTrackingRequest != null) {
            return;
        }

        updateTrackingRequest = new JsonArrayRequest(Request.Method.GET, Constant.REST_URL + "order?userId=" + AppController.getInstance().getUserId() + "&token=" + AppController.getInstance().getUserToken()
                + "&limit=" + "20" + "&offset=" + "0", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Logger.i(TAG, response.toString());
                updateTrackingRequest = null;
                mSwipeRefreshLayout.setRefreshing(false);
                try {
                    Type listType = new TypeToken<ArrayList<TrackingDetail>>() {
                    }.getType();
                    ArrayList<TrackingDetail> trackingDetailArrayList = new Gson().fromJson(response.toString(), listType);
                    Logger.e(TAG,trackingDetailArrayList.size()+"");
                } catch (Exception e) {
                    e.printStackTrace();
                    DialogUtil.showMessageDialog(getString(R.string.err_connection), context);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                updateTrackingRequest = null;
                mSwipeRefreshLayout.setRefreshing(false);
                Util.handleVolleyError(error, context);
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(updateTrackingRequest, "updateTrackingRequest");
    }

    private void logout() {
        if (logoutRequest != null) {
            return;
        }

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage(getString(R.string.loading));
        pd.show();

        logoutRequest = new JsonObjectRequest(Request.Method.DELETE, Constant.REST_URL + "login/" + AppController.getInstance().getUserId() + "?token=" + AppController.getInstance().getUserToken(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Logger.e(TAG, response.toString());
                pd.dismiss();
                logoutRequest = null;
                try {
                    SharedPreferences.Editor editor = AppController.getInstance().getDefaultSharePreferences().edit();
                    editor.putString(Constant.SHARE_USER_EMAIL, "");
                    editor.putString(Constant.SHARE_USER_ID, "");
                    editor.putString(Constant.SHARE_USER_TOKEN, "");
                    editor.putString(Constant.SHARE_USER_PHONE, "");
                    editor.putBoolean(Constant.SHARE_IS_USER_ACTIVATED, false);
                    editor.apply();
                    invalidateOptionsMenu();
                    Toast.makeText(context, R.string.logout_success, Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    DialogUtil.showMessageDialog(getString(R.string.err_connection), context);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                logoutRequest = null;
                pd.dismiss();
                Util.handleVolleyError(error, context);
            }
        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(logoutRequest, "logout");
    }


    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    static private class ShipItemRecyclerViewAdapter extends RecyclerView.Adapter<ShipItemRecyclerViewAdapter.ViewHolder> {

        private final TypedValue mTypedValue = new TypedValue();
        private Context context;
        private int mBackground;
        private List<Shipments> mValues;
        private int selectedPosition;
        private static final int TYPE_EMPTY = 0;
        private static final int TYPE_SHIPMENT = 1;

        public ShipItemRecyclerViewAdapter(ArrayList<Shipments> items, Context context) {
            mValues = items;
            this.context = context;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public String mBoundString;

            public final LinearLayout mView;
            public final TextView title;
            public final TextView status;
            public final TextView btnTrack;
            public final ImageView icon;

            public ViewHolder(View view) {
                super(view);
                mView = (LinearLayout) view.findViewById(R.id.ll_card);
                title = (TextView) view.findViewById(R.id.tv_title);
                status = (TextView) view.findViewById(R.id.tv_status);
                btnTrack = (TextView) view.findViewById(R.id.btn_track);
                icon = (ImageView) view.findViewById(R.id.iv_ship_icon);
            }

        }

        public Shipments getValueAt(int position) {
            return mValues.get(position);
        }

        public void updateValues(ArrayList<Shipments> shipments) {
            mValues = shipments;
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int position) {
//            if (mValues.get(position).isEmpty)
            return TYPE_EMPTY;
//            else
//                return TYPE_SHIPMENT;
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

//            if (getItemViewType(position) == TYPE_SHIPMENT) {
//                holder.title.setText(mValues.get(position).nickName);
//                holder.status.setText(mValues.get(position).status);
//                holder.btnTrack.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Toast.makeText(context, "track btn clicked", Toast.LENGTH_SHORT).show();
//                    }
//                });
//                Glide.with(context)
//                        .load(mValues.get(position).serviceIconUrl)
//                        .centerCrop()
//                                //.placeholder(R.drawable.loading_spinner)
//                        .crossFade()
//                        .into(holder.icon);
//
//                holder.mView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Toast.makeText(context, "view clicked", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }
    }
}
