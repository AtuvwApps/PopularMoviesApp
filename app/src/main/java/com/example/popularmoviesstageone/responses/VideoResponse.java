package com.example.popularmoviesstageone.responses;

import com.example.popularmoviesstageone.model.Video;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class VideoResponse {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("results")
    @Expose
    private List<Video> results = null;

    public VideoResponse(){}

    public VideoResponse(String id, List<Video> results){
        this.id = id;
        this.results = results;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Video> getResults() {
        return results;
    }

    public void setResults(List<Video> results) {
        this.results = results;
    }
}
