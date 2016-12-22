package cn.ittiger.indexlist.adapter;

import cn.ittiger.indexlist.entity.BaseEntity;
import cn.ittiger.indexlist.listener.OnItemClickListener;
import cn.ittiger.indexlist.listener.OnItemLongClickListener;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

/**
 * 列表头部自定义实体数据适配器
 * @author: laohu on 2016/12/21
 * @site: http://ittiger.cn
 */
public abstract class IndexHeaderFooterAdapter<T extends BaseEntity> {
    private String mIndexValue;
    private String mIndexName;
    private List<T> mOriginalList;
    private boolean mNormalView = false;
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    public IndexHeaderFooterAdapter() {

        mNormalView = true;
    }

    public IndexHeaderFooterAdapter(String indexValue, String indexName, List<T> list) {

        mIndexValue = indexValue;
        mIndexName = indexName;
        mOriginalList = list;
    }

    public abstract RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent);

    public abstract void onBindViewHolder(RecyclerView.ViewHolder holder, int position, T itemData);

    public String getIndexValue() {

        return mIndexValue;
    }

    public String getIndexName() {

        return mIndexName;
    }

    public List<T> getOriginalList() {

        return mOriginalList;
    }

    public boolean isNormalView() {

        return mNormalView;
    }

    public OnItemClickListener getOnItemClickListener() {

        return mOnItemClickListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {

        mOnItemClickListener = onItemClickListener;
    }

    public OnItemLongClickListener getOnItemLongClickListener() {

        return mOnItemLongClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {

        mOnItemLongClickListener = onItemLongClickListener;
    }
}
