package at.stefanirndorfer.popularmovies.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TrailerQueryResponse {

    @SerializedName("id")
    private Integer id;
    @SerializedName("results")
    private List<TrailerData> trailerData;

    public TrailerQueryResponse(Integer id, List<TrailerData> trailerData) {
        this.id = id;
        this.trailerData = trailerData;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<TrailerData> getTrailerData() {
        return trailerData;
    }

    public void setTrailerData(List<TrailerData> trailerData) {
        this.trailerData = trailerData;
    }


}
