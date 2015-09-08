package me.srineha.dravyasms;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import me.srineha.dravyasms.Server.PostTask;
import me.srineha.dravyasms.Server.Result;


/**
 * A login screen that offers login via phone/password.
 */
public class LoginActivity extends MyActivity {



    // UI references.
    private AutoCompleteTextView mphoneView;
    private EditText mPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if(me!=null)
            startNextActivity();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up the login form.
        mphoneView = (AutoCompleteTextView) findViewById(R.id.phone);

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

        AppCompatButton mphoneSignInButton = (AppCompatButton) findViewById(R.id.phone_sign_in_button);
        mphoneSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        ColorStateList csl = new ColorStateList(new int[][]{new int[0]}, new int[]{getResources().getColor(R.color.dark_primary)});
        mphoneSignInButton.setSupportBackgroundTintList(csl);

    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid phone, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {

        // Reset errors.
        mphoneView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        final String phone = mphoneView.getText().toString();
        final String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid phone address.
        if (TextUtils.isEmpty(phone)) {
            mphoneView.setError(getString(R.string.error_field_required));
            focusView = mphoneView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            new PostTask(this, "Logging you in...") {
                @Override
                public List<NameValuePair> getPostData(String[] params, int i) {
                    List<NameValuePair> nameValuePairs = new ArrayList<>();
                    nameValuePairs.add(new BasicNameValuePair("phone", phone));
                    nameValuePairs.add(new BasicNameValuePair("password", password));
                    return nameValuePairs;
                }


                @Override
                public void onPostExecute(Result ret) {
                    super.onPostExecute(ret);
                    if (ret.statusCode == 200) {
                        try {
                            JSONObject data = ret.data.optJSONObject("data");
                            me = new User(data);
                            Log.d("User", me.toString());
                            startNextActivity();
                        } catch (Exception E) {
                            E.printStackTrace();
                        }

                        Toast.makeText(context, ret.statusMessage, Toast.LENGTH_LONG).show();
                        startNextActivity();
                    }
                }
            }.execute(getUrl("/login"));
        }
    }


    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }


    public void startNextActivity(){
        startActivity(new Intent(this,MainActivity.class));
        finish();
    }
}