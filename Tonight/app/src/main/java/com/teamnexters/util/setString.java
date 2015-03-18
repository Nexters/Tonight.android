package com.teamnexters.util;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

/**
 * Created by lgpc on 2015-03-18.
 */
public class setString {
    private static String jtoString;
    private static JSONObject jObject;
    private static JSONArray jArray;
    private static JSONObject sObject;

    public static String string_LG0001(){
        jObject=new JSONObject();
        sObject=new JSONObject();
        jArray=new JSONArray();
        try {
            sObject.put("usr_uuid", "444322223344");
            jArray.put(sObject);
            jObject.put("_req_data", jArray);//배열을 넣음
            jObject.put("_req_svc", "LG0001");
        }catch(JSONException e){
            e.printStackTrace();
        }
        jtoString = jObject.toString();
        return jtoString;
    }
    public static String string_CR0001(String cont){

        jObject=new JSONObject();
        sObject=new JSONObject();
        jArray=new JSONArray();

        try {
            sObject.put("cont", cont);
            jArray.put(sObject);
            jObject.put("_req_data", jArray);//배열을 넣음
            jObject.put("_req_svc", "CR0001");
        }catch(JSONException e){
            e.printStackTrace();
        }
        jtoString = jObject.toString();
        return jtoString;
    }

    public static String string_CR0002(){
        jObject = new JSONObject();
        jArray = new JSONArray();
        sObject = new JSONObject();//jArray 내에 들어갈 json
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
        //Log.i("asd", jtoString);
        return jtoString;
    }

}
