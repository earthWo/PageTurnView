package win.whitelife.library;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Region;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

/**
 * @author wuzefeng
 * @date 2018/1/29
 */
public class PageTurnView extends View {

    /**
     * 手指触摸点
     */
    private PointF mTouchPoint;

    private PointF mLastTouchPoint;

    /**
     * 宽
     */
    private int mWidth;

    /**
     * 高
     */
    private int mHeight;


    /**
     * 点击有效的宽度
     */
    private int mEffectiveWidth=100;

    /**
     * 点击有效的高度
     */
    private int mEffectiveHeight=100;


    /**
     * A点坐标
     */
    private PointF APoint;

    /**
     * F点坐标
     */
    private PointF FPoint;

    /**
     * G点坐标
     */
    private PointF GPoint;


    /**
     * E点坐标
     */
    private PointF EPoint;


    /**
     * H点坐标
     */
    private PointF HPoint;


    /**
     * C点坐标
     */
    private PointF CPoint;

    /**
     * J点坐标
     */
    private PointF JPoint;


    /**
     * B点坐标
     */
    private PointF BPoint;


    /**
     * K点坐标
     */
    private PointF KPoint;

    /**
     * D点坐标
     */
    private PointF DPoint;

    /**
     * I点坐标
     */
    private PointF IPoint;

    private Path mCurrentPagePath;

    /**
     * 背面的path
     */
    private Path mBackPath;

    /**
     * 下一页的path
     */
    private Path mNextPagePath;

    /**
     * 触摸是否有效
     */
    private boolean touchEffective;

    private static final String TAG="翻页点";

    private boolean isStartTurning;

    private int touchMode;

    private Scroller mScroller;

    private static final int SCROLL_TIME=500;

    private GradientDrawable drawableHorizontalRight;

    private GradientDrawable drawableBottomLeft;

    private GradientDrawable drawableTopLeft;

    private GradientDrawable drawableBackTop;

    private GradientDrawable drawableBackBottom;


//    private float[] mMatrixArray = { 0, 0, 0, 0, 0, 0, 0, 0, 1.0f };
//    private Matrix mMatrix;

    public PageTurnView(Context context) {
        this(context,null);
    }

    public PageTurnView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public PageTurnView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        //初始化区域和触摸点
        mCurrentPagePath=new Path();
        mBackPath=new Path();
        mNextPagePath=new Path();
        mTouchPoint=new PointF();
        mLastTouchPoint=new PointF();
//        mMatrix=new Matrix();
        //渐变颜色数组
        int [] gradientColors = new int[]{ 0x01333333,0x44333333};
        drawableHorizontalRight = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, gradientColors);
        drawableHorizontalRight.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        gradientColors = new int[]{ 0x44333333,0x01333333};
        drawableBottomLeft = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, gradientColors);
        drawableBottomLeft.setGradientType(GradientDrawable.LINEAR_GRADIENT);

        gradientColors = new int[]{ 0x01333333,0x44333333};
        drawableTopLeft = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, gradientColors);
        drawableTopLeft.setGradientType(GradientDrawable.LINEAR_GRADIENT);

        //渐变颜色数组
        gradientColors = new int[]{0x00333333, 0x55333333};
        drawableBackTop = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, gradientColors);
        drawableBackTop.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        drawableBackBottom = new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, gradientColors);
        drawableBackBottom.setGradientType(GradientDrawable.LINEAR_GRADIENT);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //初始化bitmap和canvas
        //绘制当前页
        drawCurrentPagePath(canvas);
        if(isStartTurning){
            //绘制下一页
            drawPathC(canvas,mCurrentPagePath);
            //绘制背面
            drawPathB(canvas,mCurrentPagePath);
        }
        //绘制整体
    }


    private void drawCurrentPagePath(Canvas canvas){
        canvas.save();
        canvas.clipPath(getCurrentPagePath());
        canvas.drawBitmap(mABitmap,0,0,null);

        //绘制垂直的阴影
        if(touchMode==TouchMode.TOUCH_RIGHT&&APoint.x>0&&isStartTurning){
            drawPathAHorizontalShadow(canvas,mCurrentPagePath);
        }else{
            drawCurrentPageShadowLeft(canvas);
            drawCurrentPageShadowRight(canvas);
        }
        canvas.restore();
    }


    private final static int SHADOW_WIDTH=20;



    private void drawCurrentPageShadowLeft(Canvas canvas){
        if(EPoint==null){
            return;
        }
        canvas.restore();
        canvas.save();


        int left;
        int right;
        int top = (int) EPoint.y;
        int bottom = (int) (EPoint.y+mHeight);
        GradientDrawable gradientDrawable;
        if (touchMode==TouchMode.TOUCH_TOP) {
            left = (int) (EPoint.x - SHADOW_WIDTH);
            right = (int) (EPoint.x);
            gradientDrawable = drawableTopLeft;
        } else {
            left = (int) (EPoint.x);
            right = (int) (EPoint.x + SHADOW_WIDTH);
            gradientDrawable = drawableBottomLeft;
        }

        Path mPath = new Path();
        mPath.moveTo(APoint.x-SHADOW_WIDTH,APoint.y);
        mPath.lineTo(DPoint.x,DPoint.y);
        mPath.lineTo(EPoint.x,EPoint.y);
        mPath.lineTo(APoint.x,APoint.y);
        mPath.close();
        canvas.clipPath(mCurrentPagePath);
        canvas.clipPath(mPath,Region.Op.INTERSECT);

        float mDegrees = (float) Math.toDegrees(Math.atan2(EPoint.x-APoint.x, APoint.y-EPoint.y));
        canvas.rotate(mDegrees, EPoint.x, EPoint.y);

        gradientDrawable.setBounds(left,top,right,bottom);
        gradientDrawable.draw(canvas);
    }


    /**
     * 绘制A区域右阴影
     * @param canvas
     */
    private void drawCurrentPageShadowRight(Canvas canvas){
        if(HPoint==null){
            return;
        }
        canvas.restore();
        canvas.save();
        //view对角线长度
        float viewDiagonalLength = (float) Math.hypot(mWidth, mHeight);
        int left = (int) HPoint.x;
        //需要足够长的长度
        int right = (int) (HPoint.x + viewDiagonalLength*6);
        int top;
        int bottom;

        GradientDrawable gradientDrawable;
        if (touchMode==TouchMode.TOUCH_TOP) {
            top = (int) (HPoint.y- SHADOW_WIDTH);
            bottom = (int) HPoint.y;
            gradientDrawable=drawableTopLeft;
        } else {
            top = (int) HPoint.y;
            bottom = (int) (HPoint.y+SHADOW_WIDTH);
            gradientDrawable=drawableBottomLeft;
        }
        gradientDrawable.setBounds(left,top,right,bottom);

        Path mPath = new Path();
        mPath.moveTo(APoint.x- SHADOW_WIDTH,APoint.y);
        mPath.lineTo(HPoint.x,HPoint.y);
        mPath.lineTo(APoint.x,APoint.y);
        mPath.close();
        canvas.clipPath(mCurrentPagePath);
        canvas.clipPath(mPath);

        float mDegrees = (float) Math.toDegrees(Math.atan2(APoint.y-HPoint.y, APoint.x-HPoint.x));
        canvas.rotate(mDegrees, HPoint.x, HPoint.y);
        gradientDrawable.draw(canvas);
    }





    private void drawPathAHorizontalShadow(Canvas canvas, Path pathA){
        canvas.restore();
        canvas.save();
        canvas.clipPath(pathA);
        //阴影矩形最大的宽度
        int left = (int) (APoint.x -SHADOW_WIDTH);
        int right = (int) (APoint.x);
        int top = 0;
        int bottom = mHeight;
        GradientDrawable gradientDrawable = drawableHorizontalRight;
        gradientDrawable.setBounds(left,top,right,bottom);
        gradientDrawable.draw(canvas);
    }



    private void drawPathB(Canvas canvas,Path path){
        canvas.save();
        canvas.clipPath(path);
        //裁剪出C区域不同于A区域的部分
        canvas.clipPath(getBackPath(), Region.Op.REVERSE_DIFFERENCE);

        //进行矩阵翻转，没看懂，先不加
//        float eh = (float) Math.hypot(FPoint.x - EPoint.x,HPoint.y - FPoint.y);
//        float sin0 = (FPoint.x - EPoint.x) / eh;
//        float cos0 = (HPoint.y - FPoint.y) / eh;
//        //设置翻转和旋转矩阵
//        mMatrixArray[0] = -(1-2 * sin0 * sin0);
//        mMatrixArray[1] = 2 * sin0 * cos0;
//        mMatrixArray[3] = 2 * sin0 * cos0;
//        mMatrixArray[4] = 1 - 2 * sin0 * sin0;
//
//        mMatrix.reset();
//        mMatrix.setValues(mMatrixArray);//翻转和旋转
//        mMatrix.preTranslate(-EPoint.x, -EPoint.y);//沿当前XY轴负方向位移得到 矩形A₃B₃C₃D₃
//        mMatrix.postTranslate(EPoint.x, EPoint.y);//沿原XY轴方向位移得到 矩形A4 B4 C4 D4
        canvas.drawBitmap(mCBitmap,0,0, null);



        drawPathCShadow(canvas);

        canvas.restore();
    }


    private void drawPathC(Canvas canvas,Path path){
        canvas.save();
        canvas.clipPath(path);
        canvas.clipPath(getNextPagePath(),Region.Op.UNION);
        canvas.clipPath(getBackPath(),Region.Op.REVERSE_DIFFERENCE);
        canvas.drawBitmap(mCBitmap,0,0,null);



        canvas.restore();
    }

    /**
     * 绘制C区域阴影，阴影左浅右深
     * @param canvas
     */
    private void drawPathCShadow(Canvas canvas){
        int deepOffset = 1;//深色端的偏移值
        int lightOffset = -30;//浅色端的偏移值
        float viewDiagonalLength = (float) Math.hypot(mWidth, mHeight);//view对角线长度
        int midpoint_ce = (int) (CPoint.x + EPoint.x) / 2;//ce中点
        int midpoint_jh = (int) (JPoint.y + HPoint.y) / 2;//jh中点
        float minDisToControlPoint = Math.min(Math.abs(midpoint_ce - EPoint.x), Math.abs(midpoint_jh - HPoint.y));//中点到控制点的最小值

        int left;
        int right;
        int top = (int) CPoint.y;
        int bottom = (int) (viewDiagonalLength + CPoint.y);
        GradientDrawable gradientDrawable;
        if (touchMode==TouchMode.TOUCH_TOP) {
            gradientDrawable = drawableBackTop;
            left = (int) (CPoint.x - lightOffset);
            right = (int) (CPoint.x + minDisToControlPoint + deepOffset);
        } else {
            gradientDrawable = drawableBackBottom;
            left = (int) (CPoint.x - minDisToControlPoint - deepOffset);
            right = (int) (CPoint.x + lightOffset);
        }
        gradientDrawable.setBounds(left,top,right,bottom);

        float mDegrees = (float) Math.toDegrees(Math.atan2(EPoint.x- FPoint.x, HPoint.y - FPoint.y));
        canvas.rotate(mDegrees, CPoint.x, CPoint.y);
        gradientDrawable.draw(canvas);
    }



    private void initBitmap(){
        mABitmap = Bitmap.createBitmap(mWidth,mHeight, Bitmap.Config.ARGB_8888);
        mBBitmap = Bitmap.createBitmap(mWidth,mHeight, Bitmap.Config.ARGB_8888);
        mCBitmap = Bitmap.createBitmap(mWidth,mHeight, Bitmap.Config.ARGB_8888);
        Canvas canvasA=new Canvas(mABitmap);
        canvasA.drawColor(Color.WHITE);
        Canvas canvasB=new Canvas(mBBitmap);
        canvasB.drawColor(Color.WHITE);
        Canvas canvasC=new Canvas(mCBitmap);
        canvasC.drawColor(Color.WHITE);
    }

    private Path getCurrentPagePath(){
        if(isStartTurning){
            if(touchMode==TouchMode.TOUCH_TOP){
                return getCurrentPathFromTop();
            }else if(touchMode==TouchMode.TOUCH_BOTTOM){
                return getCurrentPathFromBottom();
            }else{
                return getCurrentPathFromRight();
            }
        }else{
            mCurrentPagePath.reset();
            mCurrentPagePath.lineTo(0,mHeight);
            mCurrentPagePath.lineTo(mWidth,mHeight);
            mCurrentPagePath.lineTo(mWidth,0);
            mCurrentPagePath.close();
            return mCurrentPagePath;
        }
    }

    /**
     * 获取下一页path
     * @return
     */
    private Path getNextPagePath(){
        if(!isStartTurning){
            return null;
        }
        mNextPagePath.reset();
        mNextPagePath.lineTo(0,mHeight);
        mNextPagePath.lineTo(mWidth,mHeight);
        mNextPagePath.lineTo(mWidth,0);
        mNextPagePath.close();
        return mNextPagePath;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x=event.getX();
        float y=event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(isTouchEffective(x)){
                    touchEffective=true;
                    isStartTurning=true;
                    mTouchPoint.set(x,y);
                    mLastTouchPoint.set(x,y);
                    //初始化A点坐标
                    if(APoint==null){
                        APoint=new PointF();
                    }
                    APoint.set(mTouchPoint.x,mTouchPoint.y);
                    initFPoint(y);
                    computeAllPointLocation();
                }
                invalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
                if(touchEffective){
                    float mx=x-mTouchPoint.x;
                    float my=y-mTouchPoint.y;
                    mTouchPoint.set(x,y);
                    //初始化A点坐标
                    if(APoint==null){
                        APoint=new PointF();
                    }
                    //移动后的点
                    if(canMove(APoint.x+mx,APoint.y+my)){
                        APoint.set(APoint.x+mx,APoint.y+my);
                        computeAllPointLocation();
                        invalidate();
                    }
                }
                break;
            case  MotionEvent.ACTION_CANCEL:
            case  MotionEvent.ACTION_UP:
                if(isStartTurning){
                    startScroller();
                }
                touchEffective=false;
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 获取f点在右下角的path
     * @return
     */
    private Path getCurrentPathFromBottom(){
        mCurrentPagePath.reset();
        //移动到右下角
        mCurrentPagePath.lineTo(mWidth, 0);
        //移动到J点
        mCurrentPagePath.lineTo(JPoint.x,JPoint.y);
        //从J到K画贝塞尔曲线，控制点为H
        mCurrentPagePath.quadTo(HPoint.x,HPoint.y,KPoint.x,KPoint.y);
        //移动到a点
        mCurrentPagePath.lineTo(APoint.x,APoint.y);
        //移动到B点
        mCurrentPagePath.lineTo(BPoint.x,BPoint.y);
        //从B到C画贝塞尔曲线，控制点为E
        mCurrentPagePath.quadTo(EPoint.x,EPoint.y,CPoint.x,CPoint.y);

        mCurrentPagePath.lineTo(0,mHeight);
        mCurrentPagePath.close();//闭合区域
        return mCurrentPagePath;
    }

    private Path getCurrentPathFromTop(){
        mCurrentPagePath.reset();
        //移动到c点
        mCurrentPagePath.lineTo(CPoint.x,CPoint.y);
        //从c到b画贝塞尔曲线，控制点为e
        mCurrentPagePath.quadTo(EPoint.x,EPoint.y,BPoint.x,BPoint.y);
        //移动到a点
        mCurrentPagePath.lineTo(APoint.x,APoint.y);
        //移动到k点
        mCurrentPagePath.lineTo(KPoint.x,KPoint.y);
        //从k到j画贝塞尔曲线，控制点为h
        mCurrentPagePath.quadTo(HPoint.x,HPoint.y,JPoint.x,JPoint.y);
        //移动到右下角
        mCurrentPagePath.lineTo(FPoint.x,mHeight);
        //移动到左下角
        mCurrentPagePath.lineTo(0, mHeight);
        mCurrentPagePath.close();
        return mCurrentPagePath;
    }

    private Path getBackPath(){
        if(!isStartTurning){
            return null;
        }
        if(touchMode==TouchMode.TOUCH_RIGHT){
            mBackPath.reset();
            mBackPath.moveTo(JPoint.x,JPoint.y);
            mBackPath.lineTo(IPoint.x,IPoint.y);
            mBackPath.lineTo(DPoint.x,DPoint.y);
            mBackPath.lineTo(CPoint.x,CPoint.y);
            mBackPath.close();
            return mBackPath;
        }else{
            mBackPath.reset();
            //移动到J点
            mBackPath.moveTo(IPoint.x,IPoint.y);
            //从B到C画贝塞尔曲线，控制点为E
            mBackPath.lineTo(DPoint.x,DPoint.y);

            //移动到b点
            mBackPath.lineTo(BPoint.x,BPoint.y);

            //移动到d点
            mBackPath.lineTo(APoint.x,APoint.y);
            //从J到K画贝塞尔曲线，控制点为H
            mBackPath.lineTo(KPoint.x,KPoint.y);
            //移动到k点
            mBackPath.close();//闭合区域
            return mBackPath;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mWidth=MeasureSpec.getSize(widthMeasureSpec);
        mHeight=MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(mWidth,mHeight);
        initBitmap();
    }

    private boolean canMove(float x,float y){
        if(touchMode==TouchMode.TOUCH_RIGHT){
            return x>0&&x<mWidth;
        }else{
            float ex= half(x,FPoint.x)-(FPoint.y-half(y,FPoint.y))*(FPoint.y-half(y,FPoint.y))/(FPoint.x-half(x,FPoint.x));
            //计算C点坐标
            return FPoint.x-(FPoint.x-ex)*3/2>=0;
        }
    }

    private boolean isPointActive(PointF pointF){
        return !Float.isNaN(pointF.x)&&!Float.isNaN(pointF.y);
    }

    private void closeAnimal(){
        mScroller=null;
        isStartTurning=false;
    }

    /**
     * 计算所有点的坐标
     */
    private void computeAllPointLocation(){
        if(GPoint==null){
            GPoint=new PointF();
        }
        if (EPoint == null) {
            EPoint = new PointF();
        }
        if (HPoint == null) {
            HPoint = new PointF();
        }
        if (CPoint == null) {
            CPoint = new PointF();
        }
        if (JPoint == null) {
            JPoint = new PointF();
        }
        if (BPoint == null) {
            BPoint = new PointF();
        }
        if (KPoint == null) {
            KPoint = new PointF();
        }
        if (DPoint == null) {
            DPoint = new PointF();
        }
        if (IPoint == null) {
            IPoint = new PointF();
        }
        if(touchMode==TouchMode.TOUCH_RIGHT){

            CPoint.set(APoint.x,mHeight);
            JPoint.set(APoint.x,0);

            IPoint.set(APoint.x+(mWidth-APoint.x)/3,0);
            DPoint.set(IPoint.x,mHeight);

        }else {

            //计算G点位置
            GPoint.set(half(APoint.x, FPoint.x), half(APoint.y, FPoint.y));

            if (!isPointActive(GPoint)) {
                closeAnimal();
                return;
            }

            //计算E点坐标
            EPoint.set(getEPointX(), FPoint.y);

            if (!isPointActive(EPoint)) {
                closeAnimal();
                return;
            }

            //计算H点坐标
            HPoint.set(FPoint.x, getHPointY());

            if (!isPointActive(HPoint)) {
                closeAnimal();
                return;
            }

            //计算C点坐标
            CPoint.set(FPoint.x - (FPoint.x - EPoint.x) * 3 / 2, FPoint.y);

            if (!isPointActive(CPoint)) {
                closeAnimal();
                return;
            }

            JPoint.set(FPoint.x, FPoint.y - (FPoint.y - HPoint.y) * 3 / 2);

            if (!isPointActive(JPoint)) {
                closeAnimal();
                return;
            }

            //计算B点坐标
            BPoint.set(getBPointX(), getBPointY());

            if (!isPointActive(BPoint)) {
                closeAnimal();
                return;
            }
            //计算K点坐标
            KPoint.set(getKPointX(), getKPointY());

            if (!isPointActive(KPoint)) {
                closeAnimal();
                return;
            }

            //计算D点坐标
            DPoint.set(getDPointX(), getDPointY());

            if (!isPointActive(DPoint)) {
                closeAnimal();
                return;
            }

            //计算I点坐标
            IPoint.set(getIPointX(), getIPointY());

            if (!isPointActive(IPoint)) {
                closeAnimal();
                return;
            }
        }
    }

    /**
     * 计算D点X坐标
     * ((cx+bx)/2+ex)/2
     * @return
     */
    private float getDPointX(){
        return ((CPoint.x+BPoint.x)/2+EPoint.x)/2;
    }

    /**
     * 计算D点Y坐标
     * ((cy+by)/2+ey)/2
     * @return
     */
    private float getDPointY(){
        return ((CPoint.y+BPoint.y)/2+EPoint.y)/2;
    }

    /**
     * 计算I点X坐标
     * ((cx+bx)/2+ex)/2
     * @return
     */
    private float getIPointX(){
        return ((KPoint.x+JPoint.x)/2+HPoint.x)/2;
    }

    /**
     * 计算I点Y坐标
     * ((cy+by)/2+ey)/2
     * @return
     */
    private float getIPointY(){
        return ((KPoint.y+JPoint.y)/2+HPoint.y)/2;
    }

    /**
     * 计算E点x坐标
     * @return
     */
    private float getEPointX(){
        return GPoint.x-(FPoint.y-GPoint.y)*(FPoint.y-GPoint.y)/(FPoint.x-GPoint.x);
    }

    /**
     * 计算H点y坐标
     * @return
     */
    private float getHPointY(){
        return FPoint.y-(FPoint.y-GPoint.y)*(FPoint.x-EPoint.x)/(GPoint.x-EPoint.x);
    }

    /**
     * 计算B点X坐标
     *  x=( (x4*y3-y4*x3)/(x4-x3)-(x2*y1-y2*x1)/(x2-x1)) /
     * ((y2-y1)/(x2-x1)- (y4-y3)/(x4-x3) )
     * @return
     */
    private float getBPointX(){
        return ((APoint.x*EPoint.y-APoint.y*EPoint.x)/(APoint.x-EPoint.x)-
                (JPoint.x*CPoint.y-JPoint.y*CPoint.x)/(JPoint.x-CPoint.x))/
                ((JPoint.y-CPoint.y)/(JPoint.x-CPoint.x)-(APoint.y-EPoint.y)/(APoint.x-EPoint.x));
    }

    /**
     * 计算B点Y坐标
     * y= ( (x4*y3-y4*x3) (x2-x1)- (x2*y1-y2*x1) (x4-x3) ) /
     * ( (y2-y1) (x4-x3)- (y4-y3) (x2-x1) )
     * @return
     */
    private float getBPointY(){
        return ((APoint.x*EPoint.y-APoint.y*EPoint.x)*(JPoint.y-CPoint.y)-
                (JPoint.x*CPoint.y-JPoint.y*CPoint.x)*(APoint.y-EPoint.y))/
                ((JPoint.y-CPoint.y)*(APoint.x-EPoint.x)-(APoint.y-EPoint.y)*(JPoint.x-CPoint.x));
    }

    /**
     * 计算K点X坐标
     *  x=( (x4*y3-y4*x3)/(x4-x3)-(x2*y1-y2*x1)/(x2-x1)) /
     * ((y2-y1)/(x2-x1)- (y4-y3)/(x4-x3) )
     * @return
     */
    private float getKPointX(){
        return ((APoint.x*HPoint.y-APoint.y*HPoint.x)/(APoint.x-HPoint.x)-
                (JPoint.x*CPoint.y-JPoint.y*CPoint.x)/(JPoint.x-CPoint.x))/
                ((JPoint.y-CPoint.y)/(JPoint.x-CPoint.x)-(APoint.y-HPoint.y)/(APoint.x-HPoint.x));
    }

    /**
     * 计算K点Y坐标
     * ((y1 - y2) * (x3 * y4 - x4 * y3) - (x1 * y2 - x2 * y1) * (y3 - y4))
     / ((y1 - y2) * (x3 - x4) - (x1 - x2) * (y3 - y4));
     * @return
     */
    private float getKPointY(){
        return ((APoint.x*HPoint.y-APoint.y*HPoint.x)*(JPoint.y-CPoint.y)-
                (JPoint.x*CPoint.y-JPoint.y*CPoint.x)*(APoint.y-HPoint.y))/
                ((JPoint.y-CPoint.y)*(APoint.x-HPoint.x)-(APoint.y-HPoint.y)*(JPoint.x-CPoint.x));
    }

    private float half(float a,float b){
        return (a+b)/2;
    }

    /**
     * 初始化F点坐标
     * @param y
     */
    private void initFPoint(float y){
        FPoint=new PointF();
        if(y<mEffectiveHeight){
            touchMode=TouchMode.TOUCH_TOP;
            FPoint.set(mWidth,0);
        }else if(y>mHeight-mEffectiveHeight) {
            FPoint.set(mWidth,mHeight);
            touchMode=TouchMode.TOUCH_BOTTOM;
        }else if(y<mHeight-mEffectiveHeight&&y>mEffectiveHeight){
            FPoint.set(mWidth,mHeight);
            touchMode=TouchMode.TOUCH_RIGHT;
        }
    }

    private Bitmap mABitmap;


    private Bitmap mBBitmap;


    private Bitmap mCBitmap;





    /**
     * 开始动画
     */
    private void startScroller(){
        mScroller=new Scroller(getContext());
        int startX= (int) APoint.x;
        int startY= (int) APoint.y;

        int scrollX;
        int scrollY;

        if(touchMode==TouchMode.TOUCH_RIGHT){
            if(mWidth-APoint.x<mWidth/4){
                scrollX=mWidth-startX;
            }else{
                scrollX=-mWidth-startX;
            }
            scrollY=0;
        }else if((FPoint.x-APoint.x)*(FPoint.x-APoint.x)+(FPoint.y-APoint.y)*(FPoint.y-APoint.y)>=mWidth*mWidth/4){
            scrollX=-mWidth-startX;
            if(touchMode==TouchMode.TOUCH_TOP){
                scrollY=-startY;
            }else if(touchMode==TouchMode.TOUCH_BOTTOM){
                scrollY=mHeight-startY;
            }else{
                scrollY=0;
            }
        }else{
            scrollX=mWidth-startX;
            if(touchMode==TouchMode.TOUCH_TOP){
                scrollY=-startY;
            }else if(touchMode==TouchMode.TOUCH_BOTTOM){
                scrollY=mHeight-startY;
            }else{
                scrollY=0;
            }
        }
        mScroller.startScroll(startX,startY,scrollX,scrollY,SCROLL_TIME);
        invalidate();
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        //滚动是否结束
        if(mScroller!=null&&mScroller.computeScrollOffset()){
            APoint.set(mScroller.getCurrX(),mScroller.getCurrY());
            if(mScroller.getCurrX()==mWidth||mScroller.getCurrX()==-mWidth){
                mScroller=null;
                isStartTurning=false;
            }
            computeAllPointLocation();
            invalidate();
        }else if(mScroller!=null){
            //动画执行完成
            isStartTurning=false;
            invalidate();
            mScroller=null;
        }
    }



    /**
     * 按下时是否有效
     * @param x
     * @return
     */
    private  boolean isTouchEffective(float x){
        return x<mWidth&&x>mWidth-mEffectiveWidth;
    }


    public Path getCurrentPathFromRight() {
        mCurrentPagePath.reset();
        mCurrentPagePath.lineTo(JPoint.x,JPoint.y);
        mCurrentPagePath.lineTo(CPoint.x,CPoint.y);
        mCurrentPagePath.lineTo(0,mHeight);
        mCurrentPagePath.close();
        return mCurrentPagePath;
    }
}
