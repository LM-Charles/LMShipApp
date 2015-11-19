package lmdelivery.longmen.com.android.data;

import android.text.TextUtils;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import lmdelivery.longmen.com.android.util.CountryCode;

/**
 * Created by rzhu on 6/22/2015.
 */
@Table(name = "Addresses")
public class Address extends Model implements Serializable {
    @Column(name = "FullAddress")
    private String fullAddress;
    @Column(name = "StreetName")
    @SerializedName("address")
    private String streetName;
    @Column(name = "UnitNumber")
    @SerializedName("address2")
    private String unitNumber;
    @Column(name = "PostalCode")
    @SerializedName("postal")
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
        String result = "";
        if(!TextUtils.isEmpty(name))
            result += name + "\n";
        if(!TextUtils.isEmpty(name))
            result += phone + "\n";

        String countryName = !TextUtils.isEmpty(CountryCode.getCountryName(country))?CountryCode.getCountryName(country):country;

        if(countryName.equals("China") || countryName.equals("中国") || countryName.equals("CN")){
            result += streetName;
            if(!unitNumber.isEmpty()){
                result += " " + unitNumber + "\n";
            }
            result += city + ", " + province + ", " + postalCode + "\n" + countryName;
        }else{
            if(!unitNumber.isEmpty()){
                result += unitNumber+"-";
            }
            result += streetName + "\n" + city + ", " + province + ", " + postalCode + "\n" + countryName;
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
