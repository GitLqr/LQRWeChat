package com.lqr.wechat.model.data;


import java.io.Serializable;

public class LocationData implements Serializable {
    private double lat;
    private double lng;
    private String poi;
    private String imgUrl;

    public LocationData(double lat, double lng, String poi, String imgUrl) {
        this.lat = lat;
        this.lng = lng;
        this.poi = poi;
        this.imgUrl = imgUrl;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getPoi() {
        return poi;
    }

    public void setPoi(String poi) {
        this.poi = poi;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
