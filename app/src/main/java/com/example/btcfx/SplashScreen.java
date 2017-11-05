package com.example.btcfx;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;
import java.util.List;

public class SplashScreen extends AppCompatActivity {

    //Initializing variables
    private static final String TAG = "SplashScreen";
    private static final String REQUESTTAG = "string request first";
    private String answer;
    private String firstPart = "https://min-api.cryptocompare.com/data/price?fsym=BTC&tsyms=";
    private RequestQueue mRequestQueue;
    private LaunchStatus launchStatus;
    private SharedPreferences result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Checking for first time launch
        launchStatus = new LaunchStatus(this);
        // if its is launching for the first time set the default url, make request and get response.
        if (launchStatus.isFirstTimeLaunch()) {
            launchStatus.setFirstTimeLaunch(false);

            setDefault();
            sendRequest();
            saveResult();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();

        }
        // if the app is not lunching for the first time send request save result then start MainActivity
        else {
            sendRequest();
            saveResult();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();

        }
    }

    // Save the result from the api request into shared preference
    public void saveResult() {
        SharedPreferences result = getSharedPreferences("PREF", 0);
        String resultString = result.getString("results", "");
        String[] itemResult = resultString.split(",");
        List<String> items = new ArrayList<>();
        for (int i = 0; i < itemResult.length; i++) {
            items.add(itemResult[i]);
        }
        for (int i = 0; i < items.size(); i++)
            Log.d("list item", items.get(i));


    }

    //set the default url
    public void setDefault() {
        List<String> defaults = new ArrayList<>();
        defaults.add("ETH,USD,AUD,CAD,GBP,YEN,");
        defaults.add("LYD,MXN,MAD,BMD,BIF,");
        defaults.add("TMT,ANG,RUB,PHP,ZAR,");
        defaults.add("UGX,AED,UAH,EUR,GSD,NGN");

        //save the default url into shared preference
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(firstPart);
        for (String s : defaults) {
            stringBuilder.append(s);

        }

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.saved_high_score), stringBuilder.toString());
        editor.commit();

    }

    //read the url from the shared preference
    public String getUrl() {

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        String defaultValue = getResources().getString(R.string.saved_high_score_default);
        String api = sharedPref.getString(getString(R.string.saved_high_score), defaultValue);

        return api.toString();
    }

    public void sendRequest() {

        //cache for http request
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB capacity
        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());
        String api = getUrl();

        // Instantiate the RequestQueue with the cache and network.
        mRequestQueue = new RequestQueue(cache, network);
        // Start the queue
        mRequestQueue.start();


        answer = new String();
        // Formulate the request and handle the response.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, api,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //save the response to shared preference
                        SharedPreferences result = getSharedPreferences("PREF", 0);
                        SharedPreferences.Editor editor = result.edit();
                        editor.putString("results", response.toString());
                        editor.commit();


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                        Toast.makeText(getApplicationContext(), "Please check connection", Toast.LENGTH_SHORT).show();

                    }
                });


        // Add the request to the RequestQueue.
        stringRequest.setTag(REQUESTTAG);
        //mRequestQueue.stop();
        mRequestQueue.add(stringRequest);

    }


    //launch status checker, to determine if app is launched for the first time
    public class LaunchStatus {
        // Shared preferences file name
        private static final String PREF_NAME = "launch";
        private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
        private SharedPreferences pref;
        private SharedPreferences.Editor editor;
        private Context _context;
        // shared pref mode
        private int PRIVATE_MODE = 0;

        public LaunchStatus(Context context) {
            this._context = context;
            pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
            editor = pref.edit()
            ;
        }

        public boolean isFirstTimeLaunch() {
            return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
        }


        public void setFirstTimeLaunch(boolean isFirstTime) {
            editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
            editor.commit();
        }

    }
}

