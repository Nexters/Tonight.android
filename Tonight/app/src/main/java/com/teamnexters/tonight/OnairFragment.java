package com.teamnexters.tonight;
/**
 * Created by lgpc on 2015-03-16.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.teamnexters.util.httpClient;
import com.teamnexters.util.setString;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.teamnexters.util.http_connection_CR002;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OnAirFragment extends Fragment implements AbsListView.OnScrollListener , View.OnClickListener {
    public static List<String> values = new ArrayList<String>();
    public  ArrayAdapter<String> adapter;
    private static final String MSG = "OnairFragemnt";
    private ListView lv;
    private EditText editText;
    private InputMethodManager imm;
    private LayoutInflater mInflater;
    private boolean lastitemVisibleFlag = false;
    private View footView;
    private static String lastChatNum;


    private final String url="http://ssss.maden.kr/gateway";
    private static http_connection_CR002 http_CR0002;

    private boolean mLockListView = false;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        /////////////// 제일처음 연결 객체 초기화
        AsyncHttpClient client = httpClient.getInstance();
        PersistentCookieStore myCookie= new PersistentCookieStore(getActivity());
        client.setCookieStore(myCookie);

        try {
            httpClient.get(url, setString.string_LG0001(), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                    Log.i("LG0001_성공", response.toString());
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_onair, container, false);
        lv = (ListView) v.findViewById(R.id.listview);

        editText=(EditText) v.findViewById(R.id.ChatBox);
        Button button=(Button) v.findViewById(R.id.ChatButton);
        button.setOnClickListener(this);

        adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item, R.id.textview, values);
        http_CR0002 = new http_connection_CR002(getActivity(),url,adapter);
        mInflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        footView = mInflater.inflate(R.layout.footer,null);
        footView.setVisibility(View.INVISIBLE);

        lv.addFooterView(footView);
        lv.setFooterDividersEnabled(false);

        lv.setOnScrollListener(this);
        lv.setAdapter(adapter);
        Log.i(MSG, "onCreateView");


        return v;
    }
    @Override
    public void onClick(View v){

        Log.i("ONCLICK", values.toString());

        String cont= editText.getText().toString();
        editText.setText("");
        try {
            httpClient.get(url, setString.string_CR0001(cont), new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                    Log.i("CR0001_성공", response.toString());
                }
            });
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static OnAirFragment newInstance(String text) {

        OnAirFragment f = new OnAirFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

        if(scrollState == SCROLL_STATE_IDLE && lastitemVisibleFlag && mLockListView == false) {
            footView.setVisibility(View.VISIBLE);
            //lv.addFooterView(footView);

            mLockListView=true;

            Runnable run = new Runnable() {
                @Override
                public void run() {
                    try {
                        httpClient.get(url, setString.string_CR0002(), new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                                JSONObject rsltObject;
                                JSONArray rsltArray;
                                JSONArray chatArray;
                                JSONObject chatObject;
                                try {
                                    String _res_svc = null;//_res_svc
                                    String buffer=null;
                                    _res_svc = response.getString("_res_svc");
                                    if(_res_svc.equals("ERROR")){
                                        //에러처리
                                    }else{
                                        rsltArray=response.getJSONArray("_res_data");
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
                                Log.i("CR0002_성공", response.toString());
                                mLockListView = false;
                                footView.setVisibility(View.INVISIBLE);
                            }
                        });
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            adapter.notifyDataSetChanged();
            android.os.Handler asd = new android.os.Handler();
            asd.postDelayed(run, 1000);
        }

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        lastitemVisibleFlag= (totalItemCount>0) && (firstVisibleItem+visibleItemCount >= totalItemCount);
    }
}

