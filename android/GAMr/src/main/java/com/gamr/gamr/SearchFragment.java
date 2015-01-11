package com.gamr.gamr;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by beni on 1/10/15.
 */
public class SearchFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private static final String GET_MISC_QUALS_URL = "http://54.67.36.137:5000/miscquals/";// GET
    private Button searchButton;
    int seriousness;
    private TextView seriousnessLabel;
    private SeekBar seriousnessSeekBar;
    private RadioGroup PLTorTLPgroup;
    private Boolean plt;
    private String searchType;
    private EditText searchBar;
    private LinearLayout miscQualsPane;
    private View root;


    @Override
    public void onCreate (Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        root = inflater.inflate(R.layout.fragment_search, null);

        Spinner spinner = (Spinner) root.findViewById(R.id.search_spinner);
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.search_spinner_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        searchButton = (Button) root.findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() { // /posts/my post w/ user and pass to get back array of list
            @Override
            public void onClick(View v) {
                onSearchClicked();
            }
        });

        searchBar = (EditText) root.findViewById(R.id.search_query);

        seriousness = 1;
        seriousnessLabel = (TextView) root.findViewById(R.id.search_seriousness_label);
        seriousnessSeekBar = (SeekBar) root.findViewById(R.id.search_seriousness);
        seriousnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                seriousness = Math.round(progress / 25) + 1;
                seriousnessLabel.setText(""+seriousness);
            }
        });

        plt = true;
        PLTorTLPgroup = (RadioGroup) root.findViewById(R.id.search_plt_or_tlp);
        PLTorTLPgroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                plt = 0==i;
            }
        });

        miscQualsPane = (LinearLayout) root.findViewById(R.id.search_miscquals_pane);

        return root;
    }

    private void onSearchClicked() {

        // Query server here
        String jsonString = "{\"levels\":\"Combo Box (1, 2, 3)\"\"levels\":\"Combo Box (2, 4, 6)\"}";
    }

    @Override
    public void onPause () { super.onPause(); }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        searchType = (String) parent.getItemAtPosition(pos);
        if(searchType.equals("Game Title") && !searchBar.getText().toString().equals("")) {
            miscQualsPane.setVisibility(View.VISIBLE);
            new GetMiscQualsTask().execute();
        }
        else {
            miscQualsPane.setVisibility(View.GONE);
        }
    }

    /** Represents an asynchronous login/registration task used to authenticate the user */
    public class GetMiscQualsTask extends AsyncTask<Void, Void, String> {

        /* Query server with supplied login credentials and return JSON response String */
        @Override protected String doInBackground(Void... params) {
            // Create a new HttpClient and Post Header
            String url = GET_MISC_QUALS_URL + searchBar.getText().toString().replace(" ", "%20");
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);

            try {
                // Add login credentials to post data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(0);
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                ResponseHandler<String> responseHandler=new BasicResponseHandler();
                String jsonString = httpclient.execute(httppost, responseHandler);
                Log.d("gamr", jsonString);
                return jsonString;
            }
            catch (ClientProtocolException e) { Log.e("gamr", "ClientProtocol during login");}
            catch (IOException e) { Log.e("gamr", "IOException during login"); }
            Log.wtf("gamr", "Something went terribly wrong");
            return null;
        }

        @Override protected void onPostExecute(String jsonString) {
            try {
                JSONObject miscquals = new JSONObject(jsonString);
                Iterator<String> keys = miscquals.keys();
                while(keys.hasNext()) {
                    String key = keys.next();
                    String value = (String) miscquals.get(key);
                    ((LinearLayout) root.findViewById(R.id.search_miscquals_pane)).addView(new MiscQualView(getActivity(), key, value));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}
}
