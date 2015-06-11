package lmdelivery.longmen.com.android.util;

import android.content.Context;
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
}
