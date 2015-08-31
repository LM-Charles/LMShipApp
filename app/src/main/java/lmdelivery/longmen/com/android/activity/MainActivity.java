package lmdelivery.longmen.com.android.activity;

import android.content.Context;
import android.content.Intent;
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

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import lmdelivery.longmen.com.android.R;
import lmdelivery.longmen.com.android.bean.Shipment;


public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private Context context;
    private RecyclerView recyclerView;
    private ShipItemRecyclerViewAdapter adapter;

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
        final SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);

//        rv.setVisibility(View.GONE);
        setupRecyclerView();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        ArrayList<Shipment> shipItems = new ArrayList<>();
                        for (int i = 0; i < 10; i++) {
                            shipItems.add(Shipment.newFakeShipmentInstance());
                        }
                        adapter.updateValues(shipItems);
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });
    }

    private void setupRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        ArrayList<Shipment> shipItems = new ArrayList<>();
        Shipment empty = Shipment.newEmptyShipmentInstance();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
            case R.id.action_settings:
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
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
        private List<Shipment> mValues;
        private int selectedPosition;
        private static final int TYPE_EMPTY = 0;
        private static final int TYPE_SHIPMENT = 1;

        public ShipItemRecyclerViewAdapter(ArrayList<Shipment> items, Context context) {
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

        public Shipment getValueAt(int position) {
            return mValues.get(position);
        }

        public void updateValues(ArrayList<Shipment> shipments) {
            mValues = shipments;
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int position) {
            if (mValues.get(position).isEmpty)
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
                holder.title.setText(mValues.get(position).nickName);
                holder.status.setText(mValues.get(position).status);
                holder.btnTrack.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, "track btn clicked", Toast.LENGTH_SHORT).show();
                    }
                });
                Glide.with(context)
                        .load(mValues.get(position).serviceIconUrl)
                        .centerCrop()
                                //.placeholder(R.drawable.loading_spinner)
                        .crossFade()
                        .into(holder.icon);

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, "view clicked", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }
    }
}
