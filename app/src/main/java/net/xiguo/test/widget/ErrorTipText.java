package net.xiguo.test.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;

import net.xiguo.test.R;
import net.xiguo.test.utils.LogUtil;

/**
 * Created by army on 2017/4/11.
 */

public class ErrorTipText extends AppCompatTextView {
    public ErrorTipText(Context context) {
        this(context, null);
    }

    public ErrorTipText(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public ErrorTipText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint fill = new Paint();
        fill.setColor(Color.WHITE);
        fill.setStyle(Paint.Style.FILL);
        Paint border = new Paint();
        border.setColor(0x11000000);
        border.setStrokeWidth(2);
        border.setStyle(Paint.Style.STROKE);
        int width = getWidth();
        int height = getHeight();
        canvas.drawRoundRect(new RectF(0, 0, width, height - 28), 10, 10, fill);
        canvas.drawRoundRect(new RectF(0, 0, width, height - 28), 10, 10, border);
        Path path = new Path();
        path.moveTo(30, height - 30);
        path.lineTo(30, height);
        path.lineTo(60, height - 30);
        path.close();
        canvas.drawPath(path, fill);
        Path path2 = new Path();
        path2.moveTo(30, height - 28);
        path2.lineTo(30, height);
        path2.lineTo(60, height - 28);
        canvas.drawPath(path2, border);
        super.onDraw(canvas);
    }

    public void showNameAndPassNotMatch() {
        SpannableString spannableString = new SpannableString("用户名与密码不匹配哟");
        spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.errorTipStrong)), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.errorTipStrong)), 4, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        setText(spannableString);
        setVisibility(VISIBLE);
    }
}
