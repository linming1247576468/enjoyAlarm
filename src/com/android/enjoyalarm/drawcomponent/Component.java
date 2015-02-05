package com.android.enjoyalarm.drawcomponent;

import java.util.ArrayList;
import java.util.List;

import com.android.enjoyalarm.alarmliststate.StatePeriod.Period1;
import com.android.enjoyalarm.alarmliststate.StatePeriod.Period2;

import android.graphics.Canvas;

/**
 * 组件绘制器，由外部定义变量（位移，透明度，缩放度）的变化曲线（线性）
 *
 */
public abstract class Component {

	private List<Entry1> mScaleEntryList = new ArrayList<Entry1>();
	private List<Entry1> mAlphaEntryList = new ArrayList<Entry1>();
	private List<Entry2> mTranslationEntryList = new ArrayList<Entry2>();

	/**
	 * 
	 * 绘制当前时间点的画面
	 */
	abstract public void draw(Canvas canvas, float nowFactor);

	/**
	 * 设置缩放曲线;
	 */
	public void addScaleEntry(float startFactor, float endFactor,
			float startScale, float endScale) {
		this.mScaleEntryList.add(new Entry1(startFactor, endFactor, startScale,
				endScale));
	}

	public void addScaleEntry(Period1 period) {
		this.mScaleEntryList.add(new Entry1(period.sFactor, period.eFactor,
				period.sValue, period.eValue));
	}

	/**
	 * 设置透明度曲线; alpha:[0,1]
	 */
	public void addAlphaEntry(float startFactor, float endFactor,
			float startAlpha, float endAlpha) {
		this.mAlphaEntryList.add(new Entry1(startFactor, endFactor, startAlpha,
				endAlpha));
	}

	public void addAlphaEntry(Period1 period) {
		this.mAlphaEntryList.add(new Entry1(period.sFactor, period.eFactor,
				period.sValue, period.eValue));
	}

	/**
	 * 设置位移曲线 ;位移指图片的中心坐标在相对坐标下的比例，即在可视范围内x,y均是[0,1]
	 */
	public void addTranslationEntry(float startFactor, float endFactor,
			float startX, float endX, float startY, float endY) {
		this.mTranslationEntryList.add(new Entry2(startFactor, endFactor,
				startX, endX, startY, endY));
	}

	public void addTranslationEntry(Period2 period) {
		this.mTranslationEntryList.add(new Entry2(period.sFactor,
				period.eFactor, period.sValue1, period.eValue1, period.sValue2,
				period.eValue2));
	}

	public void removeAllALphaEntry() {
		mAlphaEntryList.clear();
	}
	
	public void removeAllScaleEntry() {
		mScaleEntryList.clear();
	}
	
	public void removeAllTransEntry() {
		mTranslationEntryList.clear();
	}
	
	
	public float getScale(float nowFactor, float defaultValue) {
		for (Entry1 entry : mScaleEntryList) {
			if (nowFactor >= entry.startFactor && nowFactor < entry.endFactor) {
				return entry.getValue(nowFactor);
			}
		}

		return defaultValue;
	}

	/**
	 * return alpha[0,1]
	 */
	public float getAlpha(float nowFactor, float defaultValue) {
		for (Entry1 entry : mAlphaEntryList) {
			if (nowFactor >= entry.startFactor && nowFactor < entry.endFactor) {
				return entry.getValue(nowFactor);
			}
		}

		return defaultValue;
	}


	
	/**
	 * 返回在相对坐标下的比例; 若没有设置当前点，则返回null
	 */
	public XYEntity getTranslation(float nowFactor) {
		for (Entry2 entry : mTranslationEntryList) {
			if (nowFactor >= entry.startFactor && nowFactor < entry.endFactor) {
				return new XYEntity(entry.getValue1(nowFactor),
						entry.getValue2(nowFactor));
			}
		}

		return null;
	}


	public class XYEntity {
		public float x;
		public float y;

		public XYEntity(float x, float y) {
			this.x = x;
			this.y = y;
		}
	}
}
