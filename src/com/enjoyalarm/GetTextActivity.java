package com.enjoyalarm;

import android.app.Activity;
import android.app.Service;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.scut.enjoyalarm.R;

public class GetTextActivity extends Activity {

	EditText mGetTextEditText;
	View mGetTextLayout;
	View mLayerView;
	Button mButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
//		getWindow().setWindowAnimations(R.style.GetTextStyle);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_text_get_layout);
		
		mGetTextEditText = (EditText) findViewById(R.id.get_text_et);
		mGetTextLayout = findViewById(R.id.get_text_layout);
		mLayerView = findViewById(R.id.layer);
		mButton = (Button) findViewById(R.id.get_text_confirm_bt);
		mButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				closeGetTextWindow();
			}
		});
		
		mLayerView.setBackgroundColor(Color.parseColor("#77000000"));
		
		mGetTextLayout.setVisibility(View.GONE);
		mLayerView.setVisibility(View.GONE);
	}
	
	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		
		openGetTextWindow();
	}
	
	private void openGetTextWindow() {
		mGetTextEditText.requestFocus();
		mGetTextLayout.setVisibility(View.VISIBLE);
		mLayerView.setVisibility(View.VISIBLE);
		Animation animation1 = AnimationUtils.loadAnimation(this,
				R.anim.get_text_slide_in);
		Animation animation2 = AnimationUtils.loadAnimation(this,
				R.anim.get_text_layer_in);
		mGetTextLayout.startAnimation(animation1);
		mLayerView.startAnimation(animation2);

		// open im
		InputMethodManager im = (InputMethodManager)getSystemService(Service.INPUT_METHOD_SERVICE);
		im.showSoftInput(mGetTextEditText, InputMethodManager.SHOW_IMPLICIT);
	}

	private void closeGetTextWindow() {
		mGetTextEditText.clearFocus();
		Animation animation1 = AnimationUtils.loadAnimation(this,
				R.anim.get_text_slide_out);
		Animation animation2 = AnimationUtils.loadAnimation(this,
				R.anim.get_text_layer_out);
		animation1.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				mGetTextLayout.setVisibility(View.GONE);
				mLayerView.setVisibility(View.GONE);
			}
		});
		mGetTextLayout.startAnimation(animation1);
		mLayerView.startAnimation(animation2);

		// close im
		InputMethodManager im = (InputMethodManager)getSystemService(Service.INPUT_METHOD_SERVICE);
		im.hideSoftInputFromWindow(mGetTextEditText.getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
	}
}
