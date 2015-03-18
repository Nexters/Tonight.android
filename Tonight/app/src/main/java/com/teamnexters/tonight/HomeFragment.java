package com.teamnexters.tonight;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

public class HomeFragment extends Fragment {
    private static final String ARG_PARAM = "param";
    private static final String MSG = "HomeFragment";
    private String mParam;

    private static String strJSONInput;
    private static String strJSONOutput;

    private TextView countView;
    private TextView textView;
    private ImageView animatedStar;
    private AnimationDrawable starAnimate1;
    private String res_cnt = null;
    private int _res_remain_hour;
    private int _res_remain_min;
    private int _res_remain_second;
    private long seconds = 0;
    private String usr_pushid;

    private TextView remainTime;
    private Typeface typeface;

    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    private BroadTimer broadTimer;


    String usr_uuid = null;
    String alarmyn = "Y";
    final String LG0001 = "LG0001"; //로그인

    static final String url = "http://ssss.maden.kr/gateway";

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

        usr_uuid = GetDevicesUUID(getActivity().getBaseContext());
        usr_pushid = GCMRegistrar.getRegistrationId(getActivity().getBaseContext());

        JSONObject jObject = new JSONObject();
        JSONArray jArray = new JSONArray();
        JSONObject sObject = new JSONObject();//jArray 내에 들어갈 json

        try {
            sObject.put("alarmyn", alarmyn);
            sObject.put("usr_pushid", "임시값dkfkejkf");
            sObject.put("usr_uuid", usr_uuid);
            jArray.put(sObject);
            jObject.put("_req_data", jArray);//배열을 넣음
            jObject.put("_req_svc", LG0001);

            //원하는 json데이터 값 : jstring
            String jstring = jObject.toString();
            Log.i(MSG, "json :" + jstring);
            RequestParams paramList = new RequestParams("JSONData", jstring);
            //async library 사용
            AsyncHttpClient mClient = new AsyncHttpClient();
            mClient.post(getActivity(), url, paramList, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        //데이터보낸 뒤 서버에서 데이터를 받아오는 과정
                        String _res_svc = null;//_res_svc
                        //로그인세션처리해야함
                        _res_svc = response.getString("_res_svc");
                        if (_res_svc.equals("ERROR")) {

                        } else {
                            JSONArray _res_data = null;//_res_data
                            _res_data = response.getJSONArray("_res_data");
                            JSONObject _res_result = (JSONObject) _res_data.get(0);
                            String _res_is_next_brdcast = (String) _res_result.get("is_next_brdcast");//방송등록여부
                            if (_res_is_next_brdcast.equalsIgnoreCase("y")) { //다음방송 있음. 시간/사연 정보 받기 가능

                                String _res_date = (String) _res_result.get("date");//날짜
                                _res_remain_hour = (int) _res_result.get("remain_hour");//남은hour
                                _res_remain_min = (int) _res_result.get("remain_min");//남은minute
                                _res_remain_second = (int) _res_result.get("remain_second");//남은second
                                seconds = _res_remain_hour * 3600000 + _res_remain_min * 60000 + (_res_remain_second * 1000);
                                broadTimer = new BroadTimer(seconds, 1000);
                                broadTimer.start();
                                //방송등록여부 미리체크하기.(구현해야함)
                                res_cnt = (String) _res_result.get("res_cnt");//사연갯수
                                countView.setText("오늘 온 사연 " + res_cnt + "개");
                                String res_login_yn = (String) _res_result.get("_login_yn");//로그인여부
                                System.out.println("#############" + _res_svc + "@@@@@@@" + _res_is_next_brdcast + _res_date + res_cnt + res_login_yn);
                                String time1 = String.valueOf(_res_remain_hour);
                                String time2 = String.valueOf(_res_remain_min);
                                String time3 = String.valueOf(_res_remain_second);
                                System.out.println(time1 + time2 + time3);

                            } else if (_res_is_next_brdcast.equalsIgnoreCase("n")) {
                                textView.setText(R.string.broadNo);
                                
                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
        //서버와의 연결 체크
        if (isConnected()) {
            Log.d(MSG, "서버와 연결되었습니다");
            //Toast.makeText(getActivity().getApplicationContext(), "서버와 연결되었습니다", Toast.LENGTH_SHORT).show();
        } else {
            Log.d(MSG, "서버와 연결되지 않았습니다");
            //Toast.makeText(getActivity().getApplicationContext(), "서버와 연결되지 않았습니다", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        countView = (TextView) view.findViewById(R.id.countView);
        textView = (TextView) view.findViewById(R.id.textView);
        remainTime = (TextView) view.findViewById(R.id.remainTime);
        animatedStar = (ImageView) view.findViewById(R.id.stars);
        starAnimate1 = (AnimationDrawable) animatedStar.getDrawable();

        try {
            getData();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setFont();
        view.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    starAnimate1.start();
                }
                return true;
            }
        });
        return view;
    }


    private void setFont() {
        typeface = Typeface.createFromAsset(getActivity().getAssets(), "font/NotoSansCJKkr-Regular.otf");
        countView.setTypeface(typeface);
        textView.setTypeface(typeface);
        remainTime.setTypeface(typeface);
    }

    private void setTextColor() {
        textView.setTextColor(getResources().getColor(R.color.oddity));
    }

    @Override
    public void onPause() {
        super.onPause();
        starAnimate1.stop();
    }

    @Override
    public void onResume() {
        super.onResume();
        starAnimate1.start();
        try {
            getData();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getData() throws JSONException {

        JSONObject jObject = new JSONObject();
        JSONArray jArray = new JSONArray();
        JSONObject sObject = new JSONObject();//jArray 내에 들어갈 json

        try {
            sObject.put("alarmyn", alarmyn);
            sObject.put("usr_pushid", "임시값dkfkejkf");
            sObject.put("usr_uuid", usr_uuid);
            jArray.put(sObject);
            jObject.put("_req_data", jArray);//배열을 넣음
            jObject.put("_req_svc", LG0001);

            //원하는 json데이터 값 : jstring
            String jstring = jObject.toString();
            Log.i(MSG, "json :" + jstring);
            RequestParams paramList = new RequestParams("JSONData", jstring);
            //async library 사용
            AsyncHttpClient mClient = new AsyncHttpClient();
            mClient.post(getActivity(), url, paramList, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        //데이터보낸 뒤 서버에서 데이터를 받아오는 과정
                        String _res_svc = null;//_res_svc
                        //로그인세션처리해야함
                        _res_svc = response.getString("_res_svc");
                        if (_res_svc.equals("ERROR")) {

                        } else {
                            JSONArray _res_data = null;//_res_data
                            _res_data = response.getJSONArray("_res_data");
                            JSONObject _res_result = (JSONObject) _res_data.get(0);
                            String _res_is_next_brdcast = (String) _res_result.get("is_next_brdcast");//방송등록여부
                            if (_res_is_next_brdcast.equalsIgnoreCase("y")) { //다음방송 있음. 시간/사연 정보 받기 가능

                                String _res_date = (String) _res_result.get("date");//날짜
                                res_cnt = (String) _res_result.get("res_cnt");//사연갯수
                                countView.setText("오늘 온 사연 " + res_cnt + "개");
                                String res_login_yn = (String) _res_result.get("_login_yn");//로그인여부
                                System.out.println("#############" + _res_svc + "@@@@@@@" + _res_is_next_brdcast + _res_date + res_cnt + res_login_yn);
                                String time1 = String.valueOf(_res_remain_hour);
                                String time2 = String.valueOf(_res_remain_min);
                                String time3 = String.valueOf(_res_remain_second);
                                System.out.println(time1 + time2 + time3);

                            } else if (_res_is_next_brdcast.equalsIgnoreCase("n")) {
                                textView.setText(R.string.broadNo);

                            }

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //서버와의 연결여부

    public boolean isConnected() {
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

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            try {
                getData();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {

            //사용자가 보지 않았을 시
        }

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
        public GCMIntentService() {
            this(PROJECT_ID);
        }

        public GCMIntentService(String project_id) {
            super(project_id);
        }

        //푸시로 받은 메시지
        @Override
        protected void onMessage(Context context, Intent intent) {
            Bundle b = intent.getExtras();

            Iterator<String> iterator = b.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String value = b.get(key).toString();
                Log.d(tag, "onMessage. " + key + " : " + value);
            }
        }

        //에러 발생시
        @Override
        protected void onError(Context context, String errorId) {
            Log.d(tag, "onError. errorId : " + errorId);
        }

        //단말에서 GCM 서비스 등록 했을 때 등록 id를 받는다
        @Override
        protected void onRegistered(Context context, String regId) {
            Log.d(tag, "onRegistered. regId : " + regId);
        }

        //단말에서 GCM 서비스 등록 해지를 하면 해지된 등록 id를 받는다
        @Override
        protected void onUnregistered(Context context, String regId) {
            Log.d(tag, "onUnregistered. regId : " + regId);
        }
    }

    public class BroadTimer extends CountDownTimer {

        int hour;
        int min;
        int sec;

        public BroadTimer(long startTime, long interval) {
            super(startTime, interval);

        }

        @Override
        public void onTick(long millisUntilFinished) {
            Date t_d = new Date(millisUntilFinished);
            hour = (int) ((millisUntilFinished / (3600 * 1000)) % 24);
            min = (int) (millisUntilFinished / (60 * 1000)) % 60;
            sec = (int) (millisUntilFinished / 1000) % 60;
            remainTime.setText(hour + ":" + min + ":" + sec);
            //remainTime.setText(timeFormat.format(t_d.getTime()));
        }

        @Override
        public void onFinish() {
            remainTime.setText("방송중입니다!");
        }

    }


}
