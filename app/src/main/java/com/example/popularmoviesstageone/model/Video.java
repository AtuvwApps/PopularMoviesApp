package com.example.popularmoviesstageone.model;

public class Video {

    private String key;
    private String type;

    public Video(){};

    public Video(String key, String type){
        this.key = key;
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
