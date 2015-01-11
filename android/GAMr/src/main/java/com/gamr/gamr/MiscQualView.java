package com.gamr.gamr;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

/**
 * Created by beni on 1/11/15.
 */
public class MiscQualView extends LinearLayout implements AdapterView.OnItemSelectedListener {
    private NameValuePair nvp;

    private String key;
    private String selection;


    public MiscQualView(Context context, String key, String value) {
        super(context);
        setOrientation(HORIZONTAL);


        // Add input element
        if(value.contains("Combo Box")) {
            // Add label

            TextView label = new TextView(context);
            label.setText(key);
            addView(label);

            // Generate spinner
            String temp = value.replace("Combo Box (", "");
            temp = temp.replace(")", "");
            String[] spinnerOptions = temp.split(", ");
            ArrayList<String> spinnerArray = new ArrayList<String>();
            for(String option : spinnerOptions) spinnerArray.add(option);

            Spinner spinner = new Spinner(context);
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, spinnerArray);
            spinner.setAdapter(spinnerArrayAdapter);

            addView(spinner);
        }
        else if(value.contains("Numeric")) {
            // Numeric range
        }
        else {
            // plain text input

        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        nvp = new BasicNameValuePair(key, selection);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}
}
