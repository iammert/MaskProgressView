package co.mobiwise.library;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.ColorUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by mertsimsek on 29/10/15.
 */
public class MaskProgressView extends View {

    /**
     * Animation Status enum
     */
    private enum Status {IDLE, PLAYING, PAUSED, STOPPED}

    /**
     * Player status
     */
    private Status mStatus = Status.IDLE;

    /**
     * While dragging this is set to true
     * When dragging ends, we check this value
     * to continue.
     */
    private boolean isProgressCancelled = false;

    /**
     * If start requested before onMeasure is called.
     * We check this value after onMeasure is called.
     */
    private boolean startRequested = false;

    /**
     * Dragging listener
     */
    private OnProgressDraggedListener onProgressDraggedListener;

    /**
     * Notify listeners when animation completed
     */
    private AnimationCompleteListener animationCompleteListener;

    /**
     * Index value for ObjectAnimator
     */
    private float indexY;

    /**
     * Current progress value
     */
    private float mCurrentProgressPosition = 0.0f;

    /**
     * Current seconds
     */
    private int mCurrentSeconds;

    /**
     * Max seconds
     */
    private int mMaxSeconds;

    /**
     * Width and Height values of Custom image
     */
    private int mHeight;

    private int mWidth;

    /**
     * String values to draw duration
     */
    private String mTextPassedDuration = "";

    private String mTextLeftDuration = "";

    /**
     * Default duration text size
     */
    private int mTextDurationSize = 60;

    /**
     * Default progress height
     */
    private int mProgressHeight = 25;

    /**
     * Default color values
     */
    private int mColorLoadedProgress = 0x26000000;

    private int mColorEmptyProgress = 0x26000000;

    private int mColorCoverMask = 0x10000000;

    private int mColorTextDuration = 0xffffffff;

    /**
     * Rects to create rectangle areas
     */
    private Rect rectFTextDuration;

    private RectF rectFEmptyProgress;

    /**
     * Cover image bitmap
     */
    private Bitmap mBitmapCoverImage;

    /**
     * Bottom shadow on image
     */
    private Drawable mDrawableBottomShadow;

    /**
     * Paint variables
     */
    private Paint mPaintProgressMask;

    private Paint mPaintLoadedProgress;

    private Paint mPaintEmptyProgress;

    private Paint mPaintCoverImage;

    private Paint mPaintCoverMask;

    private Paint mPaintTextDuration;

    /**
     * Typeface for duration text paint
     */
    private Typeface mTypefaceDuration;

    /**
     * Progress object animator
     */
    private ObjectAnimator objectAnimatorProgress;

    /**
     * Stop object animator. Better we stop progress
     * by animation.
     */
    private ObjectAnimator objectAnimatorStop;

    /**
     * Constructors
     *
     * @param context
     */
    public MaskProgressView(Context context) {
        super(context);
        init(context, null);
    }

    public MaskProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MaskProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MaskProgressView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    /**
     * Initializes paints, stylables, drawables, Rectfs.
     * It is called only once.
     *
     * @param context
     * @param attributeSet
     */
    private void init(Context context, AttributeSet attributeSet) {

        TypedArray a = context.obtainStyledAttributes(attributeSet, R.styleable.MaskProgressView);
        Drawable mDrawableCover = a.getDrawable(R.styleable.MaskProgressView_coverImage);
        Drawable mDrawablePlaceHolder = a.getDrawable(R.styleable.MaskProgressView_placeHolder);

        mColorEmptyProgress = a.getColor(R.styleable.MaskProgressView_progressEmptyColor, mColorEmptyProgress);
        mColorLoadedProgress = a.getColor(R.styleable.MaskProgressView_progressLoadedColor, mColorLoadedProgress);
        mColorCoverMask = a.getColor(R.styleable.MaskProgressView_coverMaskColor, mColorCoverMask);
        mColorTextDuration = a.getColor(R.styleable.MaskProgressView_durationTextColor, mColorTextDuration);

        mMaxSeconds = a.getInt(R.styleable.MaskProgressView_maxProgress, 0);
        mCurrentSeconds = a.getInt(R.styleable.MaskProgressView_currentProgress, 0);

        mTextDurationSize = a.getDimensionPixelSize(R.styleable.MaskProgressView_durationTextSize, mTextDurationSize);
        mProgressHeight = a.getDimensionPixelOffset(R.styleable.MaskProgressView_progressHeight, mProgressHeight);

        try {
            mDrawableBottomShadow = Drawable.createFromXml(getResources(), getResources().getXml(R.xml.bottom_shadow));
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        a.recycle();

        mTypefaceDuration = Typeface.createFromAsset(context.getAssets(), "roboto_medium.ttf");

        if (mDrawableCover != null)
            mBitmapCoverImage = drawableToBitmap(mDrawableCover);
        else if(mDrawablePlaceHolder != null)
            mBitmapCoverImage = drawableToBitmap(mDrawablePlaceHolder);

        mPaintCoverImage = new Paint();

        mPaintCoverMask = new Paint();
        mPaintCoverMask.setColor(mColorCoverMask);
        mPaintCoverMask.setAntiAlias(true);
        mPaintCoverMask.setStyle(Paint.Style.FILL);

        mPaintProgressMask = new Paint();
        mPaintProgressMask.setAntiAlias(true);
        mPaintProgressMask.setColor(ColorUtils.setAlphaComponent(mColorLoadedProgress, 50));
        mPaintProgressMask.setStyle(Paint.Style.FILL);

        mPaintLoadedProgress = new Paint();
        mPaintLoadedProgress.setAntiAlias(true);
        mPaintLoadedProgress.setColor(mColorLoadedProgress);
        mPaintLoadedProgress.setStyle(Paint.Style.FILL);

        mPaintEmptyProgress = new Paint();
        mPaintEmptyProgress.setAntiAlias(true);
        mPaintEmptyProgress.setColor(mColorEmptyProgress);
        mPaintEmptyProgress.setStyle(Paint.Style.FILL);

        mPaintTextDuration = new Paint();
        mPaintTextDuration.setAntiAlias(true);
        mPaintTextDuration.setColor(mColorTextDuration);
        mPaintTextDuration.setTextSize(mTextDurationSize);

        rectFEmptyProgress = new RectF();
        rectFTextDuration = new Rect();

        objectAnimatorProgress = ObjectAnimator.ofFloat(this, "indexY", 0.0f, 1.0f);
        objectAnimatorProgress.setDuration(mMaxSeconds * 1000);
        objectAnimatorProgress.setInterpolator(new LinearInterpolator());
        objectAnimatorProgress.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mCurrentProgressPosition == mWidth && mStatus == Status.PLAYING){
                    if(animationCompleteListener != null)
                        animationCompleteListener.onAnimationCompleted();
                    stop();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        objectAnimatorStop = ObjectAnimator.ofFloat(this, "indexY", 0.0f);
        objectAnimatorStop.setDuration(100);
        objectAnimatorStop.setInterpolator(new LinearInterpolator());

    }

    /**
     * Object Animator needs getter and setters
     * @return
     */
    public float getIndexY() {
        return indexY;
    }

    /**
     * Object Animator needs getter and setters
     * @return
     */
    public void setIndexY(float indexY) {
        this.indexY = indexY;
        mCurrentProgressPosition = calculateCurrentPositionFromIndexY(indexY);
        mCurrentSeconds = calculateCurrentSeconds(mCurrentProgressPosition);
        postInvalidate();
    }

    /**
     * Calculate witdh, height and region values here
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);

        //Calculate min side to make custom view square
        int minSide = Math.min(mWidth, mHeight);
        mWidth = minSide;
        mHeight = minSide;

        mCurrentProgressPosition = calculateCurrentPositionFromCurrentSeconds(mCurrentSeconds);
        objectAnimatorProgress.setFloatValues(mCurrentProgressPosition / mWidth, 1.0f);
        objectAnimatorProgress.setDuration((mMaxSeconds - mCurrentSeconds) * 1000);

        //scale cover
        if (mBitmapCoverImage != null)
            scaleCoverBitmap(mBitmapCoverImage);

        //set bounds of empty progress
        rectFEmptyProgress.set(0, mHeight - mProgressHeight, mWidth, mHeight);

        //set bounds of bottom shadow
        mDrawableBottomShadow.setBounds(0, (10 * mHeight) / 12, mWidth, mHeight);

        //If star requested before onMeasure is called
        if(startRequested){
            startRequested = false;
            start();
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * Where the magic happens
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //Draw cover image
        if (mBitmapCoverImage != null)
            canvas.drawBitmap(mBitmapCoverImage, 0, 0, mPaintCoverImage);

        //Draw cover image mask
        canvas.drawRect(0, 0, mWidth, mHeight, mPaintCoverMask);

        //Draw bottom shadow
        mDrawableBottomShadow.draw(canvas);

        //Draw empty progress
        canvas.drawRect(rectFEmptyProgress, mPaintEmptyProgress);

        //Draw loaded progress
        canvas.drawRect(
                0,
                mHeight - mProgressHeight,
                mCurrentProgressPosition,
                mHeight,
                mPaintLoadedProgress);

        //Draw loaded progress mask on cover image
        canvas.drawRect(
                0,
                0,
                mCurrentProgressPosition,
                mHeight,
                mPaintProgressMask);

        //Draw passed and left duration text
        mTextPassedDuration = secondsToTime(mCurrentSeconds);
        mPaintTextDuration.getTextBounds(mTextPassedDuration, 0, mTextPassedDuration.length(), rectFTextDuration);
        canvas.drawText(
                mTextPassedDuration,
                mWidth / 30,
                mHeight - mWidth / 30 - rectFTextDuration.height(),
                mPaintTextDuration);

        mTextLeftDuration = secondsToTime(mMaxSeconds - mCurrentSeconds);
        mPaintTextDuration.getTextBounds(mTextLeftDuration, 0, mTextLeftDuration.length(), rectFTextDuration);
        canvas.drawText(
                mTextLeftDuration,
                (29 * mWidth / 30) - rectFTextDuration.width(),
                mHeight - mWidth / 30 - rectFTextDuration.height(),
                mPaintTextDuration);

    }

    /**
     * Scale given bitmap to view
     * If Image is not square, Then we need to apply
     * CENTER_CROP operation.
     *
     * @param bitmap
     */
    private void scaleCoverBitmap(Bitmap bitmap) {

        int fitSide = bitmap.getWidth() > bitmap.getHeight() ? bitmap.getHeight() : bitmap.getWidth();
        float scaleRate = (float) mWidth / (float) fitSide;

        mBitmapCoverImage = Bitmap.createScaledBitmap(
                bitmap,
                (int) (bitmap.getWidth() * scaleRate),
                (int) (bitmap.getHeight() * scaleRate),
                false);

        if (bitmap.getWidth() > mWidth)
            mBitmapCoverImage = Bitmap.createBitmap(
                    mBitmapCoverImage,
                    mBitmapCoverImage.getWidth() / 2 - mWidth / 2,
                    0,
                    mWidth,
                    mHeight);
        else
            mBitmapCoverImage = Bitmap.createBitmap(
                    mBitmapCoverImage,
                    0,
                    mBitmapCoverImage.getHeight() / 2 - mHeight / 2,
                    mWidth,
                    mHeight);

        postInvalidate();

    }

    /**
     * Convert drawable to bitmap to draw bitmap to canvas.
     *
     * @param drawable
     * @return
     */
    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 1;
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 1;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    /**
     * Handle Drag event and postInvalidate when position changed.
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX();
        float y = event.getY();

        objectAnimatorProgress.cancel();
        isProgressCancelled = true;

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (y < mHeight) {
                    mCurrentProgressPosition = x;
                    mCurrentSeconds = calculateCurrentSeconds(mCurrentProgressPosition);

                    if(onProgressDraggedListener != null)
                        onProgressDraggedListener.onProgressDragging(mCurrentSeconds);

                    postInvalidate();
                }
                return true;
            case MotionEvent.ACTION_DOWN: {
                if (y < mHeight) {
                    mCurrentProgressPosition = x;
                    mCurrentSeconds = calculateCurrentSeconds(mCurrentProgressPosition);

                    if(onProgressDraggedListener != null)
                        onProgressDraggedListener.onProgressDragging(mCurrentSeconds);

                    postInvalidate();
                }
                return true;
            }
            case MotionEvent.ACTION_UP: {
                if(onProgressDraggedListener != null)
                    onProgressDraggedListener.onProgressDragged(mCurrentSeconds);
                objectAnimatorProgress.setFloatValues(mCurrentProgressPosition / mWidth, 1.0f);
                objectAnimatorProgress.setDuration((mMaxSeconds - mCurrentSeconds) * 1000);
                if (mStatus == Status.PLAYING) {
                    objectAnimatorProgress.start();
                    isProgressCancelled = false;
                }
                return true;
            }
            default:
                break;
        }

        return super.onTouchEvent(event);
    }

    public void setCoverImage(Bitmap mBitmapCoverImage){
        this.mBitmapCoverImage = mBitmapCoverImage;
        if(mWidth > 0)
            scaleCoverBitmap(mBitmapCoverImage);
    }

    public void setCoverImage(int coverDrawable){
        Drawable drawable = ContextCompat.getDrawable(getContext(), coverDrawable);
        mBitmapCoverImage = drawableToBitmap(drawable);
        if(mWidth > 0)
            scaleCoverBitmap(mBitmapCoverImage);
    }

    /**
     * Calculate current seconds value depends on progress position
     *
     * @param mCurrentProgressPosition
     * @return
     */
    private int calculateCurrentSeconds(float mCurrentProgressPosition) {
        return (int) (mCurrentProgressPosition * mMaxSeconds) / mWidth;
    }

    /**
     * Calculate current progress position by using indexY value
     *
     * @param indexY
     * @return
     */
    private float calculateCurrentPositionFromIndexY(float indexY) {
        return mWidth * indexY;
    }

    /**
     * Calculate current progress position by using current seconds
     *
     * @param mCurrentSeconds
     * @return
     */
    private int calculateCurrentPositionFromCurrentSeconds(int mCurrentSeconds) {
        return mWidth * mCurrentSeconds / mMaxSeconds;
    }

    /**
     * Convert seconds to time
     *
     * @param seconds
     * @return
     */
    private String secondsToTime(int seconds) {
        String time = "";

        String minutesText = String.valueOf(seconds / 60);
        if (minutesText.length() == 1)
            minutesText = "0" + minutesText;

        String secondsText = String.valueOf(seconds % 60);
        if (secondsText.length() == 1)
            secondsText = "0" + secondsText;

        time = minutesText + ":" + secondsText;

        return time;
    }

    /**
     * Set progress drag listener
     *
     * @param onProgressDraggedListener
     */
    public void setOnProgressDraggedListener(OnProgressDraggedListener onProgressDraggedListener) {
        this.onProgressDraggedListener = onProgressDraggedListener;
    }

    /**
     * Set Animation Complete listener
     * @param animationCompleteListener
     */
    public void setAnimationCompleteListener(AnimationCompleteListener animationCompleteListener){
        this.animationCompleteListener = animationCompleteListener;
    }

    /**
     * Set current progress
     *
     * @param mCurrentSeconds
     */
    public void setmCurrentSeconds(int mCurrentSeconds) {
        this.mCurrentSeconds = mCurrentSeconds;
        postInvalidate();
    }

    /**
     * Set Max progress
     *
     * @param mMaxSeconds
     */
    public void setmMaxSeconds(int mMaxSeconds) {
        this.mMaxSeconds = mMaxSeconds;
        if(mStatus != Status.IDLE)
            stop();
        objectAnimatorProgress.setFloatValues(0.0f, 1.0f);
        objectAnimatorProgress.setDuration(mMaxSeconds * 1000);
        mCurrentSeconds = 0;
        postInvalidate();
    }

    /**
     * Start animation horizontally
     */
    public void start() {

        if(mWidth == 0){
            startRequested = true;
            return;
        }

        if (mStatus == Status.PLAYING)
            return;

        if (mStatus == Status.IDLE || mStatus == Status.STOPPED || mCurrentProgressPosition == 0.0f)
            objectAnimatorProgress.start();

        else {
            if (Build.VERSION.SDK_INT < 19) {
                objectAnimatorProgress.setCurrentPlayTime(mCurrentSeconds * 1000);
                objectAnimatorProgress.start();
            } else {
                if (isProgressCancelled) {
                    objectAnimatorProgress.start();
                    isProgressCancelled = false;
                } else
                    objectAnimatorProgress.resume();
            }
        }

        mStatus = Status.PLAYING;
    }

    /**
     * Pause Animation
     */
    public void pause() {

        mStatus = Status.PAUSED;

        if (Build.VERSION.SDK_INT < 19)
            objectAnimatorProgress.end();
        else
            objectAnimatorProgress.pause();
    }

    /**
     * Set stop animation values to current position
     * Reset progress animation float values.
     */
    public void stop() {

        mStatus = Status.STOPPED;

        objectAnimatorStop.setFloatValues(mCurrentProgressPosition / mWidth, 0.0f);
        mCurrentSeconds = 0;
        mCurrentProgressPosition = 0.0f;
        objectAnimatorProgress.setFloatValues(mCurrentProgressPosition, 1.0f);
        objectAnimatorProgress.setDuration(mMaxSeconds * 1000);
        objectAnimatorProgress.end();
        objectAnimatorStop.start();
    }

    public boolean isPlaying() {
        return mStatus == Status.PLAYING;
    }

    public boolean isPaused(){
        return mStatus == Status.PAUSED;
    }
}
