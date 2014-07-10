package com.mani.staggeredview.demo;

import android.app.Activity;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View.OnTouchListener;
import android.view.View;
import android.widget.Scroller;

public class ZoomImageView extends ImageView implements OnTouchListener {

	private Matrix matrix = new Matrix();
	private Matrix savedMatrix = new Matrix();
	private Matrix savedMatrix2 = new Matrix();

	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;
	private int mode = NONE;

	private PointF start = new PointF();
	private PointF mid = new PointF();
	private float oldDist = 1f;

	private static final int WIDTH = 0;
	private static final int HEIGHT = 1;

	private boolean isInit = false;

	public ZoomImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		//setOnTouchListener(this);
		setScaleType(ScaleType.MATRIX);
        initView(context);
	}

	public ZoomImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ZoomImageView(Context context) {
		this(context, null);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (!isInit){
			init();
			isInit = true;
		}
	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		super.setImageBitmap(bm);
		isInit = false;
		init();
	}

	@Override
	public void setImageDrawable(Drawable drawable) {
		super.setImageDrawable(drawable);
		isInit = false;
		init();
	}

	@Override
	public void setImageResource(int resId) {
		super.setImageResource(resId);
		isInit = false;
		init();
	}

	protected void init() {
        updateMatrix();
		setImagePit();
	}

	public void setImagePit(){
		// matrix value
		float[] value = new float[9];
		this.matrix.getValues(value);

		// view volume
		int width = this.getWidth();
		int height = this.getHeight();

		// image volume
		Drawable d = this.getDrawable();
		if (d == null)  return;
		int imageWidth = d.getIntrinsicWidth();
		int imageHeight = d.getIntrinsicHeight();
		int scaleWidth = (int) (imageWidth * value[0]);
		int scaleHeight = (int) (imageHeight * value[4]);

		// image should not outside
		value[2] = 0;
		value[5] = 0;
		if (imageWidth > width || imageHeight > height){
			int target = WIDTH;
			if (imageWidth < imageHeight) target = HEIGHT;

			if (target == WIDTH) value[0] = value[4] = (float)width / imageWidth;
			if (target == HEIGHT) value[0] = value[4] = (float)height / imageHeight;

			scaleWidth = (int) (imageWidth * value[0]);
			scaleHeight = (int) (imageHeight * value[4]);

			if (scaleWidth > width) value[0] = value[4] = (float)width / imageWidth;
			if (scaleHeight > height) value[0] = value[4] = (float)height / imageHeight;
		}

		// center
		scaleWidth = (int) (imageWidth * value[0]);
		scaleHeight = (int) (imageHeight * value[4]);
		if (scaleWidth < width){
			value[2] = (float) width / 2 - (float)scaleWidth / 2;
		}
		if (scaleHeight < height){
			value[5] = (float) height / 2 - (float)scaleHeight / 2;
		}
		matrix.setValues(value);
		setImageMatrix(matrix);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		ImageView view = (ImageView) v;

		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			savedMatrix.set(matrix);
			start.set(event.getX(), event.getY());
			mode = DRAG;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			oldDist = spacing(event);
			if (oldDist > 1f) {
				savedMatrix.set(matrix);
				midPoint(mid, event);
				mode = ZOOM;
			}
			break;
		case MotionEvent.ACTION_UP:

		case MotionEvent.ACTION_POINTER_UP:
			mode = NONE;
			break;
		case MotionEvent.ACTION_MOVE:
			if (mode == DRAG) {
				matrix.set(savedMatrix);
				matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);

			}
			else if (mode == ZOOM) {
				float newDist = spacing(event);
				if (newDist > 1f) {
					matrix.set(savedMatrix);
					float scale = newDist / oldDist;
					matrix.postScale(scale, scale, mid.x, mid.y);
				}
			}
			break;
		}
		matrixTurning(matrix, view);
		view.setImageMatrix(matrix);
		return true;
	}

	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return FloatMath.sqrt(x * x + y * y);
	}

	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

	private void matrixTurning(Matrix matrix, ImageView view){
		// matrix value
		float[] value = new float[9];
		matrix.getValues(value);
		float[] savedValue = new float[9];
		savedMatrix2.getValues(savedValue);

		// view volume
		int width = view.getWidth();
		int height = view.getHeight();

		// image volume
		Drawable d = view.getDrawable();
		if (d == null)  return;
		int imageWidth = d.getIntrinsicWidth();
		int imageHeight = d.getIntrinsicHeight();
		int scaleWidth = (int) (imageWidth * value[0]);
		int scaleHeight = (int) (imageHeight * value[4]);

		// image should not move outside
		if (value[2] < width - scaleWidth)   value[2] = width - scaleWidth;
		if (value[5] < height - scaleHeight)   value[5] = height - scaleHeight;
		if (value[2] > 0)   value[2] = 0;
		if (value[5] > 0)   value[5] = 0;

		// image should not increase than 10 times
		if (value[0] > 10 || value[4] > 10){
			value[0] = savedValue[0];
			value[4] = savedValue[4];
			value[2] = savedValue[2];
			value[5] = savedValue[5];
		}

		// image should not decrease than original screen
		if (imageWidth > width || imageHeight > height){
			if (scaleWidth < width && scaleHeight < height){
				int target = WIDTH;
				if (imageWidth < imageHeight) target = HEIGHT;

				if (target == WIDTH) value[0] = value[4] = (float)width / imageWidth;
				if (target == HEIGHT) value[0] = value[4] = (float)height / imageHeight;

				scaleWidth = (int) (imageWidth * value[0]);
				scaleHeight = (int) (imageHeight * value[4]);

				if (scaleWidth > width) value[0] = value[4] = (float)width / imageWidth;
				if (scaleHeight > height) value[0] = value[4] = (float)height / imageHeight;
			}
		}

		// original small image should not small than original image
		else{
			if (value[0] < 1)   value[0] = 1;
			if (value[4] < 1)   value[4] = 1;
		}

		// image should order center
		scaleWidth = (int) (imageWidth * value[0]);
		scaleHeight = (int) (imageHeight * value[4]);
		if (scaleWidth < width){
			value[2] = (float) width / 2 - (float)scaleWidth / 2;
		}
		if (scaleHeight < height){
			value[5] = (float) height / 2 - (float)scaleHeight / 2;
		}

		matrix.setValues(value);
		savedMatrix2.set(matrix);
	}

    protected Scroller mScroller;
    protected int mTouchSlop;
    protected int mMinimumVelocity;
    protected int mMaximumVelocity;

    protected float mLastMotionX;
    protected float mLastMotionY;
    protected boolean mIsBeingDragged;
    protected VelocityTracker mVelocityTracker;
    protected int mActivePointerId=INVALID_POINTER;

    protected static final int INVALID_POINTER=-1;
    int screenWidth=480;
    int screenHeight=800;

    private void initView(Context cx) {
        //设置滚动减速器，在fling中会用到
        mScroller=new Scroller(cx, new DecelerateInterpolator(0.5f));
        final ViewConfiguration configuration=ViewConfiguration.get(cx);
        mTouchSlop=configuration.getScaledTouchSlop();
        mMinimumVelocity=configuration.getScaledMinimumFlingVelocity();
        mMaximumVelocity=configuration.getScaledMaximumFlingVelocity();

        DisplayMetrics screen = new DisplayMetrics();
        ((Activity)cx).getWindowManager().getDefaultDisplay().getMetrics(screen);
        screenWidth=screen.widthPixels;
        screenHeight=screen.heightPixels;
    }

    /**
     * 此方法为最后机会来修改mScrollX,mScrollY.
     * 这方法后将根据mScrollX,mScrollY来偏移Canvas已实现内容滚动
     */
    @Override
    public void computeScroll() {
        //super.computeScroll();
        //computeScrollImpl();
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            awakenScrollBars();
            postInvalidate();
        }
    }

    protected void computeScrollImpl() {
        final Scroller scroller=mScroller;
        if (scroller.computeScrollOffset()) { //正在滚动，让view滚动到当前位置
            int scrollY=scroller.getCurrY();
            int scrollX=scroller.getCurrX();

            float[] value = new float[9];
            matrix.getValues(value);

            Drawable d = getDrawable();
            if (d == null)  return;
            int imageWidth = d.getIntrinsicWidth();
            int imageHeight = d.getIntrinsicHeight();
            int scaleWidth = (int) (imageWidth * value[0]);
            int scaleHeight = (int) (imageHeight * value[4]);

            int maxY=scaleHeight-screenHeight;
            int maxX=scaleWidth-screenWidth;
            /*if (maxX<0){
                maxX=0;
            }
            if (maxY<0){
                maxY=0;
            }*/
            boolean toEdge=scrollY<0||scrollY>maxY;
            if (scrollY<0) {
                //scrollY=0;
            } else if (scrollY>maxY) {
                scrollY=maxY;
            }

            toEdge=toEdge||scrollX<0||scrollX>maxX;
            if (scrollX<0) {
                //scrollX=0;
            } else if (scrollX>maxX) {
                scrollX=maxX;
            }

            if (scrollY<10){
            }

            /*
             *下面等同于：
             * mScrollY = scrollY;
             * awakenScrollBars(); //显示滚动条，必须在xml中配置。
             * postInvalidate();
             */
            scrollTo(scrollX, scrollY);
            if (toEdge) {//移到两端，由于位置没有发生变化，导致滚动条不显示
                awakenScrollBars();
            }
        }
    }

    RectF rect;
    public void fling(int velocityX, int velocityY) {
        float[] value=new float[9];
        matrix.getValues(value);

        Drawable d=getDrawable();
        if (d==null) return;
        int imageWidth=d.getIntrinsicWidth();
        int imageHeight=d.getIntrinsicHeight();
        int scaleWidth=(int) (imageWidth*value[0]);
        int scaleHeight=(int) (imageHeight*value[4]);

        /*int maxY=scaleHeight-screenHeight;
        int maxX=scaleWidth-screenWidth;*/

        if (null==rect){
            rect=new RectF(0, 0, screenWidth, screenHeight);
        }
        rect.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        matrix.mapRect(rect);

        final int startX = Math.round(-rect.left);
        final int minX, maxX, minY, maxY;

        if (screenWidth < rect.width()) {
            minX = 0;
            maxX = Math.round(rect.width() - screenWidth);
        } else {
            minX = maxX = startX;
        }

        final int startY = Math.round(-rect.top);
        if (screenHeight < rect.height()) {
            minY = 0;
            maxY = Math.round(rect.height() - screenHeight);
        } else {
            minY = maxY = startY;
        }
        /*move:scrollx:-19 getScrollY:-640 cx:-19 delatX:2 deltaY:-141
        ACTION_UP:150 scrollX:-17 getScrollY:-781 initialVelocityX:-264 initialVelocityY:11726
        imageW:220,imageH:3295,minX:-13,maxX:-13, minY:0, maxY:1478, startX:-13, startY:-1509 RectF(12.915283, 1508.5526, 239.79324, 4906.566)
        screenW:1080 screenH:1920 scaleW:226 scaleH:3398*/

        Log.d(VIEW_LOG_TAG, String.format("imageW:%d,imageH:%d,minX:%d,maxX:%d, minY:%d, maxY:%d, startX:%d, startY:%d",
            imageWidth, imageHeight, minX, maxX, minY, maxY, startX, startY)+
            " "+rect+" screenW:"+screenWidth+" screenH:"+screenHeight+" scaleW:"+scaleWidth+" scaleH:"+scaleHeight);
        if (startX!=maxX||startY!=maxY) {
            mScroller.fling(mScroller.getCurrX(), mScroller.getCurrY(), velocityX, velocityY, minX, Math.max(0, maxX), minY,
                Math.max(0, maxY));
        }

        //刷新，让父控件调用computeScroll()
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean handled=false;
        handled=processScroll(event);

        return handled|super.onTouchEvent(event);
    }

    protected boolean processScroll(MotionEvent ev) {
        boolean handled=false;
        if (mVelocityTracker==null) {
            mVelocityTracker=VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev); //帮助类，用来在fling时计算移动初速度

        final int action=ev.getAction();

        switch (action&MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                if (!mScroller.isFinished()) {
                    mScroller.forceFinished(true);
                }

                mLastMotionX=ev.getX();
                mLastMotionY=ev.getY();
                mActivePointerId=ev.getPointerId(0);
                mIsBeingDragged=true;
                handled=true;

                savedMatrix.set(matrix);
                start.set(mLastMotionX, mLastMotionY);
                mode = NONE;
                break;
            }
            case MotionEvent.ACTION_POINTER_DOWN: {
                oldDist=spacing(ev);
                if (oldDist>1f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, ev);
                    mode=ZOOM;
                    mIsBeingDragged=false;
                }
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                final int pointerId=mActivePointerId;
                if (mIsBeingDragged&&INVALID_POINTER!=pointerId) {
                    final int pointerIndex=ev.findPointerIndex(pointerId);
                    float y=0;
                    float x=0;
                    try {
                        y=ev.getY(pointerIndex);
                        x=ev.getX(pointerIndex);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    int deltaY=(int) (mLastMotionY-y);
                    int delatX=(int) (mLastMotionX-x);

                    //if (Math.abs(deltaY)>mTouchSlop) { //移动距离(正负代表方向)必须大于ViewConfiguration设置的默认值,但是会卡.
                    mLastMotionX=x;
                    mLastMotionY=y;

                    /*
                     * 默认滚动时间为250ms，建议立即滚动，否则滚动效果不明显
                     * 或者直接使用scrollBy(0, deltaY);
                     */
                    if (delatX!=0&&deltaY!=0) {
                        //Log.d(VIEW_LOG_TAG, "move:scrollx:"+getScrollX()+" getScrollY:"+getScrollY()+" cx:"+mScroller.getCurrX()+" delatX:"+delatX+" deltaY:"+deltaY);
                        mScroller.startScroll(getScrollX(), getScrollY(), delatX, deltaY, 0);
                        invalidate();
                        handled=true;
                    }
                    start.set(delatX, deltaY);  //这个很关键,可以去除越界的问题
                    //}
                }
                if (mode == ZOOM) {
                    float newDist = spacing(ev);
                    if (newDist > 1f) {
                        matrix.set(savedMatrix);
                        float scale = newDist / oldDist;
                        //Log.d(VIEW_LOG_TAG, "mid:"+mid);
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                    updateMatrix();
                    handled=true;
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                final int pointerId=mActivePointerId;
                if (mIsBeingDragged&&INVALID_POINTER!=pointerId) {
                    final VelocityTracker velocityTracker=mVelocityTracker;
                    velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                    int initialVelocityX=(int) velocityTracker.getXVelocity(pointerId);
                    int initialVelocityY=(int) velocityTracker.getYVelocity(pointerId);

                    if (Math.max(Math.abs(initialVelocityX), Math.abs(initialVelocityY)) >= mMinimumVelocity) {
                        Log.d(VIEW_LOG_TAG, "ACTION_UP:"+mMinimumVelocity+" scrollX:"+getScrollX()+" getScrollY:"+getScrollY()+" initialVelocityX:"+initialVelocityX+" initialVelocityY:"+initialVelocityY);
                        matrix.set(savedMatrix);
                        matrix.postTranslate(mLastMotionX - start.x, mLastMotionY - start.y);
                        updateMatrix();
                        start.set(mLastMotionX, mLastMotionY);
                        fling(-initialVelocityX, -initialVelocityY);
                    } else {
                        //可以在这里恢复位置,比如不能超出边界.
                    }

                    mActivePointerId=INVALID_POINTER;
                    mIsBeingDragged=false;

                    if (mVelocityTracker!=null) {
                        mVelocityTracker.recycle();
                        mVelocityTracker=null;
                    }

                    handled=true;
                }
                mode = NONE;
                break;
            }
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                handled=true;
                break;
        }

        return handled;
    }

    private void updateMatrix() {
        matrixTurning(matrix, this);
        setImageMatrix(matrix);
    }

}