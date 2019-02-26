package hu.ait.android.geonote.data;

import com.google.android.gms.maps.model.LatLng;


public class Post {

    private String uid;
    private String author;
    private String title;
    private String body;
    private Double Lat;
    private Double Lon;
    private String date;

    private String imageUrl;

    public Post() {
    }


    public Post(String uid, String author, String title, String body, Double Lat, Double Lon, String date) {
        this.uid = uid;
        this.author = author;
        this.title = title;
        this.body = body;
        this.Lat = Lat;
        this.Lon = Lon;
        this.date = date;


        //this.location = location;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Double getLat() {
        return Lat;
    }

    public void setLat(Double lat) {
        Lat = lat;
    }

    public Double getLon() {
        return Lon;
    }

    public void setLon(Double lon) {
        Lon = lon;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

