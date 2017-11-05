package com.example.btcfx;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final String REQUESTTAG = "string request second";
    public String firstPart = "https://min-api.cryptocompare.com/data/price?fsym=ETH&tsyms=";
    public String answer;
    public String newCurrency;
    public SharedPreferences result;
    public String xapi;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<ListItem> listItems;
    private RequestQueue mRequestQueue;
    private SwipeRefreshLayout mSwipe;
    private String testUrl;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        testUrl = getUrl().toString();

        //check if the shared preference has been initialized correctly
        if (testUrl.equals("77")) {
            setDefault();
            sendRequest();
            loadCards();
            mSwipe = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
            mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    sendRequest();
                    loadCards();
                    mSwipe.setRefreshing(false);
                    Toast.makeText(getApplicationContext(), "Refreshed", Toast.LENGTH_SHORT).show();
                }


            });
        } else {
           mSwipe = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);

            sendRequest();
            loadCards();
            mSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    sendRequest();
                    loadCards();
                    mSwipe.setRefreshing(false);
                    Toast.makeText(getApplicationContext(), "Refreshed", Toast.LENGTH_SHORT).show();
                }


            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadCards();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        sendRequest();loadCards();
    }

    //load cards into the recycler view
    public void loadCards() {

        SharedPreferences result = getSharedPreferences("PREF", 0);
        String wordString = result.getString("results", "");

        Map<String, String> map = new HashMap<String, String>();
        //!!Your string comes here, I escape double quoutes manually !!
        String str = wordString;
        //{"BTC":0.05115,"USD":296.91,"EUR":256.47,"NGN":106412.01} regex to solve this response
        String regex = "\"(\\w+)\":(\\d+\\.\\d+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);

        //write the result into map array
        while (matcher.find()) {
            String key = matcher.group(1);
            String value = matcher.group(2);
            map.put(key, value);
        }

        //load map array result into the cards
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listItems = new ArrayList<>();

        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            ListItem listItem = new ListItem(
                    key,
                    value
            );

            listItems.add(listItem);

        }

        // initialize the cards
        adapter = new TheAdapter(listItems, this);
        recyclerView.setAdapter(adapter);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    // Handle action bar item clicks
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            // Respond to a click on the menu option
            case R.id.action_addCurrency:
                addCurrency();
                loadCards();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_reset:
                // reset and write
                setDefault();
                sendRequest();
                loadCards();

                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_about:
                // Start about activity
                Intent about = new Intent(this, About.class);
                startActivity(about);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setDefault() {
        List<String> defaults = new ArrayList<>();
        defaults.add("BTC,ETH,USD,AUD,CAD,GBP,");
        defaults.add("LYD,MXN,MAD,BMD,BIF,");
        defaults.add("TMT,ANG,RUB,PHP,ZAR,");
        defaults.add("UGX,AED,UAH,EUR,GSD,NGN");

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

    public String getUrl() {

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        String defaultValue = getResources().getString(R.string.saved_high_score_default);
        String api = sharedPref.getString(getString(R.string.saved_high_score), defaultValue);

        return api.toString();
    }

    public void sendRequest() {

        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
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
                        // Do something with the response

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
                        Toast.makeText(getApplicationContext(), "please check connection", Toast.LENGTH_SHORT).show();
                    }
                });


        stringRequest.setTag(REQUESTTAG);
        mRequestQueue.add(stringRequest);
    }


    @Override
    protected void onResume() {
        super.onResume();
        loadCards();
    }


    //add new currency to http request
    public void addCurrency() {
        AlertDialog.Builder mBulider = new AlertDialog.Builder(MainActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_addcurrrency, null);
        final EditText mCurrency = mView.findViewById(R.id.addNewCurrency);
        final Button mAdd = mView.findViewById(R.id.addButton);


        mAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(mCurrency.getText().toString().isEmpty())) {
                    newCurrency = (mCurrency.getText().toString().toUpperCase());
                    setNewDefault(newCurrency);
                    sendRequest();
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), newCurrency.toString() + " added", Toast.LENGTH_SHORT).show();


                } else {
                    Toast.makeText(MainActivity.this, "Field cannot be Empty", Toast.LENGTH_SHORT).show();

                }
            }
        });

        mBulider.setView(mView);
        dialog = mBulider.create();
        dialog.show();


    }

    //creating a new url for http request and saving to shared preference
    public void setNewDefault(String s) {
        //List<String> defaults = new ArrayList<>();

        xapi = getUrl();

        StringBuilder stringBuilder = new StringBuilder();
        // stringBuilder.append(firstPart);
        stringBuilder.append(xapi);
        stringBuilder.append(("," + s));

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.saved_high_score), stringBuilder.toString());
        editor.commit();

    }


}
