package com.example.btcfx;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.text.DecimalFormat;

public class Convert extends AppCompatActivity {

    private RadioGroup radioGroup;
    private RadioButton radioBTC;
    private RadioButton radioCurrency;
    private TextView initialCurrency;
    private EditText amountEntered;
    private Button convert;
    private TextView converted;
    private String currency_string;
    private String value_string;
    private Boolean toBTC = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convert);
        toBTC = false;

        //get data from intent that launched Convert Activity
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        //store the string data
        currency_string = extras.getString("EXTRA_CURRENCY");
        value_string = extras.getString("EXTRA_VALUE");

        //connect the radio buttons with an id for reference
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioBTC = (RadioButton) findViewById(R.id.radioBtc);
        radioCurrency = (RadioButton) findViewById(R.id.radioCurrency);
        radioCurrency.setText(currency_string);

        //connect the view object with an id for reference
        initialCurrency = (TextView) findViewById(R.id.currencyTextView);
        amountEntered = (EditText) findViewById(R.id.currencyEditText);
        convert = (Button) findViewById(R.id.convertButton);
        converted = (TextView) findViewById(R.id.convertedTextView);
        converted.setText("Value in " + currency_string);

    }

    // method for checking which radio button is selected
    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radioBtc:
                if (checked)
                    radioCurrency.setText(currency_string);
                initialCurrency.setText("BTC");
                converted.setText("Value in " + currency_string);
                toBTC = false;
                break;
            case R.id.radioCurrency:
                if (checked)
                    initialCurrency.setText(currency_string);
                converted.setText("Value in BTC");
                toBTC = true;
                break;
        }
    }

    //method to perform conversion, takes a view object
    public void convert(View v) {
        String amount = (amountEntered.getText().toString());
        Double amountToDouble = Double.parseDouble(amount);
        Double valueToDouble = Double.parseDouble(value_string);
        //logic to convert based on the radio button selected
        if (toBTC == true) {
            Double btcSolution = (amountToDouble / valueToDouble);
            DecimalFormat dFormat = new DecimalFormat("####,###,###.0000");
            String output = ((dFormat.format(btcSolution)) + "BTC");
            converted.setText(output);

        } else {
            Double currSolution = (amountToDouble * valueToDouble);
            DecimalFormat dFormat = new DecimalFormat("####,###,###.0000");
            String output = (currency_string +" "+ (dFormat.format(currSolution)));
            converted.setText(output);

        }
    }


}
