package com.example.cursappconvertasynctaskloader;
import android.content.Context;
import android.os.SystemClock;

import androidx.loader.content.AsyncTaskLoader;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

// https://betacode.net/12753/android-asynctaskloader#a62493902
public class UserAccountTaskLoader extends AsyncTaskLoader<List<Currency>> {

    private String param1;
    private String param2;

    public UserAccountTaskLoader(Context context, String param1, String param2) {
        super(context);
        this.param1 = param1;
        this.param2 = param2;
    }

    @Override
    public List<Currency> loadInBackground() {
        // Do something, for example:
        // - Download data from URL and parse it info Java object.
        // - Query data from Database into Java object.

        String url = "https://api.privatbank.ua/p24api/pubinfo?json&exchange&coursid=5";
        String jsonString = "";
        try {
            Document doc = Jsoup.connect(url).ignoreContentType(true).get();
            jsonString = doc.body().text();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();
        Currency[] currencyArray = gson.fromJson(jsonString, Currency[].class);
        List<Currency> currencyList = new ArrayList<>(Arrays.asList(currencyArray));
        currencyList.add(new Currency("UAH", "grn", 1, 1));
        SystemClock.sleep(100); // 1 mSeconds.
        return currencyList;
    }

}