package lmdelivery.longmen.com.android.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import lmdelivery.longmen.com.android.Constant;
import lmdelivery.longmen.com.android.R;
import lmdelivery.longmen.com.android.bean.RateItem;

/**
 * Created by rufuszhu on 15-09-07.
 */
public class DialogUtil {
    private static final java.lang.String TAG = DialogUtil.class.getSimpleName();

    public static void showMessageDialog(String message, Context context){
        try {
            new AlertDialog.Builder(context)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public DialogUtil() {
    }

    public static void showSingleEstimateDialog(Context context, RateItem rateItem) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.rate_item_layout, null);
        TextView tvCarrierType= (TextView) view.findViewById(R.id.tv_carrier_type);

        TextView tvEstDate= (TextView) view.findViewById(R.id.tv_est_date);
        TextView tvPrice= (TextView) view.findViewById(R.id.tv_price);
        ImageView ivImage = (ImageView) view.findViewById(R.id.logo);

        Glide.with(context)
                .load(Constant.ENDPOINT + rateItem.getService_icon_url())
                .crossFade()
                .error(R.mipmap.ic_launcher)
                .into(ivImage);

        tvCarrierType.setText(Util.toDisplayCase(rateItem.getServiceName()));
        tvEstDate.setText(context.getString(R.string.estimate_delivery) + DateUtil.UnixTimeToDateString(rateItem.getEstimatedDelivery()));
        try {
            tvPrice.setText("$" + Util.roundTo2(Double.parseDouble(rateItem.getEstimate())));
        }catch (Exception e){
            Logger.e("Failed to parse price");
            tvPrice.setText("$-");
        }

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
        builder.setView(view)
//                .setTitle(context.getString(R.string.set_length))
                .setPositiveButton(context.getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setCancelable(true)
                .show();
    }
}
