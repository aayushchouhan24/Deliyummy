package com.deliyummy.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Cart {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("imageUrl")
    @Expose
    private String imageUrl;
    @SerializedName("number")
    @Expose
    private String number;
    @SerializedName("deliveryTime")
    @Expose
    private String deliveryTime;
    @SerializedName("deliveryCharges")
    @Expose
    private String deliveryCharges;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("note")
    @Expose
    private String note;
    @SerializedName("info")
    @Expose
    private String info;

    public String getInfo() {
        return info;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getDeliveryCharges() {
        return deliveryCharges;
    }

    public void setDeliveryCharges(String deliveryCharges) {
        this.deliveryCharges = deliveryCharges;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }


}
