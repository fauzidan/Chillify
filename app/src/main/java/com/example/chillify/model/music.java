package com.example.chillify.model;

public class music {

    String id, judul, band, link, img_url;

    public music(String id, String judul, String band, String link, String img_url) {
        this.id = id;
        this.judul = judul;
        this.band = band;
        this.link = link;
        this.img_url = img_url;
    }

    public music(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getBand() {
        return band;
    }

    public void setBand(String band) {
        this.band = band;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }
}
