package com.fmo.jmcat1121;

public class GridItem {
    private String imageUrl;
    private String title;
    private String year;
    private int id;
    private String genres;
    private String overview;

    public GridItem(String imageUrl, String title, String year, int id, String genres, String overview) {
        this.imageUrl = imageUrl;
        this.title = title;
        this.year = year;
        this.id = id;
        this.genres = genres;
        this.overview = overview;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getId() {
        return id;
    }

    public String getGenres() {
        return genres;
    }

    public String getOverview() {
        return overview;
    }

    public String getTitle() {
        return title;
    }

    public String getYear() {
        return year;
    }
}
