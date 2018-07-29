package com.example.muhammadhamza.mykarachi;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by MuhammadHamza on 27/06/2018.
 */

public class PlaceInfo {

    private String name;
    private String phoneNumber;
    private String Id;
    private String address;
    private Uri websiteUri;
    private LatLng latlng;
    private float rating;
    private String attributions;

    public PlaceInfo(String name, String phoneNumber, String id, String address, Uri websiteUri, LatLng latlng, float rating, String attributions) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        Id = id;
        this.address = address;
        this.websiteUri = websiteUri;
        this.latlng = latlng;
        this.rating = rating;
        this.attributions = attributions;
    }

    public PlaceInfo() {

    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getId() {
        return Id;
    }

    public String getAddress() {
        return address;
    }

    public Uri getWebsiteUri() {
        return websiteUri;
    }

    public LatLng getLatlng() {
        return latlng;
    }

    public float getRating() {
        return rating;
    }

    public String getAttributions() {
        return attributions;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setId(String id) {
        Id = id;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setWebsiteUri(Uri websiteUri) {
        this.websiteUri = websiteUri;
    }

    public void setLatlng(LatLng latlng) {
        this.latlng = latlng;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setAttributions(String attributions) {
        this.attributions = attributions;
    }

    @Override
    public String toString() {
        return "PlaceInfo{" +
                "name='" + name + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", Id='" + Id + '\'' +
                ", address='" + address + '\'' +
                ", websiteUri=" + websiteUri +
                ", latlng=" + latlng +
                ", rating=" + rating +
                ", attributions='" + attributions + '\'' +
                '}';
    }
}
