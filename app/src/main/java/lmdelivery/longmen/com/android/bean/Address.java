package lmdelivery.longmen.com.android.bean;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.Serializable;

/**
 * Created by rzhu on 6/22/2015.
 */
@Table(name = "Addresses")
public class Address extends Model implements Serializable {
    @Column(name = "FullAddress")
    private String fullAddress;
    @Column(name = "StreetName")
    private String streetName;
    @Column(name = "UnitNumber")
    private String unitNumber;
    @Column(name = "PostalCode")
    private String postalCode;
    @Column(name = "City")
    private String city;
    @Column(name = "Country")
    private String country;
    @Column(name = "Province")
    private String province;
    @Column(name = "Name")
    private String name;
    @Column(name = "Phone")
    private String phone;

    public Address() {
        name="";
        phone="";
        fullAddress="";
        streetName="";

        unitNumber="";
        postalCode="";
        city="";
        country="";
        province="";
    }

    public String buildFullAddress(){
        String result = name + "\n" + phone + "\n";
        if(country.equals("China") || country.equals("中国")){

            result += streetName;
            if(!unitNumber.isEmpty()){
                result += " " + unitNumber + "\n";
            }
            result += city + ", " + province + ", " + postalCode + "\n" + country;
        }else{
            if(!unitNumber.isEmpty()){
                result += unitNumber+"-";
            }
            result += streetName + "\n" + city + ", " + province + ", " + postalCode + "\n" + country;
        }

        return result;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }


    public String getUnitNumber() {
        return unitNumber;
    }

    public void setUnitNumber(String unitNumber) {
        this.unitNumber = unitNumber;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }


}
