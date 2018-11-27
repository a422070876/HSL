package com.hyq.hm.hsl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by 海米 on 2018/10/25.
 */

public class GLBitmap {

    private int aPositionHandle;
    private int uMatrixHandle;
    private int uTextureSamplerHandle;
    private int aTextureCoordHandle;
    private int sHandle;
    private int hHandle;
    private int lHandle;
    private int programId;
    private int textureId;

    private FloatBuffer vertexBuffer;
    private final float[] vertexData = {
            1f, -1f,0,
            -1f, -1f,0,
            1f, 1f,0,
            -1f, 1f,0
    };
    private FloatBuffer textureVertexBuffer;
    private final float[] textureVertexData = {
            1f, 0f,//右下
            0f, 0f,//左下
            1f, 1f,//右上
            0f, 1f//左上
    };
    private final float[] modelMatrix=new float[16];
    private final float[] projectionMatrix= new float[16];
    private final float[] viewMatrix = new float[16];
    private Bitmap bitmap;
    private Context context;
    public GLBitmap(Context context,int id){
        this.context = context;
        scale = context.getResources().getDisplayMetrics().density;
        vertexBuffer = ByteBuffer.allocateDirect(vertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);
        vertexBuffer.position(0);

        textureVertexBuffer = ByteBuffer.allocateDirect(textureVertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(textureVertexData);
        textureVertexBuffer.position(0);
        bitmap = BitmapFactory.decodeResource(context.getResources(),id);

    }

    public void surfaceCreated(){
        String vertexShader = ShaderUtils.readRawTextFile(context, R.raw.bitmap_vertext_shader);
        String fragmentShader = ShaderUtils.readRawTextFile(context, R.raw.bitmap_fragment_sharder);
        programId = ShaderUtils.createProgram(vertexShader, fragmentShader);
        aPositionHandle = GLES20.glGetAttribLocation(programId, "aPosition");
        uMatrixHandle=GLES20.glGetUniformLocation(programId,"uMatrix");
        uTextureSamplerHandle=GLES20.glGetUniformLocation(programId,"sTexture");
        aTextureCoordHandle=GLES20.glGetAttribLocation(programId,"aTexCoord");
        sHandle =GLES20.glGetUniformLocation(programId,"S");
        hHandle =GLES20.glGetUniformLocation(programId,"H");
        lHandle =GLES20.glGetUniformLocation(programId,"L");
        final int[] texture=new int[1];
        GLES20.glGenTextures(1,texture,0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,texture[0]);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D,0,GLES20.GL_RGBA,bitmap,0);
        
        textureId = texture[0];
        Matrix.perspectiveM(projectionMatrix, 0, 90f, 1,  1, 50);
        Matrix.setLookAtM(viewMatrix, 0,
                0.0f, 0.0f, 2.0f,
                0.0f, 0.0f,0.0f,
                0.0f, -1.0f, 0.0f);
        GLES20.glUseProgram(programId);
        GLES20.glEnableVertexAttribArray(aPositionHandle);
        GLES20.glVertexAttribPointer(aPositionHandle, 2, GLES20.GL_FLOAT, false,
                12, vertexBuffer);

        GLES20.glEnableVertexAttribArray(aTextureCoordHandle);
        GLES20.glVertexAttribPointer(aTextureCoordHandle,2,GLES20.GL_FLOAT,false,8,textureVertexBuffer);
        GLES20.glUseProgram(0);

    }

    private int radian = 0;

    public void setRadian(int radian) {
        this.radian = radian;
    }
    private float s = 1.0f;
    public void setS(float s){
        this.s = s;
    }

    private float h = 0.0f;

    public void setH(float h) {
        this.h = h;
    }
    private float l = 1.0f;

    public void setL(float l) {
        this.l = l;
    }
    //    private float s = 0.0;

    private int rx = 0;
    private int ry = -1;
    public void setType(int type){
        if(type == 0){
            rx = 0;
            ry = -1;
            sy = 1.6f;
        }else{
            rx = -1;
            ry = 0;
            sy = 1.5f;
        }
    }

    private Rect rect = new Rect();
    public void setRect(int showWidth,int showHeight,int screenWidth,int screenHeight){
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
        int w = (int) (viewWidth*sy);
        int f = (w - viewWidth)/2;
        rect.set(left - f,top,w,viewHeight);
    }
    private float scale = 1;
    private float sy = 1.6f;

    public int getWidth(){
        return bitmap.getWidth()/10;
    }
    public int getHeight(){
        return bitmap.getHeight()/10;
    }

    void surfaceDraw(){
        GLES20.glViewport(rect.left, rect.top, rect.right, rect.bottom);
        Matrix.multiplyMM(modelMatrix, 0, projectionMatrix, 0, viewMatrix, 0);
        if(radian > 90){
            Matrix.rotateM(modelMatrix,0,radian - 180,rx,ry,0);
        }else{
            Matrix.rotateM(modelMatrix,0,radian,rx,ry,0);
        }
        Matrix.scaleM(modelMatrix,0,1.0f,sy,1f);
        modelMatrix[3] = modelMatrix[3]/scale;
        modelMatrix[7] = modelMatrix[7]/scale;


        GLES20.glClearColor(0,0,0,0);
        GLES20.glUseProgram(programId);
        GLES20.glUniformMatrix4fv(uMatrixHandle,1,false,modelMatrix,0);
        GLES20.glUniform1f(sHandle,s);
        GLES20.glUniform1f(hHandle,h);
        GLES20.glUniform1f(lHandle,l);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textureId);
        GLES20.glUniform1i(uTextureSamplerHandle,0);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,0);
        GLES20.glUseProgram(0);
    }
}
