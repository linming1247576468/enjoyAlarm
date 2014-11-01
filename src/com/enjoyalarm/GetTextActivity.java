package com.enjoyalarm;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.scut.enjoyalarm.R;

public class GetTextActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().setWindowAnimations(R.style.GetTextStyle);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_get_text);
		final Button button = (Button) findViewById(R.id.button1);
		final ViewGroup viewGroup = (ViewGroup)button.getParent().getParent();
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		
//		viewGroup.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.get_text_open_in_anim));
	}

}
