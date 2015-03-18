package com.teamnexters.util;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONObject;

/**
 * Created by lgpc on 2015-03-16.
 */
public abstract class http_connection {
    public Context context;
    public String url;
    public JSONObject ResponseData;
    public String jtoString;
    public ArrayAdapter<String> adapter;
    //public List<String> values;
    public static final AsyncHttpClient mClient= new AsyncHttpClient();

    public http_connection(Context context,String url,ArrayAdapter<String> adapter){
        this.context=context;
        this.url=url;
        this.adapter=adapter;
    }
    abstract void GetStringToPost(String cont); ///각각의 코드에서
    public JSONObject Post_Response(){
        try {
            RequestParams paramList = new RequestParams("JSONData",jtoString );
            //AsyncHttpClient mClient = new AsyncHttpClient();
            //mClient.setCookieStore();
            mClient.post(context, url, paramList, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                    Log.i("asd",response.toString());
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }

       return ResponseData;
    }
}
