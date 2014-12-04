package com.enjoyalarm.drawcomponent;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * 图片绘制器，初始位移，缩放度和透明度分别为世界中心, 1.0f, 1.0f 若某个时间点没有设置相应的变化曲线，则保持原先的值
 * 
 */
public class BitmapComponent extends Component {

	private Bitmap mBackgroundBitmap;
	private float mWidth;
	private float mHeight;
	private float mViewWidth;
	private float mViewHeight;
	private float mScale;
	private float mAlpha;
	private XYEntity mXyEntity;
	private RectF mRectF;
	private Paint mPaint;

	public BitmapComponent(Bitmap bitmap, float width, float height,
			float viewWidth, float viewHeight) {

		mBackgroundBitmap = bitmap;
		mWidth = width;
		mHeight = height;
		mViewWidth = viewWidth;
		mViewHeight = viewHeight;
		mScale = 1f;
		mAlpha = 1f;
		mXyEntity = new XYEntity(0.5f, 0.5f);
		mRectF = new RectF();
		mPaint = new Paint();
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
		mPaint.setAlpha((int) (255 * mAlpha));
		float gapX = mWidth * mScale * 0.5f;
		float gapY = mHeight * mScale * 0.5f;
		mRectF.left = mViewWidth * mXyEntity.x - gapX;
		mRectF.right = mViewWidth * mXyEntity.x + gapX;
		mRectF.top = mViewHeight * mXyEntity.y - gapY;
		mRectF.bottom = mViewHeight * mXyEntity.y + gapY;
		canvas.drawBitmap(mBackgroundBitmap, null, mRectF, mPaint);
	}

}
