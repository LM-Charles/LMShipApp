package lmdelivery.longmen.com.android.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import lmdelivery.longmen.com.android.R;
import lmdelivery.longmen.com.android.data.Shipments;

/**
 * Created by rufus on 2015-12-15.
 */
public class PackageItemView extends LinearLayout {


    public PackageItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.include_shipment_status, this);

    }

    public void bindPackageItem(Shipments shipments) {

    }
}
