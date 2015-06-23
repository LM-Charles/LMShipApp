package lmdelivery.longmen.com.android.UIFragments.bean;

/**
 * Created by rzhu on 6/22/2015.
 */
public class MyAddress {
    private String fullAddress;
    private String streetName;
    private String unitNumber;
    private String postalCode;
    private String city;
    private String country;
    private String province;


    public MyAddress() {
        fullAddress="";
        streetName="";
        unitNumber="";
        postalCode="";
        city="";
        country="";
        province="";
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
