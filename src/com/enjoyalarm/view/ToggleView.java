package com.enjoyalarm.view;

import com.scut.enjoyalarm.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;

public class ToggleView extends TextView {

	private boolean mChecked;
	private Drawable mCheckedDrawable;
	private Drawable mUnCheckedDrawable;

	/**
	 * default background is unCheckedDrawable
	 */
	public ToggleView(Context context, Drawable checkedDrawable, Drawable unCheckedDrawable) {
		super(context);
		
		mCheckedDrawable = checkedDrawable;
		mUnCheckedDrawable = unCheckedDrawable;
		setChecked(false);
	}

	public ToggleView(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray typedArray = context.obtainStyledAttributes(attrs,
				R.styleable.ToggleView, 0, 0);
		mCheckedDrawable = typedArray
				.getDrawable(R.styleable.ToggleView_CheckedDrawable);
		mUnCheckedDrawable = typedArray
				.getDrawable(R.styleable.ToggleView_UnCheckedDrawable);
		typedArray.recycle();

		setChecked(false);
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
