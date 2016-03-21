package lmdelivery.longmen.com.android.data;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by rufus on 2015-11-18.
 */
@Table(name = "User")
public class User extends Model {
    @SerializedName("id")
    @Expose
    @Column(name = "remote_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public long id;
    @Column(name = "Phone")
    @Expose
    public String phone;
    @Column(name = "Email")
    @Expose
    public String email;
    @Column(name = "FirstName")
    @Expose
    public String firstName;
    @Column(name = "LastName")
    @Expose
    public String lastName;
    @Expose
    @Column(name = "Address")
    public Address address;

    public User() {
        super();
    }
}
