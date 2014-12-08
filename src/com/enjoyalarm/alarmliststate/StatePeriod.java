package com.enjoyalarm.alarmliststate;


/**
 * 定义各个动画阶段的细节
 */
public class StatePeriod {

	//exiting
	/**
	 *EXIT_LIMIT: when within the limit, then it should recover instead of exit
	 */
	public static final float EXIT_LIMIT = 0.2f;
	public static final float EXIT_FACTOR1 = 0.3f;//开始渐隐
	
	public static final Period1 EXIT_LBITMAP_ALPHA_PERIOD1 = new Period1(0f, EXIT_FACTOR1, 1f, 1f);
	public static final Period1 EXIT_LBITMAP_ALPHA_PERIOD2 = new Period1(EXIT_FACTOR1, 1f, 1f, 0f);
	public static final Period1 EXIT_LBITMAP_SCALE_PERIOD1 = new Period1(0f, 1f, 1f, 1f);
	public static final Period2 EXIT_LBITMAP_TRANS_PREIOD1 = new Period2(0f, 1f, 0.5f, 1.5f, 0.5f, 0.5f);
	
	public static final Period1 EXIT_LTEXT_ALPHA_PERIOD1 = new Period1(0f, EXIT_FACTOR1, 1f, 1f);
	public static final Period1 EXIT_LTEXT_ALPHA_PERIOD2 = new Period1(EXIT_FACTOR1, 1f, 1f, 0f);
	public static final Period1 EXIT_LTEXT_SCALE_PERIOD1 = new Period1(0f, EXIT_FACTOR1, 1f, 1f);
	public static final Period1 EXIT_LTEXT_SCALE_PERIOD2 = new Period1(EXIT_FACTOR1, 1f, 1f, 1.5f);
	public static final Period2 EXIT_LTEXT_TRANS_PREIOD1 = new Period2(0f, EXIT_FACTOR1, 0.05f, 0.25f, 0.5f, 0.5f);
	public static final Period2 EXIT_LTEXT_TRANS_PREIOD2 = new Period2(EXIT_FACTOR1, 1f, 0.25f, 0.2f, 0.5f, 0.5f);
	
	
	
	 //anim to list
	/**
	 *  LIST_LIMIT: when within the limit, then it should recover instead of list
	 */
	public static final float LIST_LIMIT = 0.1f;
	public static final float LIST_FACTOR1 = 0.2f;//开始阶段的上下滑动:end
	public static final float LIST_FACTOR2 = 0.55f;//滑动到一定阶段不再滑动，必须放手才会反方向滑动:end
	public static final float LIST_FACTOR3 = 0.65f;//停止一小会:end
	public static final float LIST_FACTOR4 = 0.9f;//反方向滑动一会后停止滑动:end
	public static final float LIST_ITEM_SCALE = 0.6f;
	
	public static final Period1 LIST_TBITMAP_ALPHA_PERIOD1 = new Period1(0f, LIST_FACTOR3, 1f, 1f);
	public static final Period1 LIST_TBITMAP_ALPHA_PERIOD2 = new Period1(LIST_FACTOR3, LIST_FACTOR4, 1f, 0f);
	public static final Period1 LIST_TBITMAP_ALPHA_PERIOD3 = new Period1(LIST_FACTOR4, 1f, 0f, 0f);
	public static final Period1 LIST_TBITMAP_SCALE_PERIOD1 = new Period1(0f, LIST_FACTOR1, 1f, 1f);
	public static final Period1 LIST_TBITMAP_SCALE_PERIOD2 = new Period1(LIST_FACTOR1, LIST_FACTOR2, 1f, LIST_ITEM_SCALE);
	public static final Period1 LIST_TBITMAP_SCALE_PERIOD3 = new Period1(LIST_FACTOR2, 1f, LIST_ITEM_SCALE, LIST_ITEM_SCALE);
	public static final Period2 LIST_TBITMAP_TRANS_PERIOD1 = new Period2(0f, LIST_FACTOR1, 0.5f, 0.5f, 0.5f, 0.6f);
	public static final Period2 LIST_TBITMAP_TRANS_PERIOD2 = new Period2(LIST_FACTOR1, LIST_FACTOR2, 0.5f, 0.5f, 0.6f, 0.65f);
	public static final Period2 LIST_TBITMAP_TRANS_PERIOD3 = new Period2(LIST_FACTOR2, LIST_FACTOR3, 0.5f, 0.5f, 0.65f, 0.65f);
	public static final Period2 LIST_TBITMAP_TRANS_PERIOD4 = new Period2(LIST_FACTOR3, LIST_FACTOR4, 0.5f, 0.5f, 0.65f, 0.5f);
	public static final Period2 LIST_TBITMAP_TRANS_PERIOD5 = new Period2(LIST_FACTOR4, 1f, 0.5f, 0.5f, 0.5f, 0.5f);
	
	public static final Period1 LIST_LITEM_ALPHA_PERIOD1 = new Period1(0f, 1f, 1f, 1f);
	public static final Period1 LIST_LITEM_SCALE_PERIOD1 = new Period1(0f, 1f, 1f, 1f);
	public static final Period2 LIST_LITEM_TRANS_PERIOD1 = new Period2(0f, LIST_FACTOR3, -0.3f, -0.3f, 0.5f, 0.5f);
	public static final Period2 LIST_LITEM_TRANS_PERIOD2 = new Period2(LIST_FACTOR3, LIST_FACTOR4, -0.3f, -0.2f, 0.5f, 0.5f);
	public static final Period2 LIST_LITEM_TRANS_PERIOD3 = new Period2(LIST_FACTOR4, 1f, -0.2f, -0.2f, 0.5f, 0.5f);
	
	public static final Period1 LIST_CTITEM_ALPHA_PERIOD1 = new Period1(0f, LIST_FACTOR3, 0f, 0f);
	public static final Period1 LIST_CTITEM_ALPHA_PERIOD2 = new Period1(LIST_FACTOR3, LIST_FACTOR4, 0f, 1f);
	public static final Period1 LIST_CTITEM_ALPHA_PERIOD3 = new Period1(LIST_FACTOR4, 1f, 1f, 1f);
	public static final Period1 LIST_CTITEM_SCALE_PERIOD1 = new Period1(0f, 1f, 1f, 1f);
	public static final Period2 LIST_CTITEM_TRANS_PERIOD1 = new Period2(0f, LIST_FACTOR3, 0.5f, 0.5f, 0.65f, 0.65f);
	public static final Period2 LIST_CTITEM_TRANS_PERIOD2 = new Period2(LIST_FACTOR3, LIST_FACTOR4, 0.5f, 0.5f, 0.65f, 0.5f);
	public static final Period2 LIST_CTITEM_TRANS_PERIOD3 = new Period2(LIST_FACTOR4, 1f, 0.5f, 0.5f, 0.5f, 0.5f);
	
	
	//click anim
	public static final Period1 CLICK_ALPHA_PERIOD1 = new Period1(0f, 1f, 1f, 0.6f);
	public static final Period1 CLICK_SCALE_PERIOD1 = new Period1(0f, 1f, 1f, 1.67f);
	
	
	
	
	
	
	public static Period1 getSymmetry(Period1 period) {
		return new Period1(period.sFactor, period.eFactor, 1 - period.sValue,
				1 - period.eValue);
	}

	public static Period2 getSymmetryValue1(Period2 period) {
		return new Period2(period.sFactor, period.eFactor, 1 - period.sValue1,
				1 - period.eValue1, period.sValue2, period.eValue2);
	}

	public static Period2 getSymmetryValue2(Period2 period) {
		return new Period2(period.sFactor, period.eFactor, period.sValue1,
				period.eValue1, 1 - period.sValue2, 1 - period.eValue2);
	}

	public static Period2 getSymmetryValues(Period2 period) {
		return new Period2(period.sFactor, period.eFactor, 1 - period.sValue1,
				1 - period.eValue1, 1 - period.sValue2, 1 - period.eValue2);
	}

	public static class Period1 {
		public float sFactor;
		public float eFactor;
		public float sValue;
		public float eValue;
		
		public Period1(float sf, float ef, float sv, float ev) {
			sFactor = sf;
			eFactor = ef;
			sValue = sv;
			eValue = ev;
		}
	}
	
	public static class Period2 {
		public float sFactor;
		public float eFactor;
		public float sValue1;
		public float eValue1;
		public float sValue2;
		public float eValue2;
		
		public Period2(float sf, float ef, float sv1, float ev1, float sv2, float ev2) {
			sFactor = sf;
			eFactor = ef;
			sValue1 = sv1;
			eValue1 = ev1;
			sValue2 = sv2;
			eValue2 = ev2;
		}
	}
}
