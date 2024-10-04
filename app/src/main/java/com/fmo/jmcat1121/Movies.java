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

public class Movies extends Fragment implements OnGridReplaceListener {
    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    private RecyclerView recyclerView;
    private GridAdapter gridAdapter;
    private List<GridItem> gridItemList;
    private GridLayoutManager layoutManager;

    private int currentPage = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private boolean searching = false;

    HashMap<Integer, String> idSet;
    HashMap<Integer, String> idSet2;

    TextInputLayout textInputLayout;

    Button button2;
    Button button1;
    SearchView searchView;

    private String currentType = "movies";

    RelativeLayout relativeLayout;
    ProgressBar pbar;


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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        button1 = getActivity().findViewById(R.id.movies);
        button2 = getActivity().findViewById(R.id.series);
        searchView = getActivity().findViewById(R.id.search);
        textInputLayout = getActivity().findViewById(R.id.dropdown);

        recyclerView = view.findViewById(R.id.recyclerView);

        // Initialize data list and adapter
        gridItemList = new ArrayList<>();
        gridAdapter = new GridAdapter(getContext(), gridItemList);

        // Set GridLayoutManager with 3 columns
        layoutManager = new GridLayoutManager(getContext(), 3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(gridAdapter);

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
                        // The user has scrolled to the bottom, load the next page
                        currentPage++;
                        fetchMovies(currentPage);
                    }
                }
            }
        });

        // Fetch the first page of movies
        fetchMovies(currentPage);
        return view;
    }

    @Override
    public void onGridReplace(String contentType) {
        // Clear the current grid data
        gridItemList.clear();
        gridAdapter.notifyDataSetChanged();

        if (relativeLayout != null) {
            relativeLayout.setVisibility(View.VISIBLE);
        }

        if (contentType.contains("searchmovies")) {
            String result = contentType.trim().replaceFirst("^searchmovies", "");
            searchMovies(result);
            searching = true;
        } else {
            searching = false;
        }
    }

    private void fetchMovies(int page) {
        isLoading = true;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIservice apiService = retrofit.create(APIservice.class);

        Call<ResponseBody> call = apiService.getMovies("popularity_desc", page, "3739740296a7d5e9a4eb8ef5586b32c2");
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

                        if (relativeLayout != null) {
                            relativeLayout.setVisibility(View.GONE);
                        }

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