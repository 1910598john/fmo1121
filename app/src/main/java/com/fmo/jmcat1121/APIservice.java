package com.fmo.jmcat1121;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIservice {

    @GET("tv/{id}")
    Call<ResponseBody> fetchSeriesSeasons(
            @Path("id") int tvId,
            @Query("language") String lang,
            @Query("api_key") String apiKey
    );

    @GET("genre/movie/list")
    Call<ResponseBody> fetchMoviesGenres(
            @Query("language") String lang,
            @Query("api_key") String apiKey
    );

    @GET("genre/tv/list")
    Call<ResponseBody> fetchSeriesGenres(
            @Query("language") String lang,
            @Query("api_key") String apiKey
    );

    @GET("search/movie")
    Call<ResponseBody> searchMovie(
            @Query("query") String query,
            @Query("api_key") String apiKey
    );
    @GET("search/tv")
    Call<ResponseBody> searchSeries(
            @Query("query") String query,
            @Query("api_key") String apiKey
    );
    @GET("discover/movie")
    Call<ResponseBody> getMovies(
            @Query("sort_by") String sort,
            @Query("page") int page,
            @Query("api_key") String apiKey
    );

    @GET("trending/tv/day")
    Call<ResponseBody> getSeries(
            @Query("language") String lang,
            @Query("page") int page,
            @Query("api_key") String apiKey
    );

    @GET("discover/movie")
    Call<ResponseBody> getActionMovies(
            @Query("with_genres") int genreId,
            @Query("with_original_language") String origlang,
            @Query("include_adult") boolean includeAdult,
            @Query("page") int page,
            @Query("api_key") String apiKey
    );

    @GET("discover/tv")
    Call<ResponseBody> getActionSeries(
            @Query("with_genres") String genreId,
            @Query("include_adult") boolean includeAdult,
            @Query("page") int page,
            @Query("api_key") String apiKey
    );

    @GET("discover/tv")
    Call<ResponseBody> getAnimatedSeries(
            @Query("with_genres") String genreId,
            @Query("include_adult") boolean includeAdult,
            @Query("page") int page,
            @Query("api_key") String apiKey
    );

    @GET("discover/movie")
    Call<ResponseBody> getAnimeMovies(
            @Query("with_genres") String genreId,
            @Query("with_original_language") String origlang,
            @Query("include_adult") boolean includeAdult,
            @Query("sort_by") String sort,
            @Query("release_date.lte") String release,
            @Query("page") int page,
            @Query("api_key") String apiKey
    );

    @GET("discover/tv")
    Call<ResponseBody> getAnimeSeries(
            @Query("with_genres") String genreId,
            @Query("with_original_language") String origlang,
            @Query("sort_by") String sort,
            @Query("page") int page,
            @Query("api_key") String apiKey
    );

    @GET("discover/movie")
    Call<ResponseBody> getCMovies(
            @Query("with_genres") String genreId,
            @Query("with_original_language") String origlang,
            @Query("sort_by") String sort,
            @Query("page") int page,
            @Query("api_key") String apiKey
    );

    @GET("discover/tv")
    Call<ResponseBody> getCSeries(
            @Query("with_genres") String genreId,
            @Query("with_original_language") String origlang,
            @Query("sort_by") String sort,
            @Query("page") int page,
            @Query("api_key") String apiKey
    );

    @GET("discover/movie")
    Call<ResponseBody> getFMovies(
            @Query("with_genres") String genreId,
            @Query("release_date.lte") String sort,
            @Query("language") String lang,
            @Query("page") int page,
            @Query("api_key") String apiKey
    );

    @GET("discover/tv")
    Call<ResponseBody> getFSeries(
            @Query("with_genres") String genreId,
            @Query("sort_by") String sort,
            @Query("language") String lang,
            @Query("page") int page,
            @Query("api_key") String apiKey
    );

    @GET("discover/movie")
    Call<ResponseBody> getGHMovies(
            @Query("with_companies") String genreId,
            @Query("page") int page,
            @Query("api_key") String apiKey
    );

    @GET("discover/movie")
    Call<ResponseBody> getFFMovies(
            @Query("with_original_language") String origlang,
            @Query("language") String lang,
            @Query("page") int page,
            @Query("api_key") String apiKey
    );

    @GET("discover/tv")
    Call<ResponseBody> getFFSeries(
            @Query("with_original_language") String origlang,
            @Query("language") String lang,
            @Query("page") int page,
            @Query("api_key") String apiKey
    );

    @GET("discover/movie")
    Call<ResponseBody> getHorrorMovies(
            @Query("with_genres") String genreId,
            @Query("sort_by") String sort,
            @Query("page") int page,
            @Query("api_key") String apiKey
    );

    @GET("discover/tv")
    Call<ResponseBody> getHorrorSeries(
            @Query("with_genres") String genreId,
            @Query("page") int page,
            @Query("api_key") String apiKey
    );

    @GET("discover/movie")
    Call<ResponseBody> getKDMovies(
            @Query("with_genres") String genreId,
            @Query("with_original_language") String origlang,
            @Query("sort_by") String sort,
            @Query("page") int page,
            @Query("api_key") String apiKey
    );

    @GET("discover/tv")
    Call<ResponseBody> getKDSeries(
            @Query("with_genres") String genreId,
            @Query("with_original_language") String origlang,
            @Query("sort_by") String sort,
            @Query("page") int page,
            @Query("api_key") String apiKey
    );
    @GET("discover/movie")
    Call<ResponseBody> getSciMovies(
            @Query("with_genres") String genreId,
            @Query("sort_by") String sort,
            @Query("page") int page,
            @Query("api_key") String apiKey
    );

    @GET("discover/tv")
    Call<ResponseBody> getSciSeries(
            @Query("with_genres") String genreId,
            @Query("sort_by") String sort,
            @Query("page") int page,
            @Query("api_key") String apiKey
    );
    @GET("discover/movie")
    Call<ResponseBody> getThriMovies(
            @Query("with_genres") String genreId,
            @Query("sort_by") String sort,
            @Query("release_date.lte") String sort2,
            @Query("page") int page,
            @Query("api_key") String apiKey
    );

    @GET("discover/tv")
    Call<ResponseBody> getThriSeries(
            @Query("with_genres") String genreId,
            @Query("sort_by") String sort,
            @Query("page") int page,
            @Query("api_key") String apiKey
    );
    @GET("discover/movie")
    Call<ResponseBody> getWarMovies(
            @Query("with_genres") String genreId,
            @Query("sort_by") String sort,
            @Query("release_date.lte") String sort2,
            @Query("page") int page,
            @Query("api_key") String apiKey
    );

    @GET("discover/tv")
    Call<ResponseBody> getWarSeries(
            @Query("with_genres") String genreId,
            @Query("sort_by") String sort,
            @Query("page") int page,
            @Query("api_key") String apiKey
    );
}
