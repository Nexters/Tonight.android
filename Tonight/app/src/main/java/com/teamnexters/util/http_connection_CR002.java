package com.teamnexters.util;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.teamnexters.tonight.OnAirFragment;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lgpc on 2015-03-17.
 */
public class http_connection_CR002 extends http_connection {

    //public static JSONObject res;

    public http_connection_CR002(Context context,String url,ArrayAdapter<String> adapter){
        super(context,url,adapter);
        //Log.i("어뎁터다",this.adapter.toString());

    }
    @Override
    public void GetStringToPost(String cont) {
        JSONObject jObject = new JSONObject();
        JSONArray jArray = new JSONArray();
        JSONObject sObject = new JSONObject();//jArray 내에 들어갈 json
        try {
            sObject.put("type", "1");
            sObject.put("chat_no","1");
            sObject.put("cnt","30");
            jArray.put(sObject);
            jObject.put("_req_data", jArray);//배열을 넣음
            jObject.put("_req_svc", "CR0002");
        }catch(JSONException e){
            e.printStackTrace();
        }
        jtoString = jObject.toString();
        Log.i("asd", jtoString);
    }


    @Override
    public JSONObject Post_Response(){
        //this.values=values;
        Log.i("POST_RESP",Long.toString(Thread.currentThread().getId()));
        try {
            RequestParams paramList = new RequestParams("JSONData",jtoString );
            //AsyncHttpClient mClient = new AsyncHttpClient();
            JsonHttpResponseHandler jsonHttpResponseHandler=new JsonHttpResponseHandler();

            mClient.post(context, url, paramList, new JsonHttpResponseHandler()
            {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                    ResponseData=response;

                    asd();
                    Log.i("out_of_Asd",Thread.currentThread().getName());
                }

            });
        } catch (Exception e){
            e.printStackTrace();
        }
        Log.i("asdeeeeeeeeeeeee", "zizizizzi");
        return ResponseData;
    }
    public void asd(){
        parseChatJsonData.setList(ResponseData, OnAirFragment.values,this.adapter);

        Log.i("thread_Asd",Thread.currentThread().getName());
    }
}
