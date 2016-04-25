package com.edgardeng.widget;

/**
 *
 * 放大缩小的 尺子
 *
 * @author Edgar Deng (http:weibo.com/edgardeng)
 * @date 16/4/25
 */


    import android.content.Context;
    import android.graphics.Canvas;
    import android.graphics.Color;
    import android.graphics.Paint;
    import android.graphics.Rect;
    import android.util.AttributeSet;
    import android.util.Log;
    import android.view.MotionEvent;
    import android.view.View;


public class DragZoomScaler  extends View {


        private Paint mPaint = null;
        private float[]  values;    //要表明的刻度线
        private String[] texts;     //要标明的刻度线 显示的文字
        private float maximum;
        //    orientation
        private TICK_ORIENTATION  orientation = TICK_ORIENTATION.TOP;

        public enum TICK_ORIENTATION{
            TOP ,BOTTOM,LEFT,RIGHT
        }

        public DragZoomScaler(Context context) {
            super(context);
            mPaint = new Paint();
        }


        public DragZoomScaler(Context context, AttributeSet attrs)
        {
            super(context, attrs);
        }

        public DragZoomScaler(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);

        }
        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }


        private float perItemH          = 0;//比列
        private float originItemH       = 0;   //原始比例
        private float startDrawY        = 0;

        /** 刻度 */
        public void  setData(float[] vs,float max){
            if(mPaint == null)
                mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            values = vs;
            maximum = max;
            invalidate();
        }

        /** 刻度 */
        public void  setData(float[] vs,float max,String[] ts){
            if(mPaint == null)
                mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            values = vs;
            texts  = ts;
            maximum = max;
        }

        public void  setData(float[] vs){
            float max = vs[vs.length-1];
            setData(vs,max);
        }


        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            Log.w("DragScaler","onDraw");

            if (mPaint == null)
                mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

            float w = getWidth();
            float h = getHeight();


            int size = 20;
            int index = 0;

            mPaint.setStrokeWidth(2.0f);
            mPaint.setColor(0xFF000000);
            mPaint.setTextSize(16);

            // 1 - 100 21 个 刻度  20个颜色

            float w_scaler = w/3;

            int color = 0xFF0F0F00;
            float y = 0;
            while(index < 100)  {
                y = startDrawY + index  * perItemH;
                canvas.drawLine(0, y, w_scaler -10, y, mPaint);
                mPaint.setColor(Color.rgb(index*2,index/2,index));
                canvas.drawRect(w_scaler,y,w,y + 5 * perItemH, mPaint);
                index+=5;
            }
            y = startDrawY + index  * perItemH;
            canvas.drawLine(0, y-1, w_scaler -10, y-1, mPaint);
            canvas.drawLine(w_scaler-10,0,w_scaler-10, h, mPaint);//竖线
        }



    private int screen_W, screen_H;
    private int bitmap_W, bitmap_H;
    private int MAX_W, MAX_H, MIN_W, MIN_H;
    private int current_Top, current_Right, current_Bottom, current_Left;//

    private int start_Top = -1, start_Right = -1, start_Bottom = -1,start_Left = -1;
    private int start_x, start_y, current_x, current_y;//

    private double beforeLenght, afterLenght;//

    private float scale_temp;//


    private enum MODE {
        NONE, DRAG, ZOOM
    };

    private MODE mode = MODE.NONE;//

    private boolean isControl_V = true;//

    private boolean isControl_H = false;//

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (start_Top == -1) {
            start_Top = top;
            start_Left = left;
            start_Bottom = bottom;
            start_Right = right;
        }
        if(perItemH == 0){
            perItemH = getHeight() /100.0f;
            originItemH = perItemH;

            MAX_W=getHeight()*2;
            MAX_H=getWidth()*2;
            MIN_W=getHeight();
            MIN_H=getWidth();

        }
        Log.w("DragScaler","onLayout");

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        /**   **/

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                onTouchDown(event);
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                onPointerDown(event);
                break;

            case MotionEvent.ACTION_MOVE:
                onTouchMove(event);
                break;
            case MotionEvent.ACTION_UP:
                mode = MODE.NONE;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mode = MODE.NONE;
//                if (isScaleAnim) {
//                    doScaleAnim();
//                }
                //执行缩放还原
                break;
        }

        return true;
    }

    float before_y;
    float after_y;
    /** **/
    void onTouchDown(MotionEvent event) {
        mode = MODE.DRAG;

        current_x = (int) event.getRawX();
        current_y = (int) event.getRawY();

        start_x = (int) event.getX();
        start_y = current_y - this.getTop();

        before_y = event.getY();

    }

    /**  **/
    void onPointerDown(MotionEvent event) {
        if (event.getPointerCount() == 2) {
            mode = MODE.ZOOM;
            beforeLenght = getDistance(event);//
        }
    }

    /**   **/
    void onTouchMove(MotionEvent event) {
        int left = 0, top = 0, right = 0, bottom = 0;
        /**  拖曳  **/
        if (mode == MODE.DRAG) {
            after_y = event.getY();
            float now_x = event.getX() - start_x;
            if(Math.abs(after_y - before_y) > Math.abs(now_x)){
                if(perItemH > originItemH){
                    startDrawY += after_y - before_y;//不能超过

                    if(after_y - before_y >0 && startDrawY >0 ){
                        startDrawY = 0;
                    }
                    if(after_y - before_y <0 && startDrawY + perItemH*100 <getHeight() ){
                        startDrawY  =  getHeight() - 100  * perItemH;
                    }
                    invalidate();
                    before_y = after_y;
                    return;
                }

            }
        }
        /**  **/
        else if (mode == MODE.ZOOM) {
            Log.w("DragScaler","on ZOOM ");
            afterLenght = getDistance(event);//手指的距离

            float gapLenght = (float) afterLenght - (float)beforeLenght;

            if (Math.abs(gapLenght) > 5f) {
                scale_temp = (float) afterLenght / (float) beforeLenght;
                this.setScale(scale_temp);
                beforeLenght = afterLenght;
            }
        }




    }

    /**  **/
    double getDistance(MotionEvent event) {
        double x = event.getX(0) - event.getX(1);
        double y = event.getY(0) - event.getY(1);
        return Math.sqrt( x * x + y * y );
    }

    /**  **/
    private void setPosition(int left, int top, int right, int bottom) {
        this.layout(left, top, right, bottom);
    }

    /**   **/
    void setScale(float scale) {
        //放大
        if (scale > 1 && perItemH <= originItemH *5) { //最大 放大 5倍

            perItemH *= scale;
            invalidate();
        }
        // 缩小
        else if (scale < 1 && perItemH >= originItemH) {
            perItemH *= scale;
            if(perItemH <  originItemH) perItemH = originItemH;
            invalidate();
        }

    }


}
