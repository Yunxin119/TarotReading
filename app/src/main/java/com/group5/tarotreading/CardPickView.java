package com.group5.tarotreading;

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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class CardPickView extends View {
    private List<Card> cardList;
    private Paint paint;
    private float overlapAmount = 100f;
    private float offsetX = 0f;
    private GestureDetector gestureDetector;
    private List<RectF> cardBounds = new ArrayList<>();
    private int pickCardLimit;
    private boolean cutCardEnabled;
    private List<Card> selectedCards = new ArrayList<>();

    public interface OnCardSelectedListener {
        void onCardSelected(Card card, boolean isCutCard);
    }

    private OnCardSelectedListener onCardSelectedListener;

    public void setOnCardSelectedListener(OnCardSelectedListener listener) {
        this.onCardSelectedListener = listener;
    }


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

                offsetX -= distanceX;
                invalidate();

                return true;
            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                handleCardSelection(e.getX(), e.getY());
                return true;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }
        });
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        cardBounds.clear();

        float startX = offsetX;
        float bottomY = getHeight() - 200f;

        // 遍历所有卡片，从后往前绘制以实现叠加效果
        for (int i = cardList.size() - 1; i >= 0; i--) {
            Card card = cardList.get(i);
            Bitmap cardBitmap = card.getImage();

            float cardWidth = cardBitmap.getWidth();
            float cardHeight = cardBitmap.getHeight();
            float x = startX + i * overlapAmount;
            float y = bottomY - cardHeight / 2;

            RectF cardRect = new RectF(x, y, x + cardWidth, y + cardHeight);
            cardBounds.add(cardRect);

            canvas.save();
            canvas.translate(x, y);
            canvas.drawBitmap(cardBitmap, 0, 0, paint);
            canvas.restore();
        }
    }


    public void initialize(int pickCardLimit, boolean cutCardEnabled) {
        this.pickCardLimit = pickCardLimit;
        this.cutCardEnabled = cutCardEnabled;

        if (cutCardEnabled && !cardList.isEmpty()) {
            Card cutCard = cardList.get(cardList.size() - 1);
            cardList.remove(cutCard);
            cutCard.flip();
            selectedCards.add(cutCard);

            if (onCardSelectedListener != null) {
                onCardSelectedListener.onCardSelected(cutCard, true);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        for (RectF cardRect : cardBounds) {
            if (cardRect.contains(touchX, touchY)) {
                Log.d("CardPickView", "Touched a card, enabling rotation");
                return gestureDetector.onTouchEvent(event);  // 只有触摸到卡片时才启用旋转
            }
        }

        Log.d("CardPickView", "Touched outside card area");
        return false;
    }

    private void handleCardSelection(float touchX, float touchY) {
        if (selectedCards.size() >= pickCardLimit) {
            Log.d("CardPickView", "已达到最大选卡数量");
            return;
        }

        for (int i = 0; i < cardBounds.size(); i++) {
            RectF cardRect = cardBounds.get(i);
            if (cardRect.contains(touchX, touchY)) {
                Card selectedCard = cardList.get(i);

                if (!selectedCards.contains(selectedCard)) {
                    selectedCards.add(selectedCard);
                    cardList.remove(selectedCard);

                    if (onCardSelectedListener != null) {
                        onCardSelectedListener.onCardSelected(selectedCard, false);
                    }
                    invalidate();
                    Log.d("CardPickView", "Card selected: " + selectedCard.getName());
                } else {
                    Log.d("CardPickView", "Card already selected: " + selectedCard.getName());
                }
                break;
            }
        }
    }
}
