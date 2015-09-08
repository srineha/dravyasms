package me.srineha.dravyasms;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.srineha.dravyasms.Server.PostTask;
import me.srineha.dravyasms.Server.Result;


public class MainActivity extends MyActivity {

    // UI references.
    private EditText mphoneView;
    private EditText mamount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ((TextView)findViewById(R.id.welcomeString)).setText(String.format(getString(R.string.welcome), me.name));
        ((TextView)findViewById(R.id.balance)).setText(String.format(getString(R.string.balance), me.balance));

        AppCompatButton mphoneSignInButton = (AppCompatButton) findViewById(R.id.phone_sign_in_button);
        mphoneSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptTransfer();
            }
        });

        ColorStateList csl = new ColorStateList(new int[][]{new int[0]}, new int[]{getResources().getColor(R.color.dark_primary)});
        mphoneSignInButton.setSupportBackgroundTintList(csl);

        mphoneView = (EditText) findViewById(R.id.phone);
        mamount = (EditText) findViewById(R.id.amount);
        registerGCM();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            SharedPreferences.Editor spe = prefs.edit();
            spe.remove("user");
            spe.apply();
            me = null;
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }

        if (id == R.id.action_refresh) {
            refresh();
        }

        return super.onOptionsItemSelected(item);
    }

    public void attemptTransfer(){
        // Reset errors.
        mphoneView.setError(null);
        mamount.setError(null);

        // Store values at the time of the login attempt.
        final String phone = mphoneView.getText().toString();
        final String amount = mamount.getText().toString();

        boolean cancel = false;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(amount)) {
            mamount.setError(getString(R.string.error_invalid_password));
            cancel = true;
        }

        // Check for a valid phone address.
        if (TextUtils.isEmpty(phone)) {
            mphoneView.setError(getString(R.string.error_field_required));
            cancel = true;
        }


        if(!cancel){
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            final int amnt = Integer.parseInt(amount);

            new PostTask(this, "Transferring Money..") {
                @Override
                public List<NameValuePair> getPostData(String[] params, int i) {
                    List<NameValuePair> nameValuePairs = new ArrayList<>();
                    nameValuePairs.add(new BasicNameValuePair("from", me.id));
                    nameValuePairs.add(new BasicNameValuePair("to", phone));
                    nameValuePairs.add(new BasicNameValuePair("money",amount ));
                    return nameValuePairs;
                }


                @Override
                public void onPostExecute(Result ret) {
                    super.onPostExecute(ret);
                    if (ret.statusCode == 200) {
                        try {
                            me.balance -= amnt;
                            ((TextView)findViewById(R.id.balance)).setText(String.format(getString(R.string.balance), me.balance));
                            mphoneView.setText("");
                            mamount.setText("");
                        } catch (Exception E) {
                            E.printStackTrace();
                        }

                        Toast.makeText(context, ret.statusMessage, Toast.LENGTH_LONG).show();
                    }
                }
            }.execute(getUrl("/transfer"));
        }
    }

    public void registerGCM(){
        final GoogleCloudMessaging gcm;

        final String SENDER_ID = "10322736457"+me.id;
        gcm = GoogleCloudMessaging.getInstance(this);


        new PostTask(this){
            @Override
            protected Result doInBackground(String... params) {
                String gcm_id=null;
                try {
                    gcm_id = gcm.register(SENDER_ID);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return super.doInBackground(params[0], gcm_id);
            }

            @Override
            public List<NameValuePair> getPostData(String[] params, int i) {

                List<NameValuePair> nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("from", me.id));

                nameValuePairs.add(new BasicNameValuePair("gcm", params[i++]));

                return nameValuePairs;
            }
        }.execute(getUrl("/gcm"));
    }

    public void refresh(){
        new PostTask(this, "Refreshing data...") {
            @Override
            public List<NameValuePair> getPostData(String[] params, int i) {
                List<NameValuePair> nameValuePairs = new ArrayList<>();

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
                        ((TextView)findViewById(R.id.balance)).setText(String.format(getString(R.string.balance), me.balance));
                    } catch (Exception E) {
                        E.printStackTrace();
                    }

                }
            }
        }.execute(getUrl("/user/"+me.id));
    }
}
