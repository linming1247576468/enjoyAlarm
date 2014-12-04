package com.enjoyalarm.drawcomponent;

/**
 * 管理1个变量
 */
public class Entry1 {

	public float startFactor;
	public float endFactor;
	public float startValue;
	public float endValue;

	public Entry1(float startFactor, float endFactor, float startValue,
			float endValue) {
		this.startFactor = startFactor;
		this.endFactor = endFactor;
		this.startValue = startValue;
		this.endValue = endValue;
	}

	public float getValue(float nowFactor) {
		return startValue + (nowFactor - startFactor) * (endValue - startValue)
				/ (endFactor - startFactor);
	}
}
