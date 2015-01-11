package com.gamr.gamr;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity which displays a login screen to the user 36, 28, 88
 */
public class LoginActivity extends Activity {
    private static final String BASE_LOGIN_URL = "http://54.67.36.137:5000/user/";

    public static final String EXTRA_EMAIL = "Username"; // Key for default username

    // Values for email and password at the time of the login attempt.
    private String mUsername;
    private String mPassword;
    // UI references.
    private EditText mUsernameView;
    private EditText mPasswordView;
    private View mLoginFormView;
    private View mLoginStatusView;
    private TextView mLoginStatusMessageView;
    private Button registerBtn;

    private UserLoginTask mUserLoginTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        // Set up the login form.
        mUsername = getIntent().getStringExtra(EXTRA_EMAIL);
        mUsernameView = (EditText) findViewById(R.id.username_login);
        mUsernameView.setText(mUsername);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mLoginStatusView = findViewById(R.id.login_status);
        mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        registerBtn = (Button) findViewById(R.id.registerBtn);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRegister();
            }
        });
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If the credentials entered are not valid, the user will be asked to try again
     */
    public void attemptLogin() {
        // Reset errors
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt
        mUsername = mUsernameView.getText().toString();
        mPassword = mPasswordView.getText().toString();

        // Show a progress spinner, and kick off a worker thread to
        // perform the user login attempt.
        mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
        showProgress(true);
        mUserLoginTask = new UserLoginTask();
        mUserLoginTask.execute();
    }

    /** Represents an asynchronous login/registration task used to authenticate the user */
    public class UserLoginTask extends AsyncTask<Void, Void, String> {

        /* Query server with supplied login credentials and return JSON response String */
        @Override protected String doInBackground(Void... params) {
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(BASE_LOGIN_URL + mUsername);

            try {
                // Add login credentials to post data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("username", mUsername));
                nameValuePairs.add(new BasicNameValuePair("password", mPassword));
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
                    // incorrect username/password, display error message
                    showProgress(false);
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.requestFocus();
                }
                else {
                    // successful login, enter app
                    enterApp(jsonString);
                    //finish();
                }
            } catch (JSONException e) { e.printStackTrace(); }

        }
    }

    /* Shows the progress UI and hides the login form or vice versa */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void enterApp(String jsonString) {
        Intent myIntent = new Intent(LoginActivity.this, HomeActivity.class);
        myIntent.putExtra("ResponseJson", jsonString);
        LoginActivity.this.startActivity(myIntent);
        finish();
    }

    private void goToRegister() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        LoginActivity.this.startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }
}
