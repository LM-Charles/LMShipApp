package lmdelivery.longmen.com.android.util;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import lmdelivery.longmen.com.android.AppController;
import lmdelivery.longmen.com.android.Constant;
import lmdelivery.longmen.com.android.R;
import lmdelivery.longmen.com.android.bean.Address;
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

    public static void showSingleEstimateDialog(Context context, RateItem rateItem, RateItem insuranceRate, RateItem packageRate, DialogInterface.OnClickListener onClickListener) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog_confirm_purchase_layout, null);
        TextView tvCarrierType= (TextView) view.findViewById(R.id.tv_carrier_type);

        TextView tvEstDate= (TextView) view.findViewById(R.id.tv_est_date);
        TextView tvTotalPrice= (TextView) view.findViewById(R.id.tv_total_price);
        TextView tvTaxPrice= (TextView) view.findViewById(R.id.tv_tax_price);
        TextView tvPackagePrice= (TextView) view.findViewById(R.id.tv_package_price);
        TextView tvInsurancePrice= (TextView) view.findViewById(R.id.tv_insurance_price);
        TextView tvShipPrice= (TextView) view.findViewById(R.id.tv_ship_price);
        ImageView ivImage = (ImageView) view.findViewById(R.id.logo);

        Glide.with(context)
                .load(Constant.ENDPOINT + rateItem.getService_icon_url())
                .crossFade()
                .error(R.mipmap.ic_launcher)
                .into(ivImage);

        tvCarrierType.setText(Util.toDisplayCase(rateItem.getServiceName()));
        tvEstDate.setText(context.getString(R.string.estimate_delivery) + DateUtil.UnixTimeToDateString(rateItem.getEstimatedDelivery()));
        double taxTotal = 0.00;

        try {
            tvShipPrice.setText("$ " + Util.roundTo2(rateItem.getEstimate()));
        }catch (Exception e){
            Logger.e("Failed to parse estimate price");
            tvShipPrice.setText("$-");
        }

        try {
            tvInsurancePrice.setText("$ " + Util.roundTo2(insuranceRate.getEstimate()));
        }catch (Exception e){
            Logger.e("Failed to parse insurance price");
            tvInsurancePrice.setText("$-");
        }

        try {
            tvPackagePrice.setText("$ " + Util.roundTo2(packageRate.getEstimate()));
        }catch (Exception e){
            Logger.e("Failed to parse package price");
            tvPackagePrice.setText("$-");
        }


        try {
            taxTotal = rateItem.getTaxEstimate() + insuranceRate.getTaxEstimate() + packageRate.getTaxEstimate();
            tvTaxPrice.setText("$ " + Util.roundTo2(taxTotal));
        }catch (Exception e){
            Logger.e("Failed to parse tax");
            tvTaxPrice.setText("$-");
        }

        try {
            double total = taxTotal + packageRate.getEstimate() + rateItem.getEstimate() + insuranceRate.getEstimate();
            tvTotalPrice.setText("$ " + Util.roundTo2(total));
        }catch (Exception e){
            Logger.e("Failed to parse total price");
            tvTotalPrice.setText("$-");
        }

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context);
        builder.setView(view)
//                .setTitle(context.getString(R.string.set_length))
                .setPositiveButton(context.getString(R.string.confirm), onClickListener)
                .setCancelable(true)
                .show();
    }


}
