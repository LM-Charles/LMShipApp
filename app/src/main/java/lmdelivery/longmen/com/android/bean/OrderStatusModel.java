package lmdelivery.longmen.com.android.bean;

import java.io.Serializable;

/**
 * Created by rufus on 2015-09-22.
 */
public class OrderStatusModel implements Serializable{
    String status;
    String statusDescription;
    String handler;
    String statusDate;

    public String getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(String statusDate) {
        this.statusDate = statusDate;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
