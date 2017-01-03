package cn.ittiger.indexlist.adapter;

import cn.ittiger.indexlist.entity.BaseEntity;
import cn.ittiger.indexlist.entity.IndexStickyEntity;
import cn.ittiger.indexlist.helper.ConvertHelper;
import cn.ittiger.indexlist.listener.OnItemClickListener;
import cn.ittiger.indexlist.listener.OnItemLongClickListener;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.ViewGroup;

import java.util.ArrayList;
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
    private int mItemType = -1;
    private List<IndexStickyEntity<T>> mEntityList;
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

    void transfer(int itemType) {

        mItemType = itemType;
        //转换得到当前添加的Header数据
        mEntityList = ConvertHelper.transferHeaderFooterData(this, mItemType);

        if(isNormalView() == false && !TextUtils.isEmpty(getIndexName())) {
            //当前添加的Header为索引Header且要显示的索引标题不为空时才创建索引实体
            IndexStickyEntity<T> indexEntity = ConvertHelper.createIndexEntity(getIndexValue(), getIndexName());
            mEntityList.add(0, indexEntity);
        }
    }

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

    /**
     * 添加到{@link cn.ittiger.indexlist.IndexStickyView}中之后才会有值，否则为长度为0的列表
     * @return
     */
    public List<IndexStickyEntity<T>> getEntityList() {

        return mEntityList == null ? new ArrayList<IndexStickyEntity<T>>(0) : mEntityList;
    }

    /**
     * 添加到{@link cn.ittiger.indexlist.IndexStickyView}中之后才会有值，否则为-1
     * @return
     */
    public int getItemType() {

        return mItemType;
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
