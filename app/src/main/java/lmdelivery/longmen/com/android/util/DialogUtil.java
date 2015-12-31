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
import lmdelivery.longmen.com.android.data.RateItem;

/**
 * Created by rufuszhu on 15-09-07.
 */
public class DialogUtil {
    private static final java.lang.String TAG = DialogUtil.class.getSimpleName();

    public static void showMessageDialog(String message, Context context){
        try {
            new AlertDialog.Builder(context)
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                        dialog.dismiss();
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
                .error(R.mipmap.logo)
                .into(ivImage);

        tvCarrierType.setText(Util.toDisplayCase(rateItem.getServiceName()));
        tvEstDate.setText(context.getString(R.string.estimate_delivery) + DateUtil.UnixTimeToDateString(rateItem.getEstimatedDelivery()));
        double taxTotal = 0.00;

        try {
            tvShipPrice.setText("$ " + rateItem.getEstimate());
        }catch (Exception e){
            Logger.e("Failed to parse estimate price");
            tvShipPrice.setText("$-");
        }

        try {
            tvInsurancePrice.setText("$ " + insuranceRate.getEstimate());
        }catch (Exception e){
            Logger.e("Failed to parse insurance price");
            tvInsurancePrice.setText("$-");
        }

        try {
            tvPackagePrice.setText("$ " + packageRate.getEstimate());
        }catch (Exception e){
            Logger.e("Failed to parse ic_package price");
            tvPackagePrice.setText("$-");
        }


        try {
            taxTotal = rateItem.getTaxEstimate() + insuranceRate.getTaxEstimate() + packageRate.getTaxEstimate();
            tvTaxPrice.setText("$ " + taxTotal);
        }catch (Exception e){
            Logger.e("Failed to parse tax");
            tvTaxPrice.setText("$-");
        }

        try {
            double total = taxTotal + packageRate.getEstimate() + rateItem.getEstimate() + insuranceRate.getEstimate();
            tvTotalPrice.setText("$ " + total);
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
