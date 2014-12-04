package com.enjoyalarm.drawcomponent;

/**
 * 管理2个变量
 */
public class Entry2 {

	public float startFactor;
	public float endFactor;
	public float startValue1;
	public float startValue2;
	public float endValue1;
	public float endValue2;

	public Entry2(float startFactor, float endFactor, float startValue1,
			float endValue1, float startValue2, float endValue2) {
		this.startFactor = startFactor;
		this.endFactor = endFactor;
		this.startValue1 = startValue1;
		this.startValue2 = startValue2;
		this.endValue1 = endValue1;
		this.endValue2 = endValue2;
	}

	public float getValue1(float nowFactor) {
		return startValue1 + (nowFactor - startFactor)
				* (endValue1 - startValue1) / (endFactor - startFactor);
	}

	public float getValue2(float nowFactor) {
		return startValue2 + (nowFactor - startFactor)
				* (endValue2 - startValue2) / (endFactor - startFactor);
	}
}
