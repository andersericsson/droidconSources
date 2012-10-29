package com.jayway.dynamics.expander;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

public class ExpanderView extends FrameLayout {

	private static final int HANDLE_SIZE = 40;

	private boolean open;

	private Dynamics childTop = new Dynamics(400, 0.7f);

	private View child;

	private Paint paint = new Paint();

	private int touchStartY;

	private int childTopStart;

	private boolean touched;

	private VelocityTracker velocityTracker;

	private Runnable expandAnimator = new Runnable() {
		@Override
		public void run() {
			childTop.update(AnimationUtils.currentAnimationTimeMillis());
			if (!childTop.isAtRest()) {
				postDelayed(this, 16);
			}
			requestLayout();
		}
	};

	public ExpanderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setWillNotDraw(false);
		paint.setAntiAlias(true);
	}

	public void setOpen(boolean open) {
		this.open = open;
		long now = AnimationUtils.currentAnimationTimeMillis();
		int height = getChildAt(0).getMeasuredHeight();
		childTop.setTargetPosition(open ? 0 : -height, now);
		removeCallbacks(expandAnimator);
		post(expandAnimator);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (isOnHandle(event)) {
				startTouch(event);
				return true;
			} else {
				return false;
			}

		case MotionEvent.ACTION_MOVE:
			handleMove(event);
			return true;

		default:
			endTouch(event);
			return true;

		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		childTop.setPosition(open ? 0 : -child.getMeasuredHeight(),
				AnimationUtils.currentAnimationTimeMillis());
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		child = getChildAt(0);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width = MeasureSpec.makeMeasureSpec(getMeasuredWidth(),
				MeasureSpec.EXACTLY);
		int height = MeasureSpec.makeMeasureSpec(getMeasuredHeight()
				- HANDLE_SIZE, MeasureSpec.AT_MOST);
		getChildAt(0).measure(width, height);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		int cTop = (int) childTop.getPosition();
		child.layout(0, cTop, child.getWidth(), cTop + child.getHeight());
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		final int handleTop = (int) (childTop.getPosition() + child.getHeight());
		drawBackground(canvas, handleTop);
		drawHandle(canvas, handleTop);
	}

	private boolean isOnHandle(MotionEvent event) {
		final int handleTop = (int) (childTop.getPosition() + child.getHeight());
		return event.getY() > handleTop
				&& event.getY() < handleTop + HANDLE_SIZE;
	}

	private void startTouch(MotionEvent event) {
		velocityTracker = VelocityTracker.obtain();
		velocityTracker.addMovement(event);
		removeCallbacks(expandAnimator);
		touchStartY = (int) event.getY();
		childTopStart = (int) childTop.getPosition();
		touched = true;
		invalidate();
	}

	private void handleMove(MotionEvent event) {
		velocityTracker.addMovement(event);
		int diff = (int) (event.getY() - touchStartY);
		long now = AnimationUtils.currentAnimationTimeMillis();
		childTop.setPosition(childTopStart + diff, now);
		requestLayout();
	}

	private void endTouch(MotionEvent event) {
		touched = false;
		velocityTracker.addMovement(event);
		velocityTracker.computeCurrentVelocity(1000);
		float yVelocity = velocityTracker.getYVelocity();
		long now = AnimationUtils.currentAnimationTimeMillis();
		childTop.setTargetPosition(getTargetPos(yVelocity), now);
		childTop.setVelocity(yVelocity, now);

		removeCallbacks(expandAnimator);
		post(expandAnimator);

		velocityTracker.recycle();
		velocityTracker = null;
	}

	private int getTargetPos(float yVelocity) {
		boolean expanded;
		if (yVelocity < -100) {
			expanded = false;
		} else if (yVelocity > 100) {
			expanded = true;
		} else {
			expanded = Math.round(childTop.getPosition() / child.getHeight()) >= 0;
		}
		int targetPos = expanded ? 0 : -child.getHeight();
		return targetPos;
	}

	private void drawBackground(Canvas canvas, final int handleTop) {
		paint.setColor(0xFFDDDDDD);
		paint.setStyle(Style.FILL);
		canvas.drawRect(0, 0, getWidth(), handleTop, paint);
	}

	private void drawHandle(Canvas canvas, final int handleTop) {
		paint.setColor(touched ? 0xFFA0A0B0 : 0xFF808080);
		paint.setStyle(Style.FILL);
		canvas.drawRect(0, handleTop, getWidth(), handleTop + HANDLE_SIZE,
				paint);
		paint.setColor(Color.DKGRAY);
		paint.setStyle(Style.STROKE);
		paint.setStrokeWidth(2);
		float lineWidth = getWidth() / 6f;
		float lineX = (getWidth() - lineWidth) / 2f;
		for (int line = 1; line < 4; line++) {
			float lineY = handleTop + line * HANDLE_SIZE / 4f;
			canvas.drawLine(lineX, lineY, lineX + lineWidth, lineY, paint);
		}
	}

}
