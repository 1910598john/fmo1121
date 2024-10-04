package com.fmo.jmcat1121;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputLayout;

public class HomeActivity extends AppCompatActivity implements OnUserEarnedRewardListener {

    private final FragmentManager fragmentManager = getSupportFragmentManager();
    private Button selectedButton;
    private OnGridReplaceListener onGridReplaceListener;
    private android.widget.SearchView searchView;
    private EditText searchText;
    private Fragment selectedFragment;
    private BottomNavigationView bottomNavigationView;

    String[] options = {"Action", "Animated TV Shows", "Anime", "Chinese Drama", "Ghibli Films", "Fantasy", "Filipino", "Horror", "Korean Drama", "Sci-Fi", "Thriller", "War"};
    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> arrayAdapter;
    TextInputLayout textInputLayout;
    LinearLayout linearLayout;

    private String currentGenre = "action";
    public String currentType = "movies";

    private RewardedAd rewardedInterstitialAd;

    private int userclick = 0;



    public int getUserClick() {
        return userclick;
    }

    public void setUserclick(int currentVal) {
        userclick = currentVal + 1;
        if (userclick == 5) {
            triggerRewardAds();
        }
    }

    @Override
    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
        userclick = 0;
    }

    public void triggerRewardAds() {
        MobileAds.initialize(this, initializationStatus -> {
            RewardedAd.load(HomeActivity.this, "ca-app-pub-7877895837492687/5969145653",
            new AdRequest.Builder().build(),  new RewardedAdLoadCallback() {
                @Override
                public void onAdLoaded(RewardedAd ad) {
                    rewardedInterstitialAd = ad;

                    rewardedInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                        @Override
                        public void onAdClicked() {
                            // Called when a click is recorded for an ad.

                        }

                        @Override
                        public void onAdDismissedFullScreenContent() {
                            // Called when ad is dismissed.
                            // Set the ad reference to null so you don't show the ad a second time.

                            rewardedInterstitialAd = null;
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                            // Called when ad fails to show.

                            rewardedInterstitialAd = null;
                        }

                        @Override
                        public void onAdImpression() {
                            // Called when an impression is recorded for an ad.

                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            // Called when ad is shown.

                        }
                    });

                    rewardedInterstitialAd.show(HomeActivity.this, rewardItem -> {
                        // Handle the reward when the user earns it
                        onUserEarnedReward(rewardItem);
                    });
                }
                @Override
                public void onAdFailedToLoad(LoadAdError loadAdError) {
                    rewardedInterstitialAd = null;
                    userclick = 0;
                }
            });
        });

    }

    public String getCurrentGenre() {
        return currentGenre;
    }
    public String getCurrentType() {
        return currentType;
    }

    private String getAssociatedValue(String item) {
        switch (item) {
            case "Action":
                return "action";
            case "Animated TV Shows":
                return "animated";
            case "Anime":
                return "anime";
            case "Chinese Drama":
                return "cdrama";
            case "Ghibli Films":
                return "gh";
            case "Fantasy":
                return "f";
            case "Filipino":
                return "ff";
            case "Horror":
                return "horror";
            case "Korean Drama":
                return "kdrama";
            case "Sci-Fi":
                return "scifi";
            case "Thriller":
                return "th";
            case "War":
                return "war";
            default:
                return "action";
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        Button button1 = findViewById(R.id.movies);
        Button button2 = findViewById(R.id.series);
         searchView = findViewById(R.id.search);
        int searchTextId = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        searchText = searchView.findViewById(searchTextId);
        autoCompleteTextView  = findViewById(R.id.autoCompleteTextView);
        arrayAdapter = new ArrayAdapter<>(this, R.layout.list_item, options);
        autoCompleteTextView.setAdapter(arrayAdapter);
        autoCompleteTextView.requestFocus();

        searchText.setHint("You are searching a movie..");
        searchView.setOnQueryTextListener(new android.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (onGridReplaceListener != null) {
                    onGridReplaceListener.onGridReplace("search" + currentType + s);  // Fetch movies
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (onGridReplaceListener != null) {
                    onGridReplaceListener.onGridReplace("search" + currentType + s);  // Fetch movies
                }
                return false;
            }
        });

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
                String option = getAssociatedValue(item);
                currentGenre = option;

                setUserclick(userclick);

                if (currentGenre.equals("animated")) {
                    button1.setVisibility(View.GONE); // Hide the "Movies" button
                    button2.setVisibility(View.VISIBLE); // Show the "Series" button

                    // Reset button states
                    if (selectedButton != null) {
                        selectedButton.setBackgroundColor(getResources().getColor(R.color.button_inactive));
                    }

                    // Set the "Series" button as active
                    button2.setBackgroundColor(getResources().getColor(R.color.button_active));
                    selectedButton = button2;
                    currentType = "series";
                    searchText.setHint("You are searching a series..");
                } else {
                    button1.setVisibility(View.VISIBLE); // Show the "Movies" button

                    // Reset button states
                    if (selectedButton != null) {
                        selectedButton.setBackgroundColor(getResources().getColor(R.color.button_inactive));
                    }

                    if (currentGenre.equals("gh")) {
                        button2.setVisibility(View.GONE); // Hide the "Series" button
                        button1.setBackgroundColor(getResources().getColor(R.color.button_active)); // Set "Movies" button as active
                        selectedButton = button1;
                        currentType = "movies";
                        searchText.setHint("You are searching a movie..");
                    } else {
                        button2.setVisibility(View.VISIBLE); // Show the "Series" button if applicable

                        // Set the "Movies" button as active
                        button1.setBackgroundColor(getResources().getColor(R.color.button_active));
                        selectedButton = button1;
                        currentType = "movies";
                        searchText.setHint("You are searching a movie..");
                    }
                }

                if (onGridReplaceListener != null) {
                    onGridReplaceListener.onGridReplace(currentGenre + currentType);  // Fetch movies
                }
            }
        });

        searchText.setTextColor(Color.parseColor("#ffc400"));  // Change to your preferred color
        searchText.setHintTextColor(Color.parseColor("#c9b15f"));
        selectedButton = button1;
        selectedButton.setBackgroundColor(getResources().getColor(R.color.button_active));
        button1.setOnClickListener(v -> handleButtonClick(button1, "movies"));
        button2.setOnClickListener(v -> handleButtonClick(button2, "series"));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
        }

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                setUserclick(userclick);

                textInputLayout = findViewById(R.id.dropdown);
                linearLayout = findViewById(R.id.homeaclayout);
                selectedFragment = null;
                int itemId = item.getItemId();
                if (itemId == R.id.genres) {
                    selectedFragment = new Home();

                    if (currentType.equals("movies")) {
                        button1.setBackgroundColor(getResources().getColor(R.color.button_active));
                        button2.setBackgroundColor(getResources().getColor(R.color.button_inactive));
                        selectedButton = button1;
                    } else {
                        button1.setBackgroundColor(getResources().getColor(R.color.button_inactive));
                        button2.setBackgroundColor(getResources().getColor(R.color.button_active));
                        selectedButton = button2;
                    }

                } else if (itemId == R.id.series) {
                    searchText.setHint("You are searching a series..");
                    currentType = "series";
                    selectedFragment = new Series();
                } else if (itemId == R.id.movies) {
                    searchText.setHint("You are searching a movie..");
                    currentType = "movies";
                    selectedFragment = new Movies();
                } else if (itemId == R.id.watch) {
                    selectedFragment = new Watch();
                }

                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) linearLayout.getLayoutParams();
                if (itemId == R.id.series || itemId == R.id.movies || itemId == R.id.watch) {
                    textInputLayout.setEnabled(false);
                    button2.setEnabled(false);
                    button1.setEnabled(false);
                } else {
                    textInputLayout.setEnabled(true);
                    button2.setEnabled(true);
                    button1.setEnabled(true);

                    autoCompleteTextView.requestFocus();
                }
                if (itemId == R.id.watch) {
                    searchView.setVisibility(View.GONE);
                } else {
                    searchView.setVisibility(View.VISIBLE);
                    autoCompleteTextView.requestFocus();
                }
                searchView.setFocusable(false);
                searchView.clearFocus();

                if (selectedFragment != null) {
                    fragmentManager.beginTransaction().replace(R.id.fragmentContainer, selectedFragment).commit();
                    onGridReplaceListener = (OnGridReplaceListener) selectedFragment;
                }
                return true;
            }
        });
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.genres);
        }
    }

//            MenuItem home = bottomNavigationView.getMenu().findItem(R.id.genres);
//            MenuItem series = bottomNavigationView.getMenu().findItem(R.id.series);
//            MenuItem movies = bottomNavigationView.getMenu().findItem(R.id.movies);
//            MenuItem watch = bottomNavigationView.getMenu().findItem(R.id.watch);

    private void handleButtonClick(Button button, String type) {
        setUserclick(userclick);

        if (!(currentType.equals("movies") && currentGenre.equals("animated"))) {
            if (selectedButton != null) {
                selectedButton.setBackgroundColor(getResources().getColor(R.color.button_inactive));
            }
            button.setBackgroundColor(getResources().getColor(R.color.button_active));
            selectedButton = button;
            if (onGridReplaceListener != null) {
                onGridReplaceListener.onGridReplace(currentGenre + type);  // Fetch movies
            }
            currentType = type;
        }

        if (type.equals("movies")) {
            searchText.setHint("You are searching a movie..");
        } else {
            searchText.setHint("You are searching a series..");
        }

    }
}
