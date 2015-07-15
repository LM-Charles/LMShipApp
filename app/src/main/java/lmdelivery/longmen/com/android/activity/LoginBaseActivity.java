package lmdelivery.longmen.com.android.activity;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;

import lmdelivery.longmen.com.android.R;
import lmdelivery.longmen.com.android.util.Logger;

/**
 * Created by rzhu on 7/6/2015.
 */
public class LoginBaseActivity extends AppCompatActivity{
    private ViewTreeObserver.OnGlobalLayoutListener keyboardLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {

        @Override
        public void onGlobalLayout() {

            //this solution doesn't work because screen size is not changing when keyboard show/hide,
            // so I hard code it to always collapse the toolbar image to make more room for user to enter edittext
            onShowKeyboard(0);
//            int heightDiff = rootLayout.getRootView().getHeight() - rootLayout.getHeight();
//            int rootHeight = rootLayout.getRootView().getHeight();
//            int rootlayoutHeight = rootLayout.getHeight();
//            View view = getWindow().findViewById(Window.ID_ANDROID_CONTENT);
//            int contentViewTop = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getHeight();
//
//            LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(LoginBaseActivity.this);
//
//            Logger.e("LoginBaseActivity", "heightDiff: " + heightDiff + " contentViewTop: " + contentViewTop);
//            Logger.e("LoginBaseActivity", "rootHeight: " + rootHeight + " rootlayoutHeight: " + rootlayoutHeight);
//            if(heightDiff <= contentViewTop){
//                onHideKeyboard();
//
//                Intent intent = new Intent("KeyboardWillHide");
//                broadcastManager.sendBroadcast(intent);
//            } else {
//                int keyboardHeight = heightDiff - contentViewTop;
//                onShowKeyboard(keyboardHeight);
//
//                Intent intent = new Intent("KeyboardWillShow");
//                intent.putExtra("KeyboardHeight", keyboardHeight);
//                broadcastManager.sendBroadcast(intent);
//            }
        }
    };

    private boolean keyboardListenersAttached = false;
    private ViewGroup rootLayout;

    protected void onShowKeyboard(int keyboardHeight) {}
    protected void onHideKeyboard() {}

    protected void attachKeyboardListeners() {
        if (keyboardListenersAttached) {
            return;
        }

        rootLayout = (ViewGroup) findViewById(R.id.main_content);
        rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(keyboardLayoutListener);

        keyboardListenersAttached = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (keyboardListenersAttached) {
            rootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(keyboardLayoutListener);
        }
    }

}
