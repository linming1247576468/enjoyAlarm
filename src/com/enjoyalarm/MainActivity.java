package com.enjoyalarm;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

import com.enjoyalarm.view.AlarmSettingView;
import com.scut.enjoyalarm.R;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        AlarmSettingView settingView = new AlarmSettingView(this, -1);
        setContentView(settingView);
    }

}
