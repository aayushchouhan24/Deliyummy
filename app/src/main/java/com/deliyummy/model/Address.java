package com.deliyummy.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Address {

    
    
    
    @SerializedName("address_name")
    @Expose
    private String address_name;

    public String getAddress_name() {
        return address_name;
    }

    public void setAddress_name(String add_name) {
        this.address_name = add_name;
    }



    @SerializedName("user_name")
    @Expose
    private String user_name;

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }




    @SerializedName("currentLatitude")
    @Expose
    private String currentLatitude;

    public String getCurrentLatitude() {
        return currentLatitude;
    }

    public void setCurrentLatitude(String currentLatitude) {
        this.currentLatitude = currentLatitude;
    }



    

    @SerializedName("currentLongitude")
    @Expose
    private String currentLongitude;

    public String getCurrentLongitude() {
        return currentLongitude;
    }

    public void setCurrentLongitude(String currentLongitude) {
        this.currentLongitude = currentLongitude;
    }




    @SerializedName("landmark")
    @Expose
    private String landmark;

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }




    @SerializedName("phone")
    @Expose
    private String phone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }




    @SerializedName("address")
    @Expose
    private String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }





    @SerializedName("pincode")
    @Expose
    private String pincode;

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }




    @SerializedName("city")
    @Expose
    private String city;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

}
