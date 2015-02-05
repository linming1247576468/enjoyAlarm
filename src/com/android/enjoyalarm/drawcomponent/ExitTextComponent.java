package com.android.enjoyalarm.drawcomponent;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;

/**
 * 退出文字绘制器，初始位移，缩放度和透明度分别为左上角, 1.0f, 1.0f 若某个时间点没有设置相应的变化曲线，则保持原先的值
 */
public class ExitTextComponent extends Component {

	private String[] mChars;
	private XYEntity mXyEntity;
	private float mScale;
	private float mAlpha;
	private float mTextSize;
	private float mViewWidth;
	private float mViewHeight;
	private Paint mPaint;

	public ExitTextComponent(String[] chars, float textSize, int textColor, float viewWidth, float viewHeight) {
		mChars = chars;
		mTextSize = textSize;
		mViewWidth = viewWidth;
		mViewHeight = viewHeight;
		mPaint = new Paint();
		mPaint.setTextAlign(Align.CENTER);
		mPaint.setColor(textColor);
		mXyEntity = new XYEntity(0, 0);
		mScale = 1f;
		mAlpha = 1f;
	}

	@Override
	public void draw(Canvas canvas, float nowFactor) {
		// set mScale
		mScale = getScale(nowFactor, mScale);

		// set mAlpha
		mAlpha = getAlpha(nowFactor, mAlpha);

		// set translation
		XYEntity tempEntity = getTranslation(nowFactor);
		mXyEntity = tempEntity == null ? mXyEntity : tempEntity;

		// draw
		float zeroIndex = (mChars.length - 1f) / 2;
		float nowTextSize = mTextSize * mScale;
		mPaint.setTextSize(nowTextSize);
		mPaint.setAlpha((int) (255 * mAlpha));
		float x = mViewWidth * mXyEntity.x;
		float y = mViewHeight * mXyEntity.y;
		for (int i = 0; i < mChars.length; i++) {
			canvas.drawText(mChars[i], x, y + (i - zeroIndex) * nowTextSize
					* 1.2f, mPaint);
		}
	}

}
