package com.example.cursappconvertasynctaskloader;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.cursappconvertasynctaskloader.databinding.ActivityMainBinding;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity //
        implements LoaderManager.LoaderCallbacks<List<Currency>>,
        Loader.OnLoadCanceledListener<List<Currency>> {

    private static final String LOG_TAG = "AndroidExample";
    private static final int LOADER_ID_USERACCOUNT = 10000;

    // private Button buttonLoad;
    // private Button buttonCancel;
    // private ProgressBar progressBar;
    // private TextView textView;

    private static final String KEY_PARAM1 = "SomeKey1";
    private static final String KEY_PARAM2 = "SomeKey2";

    private LoaderManager loaderManager;
    private ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // setContentView(R.layout.activity_main);

        // this.buttonLoad = (Button) this.findViewById(R.id.button_load);
        // this.buttonCancel = (Button) this.findViewById(R.id.button_cancel);
        // this.progressBar = (ProgressBar) this.findViewById(R.id.progressBar);
        // this.textView = (TextView) this.findViewById(R.id.textView);


        // Hide ProgressBar.
        binding.progressBar.setVisibility(View.GONE);
        binding.buttonCancel.setEnabled(false);

        binding.buttonLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickButtonLoad();
            }
        });

        binding.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickButtonCancel();
            }
        });

        binding.convertButton.setOnClickListener(view -> {
            Currency currencyGet = (Currency) binding.currencyGetSpinner.getSelectedItem();
            Currency currencyHave = (Currency) binding.currencyHaveSpinner.getSelectedItem();
            String moneyString = binding.editTextHaveCurrency.getText().toString();

            double moneyHave = Double.parseDouble(moneyString); // exception
            double moneyGet = Math.round(moneyHave * currencyHave.getBuy() / currencyGet.getSale() * 100.0) / 100.0;
            binding.getCurrencyTextView.setText(String.valueOf(moneyGet));
        });
        this.loaderManager = LoaderManager.getInstance(this);

        binding.buttonLoad.performClick();
    }

    // User click on "Load Data" button.
    private void clickButtonLoad() {
        // this.textView.setText("");
        Log.i(LOG_TAG, "loadUserAccount");
        LoaderManager.LoaderCallbacks<List<Currency>> loaderCallbacks = this;

        // Arguments:
        Bundle args = new Bundle();
        args.putString(KEY_PARAM1, "Some value1");
        args.putString(KEY_PARAM2, "Some value2");

        // You can pass a null args to a Loader
        Loader<List<Currency>> loader =  this.loaderManager.initLoader(LOADER_ID_USERACCOUNT, args, loaderCallbacks);
        try {
            loader.registerOnLoadCanceledListener(this); // Loader.OnLoadCanceledListener
        } catch(IllegalStateException e) {
            // There is already a listener registered
        }
        loader.forceLoad(); // Start Loading..
    }

    // User click on "Cancel" button.
    private void clickButtonCancel() {
        Log.i(LOG_TAG, "cancelLoadUserAccount");
        Loader<List<Currency>> loader = this.loaderManager.getLoader(LOADER_ID_USERACCOUNT);
        if(loader != null)  {
            boolean cancelled = loader.cancelLoad();
        }
    }

    // Implements method of LoaderManager.LoaderCallbacks
    @NonNull
    @Override
    public Loader<List<Currency>> onCreateLoader(int id, @Nullable Bundle args) {
        Log.i(LOG_TAG, "onCreateLoader");

        binding.progressBar.setVisibility(View.VISIBLE); // To show

        if(id == LOADER_ID_USERACCOUNT) {
            binding.buttonLoad.setEnabled(false);
            binding.buttonCancel.setEnabled(true);
            // Parameters:
            String param1 = (String) args.get(KEY_PARAM1);
            String param2 = (String) args.get(KEY_PARAM2);
            // Return a Loader.
            return new UserAccountTaskLoader(MainActivity.this, param1, param2);
        }
        throw new RuntimeException("TODO..");
    }

    // Implements method of LoaderManager.LoaderCallbacks
    @Override
    public void onLoadFinished(@NonNull Loader<List<Currency>> loader, List<Currency> data) {
        Log.i(LOG_TAG, "onLoadFinished");

        if(loader.getId() == LOADER_ID_USERACCOUNT) {
            // Destroy a Loader by ID.
            this.loaderManager.destroyLoader(loader.getId());

            ArrayAdapter<Currency> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, data);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            binding.currencyGetSpinner.setAdapter(adapter);  // Применяем адаптер к элементу spinner
            binding.currencyHaveSpinner.setAdapter(adapter);  // Применяем адаптер к элементу spinner

//            ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, countries);
//            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//            binding.currencyGetSpinner.setAdapter(adapter);  // Применяем адаптер к элементу spinner
//            binding.currencyHaveSpinner.setAdapter(adapter);  // Применяем адаптер к элементу spinner


//            String s= "";
//
//            for(Currency currency: data)  {
//                s += "ccy: " + currency.getCcy() + "\t buy: " + currency.getBuy() + "\t \t \t \t \t \t sell: " + currency.getSale() +"\n";
//            }
//            this.textView.setText(s);
//
//            // ccy: USD buy:30 sell:32
//            // cc:  EUR buy:35 sell 37



            // Hide ProgressBar.
            binding.progressBar.setVisibility(View.GONE);
            binding.buttonLoad.setEnabled(true);
            binding.buttonCancel.setEnabled(false);
        }
    }

    // Implements method of LoaderManager.LoaderCallbacks
    @Override
    public void onLoaderReset(@NonNull Loader<List<Currency>> loader) {
        Log.i(LOG_TAG, "onLoaderReset");
        // this.textView.setText("");
    }

    // Implements method of Loader.OnLoadCanceledListener
    @Override
    public void onLoadCanceled(@NonNull Loader<List<Currency>> loader) {
        Log.i(LOG_TAG, "onLoadCanceled");

        if(loader.getId() == LOADER_ID_USERACCOUNT) {
            // Destroy a Loader by ID.
            this.loaderManager.destroyLoader(loader.getId());
            binding.progressBar.setVisibility(View.GONE); // To hide
            binding.buttonLoad.setEnabled(true);
            binding.buttonCancel.setEnabled(false);
        }
    }

    // =============================================================================================

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        // return true so that the menu pop up is opened
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        return super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.english:
                Toast.makeText(MainActivity.this,
                        "English language",
                        Toast.LENGTH_LONG).show();
                setLocale("en");
                recreate();
                break;

            case R.id.ukraine:
                Toast.makeText(MainActivity.this,
                        "Ukraine language",
                        Toast.LENGTH_LONG).show();
                setLocale("uk");
                recreate();
                break;
        }
        return true;
    }

    // =============================================================================================



    public void setLocale(String locale) // Pass "en","hi", etc.
    {
        Locale myLocale = new Locale(locale);
        Locale.setDefault(myLocale);
        Configuration config = getBaseContext().getResources().getConfiguration();
        config.setLocale(myLocale);

        getBaseContext().createConfigurationContext(config);
//        getBaseContext().getResources().updateConfiguration(config,
//                getBaseContext().getResources().getDisplayMetrics());

    }


    public void setLocale2(String locale) // Pass "en","hi", etc.
    {
        Locale myLocale = new Locale(locale);
        // Saving selected locale to session - SharedPreferences.
        // saveLocale(locale);
        // Changing locale.
        Locale.setDefault(myLocale);

        android.content.res.Configuration config = new android.content.res.Configuration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(myLocale);
        } else {
            config.locale = myLocale;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            getBaseContext().createConfigurationContext(config);
        } else {
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        }

    }


}