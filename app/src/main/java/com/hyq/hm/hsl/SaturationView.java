package com.hyq.hm.hsl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by 海米 on 2018/11/27.
 */

public class SaturationView extends View {
    public SaturationView(Context context) {
        super(context);
        init();
    }

    public SaturationView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    private ColorMatrix imageMatrix = new ColorMatrix();
    private ColorMatrix sMatrix;
    private ColorMatrix hMatrix;
    private ColorMatrix lMatrix;
    private ColorMatrixColorFilter colorMatrixColorFilter;
    private Paint paint;
    private Rect rect = new Rect();
    private RectF rectF = new RectF();
    private void init(){
        paint = new Paint();
        sMatrix = new ColorMatrix();
        hMatrix = new ColorMatrix();
        lMatrix = new ColorMatrix();
        sMatrix.setSaturation(1);
        hMatrix.setRotate(0,0);
        hMatrix.setRotate(1,0);
        hMatrix.setRotate(2,0);
        lMatrix.setScale(1,1,1,1);
        imageMatrix = new ColorMatrix();
        imageMatrix.postConcat(sMatrix);
        imageMatrix.postConcat(hMatrix);
        imageMatrix.postConcat(lMatrix);
        colorMatrixColorFilter = new ColorMatrixColorFilter(imageMatrix);

    }
    private Bitmap bitmap;

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        rect.set(0,0,bitmap.getWidth(),bitmap.getHeight());
    }
    public void setSat(float sat) {
        sMatrix.setSaturation(sat);
        imageMatrix = new ColorMatrix();
        imageMatrix.postConcat(sMatrix);
        imageMatrix.postConcat(hMatrix);
        imageMatrix.postConcat(lMatrix);
        colorMatrixColorFilter = new ColorMatrixColorFilter(imageMatrix);
        postInvalidate();
    }
    public void setHue(float hue){
        hMatrix.setRotate(0,hue);
        hMatrix.setRotate(1,hue);
        hMatrix.setRotate(2,hue);
        imageMatrix = new ColorMatrix();
        imageMatrix.postConcat(sMatrix);
        imageMatrix.postConcat(hMatrix);
        imageMatrix.postConcat(lMatrix);
        colorMatrixColorFilter = new ColorMatrixColorFilter(imageMatrix);
        postInvalidate();
    }
    public void setLum(float lum){
        lMatrix.setScale(lum,lum,lum,1);
        imageMatrix = new ColorMatrix();
        imageMatrix.postConcat(sMatrix);
        imageMatrix.postConcat(hMatrix);
        imageMatrix.postConcat(lMatrix);
        colorMatrixColorFilter = new ColorMatrixColorFilter(imageMatrix);
        postInvalidate();
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(getMeasuredWidth() != 0){
            int screenWidth = getMeasuredWidth();
            int screenHeight = getMeasuredHeight();

            int showWidth = bitmap.getWidth();
            int showHeight = bitmap.getHeight();

            int left,top,viewWidth,viewHeight;
            float sh = screenWidth*1.0f/screenHeight;
            float vh = showWidth*1.0f/showHeight;
            if(sh < vh){
                left = 0;
                viewWidth = screenWidth;
                viewHeight = (int)(showHeight*1.0f/showWidth*viewWidth);
                top = (screenHeight - viewHeight)/2;
            }else{
                top = 0;
                viewHeight = screenHeight;
                viewWidth = (int)(showWidth*1.0f/showHeight*viewHeight);
                left = (screenWidth - viewWidth)/2;
            }
            rectF.set(left,top,left+viewWidth,top+viewHeight);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColorFilter(colorMatrixColorFilter);
        canvas.drawBitmap(bitmap, rect, rectF, paint);
    }
}
