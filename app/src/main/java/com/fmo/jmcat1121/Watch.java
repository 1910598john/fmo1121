package com.fmo.jmcat1121;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Watch extends Fragment implements OnGridReplaceListener{
    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    private RecyclerView recyclerView;
    private GridAdapter gridAdapter;
    private List<GridItem> gridItemList;
    private GridLayoutManager layoutManager;

    private WebView webView;
    private View customView;
    private WebChromeClient.CustomViewCallback customViewCallback;

    private View mCustomView;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;
    protected FrameLayout mFullscreenContainer;
    private int mOriginalSystemUiVisibility;

    TextView watchtextview;
    WebView watchwebview;
    LinearLayout watchlayoutview;
    TextView watchtitleview;
    TextView watchoverview;
    TextView watchgenreview;
    ImageView watchimageview;

    AutoCompleteTextView autoCompleteTextView;

    ArrayAdapter<String> arrayAdapter;


    LinearLayout linearLayout;

    List<String> optionsList2;
    HashMap<String, String> seasonsMap2;
    HashMap<String, String> epMap;

    String s = "1";
    String e = "1";
    String currentID;

    private BottomNavigationView bottomNavigationView;

    private static final int FULL_SCREEN_SETTING = View.SYSTEM_UI_FLAG_FULLSCREEN |
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
            View.SYSTEM_UI_FLAG_IMMERSIVE;

    private void seasonSelect(String id, View view) {
        optionsList2 = new ArrayList<>();
        int ep_count = 0;
        for (Map.Entry<String, String> entry : epMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key.equals(id)) {
                ep_count = Integer.parseInt(value);
                break;
            }
        }

        for (int i = 1; i < ep_count + 1; i++) {
            optionsList2.add(String.format("Episode %s", i));
        }

        AutoCompleteTextView autoCompleteTextView2  = view.findViewById(R.id.autoCompleteTextView3);
        ArrayAdapter<String> arrayAdapter2 = new ArrayAdapter<>(requireContext(), R.layout.list_item, optionsList2);
        autoCompleteTextView2.setAdapter(arrayAdapter2);
        arrayAdapter2.notifyDataSetChanged();

        autoCompleteTextView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view2, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
                String numberString = item.replaceAll("[^0-9]", "");
                e = numberString;
                changed();
            }
        });
    }

    public void changed() {
        String iframeUrl = String.format("<iframe src=\"https://vidsrc.me/embed/tv?tmdb=%s&season=%s&episode=%s\" width=\"100%%\" height=\"100%%\" frameborder=\"0\" allowfullscreen></iframe>", currentID, s, e);
        webView.loadData(iframeUrl, "text/html", "UTF-8");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("fmo", Context.MODE_PRIVATE);
        String id = sharedPreferences.getString("id", "default_id");

        currentID = id;

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (webView != null && webView.canGoBack()) {
                    webView.goBack();
                }
            }
        };

        // Register the callback
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), callback);

        View view = inflater.inflate(R.layout.fragment_watch, container, false);

        bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
        MenuItem watch = bottomNavigationView.getMenu().findItem(R.id.watch);
        watch.setChecked(true);

        HomeActivity activity = (HomeActivity) getActivity();
        if (activity != null) {

            String currentType = sharedPreferences.getString("type", "default_type");

            if (currentType.equals("series")) {
                linearLayout = view.findViewById(R.id.seriesddlayout);
                linearLayout.setVisibility(View.VISIBLE);

                //fetch seasons and episodes..
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                APIservice apiService = retrofit.create(APIservice.class);

                Call<ResponseBody> call = apiService.fetchSeriesSeasons(Integer.parseInt(id), "en-US", "3739740296a7d5e9a4eb8ef5586b32c2");
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            try {
                                String res  = response.body().string();
                                JSONObject json = new JSONObject(res);
                                JSONArray seasons = json.getJSONArray("seasons");

                                List<String> optionsList = new ArrayList<>();


                                epMap = new HashMap<>();

                                seasonsMap2 = new HashMap<>();

                                for (int i = 0; i < seasons.length(); i++) {
                                    JSONObject season = seasons.getJSONObject(i);
                                    String seasonName = season.getString("name");
                                    String ep_count = season.getString("episode_count");
                                    String s_num = season.getString("season_number");
                                    optionsList.add(seasonName);
                                    epMap.put(s_num, ep_count);
                                    seasonsMap2.put(s_num, seasonName);
                                }

                                //show dropdowns..

                                autoCompleteTextView  = view.findViewById(R.id.autoCompleteTextView2);
                                arrayAdapter = new ArrayAdapter<>(requireContext(), R.layout.list_item, optionsList);
                                autoCompleteTextView.setAdapter(arrayAdapter);

                                autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view2, int i, long l) {
                                        String item = adapterView.getItemAtPosition(i).toString();
                                        for (Map.Entry<String, String> entry : seasonsMap2.entrySet()) {
                                            if (entry.getValue().equals(item)) {
                                                String key = entry.getKey();
                                                s = key;

                                                seasonSelect(key, view);
                                                break; // Optional: exit the loop if only one match is expected
                                            }
                                        }
                                    }
                                });

                                if (seasonsMap2 != null && seasonsMap2.containsKey("1")) {
                                    String name = seasonsMap2.get("1");
                                    if (!name.contains("Special")) {
                                        seasonSelect("1", view);
                                    } else {
                                        seasonSelect("2", view);
                                    }
                                }

//                                if (!optionsList.isEmpty()) {
//                                    autoCompleteTextView.setText(optionsList.get(0), false);
//                                }


                            } catch (IOException | JSONException e) {
                                throw new RuntimeException(e);
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
            }
        }


        webView = view.findViewById(R.id.watchwebview);

        watchtextview = view.findViewById(R.id.watchttextview);
        watchwebview = view.findViewById(R.id.watchwebview);
        watchlayoutview = view.findViewById(R.id.watchlayoutview);

        watchtitleview = view.findViewById(R.id.watchtitleview);
        watchgenreview = view.findViewById(R.id.watchgenreview);
        watchoverview = view.findViewById(R.id.watchoverview);
        watchimageview = view.findViewById(R.id.watchimageview);

        // Enable JavaScript
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true); // Enable DOM storage
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false); // Hides the zoom buttons
        webSettings.setSupportZoom(true);


        // Set WebViewClient to handle links within the WebView
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);

            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                // Check if the URL is an ad link

                view.goBack();  // Go back in WebView's history

                return super.shouldOverrideUrlLoading(view, request);
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            private View mCustomView;
            private WebChromeClient.CustomViewCallback mCustomViewCallback;
            private int mOriginalOrientation;
            private int mOriginalSystemUiVisibility;

            public void onHideCustomView() {
                ((FrameLayout) getActivity().getWindow().getDecorView()).removeView(this.mCustomView);
                this.mCustomView = null;
                getActivity().getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
                getActivity().setRequestedOrientation(this.mOriginalOrientation);
                this.mCustomViewCallback.onCustomViewHidden();
                this.mCustomViewCallback = null;
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
            }

            @Override
            public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback) {
                if (this.mCustomView != null) {
                    onHideCustomView();
                    return;
                }
                this.mCustomView = paramView;
                this.mOriginalSystemUiVisibility = getActivity().getWindow().getDecorView().getSystemUiVisibility();
                this.mOriginalOrientation = getActivity().getRequestedOrientation();
                this.mCustomViewCallback = paramCustomViewCallback;
                ((FrameLayout) getActivity().getWindow()
                        .getDecorView())
                        .addView(this.mCustomView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                getActivity().getWindow().getDecorView().setSystemUiVisibility(FULL_SCREEN_SETTING);
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                this.mCustomView.setOnSystemUiVisibilityChangeListener(visibility -> updateControls());
            }

            @Override
            public Bitmap getDefaultVideoPoster() {
                return Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
            }

            void updateControls() {
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) this.mCustomView.getLayoutParams();
                params.bottomMargin = 0;
                params.topMargin = 0;
                params.leftMargin = 0;
                params.rightMargin = 0;
                params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                this.mCustomView.setLayoutParams(params);
                getActivity().getWindow().getDecorView().setSystemUiVisibility(FULL_SCREEN_SETTING);
            }
        });

        if (id != null && !id.isEmpty() && !id.equals("default_id")) {
            String imageUrl = sharedPreferences.getString("imageUrl", "default_image_url"); // Provide default value if key is not found
            String title = sharedPreferences.getString("title", "default_title");
            String year = sharedPreferences.getString("year", "default_year");
            String genres = sharedPreferences.getString("genres", "default_genres");
            String overview = sharedPreferences.getString("overview", "default_overview");
            String currentType = sharedPreferences.getString("type", "default_type");

            String iframeUrl;
            if (currentType.equals("movies")) {
                iframeUrl = String.format("<iframe src=\"https://vidsrc.xyz/embed/movie/%s\" width=\"100%%\" height=\"100%%\" frameborder=\"0\" allowfullscreen></iframe>", id);
            } else {
                iframeUrl = String.format("<iframe src=\"https://vidsrc.me/embed/tv?tmdb=%s&season=1&episode=1\" width=\"100%%\" height=\"100%%\" frameborder=\"0\" allowfullscreen></iframe>", id);
            }

            webView.setBackgroundColor(Color.TRANSPARENT);
            webView.getSettings().setTextZoom(200);

            webView.loadData(iframeUrl, "text/html", "UTF-8");

            watchoverview.setText(overview);
            watchtitleview.setText(title + " " +year);
            watchgenreview.setText("Genre: " +genres);

            Glide.with(this)
                    .load(imageUrl)
                    .into(watchimageview);

            watchwebview.setVisibility(View.VISIBLE);
            watchtextview.setVisibility(View.GONE);
            watchlayoutview.setVisibility(View.VISIBLE);

        } else {
            watchwebview.setVisibility(View.GONE);
            watchtextview.setVisibility(View.VISIBLE);
            watchlayoutview.setVisibility(View.GONE);
        }

        return view;

    }

    @Override
    public void onGridReplace(String contentType) {
        // Clear the current grid data
        gridItemList.clear();
        gridAdapter.notifyDataSetChanged();

    }
}