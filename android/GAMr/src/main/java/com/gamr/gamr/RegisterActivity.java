package com.gamr.gamr;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by beni on 1/10/15.
 */
public class RegisterActivity extends Activity {
    private static final String BASE_CREATE_USER_URL = "http://54.67.36.137:5000/user/add/"; // /user/edit/ with password and fields to change to edit profile /user/del for delete

    // Pre-defined user profile options
    private static final String[] PLATFORMS = new String[]{"PC", "PS3", "PS4", "Xbox360", "Xbone", "WiiU", "Wii", "Mobile", "Browser"};
    private static final String[] GENRES = new String[]{"RTS", "FPS", "RPG", "Social", "MOBA", "Strategy", "Racing"};

    // User profile selections/statistics
    private String username, password, description;
    private ArrayList<String> platforms, genres, games;
    private double reputation;
    private int seriousness;

    // UI references
    private EditText userNameField, passwordField, descriptionField, gamesSearch;
    private Button addClass;
    private TextView gamesListing, seriousnessLabel;
    private SeekBar seriousnessSeekBar;

    // Instance of CreateUserTask
    private CreateUserTask createUserTask;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        populatePlatforms();
        populateGenres();

        userNameField = (EditText) findViewById(R.id.register_username);
        passwordField = (EditText) findViewById(R.id.register_password);
        descriptionField = (EditText) findViewById(R.id.register_description);

        games = new ArrayList<String>();
        genres = new ArrayList<String>();
        platforms = new ArrayList<String>();

        gamesSearch = (EditText) findViewById(R.id.games_search);
        gamesListing = (TextView) findViewById(R.id.games_listing);
        addClass = (Button) findViewById(R.id.add_class_btn);
        addClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String game = gamesSearch.getText().toString();
                gamesSearch.setText("");
                games.add(game);
                if(games.size()>1) game = ", " + game;
                gamesListing.setText(gamesListing.getText() + game);
            }
        });
        seriousness = 3;
        seriousnessLabel = (TextView) findViewById(R.id.seriousness_label);
        seriousnessSeekBar = (SeekBar) findViewById(R.id.seriousness_slider);
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

        reputation = 0.5; // Default value for account creation

        findViewById(R.id.create_user_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });
    }

    /* Create account from entered info if info is valid */
    private void createAccount() {
        username = userNameField.getText().toString();
        password = passwordField.getText().toString();
        description = descriptionField.getText().toString();
        if(username==null || password==null) {
            Toast.makeText(getApplicationContext(), "Username and Password are required", Toast.LENGTH_LONG).show();
        }
        else { // Create account
            //showProgress(true);
            createUserTask = new CreateUserTask();
            createUserTask.execute();
        }
    }

    private void populatePlatforms() {
        LinearLayout container = (LinearLayout) findViewById(R.id.platforms);
        for(final String platform: PLATFORMS) {
            CheckBox checkbox = new CheckBox(this);
            checkbox.setText(platform);
            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked) platforms.add(platform);
                    else platforms.remove(platform);
                }
            });
            container.addView(checkbox);
        }
    }

    private void populateGenres() {
        LinearLayout container = (LinearLayout) findViewById(R.id.genres);
        for(final String genre: GENRES) {
            CheckBox checkbox = new CheckBox(this);
            checkbox.setText(genre);
            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked) genres.add(genre);
                    else genres.remove(genre);
                }
            });
            container.addView(checkbox);
        }
    }

    /** Represents an asynchronous login/registration task used to authenticate the user */
    public class CreateUserTask extends AsyncTask<Void, Void, String> {

        /* Query server with supplied login credentials and return JSON response String */
        @Override protected String doInBackground(Void... params) {
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(BASE_CREATE_USER_URL + username);

            try {
                // Add login credentials to post data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(8);
                // password
                nameValuePairs.add(new BasicNameValuePair("password", password));
                // description
                nameValuePairs.add(new BasicNameValuePair("description", description));
                // platforms
                String platformsJson = "[\"";
                for(String platform : platforms) platformsJson += platform + "\",\"";
                platformsJson = platformsJson.substring(0, platformsJson.length()-2) + "]";
                Log.d("gamr", platformsJson);
                if(platforms.size()==0) platformsJson = "[]";
                nameValuePairs.add(new BasicNameValuePair("platforms", platformsJson));
                // genres
                String genresJson = "[\"";
                for(String genre : genres) genresJson += genre + "\",\"";
                genresJson = genresJson.substring(0, genresJson.length()-2) + "]";
                Log.d("gamr", genresJson);
                if(genres.size()==0) genresJson = "[]";
                nameValuePairs.add(new BasicNameValuePair("genres", genresJson));
                // games
                String gamesJson = "[\"";
                for(String game : games) gamesJson += game + "\",\"";
                gamesJson = gamesJson.substring(0, gamesJson.length()-2) + "]";
                Log.d("gamr", gamesJson);
                if(games.size()==0) gamesJson = "[]";
                nameValuePairs.add(new BasicNameValuePair("games", gamesJson));
                // Seriousness
                nameValuePairs.add(new BasicNameValuePair("seriousness", ""+seriousness));
                // Reputation
                nameValuePairs.add(new BasicNameValuePair("reputation", ""+reputation));
                // miscQuals
                nameValuePairs.add(new BasicNameValuePair("miscQuals", "{}"));

                // Attach post data
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                ResponseHandler<String> responseHandler=new BasicResponseHandler();
                String jsonString = httpclient.execute(httppost, responseHandler);
                return jsonString;
            }
            catch (ClientProtocolException e) { Log.e("gamr", "ClientProtocol during login");}
            catch (IOException e) { Log.e("gamr", "IOException during login"); }
            Log.wtf("gamr", "Something went terribly wrong");
            return null;
        }

        /* Enter app or display error message and stay on login page based on json response */
        @Override protected void onPostExecute(String jsonString) {
            try {
                JSONObject resultJson = new JSONObject(jsonString);
                if(resultJson.has("error")) {
                    if(resultJson.get("error")=="403") {
                        // Username contains illegal characters, display error message
                        userNameField.setError("Username contains illegal characters. Please choose another");
                    }
                    else {
                        // Username already taken, display error message
                        userNameField.setError("Username already taken. Please choose another");
                        userNameField.requestFocus();
                    }
                }
                else {
                    // User created successfully, enter app
                    enterApp(jsonString);
                }
            } catch (JSONException e) { e.printStackTrace(); }

        }
    }

    private void enterApp(String jsonString) {
        Intent myIntent = new Intent(RegisterActivity.this, HomeActivity.class);
        myIntent.putExtra("ResponseJson", jsonString);
        RegisterActivity.this.startActivity(myIntent);
        finish();
    }
}