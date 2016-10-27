package org.hp.serialedittext;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.EditText;


import static android.graphics.Paint.ANTI_ALIAS_FLAG;

/**
 * Created by paul on 16/10/25.
 */

public class SerialEditText extends EditText {

    private int defaultContMargin = 2;
    private static final int defaultSplitLineWidth = 3;

    private int mBorderColor = 0x00979797;
    private float mBorderWidth = 2;

    private int mSerialTextLength = 6;//长度
    private int mSerialTextColor = 0x0000000;
    private float mSerialTextSize = 14;

    private Paint mSerialTextPaint = new Paint(ANTI_ALIAS_FLAG);
    private Paint mBorderPaint = new Paint(ANTI_ALIAS_FLAG);

    private Paint mBackgroundPaint = new Paint(ANTI_ALIAS_FLAG);
    private int mCurrentTextLength;

    private CharSequence mCurrentText;


    public SerialEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        mBorderWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mBorderWidth, dm);
        mSerialTextLength = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mSerialTextLength, dm);
        mSerialTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mSerialTextSize, dm);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SerialEditText, 0, 0);
        try {

//            mBorderColor = a.getColor(R.styleable.SerialEditText_setBorderColor, mBorderColor);
            mBorderWidth = a.getDimension(R.styleable.SerialEditText_setBorderWidth, mBorderWidth);
            mSerialTextLength = a.getInt(R.styleable.SerialEditText_setTextLength, mSerialTextLength);
            mSerialTextColor = a.getColor(R.styleable.SerialEditText_setTextColor, mSerialTextColor);
            mSerialTextSize = a.getDimension(R.styleable.SerialEditText_setTextSize, mSerialTextSize);
        } finally {

            a.recycle();
        }
        this.setFilters(new InputFilter[]{new InputFilter.LengthFilter(mSerialTextLength)}); //最大输入长度
        mBorderColor = Color.parseColor("#979797");
        mBorderPaint.setStrokeWidth(mBorderWidth);
        mBorderPaint.setColor(mBorderColor);
        mSerialTextPaint.setTextSize(mSerialTextSize);
        mSerialTextPaint.setStyle(Paint.Style.FILL);
        mSerialTextPaint.setColor(mSerialTextColor);

        mBackgroundPaint.setColor(Color.parseColor("#f0f0f0"));
        mBackgroundPaint.setStyle(Paint.Style.FILL);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();

        // 外边框
        RectF rect = new RectF(0, 0, width, height);
        mBorderPaint.setColor(mBorderColor);
        canvas.drawRoundRect(rect, 0, 0, mBorderPaint);

        // 内容区
        defaultContMargin = dip2px(getContext(), 1);
        RectF rectIn = new RectF(rect.left + defaultContMargin, rect.top + defaultContMargin,
                rect.right - defaultContMargin, rect.bottom - defaultContMargin);
        mBorderPaint.setColor(Color.WHITE);
        canvas.drawRoundRect(rectIn, 0, 0, mBorderPaint);

        // 分割线
        mBorderPaint.setColor(mBorderColor);
        for (int i = 1; i < mSerialTextLength; i++) {
            float x = width * i / mSerialTextLength;
            canvas.drawLine(x, 0, x, height, mBorderPaint);


        }

        // 数字
        float centerX, centerY = height / 2;
        float half = width / mSerialTextLength / 2;

        for (int i = 0; i < mCurrentTextLength; i++) {
            centerX = width * i / mSerialTextLength + half;
            String text = String.valueOf(mCurrentText.charAt(i));
            float[] textWidth = new float[2];
            int wi = mSerialTextPaint.getTextWidths(text, textWidth);


            float x2 = width * (i + 1) / mSerialTextLength;
            float x1 = width * (i) / mSerialTextLength;
            RectF r = new RectF(x1 + mBorderWidth, mBorderWidth, x2 - mBorderWidth, height - mBorderWidth);
            canvas.drawRect(r, mBackgroundPaint);
            canvas.drawText(text, centerX - textWidth[0] / 2, centerY + textWidth[0] / 2, mSerialTextPaint);
        }
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        this.mCurrentTextLength = text.toString().length();
        this.mCurrentText = text;
        invalidate();
    }

    public int getBorderColor() {
        return mBorderColor;
    }

    public void setBorderColor(int borderColor) {
        this.mBorderColor = borderColor;
        mBorderPaint.setColor(borderColor);
        invalidate();
    }

    public float getBorderWidth() {
        return mBorderWidth;
    }

    public void setBorderWidth(float borderWidth) {
        this.mBorderWidth = borderWidth;
        mBorderPaint.setStrokeWidth(borderWidth);
        invalidate();
    }


    public void setTextColor(int textColor) {
        this.mSerialTextColor = textColor;
        mSerialTextPaint.setColor(textColor);
        invalidate();
    }


    /**
     * dp2px
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * px2dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
