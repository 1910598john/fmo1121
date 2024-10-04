package com.fmo.jmcat1121;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Home extends Fragment implements OnGridReplaceListener{
    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    private RecyclerView recyclerView;
    private GridAdapter gridAdapter;
    private List<GridItem> gridItemList;
    private GridLayoutManager layoutManager;
    private BottomNavigationView bottomNavigationView;

    private int currentPage = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private boolean searching = false;


    HashMap<Integer, String> idSet;
    HashMap<Integer, String> idSet2;

    private String currentType;

    TextInputLayout textInputLayout;

    Button button2;
    Button button1;
    SearchView searchView;

    RelativeLayout relativeLayout;
    ProgressBar pbar;



    // Implementation of the required method
    @Override
    public void onGridReplace(String contentType) {
        // Clear the current grid data
        gridItemList.clear();
        gridAdapter.notifyDataSetChanged();

        if (relativeLayout != null) {
            relativeLayout.setVisibility(View.VISIBLE);
        }


        currentPage = 1;

        if (contentType.contains("searchmovies")) {
            String result = contentType.trim().replaceFirst("^searchmovies", "");
            searchMovies(result);
            searching = true;
        } else if (contentType.contains("searchseries")) {
            String result = contentType.trim().replaceFirst("^searchseries", "");
            searchSeries(result);
            searching = true;
        } else {
            searching = false;
        }

        // Fetch new data based on contentType
        if ("actionmovies".equals(contentType)) {
            fetchActionMovies(1); // Fetch movies
            currentType = contentType;
        } else if ("actionseries".equals(contentType)) {
            fetchActionSeries(1); // Fetch series
            currentType = contentType;
        } else if ("animatedseries".equals(contentType)) {
            fetchAnimatedSeries(1); // Fetch series
            currentType = contentType;
        } else if ("animemovies".equals(contentType)) {
            fetchAnimeMovies(1); // Fetch series
            currentType = contentType;
        } else if ("animeseries".equals(contentType)) {
            fetchAnimeSeries(1); // Fetch series
            currentType = contentType;
        } else if ("cdramamovies".equals(contentType)) {
            fetchCMovies(1); // Fetch series
            currentType = contentType;
        } else if ("cdramaseries".equals(contentType)) {
            fetchCSeries(1); // Fetch series
            currentType = contentType;
        } else if ("ghmovies".equals(contentType)) {
            fetchGHMovies(1); // Fetch series
            currentType = contentType;
        } else if ("fmovies".equals(contentType)) {
            fetchFMovies(1); // Fetch series
            currentType = contentType;
        } else if ("fseries".equals(contentType)) {
            fetchFSeries(1); // Fetch series
            currentType = contentType;
        } else if ("ffmovies".equals(contentType)) {
            fetchFFMovies(1); // Fetch series
            currentType = contentType;
        } else if ("ffseries".equals(contentType)) {
            fetchFFSeries(1); // Fetch series
            currentType = contentType;
        } else if ("horrormovies".equals(contentType)) {
            fetchHorrorMovies(1); // Fetch series
            currentType = contentType;
        } else if ("horrorseries".equals(contentType)) {
            fetchHorrorSeries(1); // Fetch series
            currentType = contentType;
        } else if ("kdramamovies".equals(contentType)) {
            fetchKDMovies(1); // Fetch series
            currentType = contentType;
        } else if ("kdramaseries".equals(contentType)) {
            fetchKDSeries(1); // Fetch series
            currentType = contentType;
        } else if ("scifimovies".equals(contentType)) {
            fetchSciMovies(1); // Fetch series
            currentType = contentType;
        } else if ("scifiseries".equals(contentType)) {
            fetchSciSeries(1); // Fetch series
            currentType = contentType;
        } else if ("thmovies".equals(contentType)) {
            fetchThriMovies(1); // Fetch series
            currentType = contentType;
        } else if ("thseries".equals(contentType)) {
            fetchThriSeries(1); // Fetch series
            currentType = contentType;
        } else if ("warmovies".equals(contentType)) {
            fetchWarMovies(1); // Fetch series
            currentType = contentType;
        } else if ("warseries".equals(contentType)) {
            fetchWarSeries(1); // Fetch series
            currentType = contentType;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        button1 = getActivity().findViewById(R.id.movies);
        button2 = getActivity().findViewById(R.id.series);
        searchView = getActivity().findViewById(R.id.search);
        textInputLayout = getActivity().findViewById(R.id.dropdown);

        relativeLayout = view.findViewById(R.id.loading_view);
        pbar = view.findViewById(R.id.progressBar);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIservice apiService = retrofit.create(APIservice.class);

        Call<ResponseBody> call = apiService.fetchMoviesGenres("en", "3739740296a7d5e9a4eb8ef5586b32c2");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {

                    try {
                        String res = response.body().string();
                        JSONObject json = new JSONObject(res);
                        JSONArray arr = json.getJSONArray("genres");

                        idSet = new HashMap<>();
                        for (int j = 0; j < arr.length(); j++) {
                            JSONObject jsonObject = arr.getJSONObject(j);  // Get the JSONObject at index j
                            int id = jsonObject.getInt("id");  // Extract the "id" from the JSONObject
                            String name = jsonObject.getString("name");
                            idSet.put(id, name);
                        }

                    } catch (IOException | JSONException e) {
                        throw new RuntimeException(e);
                    }


                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

        Call<ResponseBody> call2 = apiService.fetchSeriesGenres("en", "3739740296a7d5e9a4eb8ef5586b32c2");
        call2.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {

                    try {
                        String res = response.body().string();
                        JSONObject json = new JSONObject(res);
                        JSONArray arr = json.getJSONArray("genres");

                        idSet2 = new HashMap<>();
                        for (int j = 0; j < arr.length(); j++) {
                            JSONObject jsonObject = arr.getJSONObject(j);  // Get the JSONObject at index j
                            int id = jsonObject.getInt("id");  // Extract the "id" from the JSONObject
                            String name = jsonObject.getString("name");
                            idSet2.put(id, name);
                        }

                    } catch (IOException | JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

        recyclerView = view.findViewById(R.id.recyclerView);

        // Initialize data list and adapter
        gridItemList = new ArrayList<>();
        gridAdapter = new GridAdapter(getContext(), gridItemList);

        // Set GridLayoutManager with 3 columns
        layoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(gridAdapter);

        gridAdapter.setOnItemClickListener(new GridAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(GridItem item) {
                FragmentManager fragmentManager = getParentFragmentManager();  // If inside a Fragment
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment watchFragment = new Watch();
                fragmentTransaction.replace(R.id.fragmentContainer, watchFragment);
                fragmentTransaction.commit();

                textInputLayout.setEnabled(false);
                button2.setEnabled(false);
                button1.setEnabled(false);
                searchView.setVisibility(View.GONE);
                saveRowData(item);
            }
        });

        // Add scroll listener for infinite scrolling
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading && !isLastPage && !searching) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0) {
                        currentPage++;
                        load();
                    }
                }
            }
        });

        // Fetch the first page of movies
        if (getActivity() instanceof HomeActivity) {
            String g = ((HomeActivity) getActivity()).getCurrentGenre();
            String t = ((HomeActivity) getActivity()).getCurrentType();
            currentType = g + t;

            if (g.equals("gh")) {
                currentType = g + "movies";
            } else if (g.equals("animated")) {
                currentType = g + "series";
            }
        } else {
            currentType = "actionmovies";
        }

        load();
        return view;
    }

    public void load(){
        if ("actionmovies".equals(currentType)) {
            fetchActionMovies(currentPage);
        } else if ("actionseries".equals(currentType)) {
            fetchActionSeries(currentPage); // Fetch series
        } else if ("animatedseries".equals(currentType)) {
            fetchAnimatedSeries(currentPage); // Fetch series
        } else if ("animemovies".equals(currentType)) {
            fetchAnimeMovies(currentPage); // Fetch series
        } else if ("animeseries".equals(currentType)) {
            fetchAnimeSeries(currentPage); // Fetch series
        } else if ("cdramamovies".equals(currentType)) {
            fetchCMovies(currentPage); // Fetch series
        } else if ("cdramaseries".equals(currentType)) {
            fetchCSeries(currentPage); // Fetch series
        } else if ("ghmovies".equals(currentType)) {
            fetchGHMovies(currentPage); // Fetch series
        } else if ("fmovies".equals(currentType)) {
            fetchFMovies(currentPage); // Fetch series
        } else if ("fseries".equals(currentType)) {
            fetchFSeries(currentPage); // Fetch series
        } else if ("ffmovies".equals(currentType)) {
            fetchFFMovies(currentPage); // Fetch series
        } else if ("ffseries".equals(currentType)) {
            fetchFFSeries(currentPage); // Fetch series
        } else if ("horrormovies".equals(currentType)) {
            fetchHorrorMovies(currentPage); // Fetch series
        } else if ("horrorseries".equals(currentType)) {
            fetchHorrorSeries(currentPage); // Fetch series
        } else if ("kdramamovies".equals(currentType)) {
            fetchKDMovies(currentPage); // Fetch series
        } else if ("kdramaseries".equals(currentType)) {
            fetchKDSeries(currentPage); // Fetch series
        } else if ("scifimovies".equals(currentType)) {
            fetchSciMovies(currentPage); // Fetch series
        } else if ("scifiseries".equals(currentType)) {
            fetchSciSeries(currentPage); // Fetch series
        } else if ("thmovies".equals(currentType)) {
            fetchThriMovies(currentPage); // Fetch series
        } else if ("thseries".equals(currentType)) {
            fetchThriSeries(currentPage); // Fetch series
        } else if ("warmovies".equals(currentType)) {
            fetchWarMovies(currentPage); // Fetch series
        } else if ("warseries".equals(currentType)) {
            fetchWarSeries(currentPage); // Fetch series
        }
    }

    private void saveRowData(GridItem item) {
        // Example of saving data to SharedPreferences (you can adjust this)
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("fmo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("imageUrl", item.getImageUrl());
        editor.putString("title", item.getTitle());
        editor.putString("year", item.getYear());
        editor.putString("genres", item.getGenres());
        editor.putString("id", String.valueOf(item.getId()));
        editor.putString("overview", item.getOverview());
        if (currentType.contains("series")) {
            editor.putString("type", "series");
        } else {
            editor.putString("type", "movies");
        }

        editor.apply();
    }

    private void fetchActionMovies(int page) {
        isLoading = true;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIservice apiService = retrofit.create(APIservice.class);

        Call<ResponseBody> call = apiService.getActionMovies(28, "",false, page, "3739740296a7d5e9a4eb8ef5586b32c2");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String res = response.body().string();
                        JSONObject json = new JSONObject(res);
                        JSONArray arr = json.getJSONArray("results");

                        // Parse the results and add to the list
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject data = arr.getJSONObject(i);
                            String title = data.getString("title");
                            String path = data.getString("poster_path");
                            String releaseDate = data.getString("release_date");
                            int id = Integer.parseInt(data.getString("id"));
                            String genres = data.getString("genre_ids");
                            String overview = data.getString("overview");

                            try {
                                JSONArray g = new JSONArray(genres);
                                String txt = "";
                                for (int x = 0; x < g.length(); x++) {
                                    int id2 = g.getInt(x);
                                    if (idSet != null && idSet.containsKey(id2)) {
                                        String name = idSet.get(id2);
                                        txt += name + ", ";
                                    }
                                }

                                if (txt.endsWith(", ")) {
                                    txt = txt.substring(0, txt.length() - 2); // Remove last 2 characters
                                }

                                genres = txt;

                            } catch (JSONException e) {
                                genres = "Not available";
                            }

                            // Extract year from releaseDate
                            String year = releaseDate.split("-")[0];

                            int currentYear = LocalDate.now().getYear();

                            if (!year.isEmpty()) {
                                int year2 = Integer.parseInt(year);
                                int len = path.length();
                                if (year2 <= currentYear && len > 5) {
                                    gridItemList.add(new GridItem("https://image.tmdb.org/t/p/w400" + path, title, year, id, genres, overview));
                                }
                            }
                        }

                        if (relativeLayout != null) {
                            relativeLayout.setVisibility(View.GONE);
                        }


                        // Notify adapter to refresh the data
                        gridAdapter.notifyDataSetChanged();

                        isLoading = false;

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        isLoading = false; // Reset the loading flag on error
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle failure
                t.printStackTrace();
                isLoading = false; // Reset the loading flag on failure
            }
        });
    }

    private void fetchActionSeries(int page) {
        isLoading = true;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIservice apiService = retrofit.create(APIservice.class);

        Call<ResponseBody> call = apiService.getActionSeries("10759, 18", false,page, "3739740296a7d5e9a4eb8ef5586b32c2");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {

                    try {
                        String res = response.body().string();
                        JSONObject json = new JSONObject(res);
                        JSONArray arr = json.getJSONArray("results");

                        // Parse the results and add to the list
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject data = arr.getJSONObject(i);
                            String title = data.getString("name");
                            String path = data.getString("poster_path");
                            String releaseDate = data.getString("first_air_date");
                            int id = Integer.parseInt(data.getString("id"));
                            String genres = data.getString("genre_ids");
                            String overview = data.getString("overview");

                            try {
                                JSONArray g = new JSONArray(genres);
                                String txt = "";
                                for (int x = 0; x < g.length(); x++) {
                                    int id2 = g.getInt(x);
                                    if (idSet2 != null && idSet2.containsKey(id2)) {
                                        String name = idSet2.get(id2);
                                        txt += name + ", ";
                                    }
                                }

                                if (txt.endsWith(", ")) {
                                    txt = txt.substring(0, txt.length() - 2); // Remove last 2 characters
                                }

                                genres = txt;

                            } catch (JSONException e) {
                                genres = "Not available";
                            }

                            // Extract year from releaseDate
                            String year = releaseDate.split("-")[0];

                            int currentYear = LocalDate.now().getYear();

                            if (!year.isEmpty()) {
                                int year2 = Integer.parseInt(year);
                                int len = path.length();
                                if (year2 <= currentYear && len > 5) {
                                    gridItemList.add(new GridItem("https://image.tmdb.org/t/p/w400" + path, title, year, id, genres, overview));
                                }
                            }

                        }
                        relativeLayout.setVisibility(View.GONE);
                        // Notify adapter to refresh the data
                        gridAdapter.notifyDataSetChanged();

                        isLoading = false;

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        isLoading = false; // Reset the loading flag on error
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle failure
                t.printStackTrace();
                isLoading = false; // Reset the loading flag on failure
            }
        });
    }

    private void fetchAnimatedSeries(int page) {
        isLoading = true;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIservice apiService = retrofit.create(APIservice.class);

        Call<ResponseBody> call = apiService.getAnimatedSeries("16", false,page, "3739740296a7d5e9a4eb8ef5586b32c2");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {

                    try {
                        String res = response.body().string();
                        JSONObject json = new JSONObject(res);
                        JSONArray arr = json.getJSONArray("results");

                        // Parse the results and add to the list
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject data = arr.getJSONObject(i);
                            String title = data.getString("name");
                            String path = data.getString("poster_path");
                            String releaseDate = data.getString("first_air_date");
                            int id = Integer.parseInt(data.getString("id"));
                            String genres = data.getString("genre_ids");
                            String overview = data.getString("overview");

                            try {
                                JSONArray g = new JSONArray(genres);
                                String txt = "";
                                for (int x = 0; x < g.length(); x++) {
                                    int id2 = g.getInt(x);
                                    if (idSet2 != null && idSet2.containsKey(id2)) {
                                        String name = idSet2.get(id2);
                                        txt += name + ", ";
                                    }
                                }

                                if (txt.endsWith(", ")) {
                                    txt = txt.substring(0, txt.length() - 2); // Remove last 2 characters
                                }

                                genres = txt;

                            } catch (JSONException e) {
                                genres = "Not available";
                            }
                            String year = releaseDate.split("-")[0];

                            int currentYear = LocalDate.now().getYear();

                            if (!year.isEmpty()) {
                                int year2 = Integer.parseInt(year);
                                int len = path.length();
                                if (year2 <= currentYear && len > 5) {
                                    gridItemList.add(new GridItem("https://image.tmdb.org/t/p/w400" + path, title, year, id, genres, overview));
                                }
                            }


                        }
                        relativeLayout.setVisibility(View.GONE);
                        // Notify adapter to refresh the data
                        gridAdapter.notifyDataSetChanged();

                        isLoading = false;

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        isLoading = false; // Reset the loading flag on error
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle failure
                t.printStackTrace();
                isLoading = false; // Reset the loading flag on failure
            }
        });
    }

    private void fetchAnimeMovies(int page) {
        isLoading = true;
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().getTime());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIservice apiService = retrofit.create(APIservice.class);

        Call<ResponseBody> call = apiService.getAnimeMovies("16", "ja", false, "release_date.desc", today, page, "3739740296a7d5e9a4eb8ef5586b32c2");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {

                    try {
                        String res = response.body().string();
                        JSONObject json = new JSONObject(res);
                        JSONArray arr = json.getJSONArray("results");

                        // Parse the results and add to the list
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject data = arr.getJSONObject(i);
                            String title = data.getString("title");
                            String path = data.getString("poster_path");
                            String releaseDate = data.getString("release_date");
                            int id = Integer.parseInt(data.getString("id"));
                            String genres = data.getString("genre_ids");
                            String overview = data.getString("overview");

                            try {
                                JSONArray g = new JSONArray(genres);
                                String txt = "";
                                for (int x = 0; x < g.length(); x++) {
                                    int id2 = g.getInt(x);
                                    if (idSet != null && idSet.containsKey(id2)) {
                                        String name = idSet.get(id2);
                                        txt += name + ", ";
                                    }
                                }

                                if (txt.endsWith(", ")) {
                                    txt = txt.substring(0, txt.length() - 2); // Remove last 2 characters
                                }

                                genres = txt;

                            } catch (JSONException e) {
                                genres = "Not available";
                            }

                            // Extract year from releaseDate
                            String year = releaseDate.split("-")[0];

                            int currentYear = LocalDate.now().getYear();

                            if (!year.isEmpty()) {
                                int year2 = Integer.parseInt(year);
                                int len = path.length();
                                if (year2 <= currentYear && len > 5) {
                                    gridItemList.add(new GridItem("https://image.tmdb.org/t/p/w400" + path, title, year, id, genres, overview));
                                }
                            }

                        }
                        relativeLayout.setVisibility(View.GONE);
                        // Notify adapter to refresh the data
                        gridAdapter.notifyDataSetChanged();

                        isLoading = false;

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        isLoading = false; // Reset the loading flag on error
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle failure
                t.printStackTrace();
                isLoading = false; // Reset the loading flag on failure
            }
        });
    }

    private void fetchAnimeSeries(int page) {
        isLoading = true;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIservice apiService = retrofit.create(APIservice.class);

        Call<ResponseBody> call = apiService.getAnimeSeries("16", "ja","first_air_date.desc", page, "3739740296a7d5e9a4eb8ef5586b32c2");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {

                    try {
                        String res = response.body().string();
                        JSONObject json = new JSONObject(res);
                        JSONArray arr = json.getJSONArray("results");

                        // Parse the results and add to the list
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject data = arr.getJSONObject(i);
                            String title = data.getString("name");
                            String path = data.getString("poster_path");
                            String releaseDate = data.getString("first_air_date");
                            int id = Integer.parseInt(data.getString("id"));
                            String genres = data.getString("genre_ids");
                            String overview = data.getString("overview");

                            try {
                                JSONArray g = new JSONArray(genres);
                                String txt = "";
                                for (int x = 0; x < g.length(); x++) {
                                    int id2 = g.getInt(x);
                                    if (idSet2 != null && idSet2.containsKey(id2)) {
                                        String name = idSet2.get(id2);
                                        txt += name + ", ";
                                    }
                                }

                                if (txt.endsWith(", ")) {
                                    txt = txt.substring(0, txt.length() - 2); // Remove last 2 characters
                                }

                                genres = txt;

                            } catch (JSONException e) {
                                genres = "Not available";
                            }

                            // Extract year from releaseDate
                            String year = releaseDate.split("-")[0];

                            int currentYear = LocalDate.now().getYear();

                            if (!year.isEmpty()) {
                                int year2 = Integer.parseInt(year);
                                int len = path.length();
                                if (year2 <= currentYear && len > 5) {
                                    gridItemList.add(new GridItem("https://image.tmdb.org/t/p/w400" + path, title, year, id, genres, overview));
                                }
                            }
                        }
                        relativeLayout.setVisibility(View.GONE);
                        // Notify adapter to refresh the data
                        gridAdapter.notifyDataSetChanged();

                        isLoading = false;

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        isLoading = false; // Reset the loading flag on error
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle failure
                t.printStackTrace();
                isLoading = false; // Reset the loading flag on failure
            }
        });
    }

    private void fetchCMovies(int page) {
        isLoading = true;
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().getTime());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIservice apiService = retrofit.create(APIservice.class);

        Call<ResponseBody> call = apiService.getCMovies("28", "zh", "release_date.desc", page, "3739740296a7d5e9a4eb8ef5586b32c2");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {

                    try {
                        String res = response.body().string();
                        JSONObject json = new JSONObject(res);
                        JSONArray arr = json.getJSONArray("results");

                        // Parse the results and add to the list
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject data = arr.getJSONObject(i);
                            String title = data.getString("title");
                            String path = data.getString("poster_path");
                            String releaseDate = data.getString("release_date");
                            int id = Integer.parseInt(data.getString("id"));
                            String genres = data.getString("genre_ids");
                            String overview = data.getString("overview");

                            try {
                                JSONArray g = new JSONArray(genres);
                                String txt = "";
                                for (int x = 0; x < g.length(); x++) {
                                    int id2 = g.getInt(x);
                                    if (idSet != null && idSet.containsKey(id2)) {
                                        String name = idSet.get(id2);
                                        txt += name + ", ";
                                    }
                                }

                                if (txt.endsWith(", ")) {
                                    txt = txt.substring(0, txt.length() - 2); // Remove last 2 characters
                                }

                                genres = txt;

                            } catch (JSONException e) {
                                genres = "Not available";
                            }

                            // Extract year from releaseDate
                            String year = releaseDate.split("-")[0];

                            int currentYear = LocalDate.now().getYear();

                            if (!year.isEmpty()) {
                                int year2 = Integer.parseInt(year);
                                int len = path.length();
                                if (year2 <= currentYear && len > 5) {
                                    gridItemList.add(new GridItem("https://image.tmdb.org/t/p/w400" + path, title, year, id, genres, overview));
                                }
                            }

                        }
                        relativeLayout.setVisibility(View.GONE);
                        // Notify adapter to refresh the data
                        gridAdapter.notifyDataSetChanged();

                        isLoading = false;

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        isLoading = false; // Reset the loading flag on error
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle failure
                t.printStackTrace();
                isLoading = false; // Reset the loading flag on failure
            }
        });
    }

    private void fetchCSeries(int page) {
        isLoading = true;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIservice apiService = retrofit.create(APIservice.class);

        Call<ResponseBody> call = apiService.getCSeries("18", "zh","first_air_date.desc", page, "3739740296a7d5e9a4eb8ef5586b32c2");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {

                    try {
                        String res = response.body().string();
                        JSONObject json = new JSONObject(res);
                        JSONArray arr = json.getJSONArray("results");

                        // Parse the results and add to the list
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject data = arr.getJSONObject(i);
                            String title = data.getString("name");
                            String path = data.getString("poster_path");
                            String releaseDate = data.getString("first_air_date");
                            int id = Integer.parseInt(data.getString("id"));
                            String genres = data.getString("genre_ids");
                            String overview = data.getString("overview");

                            try {
                                JSONArray g = new JSONArray(genres);
                                String txt = "";
                                for (int x = 0; x < g.length(); x++) {
                                    int id2 = g.getInt(x);
                                    if (idSet2 != null && idSet2.containsKey(id2)) {
                                        String name = idSet2.get(id2);
                                        txt += name + ", ";
                                    }
                                }

                                if (txt.endsWith(", ")) {
                                    txt = txt.substring(0, txt.length() - 2); // Remove last 2 characters
                                }

                                genres = txt;

                            } catch (JSONException e) {
                                genres = "Not available";
                            }

                            // Extract year from releaseDate
                            String year = releaseDate.split("-")[0];

                            int currentYear = LocalDate.now().getYear();

                            if (!year.isEmpty()) {
                                int year2 = Integer.parseInt(year);
                                int len = path.length();
                                if (year2 <= currentYear && len > 5) {
                                    gridItemList.add(new GridItem("https://image.tmdb.org/t/p/w400" + path, title, year, id, genres, overview));
                                }
                            }
                        }
                        relativeLayout.setVisibility(View.GONE);
                        // Notify adapter to refresh the data
                        gridAdapter.notifyDataSetChanged();

                        isLoading = false;

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        isLoading = false; // Reset the loading flag on error
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle failure
                t.printStackTrace();
                isLoading = false; // Reset the loading flag on failure
            }
        });
    }

    private void fetchFMovies(int page) {
        isLoading = true;
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().getTime());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIservice apiService = retrofit.create(APIservice.class);

        Call<ResponseBody> call = apiService.getFMovies("14", today, "en", page, "3739740296a7d5e9a4eb8ef5586b32c2");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {

                    try {
                        String res = response.body().string();
                        JSONObject json = new JSONObject(res);
                        JSONArray arr = json.getJSONArray("results");

                        // Parse the results and add to the list
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject data = arr.getJSONObject(i);
                            String title = data.getString("title");
                            String path = data.getString("poster_path");
                            String releaseDate = data.getString("release_date");
                            int id = Integer.parseInt(data.getString("id"));
                            String genres = data.getString("genre_ids");
                            String overview = data.getString("overview");

                            try {
                                JSONArray g = new JSONArray(genres);
                                String txt = "";
                                for (int x = 0; x < g.length(); x++) {
                                    int id2 = g.getInt(x);
                                    if (idSet != null && idSet.containsKey(id2)) {
                                        String name = idSet.get(id2);
                                        txt += name + ", ";
                                    }
                                }

                                if (txt.endsWith(", ")) {
                                    txt = txt.substring(0, txt.length() - 2); // Remove last 2 characters
                                }

                                genres = txt;

                            } catch (JSONException e) {
                                genres = "Not available";
                            }

                            // Extract year from releaseDate
                            String year = releaseDate.split("-")[0];

                            int currentYear = LocalDate.now().getYear();

                            if (!year.isEmpty()) {
                                int year2 = Integer.parseInt(year);
                                int len = path.length();
                                if (year2 <= currentYear && len > 5) {
                                    gridItemList.add(new GridItem("https://image.tmdb.org/t/p/w400" + path, title, year, id, genres, overview));
                                }
                            }

                        }
                        relativeLayout.setVisibility(View.GONE);
                        // Notify adapter to refresh the data
                        gridAdapter.notifyDataSetChanged();

                        isLoading = false;

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        isLoading = false; // Reset the loading flag on error
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle failure
                t.printStackTrace();
                isLoading = false; // Reset the loading flag on failure
            }
        });
    }

    private void fetchFSeries(int page) {
        isLoading = true;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIservice apiService = retrofit.create(APIservice.class);

        Call<ResponseBody> call = apiService.getFSeries("10765,18", "first_air_date.desc","en", page, "3739740296a7d5e9a4eb8ef5586b32c2");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {

                    try {
                        String res = response.body().string();
                        JSONObject json = new JSONObject(res);
                        JSONArray arr = json.getJSONArray("results");

                        // Parse the results and add to the list
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject data = arr.getJSONObject(i);
                            String title = data.getString("name");
                            String path = data.getString("poster_path");
                            String releaseDate = data.getString("first_air_date");
                            int id = Integer.parseInt(data.getString("id"));
                            String genres = data.getString("genre_ids");
                            String overview = data.getString("overview");

                            try {
                                JSONArray g = new JSONArray(genres);
                                String txt = "";
                                for (int x = 0; x < g.length(); x++) {
                                    int id2 = g.getInt(x);
                                    if (idSet2 != null && idSet2.containsKey(id2)) {
                                        String name = idSet2.get(id2);
                                        txt += name + ", ";
                                    }
                                }

                                if (txt.endsWith(", ")) {
                                    txt = txt.substring(0, txt.length() - 2); // Remove last 2 characters
                                }

                                genres = txt;

                            } catch (JSONException e) {
                                genres = "Not available";
                            }

                            // Extract year from releaseDate
                            String year = releaseDate.split("-")[0];

                            int currentYear = LocalDate.now().getYear();

                            if (!year.isEmpty()) {
                                int year2 = Integer.parseInt(year);
                                int len = path.length();
                                if (year2 <= currentYear && len > 5) {
                                    gridItemList.add(new GridItem("https://image.tmdb.org/t/p/w400" + path, title, year, id, genres, overview));
                                }
                            }
                        }
                        relativeLayout.setVisibility(View.GONE);
                        // Notify adapter to refresh the data
                        gridAdapter.notifyDataSetChanged();

                        isLoading = false;

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        isLoading = false; // Reset the loading flag on error
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle failure
                t.printStackTrace();
                isLoading = false; // Reset the loading flag on failure
            }
        });
    }

    private void fetchGHMovies(int page) {
        isLoading = true;
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().getTime());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIservice apiService = retrofit.create(APIservice.class);

        Call<ResponseBody> call = apiService.getGHMovies("10342", page, "3739740296a7d5e9a4eb8ef5586b32c2");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {

                    try {
                        String res = response.body().string();
                        JSONObject json = new JSONObject(res);
                        JSONArray arr = json.getJSONArray("results");

                        // Parse the results and add to the list
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject data = arr.getJSONObject(i);
                            String title = data.getString("title");
                            String path = data.getString("poster_path");
                            String releaseDate = data.getString("release_date");
                            int id = Integer.parseInt(data.getString("id"));
                            String genres = data.getString("genre_ids");
                            String overview = data.getString("overview");

                            try {
                                JSONArray g = new JSONArray(genres);
                                String txt = "";
                                for (int x = 0; x < g.length(); x++) {
                                    int id2 = g.getInt(x);
                                    if (idSet != null && idSet.containsKey(id2)) {
                                        String name = idSet.get(id2);
                                        txt += name + ", ";
                                    }
                                }

                                if (txt.endsWith(", ")) {
                                    txt = txt.substring(0, txt.length() - 2); // Remove last 2 characters
                                }

                                genres = txt;

                            } catch (JSONException e) {
                                genres = "Not available";
                            }

                            // Extract year from releaseDate
                            String year = releaseDate.split("-")[0];

                            int currentYear = LocalDate.now().getYear();

                            if (!year.isEmpty()) {
                                int year2 = Integer.parseInt(year);
                                int len = path.length();
                                if (year2 <= currentYear && len > 5) {
                                    gridItemList.add(new GridItem("https://image.tmdb.org/t/p/w400" + path, title, year, id, genres, overview));
                                }
                            }

                        }
                        relativeLayout.setVisibility(View.GONE);
                        // Notify adapter to refresh the data
                        gridAdapter.notifyDataSetChanged();

                        isLoading = false;

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        isLoading = false; // Reset the loading flag on error
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle failure
                t.printStackTrace();
                isLoading = false; // Reset the loading flag on failure
            }
        });
    }
    private void fetchFFMovies(int page) {
        isLoading = true;
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().getTime());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIservice apiService = retrofit.create(APIservice.class);

        Call<ResponseBody> call = apiService.getFFMovies("tl", "en", page, "3739740296a7d5e9a4eb8ef5586b32c2");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {

                    try {
                        String res = response.body().string();
                        JSONObject json = new JSONObject(res);
                        JSONArray arr = json.getJSONArray("results");

                        // Parse the results and add to the list
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject data = arr.getJSONObject(i);
                            String title = data.getString("title");
                            String path = data.getString("poster_path");
                            String releaseDate = data.getString("release_date");
                            int id = Integer.parseInt(data.getString("id"));
                            String genres = data.getString("genre_ids");
                            String overview = data.getString("overview");

                            try {
                                JSONArray g = new JSONArray(genres);
                                String txt = "";
                                for (int x = 0; x < g.length(); x++) {
                                    int id2 = g.getInt(x);
                                    if (idSet != null && idSet.containsKey(id2)) {
                                        String name = idSet.get(id2);
                                        txt += name + ", ";
                                    }
                                }

                                if (txt.endsWith(", ")) {
                                    txt = txt.substring(0, txt.length() - 2); // Remove last 2 characters
                                }

                                genres = txt;

                            } catch (JSONException e) {
                                genres = "Not available";
                            }

                            // Extract year from releaseDate
                            String year = releaseDate.split("-")[0];

                            int currentYear = LocalDate.now().getYear();

                            if (!year.isEmpty()) {
                                int year2 = Integer.parseInt(year);
                                int len = path.length();
                                if (year2 <= currentYear && len > 5) {
                                    gridItemList.add(new GridItem("https://image.tmdb.org/t/p/w400" + path, title, year, id, genres, overview));
                                }
                            }

                        }
                        relativeLayout.setVisibility(View.GONE);
                        // Notify adapter to refresh the data
                        gridAdapter.notifyDataSetChanged();

                        isLoading = false;

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        isLoading = false; // Reset the loading flag on error
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle failure
                t.printStackTrace();
                isLoading = false; // Reset the loading flag on failure
            }
        });
    }

    private void fetchFFSeries(int page) {
        isLoading = true;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIservice apiService = retrofit.create(APIservice.class);

        Call<ResponseBody> call = apiService.getFFSeries("tl", "en", page, "3739740296a7d5e9a4eb8ef5586b32c2");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {

                    try {
                        String res = response.body().string();
                        JSONObject json = new JSONObject(res);
                        JSONArray arr = json.getJSONArray("results");

                        // Parse the results and add to the list
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject data = arr.getJSONObject(i);
                            String title = data.getString("name");
                            String path = data.getString("poster_path");
                            String releaseDate = data.getString("first_air_date");
                            int id = Integer.parseInt(data.getString("id"));
                            String genres = data.getString("genre_ids");
                            String overview = data.getString("overview");

                            try {
                                JSONArray g = new JSONArray(genres);
                                String txt = "";
                                for (int x = 0; x < g.length(); x++) {
                                    int id2 = g.getInt(x);
                                    if (idSet2 != null && idSet2.containsKey(id2)) {
                                        String name = idSet2.get(id2);
                                        txt += name + ", ";
                                    }
                                }

                                if (txt.endsWith(", ")) {
                                    txt = txt.substring(0, txt.length() - 2); // Remove last 2 characters
                                }

                                genres = txt;

                            } catch (JSONException e) {
                                genres = "Not available";
                            }

                            // Extract year from releaseDate
                            String year = releaseDate.split("-")[0];

                            int currentYear = LocalDate.now().getYear();

                            if (!year.isEmpty()) {
                                int year2 = Integer.parseInt(year);
                                int len = path.length();
                                if (year2 <= currentYear && len > 5) {
                                    gridItemList.add(new GridItem("https://image.tmdb.org/t/p/w400" + path, title, year, id, genres, overview));
                                }
                            }
                        }
                        relativeLayout.setVisibility(View.GONE);
                        // Notify adapter to refresh the data
                        gridAdapter.notifyDataSetChanged();

                        isLoading = false;

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        isLoading = false; // Reset the loading flag on error
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle failure
                t.printStackTrace();
                isLoading = false; // Reset the loading flag on failure
            }
        });
    }
    private void fetchHorrorMovies(int page) {
        isLoading = true;
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().getTime());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIservice apiService = retrofit.create(APIservice.class);

        Call<ResponseBody> call = apiService.getHorrorMovies("27", "popularity.desc", page, "3739740296a7d5e9a4eb8ef5586b32c2");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {

                    try {
                        String res = response.body().string();
                        JSONObject json = new JSONObject(res);
                        JSONArray arr = json.getJSONArray("results");

                        // Parse the results and add to the list
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject data = arr.getJSONObject(i);
                            String title = data.getString("title");
                            String path = data.getString("poster_path");
                            String releaseDate = data.getString("release_date");
                            int id = Integer.parseInt(data.getString("id"));
                            String genres = data.getString("genre_ids");
                            String overview = data.getString("overview");

                            try {
                                JSONArray g = new JSONArray(genres);
                                String txt = "";
                                for (int x = 0; x < g.length(); x++) {
                                    int id2 = g.getInt(x);
                                    if (idSet != null && idSet.containsKey(id2)) {
                                        String name = idSet.get(id2);
                                        txt += name + ", ";
                                    }
                                }

                                if (txt.endsWith(", ")) {
                                    txt = txt.substring(0, txt.length() - 2); // Remove last 2 characters
                                }

                                genres = txt;

                            } catch (JSONException e) {
                                genres = "Not available";
                            }

                            // Extract year from releaseDate
                            String year = releaseDate.split("-")[0];

                            int currentYear = LocalDate.now().getYear();

                            if (!year.isEmpty()) {
                                int year2 = Integer.parseInt(year);
                                int len = path.length();
                                if (year2 <= currentYear && len > 5) {
                                    gridItemList.add(new GridItem("https://image.tmdb.org/t/p/w400" + path, title, year, id, genres, overview));
                                }
                            }

                        }
                        relativeLayout.setVisibility(View.GONE);
                        // Notify adapter to refresh the data
                        gridAdapter.notifyDataSetChanged();

                        isLoading = false;

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        isLoading = false; // Reset the loading flag on error
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle failure
                t.printStackTrace();
                isLoading = false; // Reset the loading flag on failure
            }
        });
    }

    private void fetchHorrorSeries(int page) {
        isLoading = true;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIservice apiService = retrofit.create(APIservice.class);

        Call<ResponseBody> call = apiService.getHorrorSeries("9648,10765",  page, "3739740296a7d5e9a4eb8ef5586b32c2");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {

                    try {
                        String res = response.body().string();
                        JSONObject json = new JSONObject(res);
                        JSONArray arr = json.getJSONArray("results");

                        // Parse the results and add to the list
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject data = arr.getJSONObject(i);
                            String title = data.getString("name");
                            String path = data.getString("poster_path");
                            String releaseDate = data.getString("first_air_date");
                            int id = Integer.parseInt(data.getString("id"));
                            String genres = data.getString("genre_ids");
                            String overview = data.getString("overview");

                            try {
                                JSONArray g = new JSONArray(genres);
                                String txt = "";
                                for (int x = 0; x < g.length(); x++) {
                                    int id2 = g.getInt(x);
                                    if (idSet2 != null && idSet2.containsKey(id2)) {
                                        String name = idSet2.get(id2);
                                        txt += name + ", ";
                                    }
                                }

                                if (txt.endsWith(", ")) {
                                    txt = txt.substring(0, txt.length() - 2); // Remove last 2 characters
                                }

                                genres = txt;

                            } catch (JSONException e) {
                                genres = "Not available";
                            }

                            // Extract year from releaseDate
                            String year = releaseDate.split("-")[0];

                            int currentYear = LocalDate.now().getYear();

                            if (!year.isEmpty()) {
                                int year2 = Integer.parseInt(year);
                                int len = path.length();
                                if (year2 <= currentYear && len > 5) {
                                    gridItemList.add(new GridItem("https://image.tmdb.org/t/p/w400" + path, title, year, id, genres, overview));
                                }
                            }
                        }
                        relativeLayout.setVisibility(View.GONE);
                        // Notify adapter to refresh the data
                        gridAdapter.notifyDataSetChanged();

                        isLoading = false;

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        isLoading = false; // Reset the loading flag on error
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle failure
                t.printStackTrace();
                isLoading = false; // Reset the loading flag on failure
            }
        });
    }

    private void fetchKDMovies(int page) {
        isLoading = true;
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().getTime());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIservice apiService = retrofit.create(APIservice.class);

        Call<ResponseBody> call = apiService.getKDMovies("28", "ko", "release_date.desc", page,  "3739740296a7d5e9a4eb8ef5586b32c2");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {

                    try {
                        String res = response.body().string();
                        JSONObject json = new JSONObject(res);
                        JSONArray arr = json.getJSONArray("results");

                        // Parse the results and add to the list
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject data = arr.getJSONObject(i);
                            String title = data.getString("title");
                            String path = data.getString("poster_path");
                            String releaseDate = data.getString("release_date");
                            int id = Integer.parseInt(data.getString("id"));
                            String genres = data.getString("genre_ids");
                            String overview = data.getString("overview");

                            try {
                                JSONArray g = new JSONArray(genres);
                                String txt = "";
                                for (int x = 0; x < g.length(); x++) {
                                    int id2 = g.getInt(x);
                                    if (idSet != null && idSet.containsKey(id2)) {
                                        String name = idSet.get(id2);
                                        txt += name + ", ";
                                    }
                                }

                                if (txt.endsWith(", ")) {
                                    txt = txt.substring(0, txt.length() - 2); // Remove last 2 characters
                                }

                                genres = txt;

                            } catch (JSONException e) {
                                genres = "Not available";
                            }

                            // Extract year from releaseDate
                            String year = releaseDate.split("-")[0];

                            int currentYear = LocalDate.now().getYear();

                            if (!year.isEmpty()) {
                                int year2 = Integer.parseInt(year);
                                int len = path.length();
                                if (year2 <= currentYear && len > 5) {
                                    gridItemList.add(new GridItem("https://image.tmdb.org/t/p/w400" + path, title, year, id, genres, overview));
                                }
                            }

                        }
                        relativeLayout.setVisibility(View.GONE);
                        // Notify adapter to refresh the data
                        gridAdapter.notifyDataSetChanged();

                        isLoading = false;

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        isLoading = false; // Reset the loading flag on error
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle failure
                t.printStackTrace();
                isLoading = false; // Reset the loading flag on failure
            }
        });
    }

    private void fetchKDSeries(int page) {
        isLoading = true;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIservice apiService = retrofit.create(APIservice.class);

        Call<ResponseBody> call = apiService.getKDSeries("18", "ko", "first_air_date.desc", page,"3739740296a7d5e9a4eb8ef5586b32c2");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {

                    try {
                        String res = response.body().string();
                        JSONObject json = new JSONObject(res);
                        JSONArray arr = json.getJSONArray("results");

                        // Parse the results and add to the list
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject data = arr.getJSONObject(i);
                            String title = data.getString("name");
                            String path = data.getString("poster_path");
                            String releaseDate = data.getString("first_air_date");
                            int id = Integer.parseInt(data.getString("id"));
                            String genres = data.getString("genre_ids");
                            String overview = data.getString("overview");

                            try {
                                JSONArray g = new JSONArray(genres);
                                String txt = "";
                                for (int x = 0; x < g.length(); x++) {
                                    int id2 = g.getInt(x);
                                    if (idSet2 != null && idSet2.containsKey(id2)) {
                                        String name = idSet2.get(id2);
                                        txt += name + ", ";
                                    }
                                }

                                if (txt.endsWith(", ")) {
                                    txt = txt.substring(0, txt.length() - 2); // Remove last 2 characters
                                }

                                genres = txt;

                            } catch (JSONException e) {
                                genres = "Not available";
                            }

                            // Extract year from releaseDate
                            String year = releaseDate.split("-")[0];

                            int currentYear = LocalDate.now().getYear();

                            if (!year.isEmpty()) {
                                int year2 = Integer.parseInt(year);
                                int len = path.length();
                                if (year2 <= currentYear && len > 5) {
                                    gridItemList.add(new GridItem("https://image.tmdb.org/t/p/w400" + path, title, year, id, genres, overview));
                                }
                            }
                        }
                        relativeLayout.setVisibility(View.GONE);
                        // Notify adapter to refresh the data
                        gridAdapter.notifyDataSetChanged();

                        isLoading = false;

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        isLoading = false; // Reset the loading flag on error
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle failure
                t.printStackTrace();
                isLoading = false; // Reset the loading flag on failure
            }
        });
    }
    private void fetchSciMovies(int page) {
        isLoading = true;
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().getTime());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIservice apiService = retrofit.create(APIservice.class);

        Call<ResponseBody> call = apiService.getSciMovies("878", "popularity.desc",  page,  "3739740296a7d5e9a4eb8ef5586b32c2");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {

                    try {
                        String res = response.body().string();
                        JSONObject json = new JSONObject(res);
                        JSONArray arr = json.getJSONArray("results");

                        // Parse the results and add to the list
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject data = arr.getJSONObject(i);
                            String title = data.getString("title");
                            String path = data.getString("poster_path");
                            String releaseDate = data.getString("release_date");
                            int id = Integer.parseInt(data.getString("id"));
                            String genres = data.getString("genre_ids");
                            String overview = data.getString("overview");

                            try {
                                JSONArray g = new JSONArray(genres);
                                String txt = "";
                                for (int x = 0; x < g.length(); x++) {
                                    int id2 = g.getInt(x);
                                    if (idSet != null && idSet.containsKey(id2)) {
                                        String name = idSet.get(id2);
                                        txt += name + ", ";
                                    }
                                }

                                if (txt.endsWith(", ")) {
                                    txt = txt.substring(0, txt.length() - 2); // Remove last 2 characters
                                }

                                genres = txt;

                            } catch (JSONException e) {
                                genres = "Not available";
                            }
                            // Extract year from releaseDate
                            String year = releaseDate.split("-")[0];

                            int currentYear = LocalDate.now().getYear();

                            if (!year.isEmpty()) {
                                int year2 = Integer.parseInt(year);
                                int len = path.length();
                                if (year2 <= currentYear && len > 5) {
                                    gridItemList.add(new GridItem("https://image.tmdb.org/t/p/w400" + path, title, year, id, genres, overview));
                                }
                            }

                        }
                        relativeLayout.setVisibility(View.GONE);
                        // Notify adapter to refresh the data
                        gridAdapter.notifyDataSetChanged();

                        isLoading = false;

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        isLoading = false; // Reset the loading flag on error
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle failure
                t.printStackTrace();
                isLoading = false; // Reset the loading flag on failure
            }
        });
    }

    private void fetchSciSeries(int page) {
        isLoading = true;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIservice apiService = retrofit.create(APIservice.class);

        Call<ResponseBody> call = apiService.getSciSeries("10765", "first_air_date.desc",  page,"3739740296a7d5e9a4eb8ef5586b32c2");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {

                    try {
                        String res = response.body().string();
                        JSONObject json = new JSONObject(res);
                        JSONArray arr = json.getJSONArray("results");

                        // Parse the results and add to the list
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject data = arr.getJSONObject(i);
                            String title = data.getString("name");
                            String path = data.getString("poster_path");
                            String releaseDate = data.getString("first_air_date");
                            int id = Integer.parseInt(data.getString("id"));
                            String genres = data.getString("genre_ids");
                            String overview = data.getString("overview");

                            try {
                                JSONArray g = new JSONArray(genres);
                                String txt = "";
                                for (int x = 0; x < g.length(); x++) {
                                    int id2 = g.getInt(x);
                                    if (idSet2 != null && idSet2.containsKey(id2)) {
                                        String name = idSet2.get(id2);
                                        txt += name + ", ";
                                    }
                                }

                                if (txt.endsWith(", ")) {
                                    txt = txt.substring(0, txt.length() - 2); // Remove last 2 characters
                                }

                                genres = txt;

                            } catch (JSONException e) {
                                genres = "Not available";
                            }

                            // Extract year from releaseDate
                            String year = releaseDate.split("-")[0];

                            int currentYear = LocalDate.now().getYear();

                            if (!year.isEmpty()) {
                                int year2 = Integer.parseInt(year);
                                int len = path.length();
                                if (year2 <= currentYear && len > 5) {
                                    gridItemList.add(new GridItem("https://image.tmdb.org/t/p/w400" + path, title, year, id, genres, overview));
                                }
                            }
                        }
                        relativeLayout.setVisibility(View.GONE);
                        // Notify adapter to refresh the data
                        gridAdapter.notifyDataSetChanged();

                        isLoading = false;

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        isLoading = false; // Reset the loading flag on error
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle failure
                t.printStackTrace();
                isLoading = false; // Reset the loading flag on failure
            }
        });
    }
    private void fetchThriMovies(int page) {
        isLoading = true;
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().getTime());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIservice apiService = retrofit.create(APIservice.class);

        Call<ResponseBody> call = apiService.getThriMovies("53", "release_date.desc", today,  page,  "3739740296a7d5e9a4eb8ef5586b32c2");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {

                    try {
                        String res = response.body().string();
                        JSONObject json = new JSONObject(res);
                        JSONArray arr = json.getJSONArray("results");

                        // Parse the results and add to the list
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject data = arr.getJSONObject(i);
                            String title = data.getString("title");
                            String path = data.getString("poster_path");
                            String releaseDate = data.getString("release_date");
                            int id = Integer.parseInt(data.getString("id"));
                            String genres = data.getString("genre_ids");
                            String overview = data.getString("overview");

                            try {
                                JSONArray g = new JSONArray(genres);
                                String txt = "";
                                for (int x = 0; x < g.length(); x++) {
                                    int id2 = g.getInt(x);
                                    if (idSet != null && idSet.containsKey(id2)) {
                                        String name = idSet.get(id2);
                                        txt += name + ", ";
                                    }
                                }

                                if (txt.endsWith(", ")) {
                                    txt = txt.substring(0, txt.length() - 2); // Remove last 2 characters
                                }

                                genres = txt;

                            } catch (JSONException e) {
                                genres = "Not available";
                            }

                            // Extract year from releaseDate
                            String year = releaseDate.split("-")[0];

                            int currentYear = LocalDate.now().getYear();

                            if (!year.isEmpty()) {
                                int year2 = Integer.parseInt(year);
                                int len = path.length();
                                if (year2 <= currentYear && len > 5) {
                                    gridItemList.add(new GridItem("https://image.tmdb.org/t/p/w400" + path, title, year, id, genres, overview));
                                }
                            }

                        }
                        relativeLayout.setVisibility(View.GONE);
                        // Notify adapter to refresh the data
                        gridAdapter.notifyDataSetChanged();

                        isLoading = false;

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        isLoading = false; // Reset the loading flag on error
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle failure
                t.printStackTrace();
                isLoading = false; // Reset the loading flag on failure
            }
        });
    }

    private void fetchThriSeries(int page) {
        isLoading = true;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIservice apiService = retrofit.create(APIservice.class);

        Call<ResponseBody> call = apiService.getThriSeries("9648", "popularity.desc",  page,"3739740296a7d5e9a4eb8ef5586b32c2");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {

                    try {
                        String res = response.body().string();
                        JSONObject json = new JSONObject(res);
                        JSONArray arr = json.getJSONArray("results");

                        // Parse the results and add to the list
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject data = arr.getJSONObject(i);
                            String title = data.getString("name");
                            String path = data.getString("poster_path");
                            String releaseDate = data.getString("first_air_date");
                            int id = Integer.parseInt(data.getString("id"));
                            String genres = data.getString("genre_ids");
                            String overview = data.getString("overview");

                            try {
                                JSONArray g = new JSONArray(genres);
                                String txt = "";
                                for (int x = 0; x < g.length(); x++) {
                                    int id2 = g.getInt(x);
                                    if (idSet2 != null && idSet2.containsKey(id2)) {
                                        String name = idSet2.get(id2);
                                        txt += name + ", ";
                                    }
                                }

                                if (txt.endsWith(", ")) {
                                    txt = txt.substring(0, txt.length() - 2); // Remove last 2 characters
                                }

                                genres = txt;

                            } catch (JSONException e) {
                                genres = "Not available";
                            }

                            // Extract year from releaseDate
                            String year = releaseDate.split("-")[0];

                            int currentYear = LocalDate.now().getYear();

                            if (!year.isEmpty()) {
                                int year2 = Integer.parseInt(year);
                                int len = path.length();
                                if (year2 <= currentYear && len > 5) {
                                    gridItemList.add(new GridItem("https://image.tmdb.org/t/p/w400" + path, title, year, id, genres, overview));
                                }
                            }
                        }
                        relativeLayout.setVisibility(View.GONE);
                        // Notify adapter to refresh the data
                        gridAdapter.notifyDataSetChanged();

                        isLoading = false;

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        isLoading = false; // Reset the loading flag on error
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle failure
                t.printStackTrace();
                isLoading = false; // Reset the loading flag on failure
            }
        });
    }

    private void fetchWarMovies(int page) {
        isLoading = true;
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().getTime());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIservice apiService = retrofit.create(APIservice.class);

        Call<ResponseBody> call = apiService.getWarMovies("10752, 28", "release_date.desc", today,  page,  "3739740296a7d5e9a4eb8ef5586b32c2");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {

                    try {
                        String res = response.body().string();
                        JSONObject json = new JSONObject(res);
                        JSONArray arr = json.getJSONArray("results");

                        // Parse the results and add to the list
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject data = arr.getJSONObject(i);
                            String title = data.getString("title");
                            String path = data.getString("poster_path");
                            String releaseDate = data.getString("release_date");
                            int id = Integer.parseInt(data.getString("id"));
                            String genres = data.getString("genre_ids");
                            String overview = data.getString("overview");

                            try {
                                JSONArray g = new JSONArray(genres);
                                String txt = "";
                                for (int x = 0; x < g.length(); x++) {
                                    int id2 = g.getInt(x);
                                    if (idSet != null && idSet.containsKey(id2)) {
                                        String name = idSet.get(id2);
                                        txt += name + ", ";
                                    }
                                }

                                if (txt.endsWith(", ")) {
                                    txt = txt.substring(0, txt.length() - 2); // Remove last 2 characters
                                }

                                genres = txt;

                            } catch (JSONException e) {
                                genres = "Not available";
                            }

                            // Extract year from releaseDate
                            String year = releaseDate.split("-")[0];

                            int currentYear = LocalDate.now().getYear();

                            if (!year.isEmpty()) {
                                int year2 = Integer.parseInt(year);
                                int len = path.length();
                                if (year2 <= currentYear && len > 5) {
                                    gridItemList.add(new GridItem("https://image.tmdb.org/t/p/w400" + path, title, year, id, genres, overview));
                                }
                            }

                        }
                        relativeLayout.setVisibility(View.GONE);
                        // Notify adapter to refresh the data
                        gridAdapter.notifyDataSetChanged();

                        isLoading = false;

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        isLoading = false; // Reset the loading flag on error
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle failure
                t.printStackTrace();
                isLoading = false; // Reset the loading flag on failure
            }
        });
    }

    private void fetchWarSeries(int page) {
        isLoading = true;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIservice apiService = retrofit.create(APIservice.class);

        Call<ResponseBody> call = apiService.getWarSeries("10768, 10759", "popularity.desc",  page,"3739740296a7d5e9a4eb8ef5586b32c2");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {

                    try {
                        String res = response.body().string();
                        JSONObject json = new JSONObject(res);
                        JSONArray arr = json.getJSONArray("results");

                        // Parse the results and add to the list
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject data = arr.getJSONObject(i);
                            String title = data.getString("name");
                            String path = data.getString("poster_path");
                            String releaseDate = data.getString("first_air_date");
                            int id = Integer.parseInt(data.getString("id"));
                            String genres = data.getString("genre_ids");
                            String overview = data.getString("overview");

                            try {
                                JSONArray g = new JSONArray(genres);
                                String txt = "";
                                for (int x = 0; x < g.length(); x++) {
                                    int id2 = g.getInt(x);
                                    if (idSet2 != null && idSet2.containsKey(id2)) {
                                        String name = idSet2.get(id2);
                                        txt += name + ", ";
                                    }
                                }

                                if (txt.endsWith(", ")) {
                                    txt = txt.substring(0, txt.length() - 2); // Remove last 2 characters
                                }

                                genres = txt;

                            } catch (JSONException e) {
                                genres = "Not available";
                            }

                            // Extract year from releaseDate
                            String year = releaseDate.split("-")[0];

                            int currentYear = LocalDate.now().getYear();

                            if (!year.isEmpty()) {
                                int year2 = Integer.parseInt(year);
                                int len = path.length();
                                if (year2 <= currentYear && len > 5) {
                                    gridItemList.add(new GridItem("https://image.tmdb.org/t/p/w400" + path, title, year, id, genres, overview));
                                }
                            }
                        }
                        relativeLayout.setVisibility(View.GONE);
                        // Notify adapter to refresh the data
                        gridAdapter.notifyDataSetChanged();

                        isLoading = false;

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        isLoading = false; // Reset the loading flag on error
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle failure
                t.printStackTrace();
                isLoading = false; // Reset the loading flag on failure
            }
        });
    }

    private void searchMovies(String movie) {
        isLoading = true;
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Calendar.getInstance().getTime());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIservice apiService = retrofit.create(APIservice.class);

        Call<ResponseBody> call = apiService.searchMovie(movie,  "3739740296a7d5e9a4eb8ef5586b32c2");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful() && response.body() != null) {

                    try {
                        String res = response.body().string();
                        JSONObject json = new JSONObject(res);
                        JSONArray arr = json.getJSONArray("results");

                        // Parse the results and add to the list
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject data = arr.getJSONObject(i);
                            String title = data.getString("title");
                            String path = data.getString("poster_path");
                            String releaseDate = data.getString("release_date");
                            int id = Integer.parseInt(data.getString("id"));
                            String genres = data.getString("genre_ids");
                            String overview = data.getString("overview");

                            try {
                                JSONArray g = new JSONArray(genres);
                                String txt = "";
                                for (int x = 0; x < g.length(); x++) {
                                    int id2 = g.getInt(x);
                                    if (idSet != null && idSet.containsKey(id2)) {
                                        String name = idSet.get(id2);
                                        txt += name + ", ";
                                    }
                                }

                                if (txt.endsWith(", ")) {
                                    txt = txt.substring(0, txt.length() - 2); // Remove last 2 characters
                                }

                                genres = txt;

                            } catch (JSONException e) {
                                genres = "Not available";
                            }

                            // Extract year from releaseDate
                            String year = releaseDate.split("-")[0];

                            int currentYear = LocalDate.now().getYear();

                            if (!year.isEmpty()) {
                                int year2 = Integer.parseInt(year);
                                int len = path.length();
                                if (year2 <= currentYear && len > 5) {
                                    gridItemList.add(new GridItem("https://image.tmdb.org/t/p/w400" + path, title, year, id, genres, overview));
                                }
                            }

                        }
                        relativeLayout.setVisibility(View.GONE);
                        // Notify adapter to refresh the data
                        gridAdapter.notifyDataSetChanged();

                        isLoading = false;

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        isLoading = false; // Reset the loading flag on error
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle failure
                t.printStackTrace();
                isLoading = false; // Reset the loading flag on failure
            }
        });
    }

    private void searchSeries(String series) {
        isLoading = true;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIservice apiService = retrofit.create(APIservice.class);

        Call<ResponseBody> call = apiService.searchSeries(series,"3739740296a7d5e9a4eb8ef5586b32c2");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful() && response.body() != null) {

                    try {
                        String res = response.body().string();
                        JSONObject json = new JSONObject(res);
                        JSONArray arr = json.getJSONArray("results");

                        // Parse the results and add to the list
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject data = arr.getJSONObject(i);
                            String title = data.getString("name");
                            String path = data.getString("poster_path");
                            String releaseDate = data.getString("first_air_date");
                            int id = Integer.parseInt(data.getString("id"));
                            String genres = data.getString("genre_ids");
                            String overview = data.getString("overview");

                            try {
                                JSONArray g = new JSONArray(genres);
                                String txt = "";
                                for (int x = 0; x < g.length(); x++) {
                                    int id2 = g.getInt(x);
                                    if (idSet2 != null && idSet2.containsKey(id2)) {
                                        String name = idSet2.get(id2);
                                        txt += name + ", ";
                                    }
                                }

                                if (txt.endsWith(", ")) {
                                    txt = txt.substring(0, txt.length() - 2); // Remove last 2 characters
                                }

                                genres = txt;

                            } catch (JSONException e) {
                                genres = "Not available";
                            }

                            // Extract year from releaseDate
                            String year = releaseDate.split("-")[0];

                            int currentYear = LocalDate.now().getYear();

                            if (!year.isEmpty()) {
                                int year2 = Integer.parseInt(year);
                                int len = path.length();
                                if (year2 <= currentYear && len > 5) {
                                    gridItemList.add(new GridItem("https://image.tmdb.org/t/p/w400" + path, title, year, id, genres, overview));
                                }
                            }
                        }
                        relativeLayout.setVisibility(View.GONE);
                        // Notify adapter to refresh the data
                        gridAdapter.notifyDataSetChanged();

                        isLoading = false;

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                        isLoading = false; // Reset the loading flag on error
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle failure
                t.printStackTrace();
                isLoading = false; // Reset the loading flag on failure
            }
        });
    }
}