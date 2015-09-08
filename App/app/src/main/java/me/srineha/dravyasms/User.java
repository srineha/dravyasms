package me.srineha.dravyasms;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by prateek on 9/9/15.
 */
public class User {
    String id , name , phone ;
    int balance;

    public  User(){}

    public  User (JSONObject user){
        id=user.optString("id");
        name=user.optString("name");
        phone=user.optString("phone");
        balance=user.optInt("balance");
    }

    @Override
    public String toString(){
        JSONObject user = new JSONObject();
        try {
            user.put("id",id);
            user.put("name",name);
            user.put("phone",phone);
            user.put("balance",balance);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return user.toString();
    }
}
