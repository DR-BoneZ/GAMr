package com.gamr.gamr;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by beni on 1/9/15.
 */
public class HomeActivity extends Activity {
    // Fragments to make posts and search
    Fragment searchFragment, postFragment;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_home);

        //TextView responseTextView = (TextView) findViewById(R.id.response_text);
        //responseTextView.setText(getIntent().getStringExtra("ResponseJson"));

        searchFragment = new SearchFragment();
        postFragment = new SearchFragment();

        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        TabListener<SearchFragment> tlSearch = new TabListener<SearchFragment>(this, "Search", SearchFragment.class);
        TabListener<PostFragment> tlPost = new TabListener<PostFragment>(this, "Post", PostFragment.class);

        ActionBar.Tab searchTab = actionBar.newTab().setText("Search").setTabListener(tlSearch);
        ActionBar.Tab postTab = actionBar.newTab().setText("Post").setTabListener(tlPost);

        actionBar.addTab(searchTab);
        actionBar.addTab(postTab);
    }
}