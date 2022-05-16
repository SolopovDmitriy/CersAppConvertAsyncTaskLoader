package com.example.cursappconvertasynctaskloader;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.example.cursappconvertasynctaskloader.databinding.ActivityMainBinding;

import java.util.List;

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
}