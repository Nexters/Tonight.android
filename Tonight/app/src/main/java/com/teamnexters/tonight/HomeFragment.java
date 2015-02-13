package com.teamnexters.tonight;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;

public class HomeFragment extends Fragment {
    private static final String ARG_PARAM = "param";
    private static final String MSG = "HomeFragment";
    private String mParam;

    private Button btnOnAir;
    private Button button2;

    String usr_uuid = null;
    String alarmyn = "Y";
    final String LG0001 = "LG0001"; //로그인

    static final String url = "http://jung2.maden.kr/gateway/";

    public static HomeFragment newInstance(String param) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM, param);
        fragment.setArguments(args);
        return fragment;
    }

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //GCM 서비스 등록
        //GCMRegistrar.checkDevice(getActivity());
        //GCMRegistrar.checkManifest(getActivity());

        if (getArguments() != null) {
            mParam = getArguments().getString(ARG_PARAM);
        }
        //서버와의 연결 체크
        if(isConnected()) {
            Log.d(MSG, "서버와 연결되었습니다");
            Toast.makeText(getActivity().getApplicationContext(), "서버와 연결되었습니다", Toast.LENGTH_SHORT).show();
        }else{
            Log.d(MSG, "서버와 연결되지 않았습니다");
            Toast.makeText(getActivity().getApplicationContext(), "서버와 연결되지 않았습니다", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        btnOnAir = (Button) view.findViewById(R.id.btn_on_air);

        /**
         * 임시용!!!! UUID,PUSHID를 JSON으로 서버에 데이터보내기.
         */
        button2 = (Button) view.findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //기계 UUID 가져옴
                usr_uuid = GetDevicesUUID(getActivity().getBaseContext());
                Log.i(MSG,"기기UUID값 :" + usr_uuid);
                Toast.makeText(getActivity().getApplicationContext(), usr_uuid, Toast.LENGTH_SHORT).show();
                //push id
                String usr_pushid;
                usr_pushid = GCMRegistrar.getRegistrationId(getActivity().getBaseContext());
                Log.i(MSG,"pushID :" + usr_pushid);
                Toast.makeText(getActivity().getApplicationContext(), usr_pushid, Toast.LENGTH_SHORT).show();

                JSONObject jObject = new JSONObject();
                JSONArray jArray = new JSONArray();
                JSONObject sObject = new JSONObject();//jArray 내에 들어갈 json
                try
                {
                    sObject.put("alarmyn", alarmyn);
                    sObject.put("usr_pushid", "임시값dkfkejkf");
                    sObject.put("usr_uuid", usr_uuid);
                    jArray.put(sObject);
                    jObject.put("_req_data", jArray);//배열을 넣음
                    jObject.put("_req_svc", LG0001);
                }
                catch (JSONException e)
                {e.printStackTrace();}

                //원하는 json데이터 값 : jstring
                String jstring = jObject.toString();
                Log.i(MSG,"json :" + jstring);
                Toast.makeText(getActivity().getApplicationContext(), jstring, Toast.LENGTH_SHORT).show();

                DefaultHttpClient client = new DefaultHttpClient();
                try {
                    //android 3.0 부터는 네트워크작업을 UI쓰레드가 아닌 별도의 쓰레드로 돌려야해서
                    if(android.os.Build.VERSION.SDK_INT > 9) {
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        StrictMode.setThreadPolicy(policy);

                        //연결지연시
                        HttpParams params = client.getParams();
                        HttpConnectionParams.setConnectionTimeout(params, 3000);
                        HttpConnectionParams.setSoTimeout(params, 3000);
                        //Json 데이터를 서버로 전송
                        HttpPost httpPost = new HttpPost(url);
                        httpPost.setEntity(new StringEntity(jstring));
                        httpPost.setHeader("Accept", "application/json");
                        httpPost.setHeader("Content-type", "application/json");
                        //데이터보낸 뒤 서버에서 데이터를 받아오는 과정
                        HttpResponse response = client.execute(httpPost);
                        BufferedReader bufferReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
                        String line = null;
                        String result = "";

                        while ((line = bufferReader.readLine()) != null) {
                            result += line;
                            Log.i(MSG, result);
                        }
                    } //if문(android version)
                    else{
                        Log.i(MSG, "android version이 3.0 이하");
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                    client.getConnectionManager().shutdown(); // 연결 지연 종료
                }


            }
        });

        /**
         * 임시용!!!! UUID,PUSHID를 JSON으로 서버에 데이터보내기.
         */
        button2 = (Button) view.findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //기계 UUID 가져옴
                usr_uuid = GetDevicesUUID(getActivity().getBaseContext());
                Log.i(MSG,"기기UUID값 :" + usr_uuid);
                Toast.makeText(getActivity().getApplicationContext(), usr_uuid, Toast.LENGTH_SHORT).show();
                //push id
                String usr_pushid;
                usr_pushid = GCMRegistrar.getRegistrationId(getActivity().getBaseContext());
                Log.i(MSG,"pushID :" + usr_pushid);
                Toast.makeText(getActivity().getApplicationContext(), usr_pushid, Toast.LENGTH_SHORT).show();

                JSONObject jObject = new JSONObject();
                JSONArray jArray = new JSONArray();
                JSONObject sObject = new JSONObject();//jArray 내에 들어갈 json
                try
                {
                    sObject.put("alarmyn", alarmyn);
                    sObject.put("usr_pushid", "임시값dkfkejkf");
                    sObject.put("usr_uuid", usr_uuid);
                    jArray.put(sObject);
                    jObject.put("_req_data", jArray);//배열을 넣음
                    jObject.put("_req_svc", LG0001);
                }
                catch (JSONException e)
                {e.printStackTrace();}

                //원하는 json데이터 값 : jstring
                String jstring = jObject.toString();
                Log.i(MSG,"json :" + jstring);
                Toast.makeText(getActivity().getApplicationContext(), jstring, Toast.LENGTH_SHORT).show();

                DefaultHttpClient client = new DefaultHttpClient();
                try {
                    //android 3.0 부터는 네트워크작업을 UI쓰레드가 아닌 별도의 쓰레드로 돌려야해서
                    if(android.os.Build.VERSION.SDK_INT > 9) {
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        StrictMode.setThreadPolicy(policy);

                        //Param 설정
                        ArrayList<NameValuePair> paramList = new ArrayList<NameValuePair>();
                        paramList.add(new BasicNameValuePair("JSONData", jstring));

                        //연결지연시
                        HttpParams params = client.getParams();
                        HttpConnectionParams.setConnectionTimeout(params, 3000);
                        HttpConnectionParams.setSoTimeout(params, 3000);
                        //Json 데이터를 서버로 전송
                        HttpPost httpPost = new HttpPost(url);
                        httpPost.setEntity(new UrlEncodedFormEntity(paramList, "UTF-8"));
                        httpPost.setHeader("Accept", "application/json");
                        httpPost.setHeader("Content-type", "application/json");
                        //데이터보낸 뒤 서버에서 데이터를 받아오는 과정
                        HttpResponse response = client.execute(httpPost);
                        BufferedReader bufferReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
                        String line = null;
                        String result = "";

                        while ((line = bufferReader.readLine()) != null) {
                            result += line;
                            Log.i(MSG, result);
                        }
                    } //if문(android version)
                    else{
                        Log.i(MSG, "android version이 3.0 이하");
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                    client.getConnectionManager().shutdown(); // 연결 지연 종료
                }


            }
        });
        return view;
    }

    //서버와의 연결여부
    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    //UUID값 가져오기
    private String GetDevicesUUID(Context mContext) {
        TelephonyManager tManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = tManager.getDeviceId();
        return deviceId;
    }

    //서버로 request
    //지금은 요청만 보내면되니깐 메소드생략
    /*public static String makeRequest(String uri, String json) {
        try {
            HttpPost httpPost = new HttpPost(uri);
            httpPost.setEntity(new StringEntity(json));
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            return new DefaultHttpClient().execute(httpPost);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }*/

    //push id GCM
    //permission.C2D_MESSAGE 에러남 처리해야함
    public class GCMIntentService extends GCMBaseIntentService {
        private static final String tag = "GCMIntentService";
        //PROJECT_ID = google api id값
        private static final String PROJECT_ID = "임시sZmSuG1DSDPqlXUYCM";
        //public 기본 생성자를 무조건 만들어야 한다.
        public GCMIntentService(){ this(PROJECT_ID); }

        public GCMIntentService(String project_id) { super(project_id); }

        //푸시로 받은 메시지
        @Override
        protected void onMessage(Context context, Intent intent) {
            Bundle b = intent.getExtras();

            Iterator<String> iterator = b.keySet().iterator();
            while(iterator.hasNext()) {
                String key = iterator.next();
                String value = b.get(key).toString();
                Log.d(tag, "onMessage. "+key+" : "+value);
            }
        }

        //에러 발생시
        @Override
        protected void onError(Context context, String errorId) {
            Log.d(tag, "onError. errorId : "+errorId);
        }

        //단말에서 GCM 서비스 등록 했을 때 등록 id를 받는다
        @Override
        protected void onRegistered(Context context, String regId) {
            Log.d(tag, "onRegistered. regId : "+regId);
        }

        //단말에서 GCM 서비스 등록 해지를 하면 해지된 등록 id를 받는다
        @Override
        protected void onUnregistered(Context context, String regId) {
            Log.d(tag, "onUnregistered. regId : "+regId);
        }
    }


}
