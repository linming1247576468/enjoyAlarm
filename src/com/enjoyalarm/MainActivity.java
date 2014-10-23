package com.enjoyalarm;

import android.app.Activity;
import android.os.Bundle;

import com.enjoyalarm.view.AlarmSettingView;
import com.scut.enjoyalarm.R;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AlarmSettingView settingView = new AlarmSettingView(this, -1);
        setContentView(settingView);
    }

}
