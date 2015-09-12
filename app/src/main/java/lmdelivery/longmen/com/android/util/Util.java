package lmdelivery.longmen.com.android.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;

import lmdelivery.longmen.com.android.AppController;

import lmdelivery.longmen.com.android.R;

/**
 * Created by Kaiyu on 2015-06-10.
 */
public class Util {
    public static final String TAG = Util.class.getSimpleName();
    public static void closeKeyBoard(Context context, EditText myEditText){
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(myEditText.getWindowToken(), 0);
    }



    public static String trimMessage(String json, String key){
        String trimmedString = null;

        try{
            JSONObject obj = new JSONObject(json);
            trimmedString = obj.getString(key);
        } catch(JSONException e){
            Logger.e(TAG, json);
            e.printStackTrace();
            return null;
        }

        return trimmedString;
    }

    public static void handleVolleyError(VolleyError error, Context context){
        NetworkResponse response = error.networkResponse;
        Logger.e(TAG,error.toString());
        String message;
        if(response != null && response.data != null){
            message = trimMessage(new String(response.data), "message");
            if(message!=null)
                DialogUtil.showMessageDialog(message, context);
            else
                DialogUtil.showMessageDialog(context.getString(R.string.err_connection), context);
        }
        else
            DialogUtil.showMessageDialog(context.getString(R.string.err_connection), context);
    }

    public static void sendSupportEmail(Context context){
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"recipient@example.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
        i.putExtra(Intent.EXTRA_TEXT   , "body of email");
        try {
            context.startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    public static String getPhoneNumber(){
        try {
            TelephonyManager tMgr = (TelephonyManager) AppController.getAppContext().getSystemService(Context.TELEPHONY_SERVICE);

            String phone = tMgr.getLine1Number();
            if(!TextUtils.isEmpty(phone)){
                phone = phone.replaceAll("[^\\d]", "");
                if(phone.length()>2 && phone.startsWith("1")){
                    phone = phone.substring(1);
                }
                return phone;
            }else {
                return "";
            }
        }catch (Exception e){
            return "";
        }
    }

    //convert ENUM to user readable
    public static String toDisplayCase(String s) {

        s = s.replace("_", " ");
        final String ACTIONABLE_DELIMITERS = " '-/";
        StringBuilder sb = new StringBuilder();
        boolean capNext = true;
        for (char c : s.toCharArray()) {
            c = (capNext)
                    ? Character.toUpperCase(c)
                    : Character.toLowerCase(c);
            sb.append(c);
            capNext = (ACTIONABLE_DELIMITERS.indexOf((int) c) >= 0); // explicit cast not needed
        }
        return sb.toString();
    }

    public static String roundTo2(double value) {
        DecimalFormat df = new DecimalFormat("##.##");
        return df.format(value);
    }

}
