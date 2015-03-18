package com.example.lgpc.util;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lgpc on 2015-03-16.
 */
public class http_connection_LG0001 extends http_connection {


    public http_connection_LG0001(Context context,String url,ArrayAdapter<String> adapter){
        super(context,url,adapter);
    }
    @Override
    public void GetStringToPost(String cont) {
        JSONObject jObject = new JSONObject();
        JSONArray jArray = new JSONArray();
        JSONObject sObject = new JSONObject();//jArray 내에 들어갈 json
        try {
            sObject.put("usr_uuid", "444322223344");
            jArray.put(sObject);
            jObject.put("_req_data", jArray);//배열을 넣음
            jObject.put("_req_svc", "LG0001");
        }catch(JSONException e){
            e.printStackTrace();
        }
        jtoString = jObject.toString();
        Log.i("asd", jtoString);
    }
}
