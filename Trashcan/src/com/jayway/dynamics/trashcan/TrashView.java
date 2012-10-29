package com.jayway.dynamics.trashcan;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;

public class TrashView extends View {

    public static final int ITEM_SIZE = 50;

    // the trashable items
    private ArrayList<Point> items = new ArrayList<Point>();

    // paint used to draw with
    Paint paint = new Paint();

    // The lid angle
    private Dynamics lidAngle = new Dynamics(100, 0.5f);

    // open closed state of the trashcan
    private boolean trashcanOpen = false;

    // bitmaps
    private Bitmap trashcan;
    private Bitmap lid;
    private Bitmap paper;

    // position of trashcan
    private int trashLeft;
    private int trashTop;

    // touch states
    private Point touchedItem;
    private float touchedItemOffsetX;
    private float touchedItemOffsetY;

    private Runnable lidAnimator = new Runnable() {
        @Override
        public void run() {
            long now = AnimationUtils.currentAnimationTimeMillis();
            lidAngle.update(now);
            if (!lidAngle.isAtRest()) {
                postDelayed(this, 16);
            }
            invalidate();
        }
    };

    public TrashView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        loadBitmaps();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            return handleTouchDown(event);

        case MotionEvent.ACTION_MOVE:
            handleTouchMove(event);
            return true;

        default:
            handleTouchUp();
            return true;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        trashLeft = (w - trashcan.getWidth()) / 2;
        trashTop = h - trashcan.getHeight() - 20;
        createItems();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawTrashCan(canvas);
        drawItems(canvas);
    }

    private void loadBitmaps() {
        trashcan = BitmapFactory.decodeResource(getResources(), R.drawable.trashcan);
        lid = BitmapFactory.decodeResource(getResources(), R.drawable.trashcan_lid);
        paper = BitmapFactory.decodeResource(getResources(), R.drawable.paper);
    }

    private boolean handleTouchDown(MotionEvent event) {
        Point item = getContainingItem(event.getX(), event.getY());
        if (item != null) {
            touchedItem = item;
            touchedItemOffsetX = touchedItem.x - event.getX();
            touchedItemOffsetY = touchedItem.y - event.getY();
            return true;
        }
        return false;
    }

    private void handleTouchMove(MotionEvent event) {
        touchedItem.x = (int) (event.getX() + touchedItemOffsetX);
        touchedItem.y = (int) (event.getY() + touchedItemOffsetY);

        if (!trashcanOpen && isOverTrashCan(event.getX(), event.getY())) {
            openTrashcan();
        }
        if (trashcanOpen && !isOverTrashCan(event.getX(), event.getY())) {
            closeTrashcan();
        }

        invalidate();
    }

    private void handleTouchUp() {
        if (trashcanOpen) {
            trashItem(touchedItem);
        }
        touchedItem = null;
        closeTrashcan();

        // create som new items if we're out of them
        if (items.size() == 0) {
            createItems();
        }
        invalidate();
    }

    private Point getContainingItem(float x, float y) {
        for (int i = items.size() - 1; i >= 0; i--) {
            Point item = items.get(i);
            if (item.x - ITEM_SIZE < x && x < item.x + ITEM_SIZE && item.y - ITEM_SIZE < y
                    && y < item.y + ITEM_SIZE) {
                return item;
            }
        }
        return null;
    }

    private boolean isOverTrashCan(float x, float y) {
        if (y > trashTop - lid.getHeight() && trashLeft < x && x < trashLeft + trashcan.getWidth()) {
            return true;
        }
        return false;
    }

    private void openTrashcan() {
        long now = AnimationUtils.currentAnimationTimeMillis();
        lidAngle.setTargetPosition(-135, now);
        removeCallbacks(lidAnimator);
        post(lidAnimator);
        trashcanOpen = true;
    }

    private void closeTrashcan() {
        long now = AnimationUtils.currentAnimationTimeMillis();
        lidAngle.setTargetPosition(0, now);
        removeCallbacks(lidAnimator);
        post(lidAnimator);
        trashcanOpen = false;
    }

    private void trashItem(Point item) {
        items.remove(item);
    }

    private void createItems() {
        for (int i = 0; i < 10; i++) {
            int xPos = (int) (getWidth() * Math.random());
            int yPos = (int) ((getHeight() - trashcan.getHeight() * 2) * Math.random());
            items.add(new Point(xPos, yPos));
        }
    }

    private void drawTrashCan(Canvas canvas) {
        paint.setAlpha(0xFF);
        canvas.drawBitmap(trashcan, trashLeft, trashTop, paint);
        canvas.save();
        float angle = lidAngle.getPosition();
        angle = angle > 0 ? -angle : angle;
        canvas.rotate(angle, trashLeft, trashTop);
        canvas.drawBitmap(lid, trashLeft, trashTop - lid.getHeight(), paint);
        canvas.restore();
    }

    private void drawItems(Canvas canvas) {
        for (Point item : items) {
            paint.setAlpha(item == touchedItem ? 0x80 : 0xFF);
            canvas.drawBitmap(paper, item.x - paper.getWidth() / 2, item.y - paper.getHeight() / 2,
                    paint);
        }
    }
}
