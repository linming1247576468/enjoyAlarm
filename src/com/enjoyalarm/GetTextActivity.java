package com.enjoyalarm;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.enjoyalarm.view.AlarmSettingViewManager;
import com.scut.enjoyalarm.R;

public class GetTextActivity extends Activity {

	private EditText mGetTextEditText;
	private View mGetTextLayout;
	private View mLayerView;
	private Button mConfirmButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().setWindowAnimations(R.style.GetTextEmptyAnim);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_text_get_layout);
		
		//find views
		mGetTextEditText = (EditText) findViewById(R.id.get_text_et);
		mGetTextLayout = findViewById(R.id.get_text_layout);
		mLayerView = findViewById(R.id.layer);
		mConfirmButton = (Button) findViewById(R.id.get_text_confirm_bt);
		
		//set something
		String sourceData = getIntent().getStringExtra(AlarmSettingViewManager.GET_TEXT_EXTRA_SOURCE);
		mGetTextEditText.setText(sourceData);
		mGetTextEditText.setSelection(0, sourceData.length());
		
		//set listener
		mConfirmButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String result = mGetTextEditText.getText().toString();
				Intent intent = getIntent();
				intent.putExtra(AlarmSettingViewManager.GET_TEXT_EXTRA_RESULT, result);
				setResult(RESULT_OK, intent);
				closeGetTextWindow();
			}
		});
		mLayerView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				closeGetTextWindow();
			}
		});
		
		mGetTextEditText.setFocusable(true);
		mGetTextEditText.setFocusableInTouchMode(true);
		mGetTextEditText.requestFocus();
	}
	
	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		
		openGetTextWindow();
	}
	
	private void openGetTextWindow() {
		mGetTextEditText.requestFocus();
		Animation animation1 = AnimationUtils.loadAnimation(this,
				R.anim.get_text_slide_in);
		Animation animation2 = AnimationUtils.loadAnimation(this,
				R.anim.get_text_layer_in);
		mGetTextLayout.startAnimation(animation1);
		mLayerView.startAnimation(animation2);

		//android:windowSoftInputMode="stateVisible" make im open when the activity starts
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
				finish();
			}
		});
		mGetTextLayout.startAnimation(animation1);
		mLayerView.startAnimation(animation2);

		// close im
		InputMethodManager im = (InputMethodManager)getSystemService(Service.INPUT_METHOD_SERVICE);
		im.hideSoftInputFromWindow(mGetTextEditText.getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
	}

	@Override
	public void onBackPressed() {
		closeGetTextWindow();
	}
}
