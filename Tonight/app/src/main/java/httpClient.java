package com.teamnexters.tonight.util;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by lgpc on 2015-03-18.
 */
public class httpclient {

    //private static final String URL="http://ssss.maden.kr/gateway";
    private static AsyncHttpClient client = new AsyncHttpClient();
    private static RequestParams paramList;
    public static AsyncHttpClient getInstance(){
        return httpclient.client;
    }
    public static void get(String url,String jtoString,JsonHttpResponseHandler responseHandler){
        paramList = new RequestParams("JSONData",jtoString );
        client.get(url,paramList,responseHandler);
    }
    public static void post(String url,RequestParams params,AsyncHttpResponseHandler responseHandler){
        client.post(url,params,responseHandler);
    }
}
