package me.srineha.dravyasms;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONObject;

/**
 * Created by prateek on 9/9/15.
 */
public class MyActivity extends AppCompatActivity {

    User me;
    SharedPreferences prefs;
    @Override
    protected void onCreate(Bundle icicle){
        super.onCreate(icicle);
        prefs=getSharedPreferences(getString(R.string.preferences), MODE_PRIVATE);
        try {
            me = new User(new JSONObject(prefs.getString("user", "")));
            Log.d("MyAct", me.toString());
        }catch (Exception E){
            E.printStackTrace();
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        if(me!=null){
            SharedPreferences.Editor spe = prefs.edit();
            spe.putString("user", me.toString());
            spe.apply();
        }
    }
}
