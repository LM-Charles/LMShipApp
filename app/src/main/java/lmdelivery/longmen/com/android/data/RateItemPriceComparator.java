package lmdelivery.longmen.com.android.data;

import java.util.Comparator;

/**
 * Created by rufuszhu on 15-09-07.
 */
public class RateItemPriceComparator implements Comparator<RateItem> {
    @Override
    public int compare(RateItem lhs, RateItem rhs) {
        try {
            double lPrice = lhs.getEstimate();
            double rPrice = rhs.getEstimate();
            if (lPrice > rPrice)
                return 1;
            else if (lPrice == rPrice)
                return 0;
            else
                return -1;
        }catch (Exception e){
            return 0;
        }
    }
}
