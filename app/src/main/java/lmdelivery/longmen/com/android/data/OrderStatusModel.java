package lmdelivery.longmen.com.android.data;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by rufus on 2015-09-22.
 */
public class OrderStatusModel implements Serializable{
    @Expose
    String status;
    @Expose
    String statusDescription;
    @Expose
    String handler;
    @Expose
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
