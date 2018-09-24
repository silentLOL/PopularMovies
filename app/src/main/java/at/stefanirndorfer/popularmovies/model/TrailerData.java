package at.stefanirndorfer.popularmovies.model;

import com.google.gson.annotations.SerializedName;

public class TrailerData {

    @SerializedName("id")
    private String id;

    @SerializedName("iso_639_1")
    private String iso639_1;

    @SerializedName("iso_3166_1")
    private String iso3166_1;

    @SerializedName("key")
    private String key;

    @SerializedName("name")
    private String name;

    @SerializedName("site")
    private String site;

    @SerializedName("size")
    private Integer size;

    @SerializedName("type")
    private String type;

    public TrailerData(String id, String iso639_1, String iso3166_1, String key, String name, String site, Integer size, String type) {
        this.id = id;
        this.iso639_1 = iso639_1;
        this.iso3166_1 = iso3166_1;
        this.key = key;
        this.name = name;
        this.site = site;
        this.size = size;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIso639_1() {
        return iso639_1;
    }

    public void setIso639_1(String iso639_1) {
        this.iso639_1 = iso639_1;
    }

    public String getIso3166_1() {
        return iso3166_1;
    }

    public void setIso3166_1(String iso3166_1) {
        this.iso3166_1 = iso3166_1;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
