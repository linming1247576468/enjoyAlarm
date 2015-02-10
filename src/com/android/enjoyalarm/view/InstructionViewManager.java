package com.android.enjoyalarm.view;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.enjoyalarm.R;

public class InstructionViewManager implements OnClickListener{

	private Context mContext;
	private ViewGroup mMainView;
	private TextView mShowTv;
	private Button mNextBt;
	private int mStepIndex;
	private OnNextListener mOnNextListener;
	
	
	public InstructionViewManager(Context context) {
		mContext = context;
		mMainView = (ViewGroup) View.inflate(context, R.layout.alarm_instruction_layout, null);
		mShowTv = (TextView)mMainView.findViewById(R.id.instr_show_view);
		mNextBt = (Button)mMainView.findViewById(R.id.instr_next_bt);
		mNextBt.setOnClickListener(this);
		
		init();
	}
	
	/**
	 * set the beginning of instruction
	 */
	public void init() {
		mNextBt.setText(mContext.getResources().getString(R.string.instuction_next_word));
		mStepIndex = 1;
	}
	
	public View getMainView() {
		return mMainView;
	}
	
	public void setShowText(String text) {
		mShowTv.setText(text);
	}
	
	public void setFinishShowing() {
		mNextBt.setText(mContext.getResources().getString(R.string.instruction_finish_word));
	}

	@Override
	public void onClick(View v) {
		mOnNextListener.onNext(mStepIndex++);
	}
	
	public void setOnNextListener(OnNextListener listener) {
		mOnNextListener = listener;
	}
	
	
	
	interface OnNextListener {
		/**
		 * 
		 * @param stepIndex   start at 1
		 */
		void onNext(int nextIndex);
	}
}
