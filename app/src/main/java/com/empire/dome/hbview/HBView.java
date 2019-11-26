package com.empire.dome.hbview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class HBView extends View {

    private int mPanelWidth;
    private float mLineHeight;
    private float mLineWidth;
    private int MAX_LINE_NUM_X = 10;
    private int MAX_LINE_NUM_Y = 20;

    private Paint mPaint = new Paint();
    private Bitmap mWhitePiece;
    private Bitmap mBlackPiece;

    //棋子的缩放比例(行高的3/4)
    private float pieceScaleRatio = 4 * 1.0f / 4;

    private ArrayList<Point> mWhiteArray = new ArrayList<>();
    private ArrayList<Point> mBlackArray = new ArrayList<>();

    public HBView(Context context) {
        this(context, null);
    }

    public HBView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HBView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 初始化设置
     */
    private void init() {
        //初始化画笔
        mPaint.setColor(0x88000000);
        //设置抗锯齿
        mPaint.setAntiAlias(true);
        //设置防抖动
        mPaint.setDither(true);
        //设置为空心(画线)
        mPaint.setStyle(Paint.Style.STROKE);

        //初始化棋子
        mWhitePiece = getBitmap(getContext(), R.drawable.white_piece);
        mBlackPiece = getBitmap(getContext(), R.drawable.red_piece);
    }

    private  Bitmap getBitmap(Context context, int vectorDrawableId) {
        Bitmap bitmap = null;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            Drawable vectorDrawable = context.getDrawable(vectorDrawableId);
            bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                    vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            vectorDrawable.draw(canvas);
        } else {
            bitmap = BitmapFactory.decodeResource(context.getResources(), vectorDrawableId);
        }
        return bitmap;
    }

    /**
     * 测量
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int width = Math.min(widthSize, heightSize);

        //此处的逻辑判断是处理当我们自定义的View被嵌套在ScrollView中时,获得的测量模式
        // 会是UNSPECIFIED
        // 使得到的widthSize或者heightSize为0
        if (widthMode == MeasureSpec.UNSPECIFIED){
            width = heightSize;
        }else if (heightMode == MeasureSpec.UNSPECIFIED){
            width = widthSize;
        }
        //调用此方法使我们的测量结果生效
        setMeasuredDimension(width, width);
    }

    /**
     * 当宽高发生变化时回调此方法
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //此处的参数w就是在onMeasure()方法中设置的自定义View的大小
        //计算出棋盘宽度和行高
        mPanelWidth = w;
        mLineHeight = mPanelWidth * 1.0f / MAX_LINE_NUM_Y;
        mLineWidth = mPanelWidth * 1.0f / MAX_LINE_NUM_X;
//
//        //将棋子根据行高变化
//        int pieceWidth = (int) (pieceScaleRatio * mLineHeight);
//        mWhitePiece = Bitmap.createScaledBitmap(mWhitePiece, pieceWidth, pieceWidth, false);
//        mBlackPiece = Bitmap.createScaledBitmap(mBlackPiece, pieceWidth, pieceWidth, false);
    }

    /**
     * 绘制棋盘
     * @param canvas
     */
    private void drawBoard(Canvas canvas) {
        int w = mPanelWidth;
        float lineHeight = mLineHeight;
        float lineWidth = mLineWidth;

        for (int i = 0; i < MAX_LINE_NUM_X; i++) {
            int startX = (int) (lineWidth / 2);
            int endX = (int) (w - lineWidth / 2);

            int y = (int) ((0.5 + i) * lineWidth);
            //画竖线
            canvas.drawLine(y, startX, y, endX, mPaint);
        }
        for (int i = 0; i < MAX_LINE_NUM_Y; i++) {
            int startX = (int) (lineHeight / 2);
            int endX = (int) (w - lineHeight / 2);

            int y = (int) ((0.5 + i) * lineHeight);
            //画横线
            canvas.drawLine(startX, y, endX, y, mPaint);
        }

    }

    public void setWhiteArray(ArrayList<Point> mWhiteArray) {
        this.mWhiteArray = mWhiteArray;
    }

    /**
     * 进行绘制工作
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制棋盘
        drawBoard(canvas);
        drawPieces(canvas);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    /**
     * 绘制棋子
     * @param canvas
     */
    private void drawPieces(Canvas canvas) {
        //绘制白棋子
        for (int i = 0, n = mWhiteArray.size(); i < n; i++) {
            Point whitePoint = mWhiteArray.get(i);
            //棋子之间的间隔为1/4行高
            canvas.drawBitmap(mWhitePiece,
                    (whitePoint.x+0.5f) * mLineWidth- dip2px(getContext().getApplicationContext(),10),//+ (1 - pieceScaleRatio) / 2
                    (whitePoint.y+0.5f ) * mLineHeight- dip2px(getContext().getApplicationContext(),10), null);//+ (1 - pieceScaleRatio) / 2
        }
        for (int i = 0, n = mBlackArray.size(); i < n; i++) {
            Point blackPoint = mBlackArray.get(i);
            //棋子之间的间隔为1/4行高,棋子距离左右边框的距离为1/8行高
            canvas.drawBitmap(mBlackPiece,
                    (blackPoint.x + 0.5f) * mLineHeight - dip2px(getContext().getApplicationContext(), 10),//+ (1 - pieceScaleRatio) / 2
                    (blackPoint.y + 0.5f) * mLineHeight - dip2px(getContext().getApplicationContext(), 10), null);
        }
    }

    public void setTypeArray(ArrayList<Point> mBlackArray,int type) {
        this.mBlackArray = mBlackArray;
        invalidate();
    }


    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    /**
     * 防止内存不足活动被回收
     */
    private static final String INSTANCE = "instance";
    private static final String INSTANCE_WHITE_ARRAY = "instance_white_array";
    private static final String INSTANCE_BLACK_ARRAY = "instance_black_array";


    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE, super.onSaveInstanceState());
        bundle.putParcelableArrayList(INSTANCE_WHITE_ARRAY, mWhiteArray);
        bundle.putParcelableArrayList(INSTANCE_BLACK_ARRAY, mBlackArray);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle){
            Bundle bundle = (Bundle) state;
            mWhiteArray = bundle.getParcelableArrayList(INSTANCE_WHITE_ARRAY);
            mBlackArray = bundle.getParcelableArrayList(INSTANCE_BLACK_ARRAY);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE));
            return;
        }
        super.onRestoreInstanceState(state);
    }
}