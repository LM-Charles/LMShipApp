package lmdelivery.longmen.com.android.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import lmdelivery.longmen.com.android.R;
import lmdelivery.longmen.com.android.data.Shipments;
import lmdelivery.longmen.com.android.data.TrackingDetail;

/**
 * Created by rufus on 2015-12-15.
 */
public class PackageItemView extends LinearLayout {

    @Bind(R.id.tv_package_size)
    TextView tvPackageSize;

    @Bind(R.id.tv_tracking_status)
    TextView tvTrackingStatus;

    @Bind(R.id.tv_location)
    TextView tvLocation;

    public PackageItemView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.include_shipment_status, this);
        ButterKnife.bind(this);
    }

    public PackageItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.include_shipment_status, this);
        ButterKnife.bind(this);
    }

    public void bindPackageItem(Shipments shipments, String lmxStatus) {
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shipments!= null && shipments.getTracking() != null && shipments.getTracking().getTrackingURL() != null) {
                    try {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(shipments.getTracking().getTrackingURL()));
                        getContext().startActivity(browserIntent);
                    } catch (Exception e) {
                        Toast.makeText(getContext(), R.string.no_tracking_number, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), R.string.no_tracking_number, Toast.LENGTH_SHORT).show();
                }
            }
        });
        
        if (shipments.getTracking() != null && !TextUtils.isEmpty(shipments.getTracking().getTrackingCity()) && !TextUtils.isEmpty(shipments.getTracking().getTrackingCountry())) {
            tvLocation.setText(String.format("%s\n%s", shipments.getTracking().getTrackingCity(), shipments.getTracking().getTrackingCountry()));
        } else {
            tvLocation.setText(getContext().getString(R.string.not_available));
        }

        if (shipments.getShipmentPackageType().equalsIgnoreCase("CUSTOM")) {
            tvPackageSize.setText(getContext().getString(R.string.package_format, shipments.getLength(), shipments.getWidth(), shipments.getHeight()));
        } else {
            tvPackageSize.setText(shipments.getShipmentPackageType());
        }

        if (shipments.getTracking() != null && !TextUtils.isEmpty(shipments.getTracking().getTrackingStatus())) {
            tvTrackingStatus.setText(shipments.getTracking().getTrackingStatus());
        } else {
            tvTrackingStatus.setText(lmxStatus);
        }

    }
}
