package com.android.enjoyalarm;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils.TruncateAt;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.enjoyalarm.view.ToggleView;

public class MusicSelectActivity extends Activity implements OnClickListener{

	
	ToggleView mTagRingtoneTg;
	ToggleView mTagMusicTg;
	Button mConfirmBt;
	TextView mShowTv;
	ViewPager mVp;
	ListView mRingtoneLv;
	ListView mMusicLv;
	String mDefaultMusicTitle;
	int mSelectType;//0 ringtone 1 music
	int mSelectPos;
	List<String> mRingtoneTitles;
	List<String> mRingtoneUris;
	List<String> mMusicTitles;
	List<String> mMusicUris;
	boolean mHasRingtone = true;
	boolean mHasMusic = true;
	boolean mHasSelect;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_music_select);
		
		final ViewGroup layout = (ViewGroup) findViewById(R.id.music_select_layout);
		layout.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			@Override
			public void onGlobalLayout() {
				layout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				findDefaultMusicPosition();
			}
		});
		mDefaultMusicTitle = getIntent().getStringExtra(
				ActivityVariable.MUSIC_SELECT_EXTRA_ORIGINAL_MUSIC_TITLE);

		findViews();
		initViews();
	}
	

	private void findViews() {
		mTagRingtoneTg = (ToggleView) findViewById(R.id.music_select_tag_ringtone_tg);
		mTagMusicTg = (ToggleView) findViewById(R.id.music_select_tag_music_tg);
		mConfirmBt = (Button) findViewById(R.id.music_select_confirm_bt);
		mShowTv = (TextView) findViewById(R.id.music_select_show_tv);
		mVp = (ViewPager) findViewById(R.id.music_select_vp);
	}
	
	private void initViews() {
		//find ringtone
		RingtoneManager manager = new RingtoneManager(this);
		manager.setType(RingtoneManager.TYPE_RINGTONE);
		Cursor cursor = manager.getCursor();
		mRingtoneTitles = new ArrayList<String>();
		mRingtoneUris = new ArrayList<String>();
		if (cursor == null) {
			mHasRingtone = false;
		} else {
			cursor.moveToFirst();
			cursor.moveToPrevious();
			while (cursor.moveToNext()) {
				String title = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
				mRingtoneTitles.add(title);
				mRingtoneUris.add(manager.getRingtoneUri(cursor.getPosition()).toString());
			}
		}
		
		//find music
		mMusicTitles = new ArrayList<String>();
		mMusicUris = new ArrayList<String>();
		cursor = getContentResolver().query(
				MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
				new String[] { MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE},
				MediaStore.Audio.Media.IS_MUSIC + "=?", new String[]{"1"},
				MediaStore.Audio.Media.TITLE);
		if (cursor == null) {
			mHasMusic = false;
		} else {
			while (cursor.moveToNext()) {
				String title = cursor.getString(1);
				mMusicTitles.add(title);
				mMusicUris.add(ContentUris.withAppendedId(
						MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
						cursor.getLong(0)).toString());
			}
		}
		
		//init ListView
		if (mHasRingtone) {
			mRingtoneLv = new ListView(this);
			mRingtoneLv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			final LayoutInflater inflater = getLayoutInflater();
			final int padding = (int)(10 * getResources().getDisplayMetrics().density);
			TextView headerView = new TextView(this);
			headerView.setText(getResources().getString(R.string.music_select_header_text)
					.replace("##", String.valueOf(mRingtoneTitles.size())));
			headerView.setGravity(Gravity.CENTER);
			headerView.setBackgroundColor(Color.rgb(230, 230, 230));
			headerView.setPadding(padding, padding, padding, padding);
			mRingtoneLv.addHeaderView(headerView);
			
			mRingtoneLv.setAdapter(new BaseAdapter() {
				@Override
				public View getView(int position, View convertView, ViewGroup parent) {
					TextView view;
					if (convertView == null) {
						TextView textView = (TextView) inflater.inflate(
								android.R.layout.simple_list_item_single_choice,
								parent, false);
						textView.setTextSize(20);
						textView.setPadding(padding, padding, padding, padding);
						textView.setSingleLine();
						textView.setEllipsize(TruncateAt.END);
						view = textView;
					} else {
						view = (TextView) convertView;
					}
					view.setText(mRingtoneTitles.get(position));
					
					return view;
				}
				
				@Override
				public long getItemId(int position) {
					return 0;
				}
				
				@Override
				public Object getItem(int position) {
					return null;
				}
				
				@Override
				public int getCount() {
					return mRingtoneTitles.size();
				}
			});
			mRingtoneLv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					if (position > 0) {//pass header view
						mSelectType = 0;
						mSelectPos = position - 1;
						mShowTv.setText(mRingtoneTitles.get(position - 1));
						mHasSelect = true;
					}
				}
			});
		}
		if (mHasMusic) {
			mMusicLv = new ListView(this);
			mMusicLv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			final LayoutInflater inflater = getLayoutInflater();
			final int padding = (int)(10 * getResources().getDisplayMetrics().density);
			TextView headerView = new TextView(this);
			headerView.setText(getResources().getString(R.string.music_select_header_text)
					.replace("##", String.valueOf(mMusicTitles.size())));
			headerView.setGravity(Gravity.CENTER);
			headerView.setBackgroundColor(Color.rgb(230, 230, 230));
			headerView.setPadding(padding, padding, padding, padding);
			mMusicLv.addHeaderView(headerView);
			
			mMusicLv.setAdapter(new BaseAdapter() {
				@Override
				public View getView(int position, View convertView, ViewGroup parent) {
					TextView view;
					if (convertView == null) {
						TextView textView = (TextView) inflater.inflate(
								android.R.layout.simple_list_item_single_choice,
								parent, false);
						textView.setTextSize(20);
						textView.setPadding(padding, padding, padding, padding);
						textView.setSingleLine();
						textView.setEllipsize(TruncateAt.END);
						view = textView;
					} else {
						view = (TextView) convertView;
					}
					view.setText(mMusicTitles.get(position));
					
					return view;
				}
				
				@Override
				public long getItemId(int position) {
					return 0;
				}
				
				@Override
				public Object getItem(int position) {
					return null;
				}
				
				@Override
				public int getCount() {
					return mMusicTitles.size();
				}
			});
			mMusicLv.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					if (position > 0) {//pass header view
						mSelectType = 1;
						mSelectPos = position - 1;
						mShowTv.setText(mMusicTitles.get(position - 1));
						mHasSelect = true;
					}
				}
			});
		}
		
		//init viewpager
		PagerAdapter pageAdapter = new PagerAdapter() {
			
			@Override
			public boolean isViewFromObject(View view, Object object) {
				return view == object;
			}
			
			@Override
			public int getCount() {
				return 2;
			}
			
			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				View view = null;
				switch(position) {
				case 0: {
					if (mHasRingtone) {
						view = mRingtoneLv;
					} else {
						TextView tv = new TextView(MusicSelectActivity.this);
						tv.setText(getResources().getString(
								R.string.music_select_no_file));
						tv.setGravity(Gravity.CENTER);
						tv.setLayoutParams(new LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.WRAP_CONTENT));
						view = tv;
					}
					break;
				}
				
				case 1: {
					if (mHasMusic) {
						view = mMusicLv;
					} else {
						TextView tv = new TextView(MusicSelectActivity.this);
						tv.setText(getResources().getString(
								R.string.music_select_no_file));
						tv.setGravity(Gravity.CENTER);
						tv.setLayoutParams(new LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.WRAP_CONTENT));
						view = tv;
					}
					break;
				}
				}
				
				container.addView(view, position);
				return view;
			}
		};
		mVp.setAdapter(pageAdapter);
		mVp.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int position) {
				setSelectTag(position);
			}
			
			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {
			}
			
			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});
	
	
		//init others
		setSelectTag(0);
		mTagRingtoneTg.setOnClickListener(this);
		mTagMusicTg.setOnClickListener(this);
		mConfirmBt.setOnClickListener(this);
		if (mDefaultMusicTitle == null) {
			mShowTv.setText(getResources().getString(R.string.music_select_ask_action));
			
		} else {
			mShowTv.setText(mDefaultMusicTitle);
		}
	}

	
	private void findDefaultMusicPosition() {
		if (mDefaultMusicTitle != null) {
			if (mHasRingtone) {
				int index = mRingtoneTitles.indexOf(mDefaultMusicTitle);
				if (index != -1) {
					mRingtoneTitles.remove(index);
					mRingtoneTitles.add(0, mDefaultMusicTitle);
					setSelectTag(0);
					mVp.setCurrentItem(0, false);
					mRingtoneLv.setItemChecked(1, true);//pass header view
					return;
				}
			}
			if (mHasMusic) {
				int index = mMusicTitles.indexOf(mDefaultMusicTitle);
				if (index != -1) {
					mMusicTitles.remove(index);
					mMusicTitles.add(0, mDefaultMusicTitle);
					setSelectTag(1);
					mVp.setCurrentItem(1, false);
					mMusicLv.setItemChecked(1, true);//pass header view
					return;
				}
			}
		}
	}
	
	private void setSelectTag(int position) {
		switch(position) {
		case 0: {
			mTagRingtoneTg.setChecked(true);
			mTagMusicTg.setChecked(false);
			break;
		}
		
		case 1: {
			mTagRingtoneTg.setChecked(false);
			mTagMusicTg.setChecked(true);
			break;
		}
		}
	}
	
	@Override
	public void onClick(View v) {
		if (v == mTagRingtoneTg) {
			setSelectTag(0);
			mVp.setCurrentItem(0, true);
			
		} else if (v == mTagMusicTg) {
			setSelectTag(1);
			mVp.setCurrentItem(1, true);
			
		} else if (v == mConfirmBt) {
			if (mHasSelect) {
				String title;
				String uri;
				if (mSelectType == 0) {//ringtone
					title = mRingtoneTitles.get(mSelectPos);
					uri = mRingtoneUris.get(mSelectPos);
					
				} else {//music
					title = mMusicTitles.get(mSelectPos);
					uri = mMusicUris.get(mSelectPos);
				}
				Intent intent = getIntent();
				intent.putExtra(ActivityVariable.MUSIC_SELECT_EXTRA_SELECT_MUSIC_TITLE, title);
				intent.putExtra(ActivityVariable.MUSIC_SELECT_EXTRA_SELECT_MUSIC_URI, uri);
				setResult(RESULT_OK, intent);
			}
			
			finish();
		}
	}
}
