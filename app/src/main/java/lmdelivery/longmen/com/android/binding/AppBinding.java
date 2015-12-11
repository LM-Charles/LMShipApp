package lmdelivery.longmen.com.android.binding;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import lmdelivery.longmen.com.android.Constant;
import lmdelivery.longmen.com.android.R;

/**
 * Created by rufus on 2015-12-10.
 */
public class AppBinding {
    @BindingAdapter({"app:imageUrl"})
    public static void setImageUrl(ImageView view, String url) {
        Glide.with(view.getContext())
                .load(Constant.ENDPOINT + url)
                .error(R.mipmap.logo)
                .centerCrop()
                .crossFade()
                .into(view);
    }
}
