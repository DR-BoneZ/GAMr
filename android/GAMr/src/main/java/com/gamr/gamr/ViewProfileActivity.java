package com.gamr.gamr;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by beni on 1/10/15.
 */
public class ViewProfileActivity extends Activity {

    // UI references
    TextView username, description, platforms, genres, games, seriousness, miscQuals;

    TextView usernameView, descriptionView, platformsView, genresView, gamesView, seriousnessView, miscQualsView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        usernameView = (TextView) findViewById(R.id.view_profile_username);
        descriptionView = (TextView) findViewById(R.id.view_profile_description);
        platformsView = (TextView) findViewById(R.id.view_profile_platforms);
        genresView = (TextView) findViewById(R.id.view_profile_genres);
        gamesView = (TextView) findViewById(R.id.view_profile_games);
        seriousnessView = (TextView) findViewById(R.id.view_profile_seriousness);
        miscQualsView = (TextView) findViewById(R.id.view_profile_miscQuals);

        try {
            JSONObject resultJson = new JSONObject(getIntent().getStringExtra("ResponseJson"));
            usernameView.setText(Html.fromHtml("<b><u>" + resultJson.get("username").toString() + "</u></b>"));
            descriptionView.setText(Html.fromHtml("<i>" + resultJson.get("description").toString() + "</i>"));
            platformsView.setText(Html.fromHtml("<b>Platforms:</b> " + resultJson.get("platforms")));
            genresView.setText(Html.fromHtml("<b>Genres:</b> " + resultJson.get("genres")));
            gamesView.setText(Html.fromHtml("<b>Games:</b> " + resultJson.get("games")));
            seriousnessView.setText(Html.fromHtml("<b>Casual (1) to Hardcorde (5):</b> " + resultJson.get("seriousness")));
            miscQualsView.setText(Html.fromHtml("<b>Miscellaneous Qualities:</b> " + resultJson.get("miscQuals")));

        } catch (JSONException e) { e.printStackTrace(); }

    }
}