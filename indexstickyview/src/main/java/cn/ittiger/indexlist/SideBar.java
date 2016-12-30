package cn.ittiger.indexlist;

import cn.ittiger.indexstickyview.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * 类似联系人的索引bar
 * Created by ylhu on 16-12-16.
 */
class SideBar extends View implements View.OnTouchListener {
    private static final int DEFAULT_TEXT_COLOR = Color.parseColor("#8c8c8c");//默认文字颜色
    private static final int DEFAULT_TEXT_SIZE = 14;//默认文字大小14sp
    private static final int DEFAULT_FOCUS_TEXT_COLOR = Color.WHITE;//默认选中时的文字颜色
    private static final int DEFAULT_FOCUS_BACKGROUND_COLOR = Color.parseColor("#f33737");//默认选中时的背景色
    private static final int DEFAULT_BAR_ITEM_SPACE = 4;//每隔bar item间的间隔，4dp
    private static final int DEFAULT_BAR_BACKGROUND = Color.TRANSPARENT;//默认bar的背景色

    /**
     * 计算出来的高度
     */
    private int mCalViewHeight;
    /**
     * 每一个Item的高度
     */
    private float mItemHeight;
    /**
     * 要显示的索引文字列表:{#,A,B,C,D....}
     */
    private List<String> mValueList = new ArrayList<>();
    /**
     * 选中文字的索引
     */
    private int mSelectPosition;
    /**
     * 文字颜色
     */
    private int mTextColor;
    /**
     * 选中文字颜色
     */
    private int mFocusTextColor;
    /**
     * 选中文的背景色
     */
    private int mFocusBackgroundColor;
    /**
     * 文字字体大小
     */
    private float mTextSize;
    /**
     * 文字间距
     */
    private float mTextSpace;

    /**
     * 文字画笔
     */
    private TextPaint mTextPaint;
    /**
     * 选中文字画笔
     */
    private TextPaint mFocusTextPaint;
    /**
     * 当前视图背景色
     */
    private int mBackground;
    /**
     * 选中文字的背景圆画笔
     */
    private Paint mFocusTextBgPaint;

    private OnSideBarTouchListener mOnSideBarTouchListener;

    public SideBar(Context context) {

        this(context, null);
    }

    public SideBar(Context context, AttributeSet attrs) {

        this(context, attrs, 0);
    }

    public SideBar(Context context, AttributeSet attrs, int defStyleAttr) {

        super(context, attrs, defStyleAttr);
        setOnTouchListener(this);
    }

    /**
     * 初始化相关属性参数
     *
     * @param typedArray
     */
    public void initAttrs(TypedArray typedArray) {

        mTextColor = typedArray.getColor(R.styleable.IndexStickyView_sideBarTextColor, DEFAULT_TEXT_COLOR);
        mFocusTextColor = typedArray.getColor(R.styleable.IndexStickyView_sideBarFocusTextColor, DEFAULT_FOCUS_TEXT_COLOR);
        mFocusBackgroundColor = typedArray.getColor(R.styleable.IndexStickyView_sideBarFocusBackgroundColor, DEFAULT_FOCUS_BACKGROUND_COLOR);
        mTextSize = typedArray.getDimensionPixelSize(R.styleable.IndexStickyView_sideBarTextSize, 0);
        mTextSpace = typedArray.getDimensionPixelSize(R.styleable.IndexStickyView_sideBarTextSpace, 0);
        mBackground = typedArray.getColor(R.styleable.IndexStickyView_sideBarBackgroundColor, DEFAULT_BAR_BACKGROUND);

        initDefaultAttrs();
        initTextPaint();
        setBackgroundColor(mBackground);
    }

    /**
     * 初始化相关默认值
     */
    private void initDefaultAttrs() {

        if(mTextColor == 0) {
            mTextColor = DEFAULT_TEXT_COLOR;
        }
        if(mFocusTextColor == 0) {
            mFocusTextColor = DEFAULT_FOCUS_TEXT_COLOR;
        }
        if(mFocusBackgroundColor == 0) {
            mFocusBackgroundColor = DEFAULT_FOCUS_BACKGROUND_COLOR;
        }
        if(mTextSize == 0) {
            mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, DEFAULT_TEXT_SIZE, getResources().getDisplayMetrics());
        }
        if(mTextSpace == 0) {
            mTextSpace = getResources().getDisplayMetrics().density * DEFAULT_BAR_ITEM_SPACE;
        }
        if(mBackground == 0) {
            mBackground = DEFAULT_BAR_BACKGROUND;
        }
    }

    private void initTextPaint() {

        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setColor(mTextColor);

        mFocusTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mFocusTextPaint.setTextSize(mTextSize);
        mFocusTextPaint.setTextAlign(Paint.Align.CENTER);
        mFocusTextPaint.setColor(mFocusTextColor);

        mFocusTextBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //抗锯齿，对边界锯齿进行模糊处理
        mFocusTextBgPaint.setAntiAlias(true);
        mFocusTextBgPaint.setFilterBitmap(true);
        //防抖动，对图像抖动进行模糊平滑处理，使之看起来更柔和
        mFocusTextBgPaint.setDither(true);
        //实心填充绘制
        mFocusTextBgPaint.setStyle(Paint.Style.FILL);
        mFocusTextBgPaint.setColor(mFocusBackgroundColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (mValueList.size() > 0) {
            //计算SideBar的实际高度
            mCalViewHeight = (int) (((mValueList.size() - 1) * mTextPaint.getTextSize() + mFocusTextPaint.getTextSize())
                    + (mValueList.size() + 1) * mTextSpace);
        }

        if (mCalViewHeight > height) {//实际高度超过可用高度
            mCalViewHeight = height;
        }

        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(mCalViewHeight, MeasureSpec.EXACTLY));
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
        if(mValueList.size() == 0) {
            return;
        }
        //计算每项的高度
        mItemHeight = ((float) getHeight()) / mValueList.size();

        //关于文字居中参考：http://www.jianshu.com/p/c2b720fa5877和http://blog.csdn.net/zly921112/article/details/50401976
        float radius = Math.min(getWidth() / 2, mItemHeight / 2);//选中状态时圆形背景半径
        for(int i = 0; i < mValueList.size(); i++) {
            if(mSelectPosition == i) {
                //计算文本垂直居中的基准线
                float baseline = mItemHeight / 2 + (mFocusTextPaint.getFontMetrics().descent - mFocusTextPaint.getFontMetrics().ascent) / 2
                        - mFocusTextPaint.getFontMetrics().descent;
                canvas.drawCircle(getWidth() / 2, mItemHeight / 2 + mItemHeight * i, radius, mFocusTextBgPaint);
                canvas.drawText(mValueList.get(i), getWidth() / 2, baseline + mItemHeight * i, mFocusTextPaint);
            } else {
                float baseline = mItemHeight / 2 + (mTextPaint.getFontMetrics().descent - mTextPaint.getFontMetrics().ascent) / 2
                        - mTextPaint.getFontMetrics().descent;
                canvas.drawText(mValueList.get(i), getWidth() / 2, baseline + mItemHeight * i, mTextPaint);
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        int touchPosition = getPositionForPointY(event.getY());
        if(touchPosition < 0 || touchPosition >= mValueList.size()) {
            return true;
        }
        if(mOnSideBarTouchListener != null) {
            mOnSideBarTouchListener.onSideBarTouch(v, event, touchPosition);
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                if(touchPosition != mSelectPosition) {
                    setSelectPosition(touchPosition);
                }
                break;
        }
        return true;
    }

    /**
     * 根据点击的y坐标计算得到当前选中的是哪个选项
     * @param pointY
     * @return      没选中则返回-1
     */
    private int getPositionForPointY(float pointY) {

        if(mValueList.size() <= 0) {
            return -1;
        }
        //根据手按下的纵坐标与每个选项的高度计算当前所在项的索引
        int position = (int) (pointY / mItemHeight);
        if(position < 0) {
            position = 0;
        } else if(position > mValueList.size() - 1) {
            position = mValueList.size() - 1;
        }
        return position;
    }

    public void setData(List<String> list) {

        mValueList = list;
        mSelectPosition = 0;
        requestLayout();
        invalidate();
    }

    public void setOnSideBarTouchListener(OnSideBarTouchListener onSideBarTouchListener) {

        mOnSideBarTouchListener = onSideBarTouchListener;
    }

    public int getSelectPosition() {

        return mSelectPosition;
    }

    public void setSelectPosition(int selectPosition) {

        if(selectPosition != mSelectPosition) {
            mSelectPosition = selectPosition;
            invalidate();
        }
    }

    public int getPosition(String indexValue) {

        return mValueList.indexOf(indexValue);
    }

    public String getIndexValue(int position) {

        return mValueList.get(position);
    }

    public int getTextColor() {

        return mTextColor;
    }

    public void setTextColor(int textColor) {

        if(textColor != mTextColor) {
            mTextColor = textColor;
            mTextPaint.setColor(mTextColor);
            invalidate();
        }
    }

    public int getFocusTextColor() {

        return mFocusTextColor;
    }

    public void setFocusTextColor(int focusTextColor) {

        mFocusTextColor = focusTextColor;
        mFocusTextPaint.setColor(focusTextColor);
        invalidate();
    }

    public float getTextSize() {

        return mTextSize;
    }

    public void setTextSize(float textSize) {

        mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, textSize, getResources().getDisplayMetrics());
        mTextPaint.setTextSize(mTextSize);
        mFocusTextPaint.setTextSize(mTextSize);
        requestLayout();
        invalidate();
    }

    public float getTextSpace() {

        return mTextSpace;
    }

    public void setTextSpace(float textSpace) {

        mTextSpace = textSpace;
        requestLayout();
        invalidate();
    }

    public interface OnSideBarTouchListener {

        /**
         * @param v
         * @param event
         * @param touchPosition     当前touch的Item项的索引，值>=0，调用此方法时，selectPosition值还未更新
         */
        void onSideBarTouch(View v, MotionEvent event, int touchPosition);
    }
}
