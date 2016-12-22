package cn.ittiger.indexlist.demo;

import cn.ittiger.indexlist.ItemType;
import cn.ittiger.indexlist.adapter.IndexStickyViewAdapter;
import cn.ittiger.indexlist.entity.IndexStickyEntity;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by ylhu on 16-12-22.
 */
public class IndexStickyViewDecoration extends RecyclerView.ItemDecoration {

    private Drawable mDivider;
    private int mOrientation;
    //我们通过获取系统属性中的listDivider来添加，在系统中的AppTheme中设置
    public static final int[] ATRRS  = new int[]{
            android.R.attr.listDivider
    };

    public IndexStickyViewDecoration(Context context) {

        final TypedArray ta = context.obtainStyledAttributes(ATRRS);
        this.mDivider = ta.getDrawable(0);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        int position = parent.getChildAdapterPosition(view);
        IndexStickyEntity entity = ((IndexStickyViewAdapter)parent.getAdapter()).getItem(position);
        if(entity.getItemType() != ItemType.ITEM_TYPE_INDEX && position != 0) {
            outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

        super.onDraw(c, parent, state);
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++){
            final View child = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(child);
            IndexStickyEntity entity = ((IndexStickyViewAdapter)parent.getAdapter()).getItem(position);
            if(entity.getItemType() != ItemType.ITEM_TYPE_INDEX && position != 0) {
                //获得child的布局信息
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)child.getLayoutParams();
                final int top = child.getBottom() + params.bottomMargin;
                final int bottom = top + mDivider.getIntrinsicHeight();
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }

        }
    }
}
