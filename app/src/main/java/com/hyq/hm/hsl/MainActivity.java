package com.hyq.hm.hsl;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.opengl.GLES20;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {

    private Handler bitmapHandler;
    private HandlerThread bitmapThread;
    private SurfaceView surfaceView;
    private EGLUtils eglUtils;
    private GLBitmap glBitmapX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.ic_jn);
        final SaturationView imageView = findViewById(R.id.image_view);
        imageView.setBitmap(bitmap);
        SeekBar seekBarA = findViewById(R.id.seek_bar_a);
        seekBarA.setProgress(100);
        seekBarA.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                s(progress/100.0f);
                imageView.setSat(progress/100.0f);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        SeekBar seekBarB= findViewById(R.id.seek_bar_b);
        seekBarB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                h(progress/360.0f);
                imageView.setHue(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        SeekBar seekBarC= findViewById(R.id.seek_bar_c);
        seekBarC.setProgress(100);
        seekBarC.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                l(progress/100.0f);
                imageView.setLum(progress/100.0f);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        glBitmapX = new GLBitmap(this,R.drawable.ic_jn);
        glBitmapX.setType(0);
//        glBitmapY = new GLBitmap(this,R.drawable.ic_m);
//        glBitmapY.setType(1);


        bitmapThread = new HandlerThread("AudioMediaCodec");
        bitmapThread.start();
        bitmapHandler = new Handler(bitmapThread.getLooper());
        surfaceView = findViewById(R.id.surface_view);
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(final SurfaceHolder holder) {
                bitmapHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        eglUtils = new EGLUtils();
                        eglUtils.initEGL(holder.getSurface());

                        glBitmapX.surfaceCreated();
//                        glBitmapY.surfaceCreated();

                    }
                });
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, final int width, final int height) {
                bitmapHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        glBitmapX.setRect(glBitmapX.getWidth(),glBitmapX.getHeight(),width,height);
//                        glBitmapY.setRect(width - glBitmapX.getWidth() - 50,height - glBitmapY.getHeight() - 100,glBitmapY.getWidth(),glBitmapY.getHeight(),width,height);
                        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
                        glBitmapX.surfaceDraw();
//                        glBitmapY.surfaceDraw();
                        eglUtils.swap();
                    }
                });
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                bitmapHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        eglUtils.release();
                    }
                });
            }
        });

    }
    private void rotate(final int rotate){
        bitmapHandler.post(new Runnable() {
            @Override
            public void run() {
                GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
                glBitmapX.setRadian(rotate);
                glBitmapX.surfaceDraw();
//                glBitmapY.setRadian(rotate);
//                glBitmapY.surfaceDraw();
                eglUtils.swap();
            }
        });
    }

    private void s(final float s){
        bitmapHandler.post(new Runnable() {
            @Override
            public void run() {
                GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
                glBitmapX.setS(s);
                glBitmapX.surfaceDraw();
                eglUtils.swap();
            }
        });
    }
    private void h(final float h){
        bitmapHandler.post(new Runnable() {
            @Override
            public void run() {
                GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
                glBitmapX.setH(h);
                glBitmapX.surfaceDraw();
                eglUtils.swap();
            }
        });
    }
    private void l(final float l){
        bitmapHandler.post(new Runnable() {
            @Override
            public void run() {
                GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
                glBitmapX.setL(l);
                glBitmapX.surfaceDraw();
                eglUtils.swap();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        bitmapThread.quit();
        bitmapThread = null;
    }
}
