package cn.ittiger.indexlist;

import cn.ittiger.indexlist.adapter.IndexHeaderFooterAdapter;
import cn.ittiger.indexlist.adapter.IndexStickyViewAdapter;
import cn.ittiger.indexlist.entity.IndexStickyEntity;
import cn.ittiger.indexstickyview.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * 顶部固定的索引列表
 * @author: laohu on 2016/12/17
 * @site: http://ittiger.cn
 */
public class IndexStickyView extends RelativeLayout implements SideBar.OnSideBarTouchListener, Observer {
    private static final int DEFAULT_SIDEBAR_WIDTH = 24;//sidebar的默认宽度24dp
    private static final int DEFAULT_CENTER_OVERLAY_SIZE = 75;//中间提示视图的size
    private static final int DEFAULT_CENTER_OVERLAY_TEXT_COLOR = Color.WHITE;//中间提示视图的文字颜色
    private static final int DEFAULT_CENTER_OVERLAY_TEXT_SIZE = 18;//中间提示视图的文字大小

    /**
     * 右边索引条
     */
    private SideBar mSideBar;
    /**
     * 右边索引条的宽度
     */
    private int mSideBarWidth;
    /**
     * 展示数据的RecyclerView
     */
    private RecyclerView mRecyclerView;
    /**
     * RecyclerView垂直方向布局
     */
    private LinearLayoutManager mLinearLayoutManager;
    /**
     * SideBar滑动或是点击时，中间显示索引文字信息
     */
    private TextView mCenterOverlayView;
    /**
     * 固定在头部显示索引提示的视图
     */
    private RecyclerView.ViewHolder mStickyHeaderView;
    /**
     * 当前头部固定视图显示的索引值
     */
    private String mStickyIndexValue;
    /**
     * 当前视图{@link IndexStickyView} 的数据适配器
     */
    private IndexStickyViewAdapter mAdapter;

    public IndexStickyView(Context context) {

        this(context, null);
    }

    public IndexStickyView(Context context, AttributeSet attrs) {

        this(context, attrs, 0);
    }

    public IndexStickyView(Context context, AttributeSet attrs, int defStyleAttr) {

        super(context, attrs, defStyleAttr);
        initViewAttrs(context, attrs);
    }

    private void initViewAttrs(Context context, AttributeSet attrs) {

        initRecyclerView(context);
        initSideBar(context, attrs);
        initCenterOverlayView(context);
    }

    /**
     * 初始化右边SideBar
     * @param context
     * @param attrs
     */
    private void initSideBar(Context context, AttributeSet attrs) {

        int defaultSideBarWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                                DEFAULT_SIDEBAR_WIDTH, getResources().getDisplayMetrics());

        mSideBar = new SideBar(context);
        mSideBar.setOnSideBarTouchListener(this);
        mSideBarWidth = defaultSideBarWidth;
        if(attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.IndexStickyView);
            mSideBar.initAttrs(typedArray);
            mSideBarWidth = typedArray.getDimensionPixelSize(R.styleable.IndexStickyView_sideBarWidth, defaultSideBarWidth);
            typedArray.recycle();
        }

        LayoutParams layoutParams = new LayoutParams(mSideBarWidth, LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(ALIGN_PARENT_END);
        layoutParams.addRule(CENTER_VERTICAL);
        addView(mSideBar, layoutParams);
    }

    /**
     * 初始化数据列表视图
     * @param context
     */
    private void initRecyclerView(Context context) {

        mLinearLayoutManager = new LinearLayoutManager(context);
        mRecyclerView = new RecyclerView(context);
        mRecyclerView.setVerticalScrollBarEnabled(false);
        mRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.addOnScrollListener(new RecyclerViewScrollListener());

        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(mRecyclerView, layoutParams);
    }

    /**
     * 初始化滑动或点击SideBar时，中间显示索引信息的视图
     * @param context
     */
    private void initCenterOverlayView(Context context) {

        int textSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                DEFAULT_CENTER_OVERLAY_TEXT_SIZE, getResources().getDisplayMetrics());

        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                DEFAULT_CENTER_OVERLAY_SIZE, getResources().getDisplayMetrics());

        mCenterOverlayView = new TextView(context);
        mCenterOverlayView.setTextSize(textSize);
        mCenterOverlayView.setTextColor(DEFAULT_CENTER_OVERLAY_TEXT_COLOR);
        mCenterOverlayView.setGravity(Gravity.CENTER);
        mCenterOverlayView.setBackgroundResource(R.drawable.indexstickyview_center_overlay_bg);
        mCenterOverlayView.setVisibility(GONE);
        LayoutParams layoutParams = new LayoutParams(size, size);
        layoutParams.addRule(CENTER_IN_PARENT);
        addView(mCenterOverlayView, layoutParams);
    }

    /**
     * 数据列表滚动监听
     */
    class RecyclerViewScrollListener extends RecyclerView.OnScrollListener {

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

            super.onScrolled(recyclerView, dx, dy);
            int firstVisiblePosition = mLinearLayoutManager.findFirstVisibleItemPosition();
            if(firstVisiblePosition < 0 || firstVisiblePosition >= mAdapter.getItemCount()) {
                return;
            }
            IndexStickyEntity entity = mAdapter.getItem(firstVisiblePosition);
            mSideBar.setSelectPosition(mSideBar.getPosition(entity.getIndexValue()));
            if(TextUtils.isEmpty(entity.getIndexName()) && mStickyHeaderView.itemView.getVisibility() == VISIBLE) {
                //如果当前第一个可见项的索引值为空，则当前项可能是普通视图，非索引视图，因此此时需要将mStickyHeaderView进行隐藏
                mStickyIndexValue = null;
                mStickyHeaderView.itemView.setVisibility(INVISIBLE);
            } else {//第一个可见项为索引视图，则需要显示头部固定的索引提示视图
                showStickyHeaderView(entity.getIndexName(), firstVisiblePosition);
            }

            if(firstVisiblePosition + 1 >= mAdapter.getItemCount()) {
                return;
            }
            //获取第二个可见项实体对象
            IndexStickyEntity secondVisibleEntity = mAdapter.getItem(firstVisiblePosition + 1);
            if(secondVisibleEntity.getItemType() == ItemType.ITEM_TYPE_INDEX) {
                //第二个可见项是索引值视图
                View secondVisibleItemView = mLinearLayoutManager.findViewByPosition(firstVisiblePosition + 1);
                if(secondVisibleItemView.getTop() <= mStickyHeaderView.itemView.getHeight() && mStickyIndexValue != null) {
                    //当secondVisibleItemView距顶部的距离 <= mStickyHeaderView的高度时，mStickyHeaderView开始往上滑出
                    mStickyHeaderView.itemView.setTranslationY(secondVisibleItemView.getTop() - mStickyHeaderView.itemView.getHeight());
                }
            } else {
                //第二个可见项不是索引值视图
                if(mStickyHeaderView.itemView.getTranslationY() != 0) {//有偏移
                    mStickyHeaderView.itemView.setTranslationY(0);
                }
            }
        }
    }

    /**
     * 显示固定在头部的索引提示视图
     * @param indexName
     * @param position
     */
    private void showStickyHeaderView(String indexName, int position) {

        if(indexName == null) {
            if(mStickyHeaderView.itemView.getVisibility() != GONE) {
                mStickyHeaderView.itemView.setVisibility(GONE);
            }
            mStickyIndexValue = indexName;
            return;
        }
        if(mStickyHeaderView.itemView.getVisibility() != VISIBLE) {
            mStickyHeaderView.itemView.setVisibility(VISIBLE);
        }
        if(indexName.equals(mStickyIndexValue)) {
            return;
        }
        mStickyIndexValue = indexName;
        mAdapter.onBindIndexViewHolder(mStickyHeaderView, position, indexName);
    }

    @Override
    public void onSideBarTouch(View v, MotionEvent event, int touchPosition) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                showCenterOverlayView(mSideBar.getIndexValue(touchPosition));
                if(touchPosition != mSideBar.getSelectPosition()) {
                    if(touchPosition == 0) {
                        mLinearLayoutManager.scrollToPosition(0);
                    } else {
                        int recyclerViewPosition = getScrollPositionBySideBarSelectPosition(touchPosition);
                        mLinearLayoutManager.scrollToPositionWithOffset(recyclerViewPosition, 0);
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                hideCenterOverlayView();
                break;
        }
    }

    /**
     * 根据SideBar中每个索引值获取该索引值在{@link RecyclerView} 中的位置
     * @param touchPosition
     * @return
     */
    private int getScrollPositionBySideBarSelectPosition(int touchPosition) {

        String indexValue = mSideBar.getIndexValue(touchPosition);
        return mAdapter.getIndexValuePosition(indexValue);
    }

    public void setAdapter(IndexStickyViewAdapter adapter) {

        mAdapter = adapter;
        initStickyHeaderView();
        mSideBar.setData(adapter.getIndexValueList());
        mRecyclerView.setAdapter(adapter);
        IndexValueBus.getInstance().clear();
        IndexValueBus.getInstance().addObserver(this);
    }

    /**
     * 添加头部自定义索引数据适配器
     * @param adapter
     */
    public void addIndexHeaderAdapter(IndexHeaderFooterAdapter adapter) {

        if(mAdapter == null) {
            throw new NullPointerException("IndexStickyViewAdapter is null, please set IndexStickyViewAdapter first");
        }
        mAdapter.addIndexHeaderAdapter(adapter);
    }

    /**
     * 添加底部自定义索引数据适配器
     * @param adapter
     */
    public void addIndexFooterAdapter(IndexHeaderFooterAdapter adapter) {

        if(mAdapter == null) {
            throw new NullPointerException("IndexStickyViewAdapter is null, please set IndexStickyViewAdapter first");
        }
        mAdapter.addIndexFooterAdapter(adapter);
    }

    /**
     * 初始化{@link #mStickyHeaderView}
     */
    private void initStickyHeaderView() {

        if(mStickyHeaderView == null) {
            mStickyHeaderView = mAdapter.onCreateIndexViewHolder(mRecyclerView);
            mStickyHeaderView.itemView.setVisibility(INVISIBLE);
            LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            layoutParams.addRule(ALIGN_PARENT_TOP);
            for(int i = 0; i < getChildCount(); i++) {
                if(getChildAt(i) == mSideBar) {
                    addView(mStickyHeaderView.itemView, i, layoutParams);
                    break;
                }
            }
        }
    }

    /**
     * 显示中间的索引提示
     * @param indexValue
     */
    private void showCenterOverlayView(String indexValue) {

        if(mCenterOverlayView.getVisibility() != VISIBLE) {
            mCenterOverlayView.setVisibility(VISIBLE);
        }
        if(!mCenterOverlayView.getText().equals(indexValue)) {
            mCenterOverlayView.setText(indexValue);
        }
    }

    /**
     * 隐藏中的索引值提示
     */
    private void hideCenterOverlayView() {

        if(mCenterOverlayView.getVisibility() != GONE) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    mCenterOverlayView.setVisibility(GONE); 
                }
            }, 100);
        }
    }

    public void addItemDecoration(RecyclerView.ItemDecoration itemDecoration) {

        mRecyclerView.addItemDecoration(itemDecoration);
    }

    @Override
    public void update(Observable observable, Object data) {

        if(data == null) return;
        List<String> indexValueList = (List<String>) data;
        mSideBar.setData(indexValueList);
        mLinearLayoutManager.scrollToPosition(0);
    }

    public int getSideBarWidth() {

        return mSideBar.getWidth();
    }
}
