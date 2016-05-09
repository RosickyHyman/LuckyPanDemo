package com.hyman.luckypan;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Hyman on 2016/5/6.
 */
public class LuckyPan extends SurfaceView implements SurfaceHolder.Callback,Runnable{

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
    /**
     * 奖项文字
     */
    private String[] mStrs= new String[]{"单反相机","Ipad","恭喜发财","Iphone","妹纸一枚","恭喜发财"};
    /**
     * 奖项图片
     */
    private int[] mImgs=new int[]{R.mipmap.danfan,R.mipmap.ipad,R.mipmap.f015,R.mipmap.iphone,R.mipmap.meizi,R.mipmap.f040,};
    /**
     * 与图片对应的bitmap
     */
    private Bitmap[] mImgsBitmap;
    /**
     * 盘块的颜色
     */
    private int[] mColor=new int[]{0xFFFFC300,0xFFF17E01,0xFFFFC300,0xFFF17E01,0xFFFFC300,0xFFF17E01};
    /**
     * 奖项数量
     */
    private int mItemCount=6;
    /**
     * 盘块
     */
    private RectF mRange=new RectF();
    /**
     * 角度
     */
    private int mRadius;
    /**
     * 奖项画笔
     */
    private Paint mArcPaint;
    /**
     * 文字画笔
     */
    private Paint mTextPaint;
    /**
     * 速度
     */
    private double mSpeed;
    /**
     * 起始角度
     */
    private volatile float mStartAngle=0;
    /**
     * 是否停止
     */
    private boolean isShouldEnd;
    /**
     * 转盘中心位置
     */
    private int mCenter;
    /**
     * padding直接以paddingleft为准
     */
    private int mPadding;
    /**
     * 背景图
     */
    private Bitmap mBgBitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.bg2);
    /**
     * 文字大小
     */
    private float mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,20,getResources().getDisplayMetrics());

    public LuckyPan(Context context) {
        this(context,null);
    }

    public LuckyPan(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHolder=getHolder();
        mHolder.addCallback(this);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setKeepScreenOn(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width =Math.min(getMeasuredWidth(),getMeasuredHeight());

        mPadding=getPaddingLeft();
        mRadius=width-mPadding*2;

        mCenter=width/2;

        setMeasuredDimension(width,width);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //初始化盘块的画笔
        mArcPaint=new Paint();
        mArcPaint.setAntiAlias(true);
        mArcPaint.setDither(true);
        //初始化文字的画笔
        mTextPaint=new Paint();
        mTextPaint.setColor(0xffffffff);
        mTextPaint.setTextSize(mTextSize);

        //初始化盘块绘制的范围
        mRange=new RectF(mPadding,mPadding,mPadding+mRadius,mPadding+mRadius);
        //初始化图片
        mImgsBitmap=new Bitmap[mItemCount];
        for (int i = 0; i <mItemCount ; i++) {
            mImgsBitmap[i]=BitmapFactory.decodeResource(getResources(),mImgs[i]);
        }



        isRunning=true;
        t=new Thread(this);
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
            long start=System.currentTimeMillis();
            draw();
            long end=System.currentTimeMillis();

            if (end-start<50){
                try {
                    Thread.sleep(50-(end-start));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void draw() {

        try {
            mCanvas=mHolder.lockCanvas();
            if (mCanvas!=null){

                drawBg();

                float tmpAngle =mStartAngle;
                float sweepAngle=360/mItemCount;
                for (int i = 0; i <mItemCount ; i++) {
                    mArcPaint.setColor(mColor[i]);
                    //绘制盘块
                    mCanvas.drawArc(mRange,tmpAngle,sweepAngle,true,mArcPaint);
                    //绘制文本
                    drawText(tmpAngle,sweepAngle,mStrs[i]);
                    //绘制Icon
                    drawIcon(tmpAngle,mImgsBitmap[i]);
                    tmpAngle+=sweepAngle;

                }

                mStartAngle+=mSpeed;
                if (isShouldEnd) {
                    mSpeed-=1;
                }
                if (mSpeed<=0){
                    mSpeed=0;
                    isShouldEnd=false;

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mCanvas!=null){
                mHolder.unlockCanvasAndPost(mCanvas);
            }
        }
    }

    public void luckyStart(int index){
        //计算每一项的角度
        float angle=360/mItemCount;
        //计算每一项中奖范围
        float from=270-(index+1)*angle;
        float end=from+angle;
        //设置停下来需要旋转的距离
        float targetFrom=2*360+from;
        float targerEnd=2*360+end;

        /**
         * <pre>
         *     v1~0
         *     切每次-1
         *     (v1+0)*(v1+1)/2=targetFrom;
         *      v1*v1+v1-2*targetFrom=0;
         *      v1=(-1+Math.sqrt(1+8*targetFrom))/2
         * </pre>
         *
         *
         *
         */
        float v1= (float) ((-1+Math.sqrt(1+8*targetFrom))/2);
        float v2= (float) ((-1+Math.sqrt(1+8*targerEnd))/2);

        mSpeed=v1+Math.random()*(v2-v1);

        isShouldEnd=false;
    }
    public void luckyEnd(){

        mStartAngle=0;
        isShouldEnd=true;
    }

    public boolean isStart(){
        return mSpeed!=0;
    }

    public boolean isShouldEnd(){
        return isShouldEnd;
    }

    private void drawIcon(float tmpAngle, Bitmap bitmap) {

        int imgWidth =mRadius/8;
        float angle= (float) ((tmpAngle+360/mItemCount/2)*Math.PI/180);
        int x = (int) (mCenter+mRadius/4*Math.cos(angle));
        int y = (int) (mCenter+mRadius/4* Math.sin(angle));

        Rect rect=new Rect(x-imgWidth/2,y-imgWidth/2,x+imgWidth/2,y+imgWidth/2);

        mCanvas.drawBitmap(bitmap,null,rect,null);
    }


    private void drawText(float tmpAngle, float sweepAngle, String mStr) {

        Path path=new Path();
        path.addArc(mRange,tmpAngle,sweepAngle);

        float textWidth=mTextPaint.measureText(mStr);
        int hOffset =(int)(mRadius*Math.PI/mItemCount/2-textWidth/2);
        int vOffset=mRadius/12;
        mCanvas.drawTextOnPath(mStr,path,hOffset,vOffset,mTextPaint);


    }

    private void drawBg() {

        mCanvas.drawBitmap(mBgBitmap,null,new RectF(mPadding/2,mPadding/2,getMeasuredWidth()-mPadding/2,getMeasuredHeight()-mPadding/2),null);
    }
}
