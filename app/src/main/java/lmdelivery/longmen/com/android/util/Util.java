package lmdelivery.longmen.com.android.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by Kaiyu on 2015-06-10.
 */
public class Util {
    public static void closeKeyBoard(Context context, EditText myEditText){
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(myEditText.getWindowToken(), 0);
    }

    public static void showMessageDialog(String message, Context context){
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
}
