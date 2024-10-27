package com.example.tarot;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class CardPickView extends View {
    private List<Card> cardList;
    private Paint paint;
    private float rotationAngle = 0f;  // Current rotate angle
    private GestureDetector gestureDetector;
    private List<RectF> cardBounds = new ArrayList<>();

    public CardPickView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();

        CardPick cardPick = new CardPick(context);
        cardList = cardPick.getCardList();

        // Initial detector, manage rotate
        gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                Log.d("CardPickView", "onScroll triggered: distanceX = " + distanceX);

                rotationAngle += distanceX / 5;
                invalidate();

                return true;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                Log.d("CardPickView", "onDown triggered");
                return true;
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        cardBounds.clear();

        float centerX = getWidth() / 2f;
        float centerY = getHeight();
        float radius = Math.min(centerX, centerY) - 100f;

        int totalCards = cardList.size();
        float startAngle = 0f;

        // Draw all cards
        for (int i = 0; i < totalCards; i++) {

            float angle = startAngle + (360f / totalCards) * i + rotationAngle;

            double radians = Math.toRadians(angle);

            float x = (float) (centerX + radius * Math.cos(radians));
            float y = (float) (centerY + radius * Math.sin(radians));

            Card card = cardList.get(i);
            Bitmap cardBitmap = card.getImage();

            float cardWidth = cardBitmap.getWidth();
            float cardHeight = cardBitmap.getHeight();
            RectF cardRect = new RectF(x - cardWidth / 2, y - cardHeight / 2, x + cardWidth / 2, y + cardHeight / 2);
            cardBounds.add(cardRect);

            canvas.save();

            canvas.translate(x, y);
            canvas.rotate(angle + 90);
            canvas.drawBitmap(cardBitmap, -cardWidth / 2, -cardHeight / 2, paint);
            canvas.restore();
        }
    }




    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 获取触摸点
        float touchX = event.getX();
        float touchY = event.getY();

        // 检查是否触摸到了卡片
        for (RectF cardRect : cardBounds) {
            if (cardRect.contains(touchX, touchY)) {
                Log.d("CardPickView", "Touched a card, enabling rotation");
                return gestureDetector.onTouchEvent(event);  // 只有触摸到卡片时才启用旋转
            }
        }

        // 如果没有触摸到卡片，则不处理事件
        Log.d("CardPickView", "Touched outside card area");
        return false;
    }
}
