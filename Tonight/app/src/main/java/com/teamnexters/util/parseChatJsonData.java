package com.teamnexters.util;

import android.util.Log;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by lgpc on 2015-03-17.
 */
public class parseChatJsonData {

    public parseChatJsonData(){

    }

    public static void setList(JSONObject responseData, List<String> values,ArrayAdapter<String> adapter){
        Log.i("set_LIST ",Long.toString(Thread.currentThread().getId()));
        Log.i("set_list 어뎁터",adapter.toString());
        JSONObject rsltObject;
        JSONArray rsltArray;
        JSONArray chatArray;
        JSONObject chatObject;
        try {
            String _res_svc = null;//_res_svc
            String buffer=null;
            _res_svc = responseData.getString("_res_svc");
            if(_res_svc.equals("ERROR")){
                //에러처리
            }else{
                rsltArray=responseData.getJSONArray("_res_data");
                Log.i("chat", rsltArray.toString());
                rsltObject=rsltArray.getJSONObject(0);
                chatArray=rsltObject.getJSONArray("_rslt");
                for(int i=0;i<chatArray.length();i++){
                    chatObject=chatArray.getJSONObject(i);
                    Log.i("chat",chatObject.toString());

                    String usr_nn=chatObject.getString("chat_nn");
                    String chat_cont=chatObject.getString("chat_cont");
                    buffer=usr_nn+":"+chat_cont;
                    values.add(buffer);
                }

                adapter.notifyDataSetChanged();
                //Log.i("asd",OnAirFragment.adapter.toString());
                Log.i("chat", values.toString());

            }
        }catch (JSONException e){
            e.printStackTrace();
        }
           // return user;
    }
}
