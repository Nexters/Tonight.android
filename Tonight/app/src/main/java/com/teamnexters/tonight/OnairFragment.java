package com.teamnexters.tonight;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.renderscript.ScriptIntrinsicBlur;

public class OnAirFragment extends Fragment {

    private static final String MSG = "OnairFragemnt";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_onair, container, false);
        Log.i(MSG,"onCreateView");

        return v;
    }

    public static OnAirFragment newInstance(String text) {

        OnAirFragment f = new OnAirFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }
}
