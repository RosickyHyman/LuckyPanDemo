package com.hyman.luckypan;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Hyman on 2016/5/6.
 */
public class SurfaceViewTempalte extends SurfaceView implements SurfaceHolder.Callback{

    private SurfaceHolder mHolder;
    private Canvas mCanvas;

    /**
     * 用于绘制的线程
     */
    private Thread t;
    /**
     * 线程的控制开关
     */
    private boolean isRunning;
    public SurfaceViewTempalte(Context context) {
        this(context,null);
    }

    public SurfaceViewTempalte(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHolder=getHolder();
        mHolder.addCallback(this);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setKeepScreenOn(true);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isRunning=true;
        t=new Thread();
        t.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isRunning=false;

    }

    public void run(){
        while(isRunning){
            draw();
        }
    }

    private void draw() {

        try {
            mCanvas=mHolder.lockCanvas();
            if (mCanvas!=null){

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mCanvas!=null){
                mHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }
}
