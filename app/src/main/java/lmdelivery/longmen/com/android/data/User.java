package lmdelivery.longmen.com.android.data;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by rufus on 2015-11-18.
 */
@Table(name = "User")
public class User extends Model {
    @Column(name = "remote_id", unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public long id;
    @Column(name = "Phone")
    public String phone;
    @Column(name = "Email")
    public String email;
    @Column(name = "FirstName")
    public String firstName;
    @Column(name = "LastName")
    public String lastName;
    @Column(name = "Address")
    public Address address;

    public User() {
        super();
    }
}
