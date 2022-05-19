package com.example.cursappconvertasynctaskloader;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.LocaleList;
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
    private static final int LOADER_ID_CURRENCY = 10000;

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

        binding.buttonLoad.setOnClickListener(v -> clickButtonLoad());
        binding.buttonCancel.setOnClickListener(v -> clickButtonCancel());

        binding.convertButton.setOnClickListener(view -> {
            Currency currencyGet = (Currency) binding.currencyGetSpinner.getSelectedItem();
            Currency currencyHave = (Currency) binding.currencyHaveSpinner.getSelectedItem();
            String moneyString = binding.editTextHaveCurrency.getText().toString();

            double moneyHave = Double.parseDouble(moneyString); // exception
            double moneyGet = Math.round(moneyHave * currencyHave.getBuy() / currencyGet.getSale() * 100.0) / 100.0;
            binding.editTextGetCurrency.setText(String.valueOf(moneyGet));

        });
        this.loaderManager = LoaderManager.getInstance(this);

        binding.buttonLoad.performClick();

        // [uk_UA,en_US,de_DE]
        Configuration config = getBaseContext().getResources().getConfiguration();

        LocaleList localeList = config.getLocales();
        Locale current = getResources().getConfiguration().locale;



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
        Loader<List<Currency>> loader =  this.loaderManager.initLoader(LOADER_ID_CURRENCY, args, loaderCallbacks);
        try {
            loader.registerOnLoadCanceledListener(this); // Loader.OnLoadCanceledListener
        } catch(IllegalStateException e) {
            // There is already a listener registered
        }
        loader.forceLoad(); // Start Loading..
    }

    // User click on "Cancel" button.
    private void clickButtonCancel() {
        Loader<List<Currency>> loader = this.loaderManager.getLoader(LOADER_ID_CURRENCY);
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

        if(id == LOADER_ID_CURRENCY) {
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

        if(loader.getId() == LOADER_ID_CURRENCY) {
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
        if(loader.getId() == LOADER_ID_CURRENCY) {
            // Destroy a Loader by ID.
            this.loaderManager.destroyLoader(loader.getId());
            binding.progressBar.setVisibility(View.GONE); // To hide progressBar
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



    //  // [uk_UA,en_US,de_DE]
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        return super.onOptionsItemSelected(item);
        switch (item.getItemId()){
            case R.id.englishMenuItem:
                Toast.makeText(MainActivity.this,
                        "English language",
                        Toast.LENGTH_LONG).show();
                // setLocale("en_US");
                // setLocale("en");
                setLocale("en_us");
                recreate();
                break;

            case R.id.ukraineMenuItem:
                Toast.makeText(MainActivity.this,
                        "Ukraine language",
                        Toast.LENGTH_LONG).show();
                // setLocale("uk_UA");
                setLocale("uk_ua");
                recreate();
                break;
            case R.id.germanMenuItem:
                Toast.makeText(MainActivity.this,
                        "German language",
                        Toast.LENGTH_LONG).show();
                // setLocale("de_DE");
                setLocale("de_de");
                recreate();
                break;
            case R.id.lightThemeMenuItem:
                Toast.makeText(MainActivity.this,
                        "Light theme is enabled",
                        Toast.LENGTH_LONG).show();
                AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_NO);
                recreate();
                break;
            case R.id.darkThemeMenuItem:
                Toast.makeText(MainActivity.this,
                        "Dark theme is enabled",
                        Toast.LENGTH_LONG).show();
                AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_YES);
                recreate();
                break;
            case R.id.portraitMenuItem:
                Toast.makeText(MainActivity.this,
                        "Portrait orientation",
                        Toast.LENGTH_LONG).show();
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                recreate();
                break;
            case R.id.landscapeMenuItem:
                Toast.makeText(MainActivity.this,
                        "Landscape orientation",
                        Toast.LENGTH_LONG).show();
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                recreate();
                break;
        }
        return true;
    }

    // =============================================================================================





    public void setLocale(String locale) // Pass "en","uk", "de".
    {
        Locale myLocale = new Locale(locale);
        // Locale myLocale = Locale.UK;
        Locale.setDefault(myLocale);
        Configuration config = getBaseContext().getResources().getConfiguration();
//        config.isNightModeActive();
        config.setLocale(myLocale);
        LocaleList localeList = new LocaleList(myLocale);
        LocaleList.setDefault(localeList);
        config.setLocales(localeList);
        getBaseContext().createConfigurationContext(config); // обновляем config
    }



}