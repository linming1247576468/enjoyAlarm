package com.enjoyalarm.view;

import android.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;

public class ToggleView extends TextView {

	private boolean mChecked;
	private Drawable mCheckedDrawable;
	private Drawable mUnCheckedDrawable;
	
	
	public ToggleView(Context context) {
		super(context);
	}
	
	public ToggleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.style.ToggleView);
	}
	
	public void setCheckedDrawable(Drawable drawable) {
		mCheckedDrawable = drawable;
	}
	
	public void setUnCheckedDrawable(Drawable drawable) {
		mUnCheckedDrawable = drawable;
	}
	
	public void setChecked(boolean checked) {
		mChecked = checked;
		if (mChecked) {
			setBackgroundDrawable(mCheckedDrawable);
		} else {
			setBackgroundDrawable(mUnCheckedDrawable);
		}
	}

	public boolean isChecked() {
		return mChecked;
	}
}
