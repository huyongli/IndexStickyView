package cn.ittiger.indexlist.adapter;

import cn.ittiger.indexlist.IndexValueBus;
import cn.ittiger.indexlist.ItemType;
import cn.ittiger.indexlist.entity.BaseEntity;
import cn.ittiger.indexlist.entity.IndexStickyEntity;
import cn.ittiger.indexlist.helper.ConvertHelper;
import cn.ittiger.indexlist.listener.OnItemClickListener;
import cn.ittiger.indexlist.listener.OnItemLongClickListener;

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link cn.ittiger.indexlist.IndexStickyView}的数据适配器
 * @author: laohu on 2016/12/17
 * @site: http://ittiger.cn
 */
public abstract class IndexStickyViewAdapter<T extends BaseEntity> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    /**
     * 用户要展示的原始数据实体
     */
    private List<T> mOriginalList;
    /**
     * 列表中实际展示时用到的实体数据
     */
    private List<IndexStickyEntity<T>> mList;
    /**
     * 索引值在列表中位置的映射关系
     */
    private Map<String, Integer> mIndexValuePositionMap;
    /**
     * 索引值列表
     */
    private List<String> mIndexValueList;
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    //自定义头部
    private SparseArray<IndexHeaderFooterAdapter<T>> mIndexHeaderAdapters = new SparseArray<>(0);
    private List<IndexStickyEntity<T>> mIndexHeaderList = new ArrayList<>();
    private Map<String, Integer> mIndexHeaderValuePositionMap = new HashMap<>();
    private List<String> mIndexHeaderValueList = new ArrayList<>();

    //自定义Footer
    private SparseArray<IndexHeaderFooterAdapter<T>> mIndexFooterAdapters = new SparseArray<>(0);
    private List<IndexStickyEntity<T>> mIndexFooterList = new ArrayList<>();
    private Map<String, Integer> mIndexFooterValuePositionMap = new HashMap<>();
    private List<String> mIndexFooterValueList = new ArrayList<>();

    public IndexStickyViewAdapter(List<T> originalList) {

        if(originalList == null) {
            throw new NullPointerException("originalList is null");
        }
        mOriginalList = originalList;
        transferOriginalData();
    }

    private void transferOriginalData() {

        ConvertHelper.ConvertResult convertResult = ConvertHelper.transfer(mOriginalList);
        mList = convertResult.getIndexStickyEntities();
        mIndexValuePositionMap = convertResult.getIndexValuePositionMap();
        mIndexValueList = convertResult.getIndexValueList();
    }

    /**
     * 获取列表实际展示数据
     * @return
     */
    public final List<IndexStickyEntity<T>> getData() {

        return mList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        if(viewType == ItemType.ITEM_TYPE_INDEX) {//IndexSticky索引类型视图
            viewHolder = onCreateIndexViewHolder(parent);
        } else if(viewType == ItemType.ITEM_TYPE_CONTENT) {//IndexSticky索引类型视图正常Item内容视图
            viewHolder = onCreateContentViewHolder(parent);
        } else if(mIndexHeaderAdapters.indexOfKey(viewType) >= 0) {//自定义的头部视图
            viewHolder = mIndexHeaderAdapters.get(viewType).onCreateViewHolder(parent);
        } else if(mIndexFooterAdapters.indexOfKey(viewType) >= 0) {//自定义的Footer视图
            viewHolder = mIndexFooterAdapters.get(viewType).onCreateViewHolder(parent);
        } else {
            throw new IllegalStateException("don't support viewType:" + viewType);
        }
        addListener(viewHolder, viewType);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        int viewType = getItemViewType(position);
        if(viewType == ItemType.ITEM_TYPE_INDEX) {//IndexSticky索引类型视图
            onBindIndexViewHolder(holder, position, getItem(position).getIndexName());
        } else if(viewType == ItemType.ITEM_TYPE_CONTENT) {//IndexSticky索引类型视图正常Item内容视图
            onBindContentViewHolder(holder, position, getItem(position).getOriginalData());
        } else if(mIndexHeaderAdapters.indexOfKey(viewType) >= 0) {//自定义的头部视图
            mIndexHeaderAdapters.get(viewType).onBindViewHolder(holder, position, getItem(position).getOriginalData());
        } else if(mIndexFooterAdapters.indexOfKey(viewType) >= 0) {//自定义的Footer视图
            mIndexFooterAdapters.get(viewType).onBindViewHolder(holder, position, getItem(position).getOriginalData());
        } else {
            throw new IllegalStateException("don't support viewType:" + viewType);
        }
    }

    private void addListener(final RecyclerView.ViewHolder viewHolder, final int viewType) {

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = viewHolder.getAdapterPosition();
                IndexStickyEntity<T> entity = getItem(position);
                if(viewType == ItemType.ITEM_TYPE_CONTENT) {//IndexSticky索引类型视图正常Item内容视图
                    if(mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(viewHolder.itemView, position, entity.getOriginalData());
                    }
                }  else if(mIndexHeaderAdapters.indexOfKey(viewType) >= 0) {//自定义的头部视图
                    IndexHeaderFooterAdapter<T> adapter = mIndexHeaderAdapters.get(viewType);
                    if(adapter.getOnItemClickListener() != null) {
                        adapter.getOnItemClickListener().onItemClick(viewHolder.itemView, position, entity.getOriginalData());
                    }
                } else if(mIndexFooterAdapters.indexOfKey(viewType) >= 0) {//自定义的Footer视图
                    IndexHeaderFooterAdapter<T> adapter = mIndexFooterAdapters.get(viewType);
                    if(adapter.getOnItemClickListener() != null) {
                        adapter.getOnItemClickListener().onItemClick(viewHolder.itemView, position, entity.getOriginalData());
                    }
                }
            }
        });

        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                int position = viewHolder.getAdapterPosition();
                IndexStickyEntity<T> entity = getItem(position);
                if(viewType == ItemType.ITEM_TYPE_CONTENT) {//IndexSticky索引类型视图正常Item内容视图
                    if(mOnItemLongClickListener != null) {
                        mOnItemLongClickListener.onItemLongClick(viewHolder.itemView, position, entity.getOriginalData());
                    }
                }  else if(mIndexHeaderAdapters.indexOfKey(viewType) >= 0) {//自定义的头部视图
                    IndexHeaderFooterAdapter<T> adapter = mIndexHeaderAdapters.get(viewType);
                    if(adapter.getOnItemLongClickListener() != null) {
                        adapter.getOnItemLongClickListener().onItemLongClick(viewHolder.itemView, position, entity.getOriginalData());
                    }
                } else if(mIndexFooterAdapters.indexOfKey(viewType) >= 0) {//自定义的Footer视图
                    IndexHeaderFooterAdapter<T> adapter = mIndexFooterAdapters.get(viewType);
                    if(adapter.getOnItemLongClickListener() != null) {
                        adapter.getOnItemLongClickListener().onItemLongClick(viewHolder.itemView, position, entity.getOriginalData());
                    }
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {

        return mList.size() + mIndexHeaderList.size() + mIndexFooterList.size();
    }

    public final IndexStickyEntity<T> getItem(int position) {

        if(isIndexHeaderPosition(position)) {
            return mIndexHeaderList.get(position);
        }
        if(isIndexFooterPosition(position)) {
            return mIndexFooterList.get(position - mList.size() - mIndexHeaderList.size());
        }
        return mList.get(position - mIndexHeaderList.size());
    }

    @Override
    public int getItemViewType(int position) {

        return getItem(position).getItemType();
    }

    /**
     * 获取索引值在列表中的位置
     * @param indexValue
     * @return
     */
    public final int getIndexValuePosition(String indexValue) {

        if(mIndexValuePositionMap.containsKey(indexValue)) {
            return mIndexValuePositionMap.get(indexValue);
        }
        return -1;
    }

    /**
     * 获取索引值列表
     * @return
     */
    public List<String> getIndexValueList() {

        return mIndexValueList;
    }

    /**------------- 动态添加数据 -----------------**/

    public final void add(T originalEntity) {

        mOriginalList.add(originalEntity);
        refresh(1);
    }

    public final void add(List<T> originalList) {

        mOriginalList.addAll(originalList);
        refresh(originalList.size());
    }

    public final void remove(T originalEntity) {

        mOriginalList.remove(originalEntity);
        refresh(-1);
    }

    public final void removeAll() {

        mOriginalList.clear();
        refresh(-1);
    }

    public final void clear() {

        mOriginalList.clear();

    }

    public final void clearHeader() {

        if(mIndexHeaderAdapters.size() > 0) {
            for(String indexValue : mIndexValuePositionMap.keySet()) {
                mIndexValuePositionMap.remove(indexValue);
            }
            mIndexHeaderValuePositionMap.clear();

            mIndexValueList.removeAll(mIndexHeaderValueList);
            mIndexHeaderValueList.clear();

            mIndexHeaderAdapters.clear();
            mIndexHeaderList.clear();
        }
    }

    public final void clearFooter() {

        mIndexFooterAdapters.clear();
        mIndexFooterList.clear();
        mIndexFooterValueList.clear();
        mIndexFooterValuePositionMap.clear();
    }

    /**
     * 刷新数据列表
     * @param count
     */
    private void refresh(int count) {

        transferOriginalData();
        addAllIndexHeaderAdapterData();
        addAllIndexFooterAdapterData(count);
        IndexValueBus.getInstance().notifyDataSetChanged(mIndexValueList);
        notifyDataSetChanged();
    }

    /**------------ 设置Header自定义索引数据项适配器 -------------------------**/
    public void addIndexHeaderAdapter(IndexHeaderFooterAdapter<T> indexHeaderAdapter) {

        if(indexHeaderAdapter == null) {
            return;
        }
        //因为可能会添加多个Header，所以此处的ItemType按照Header个数进行变化
        int itemType = ItemType.ITEM_TYPE_INDEX_HEADER + mIndexHeaderAdapters.size();
        mIndexHeaderAdapters.put(itemType, indexHeaderAdapter);

        //转换得到当前添加的Header数据
        List<IndexStickyEntity<T>> entities = ConvertHelper.transferHeaderFooterData(indexHeaderAdapter, itemType);

        IndexStickyEntity<T> indexEntity;
        if(indexHeaderAdapter.isNormalView() == false && !TextUtils.isEmpty(indexHeaderAdapter.getIndexName())) {
            //当前添加的Header为索引Header且要显示的索引标题不为空时才创建索引实体
            indexEntity = ConvertHelper.createIndexEntity(indexHeaderAdapter.getIndexValue(), indexHeaderAdapter.getIndexName());
            entities.add(0, indexEntity);
        }
        //更新索引值与位置的映射
        for(String indexValue : mIndexValuePositionMap.keySet()) {
            mIndexValuePositionMap.put(indexValue, mIndexValuePositionMap.get(indexValue) + entities.size());
        }
        //更新之前添加的Header索引值与位置的映射
        for(String indexValue : mIndexHeaderValuePositionMap.keySet()) {
            mIndexHeaderValuePositionMap.put(indexValue, mIndexHeaderValuePositionMap.get(indexValue) + entities.size());
        }
        //如果当前添加的Header索引值不为空则添加该索引到索引列表中
        if(!TextUtils.isEmpty(indexHeaderAdapter.getIndexValue())) {
            mIndexValueList.add(0, indexHeaderAdapter.getIndexValue());
            mIndexValuePositionMap.put(indexHeaderAdapter.getIndexValue(), 0);
            mIndexHeaderValueList.add(0, indexHeaderAdapter.getIndexValue());
            mIndexHeaderValuePositionMap.put(indexHeaderAdapter.getIndexValue(), 0);
        }
        mIndexHeaderList.addAll(0, entities);
        notifyDataSetChanged();
    }

    /**
     * 更新列表后，重新将Header添加到列表中
     */
    private void addAllIndexHeaderAdapterData() {

        if(mIndexHeaderAdapters.size() > 0) {
            for(String indexValue : mIndexValuePositionMap.keySet()) {
                mIndexValuePositionMap.put(indexValue, mIndexValuePositionMap.get(indexValue) + mIndexHeaderList.size());
            }
            mIndexValuePositionMap.putAll(mIndexHeaderValuePositionMap);
            mIndexValueList.addAll(0, mIndexHeaderValueList);
        }
    }

    /**
     * 当前位置是否为Header区域
     * @param position
     * @return
     */
    private boolean isIndexHeaderPosition(int position) {

        return position < mIndexHeaderList.size();
    }

    /**------------ 设置Footer自定义索引数据项适配器 -------------------------**/
    public void addIndexFooterAdapter(IndexHeaderFooterAdapter<T> indexFooterAdapter) {

        if(indexFooterAdapter == null) {
            return;
        }
        int itemType = ItemType.ITEM_TYPE_INDEX_FOOTER + mIndexFooterAdapters.size();
        mIndexFooterAdapters.put(itemType, indexFooterAdapter);

        List<IndexStickyEntity<T>> entities = ConvertHelper.transferHeaderFooterData(indexFooterAdapter, itemType);

        IndexStickyEntity<T> indexEntity = null;
        if(indexFooterAdapter.isNormalView() == false && !TextUtils.isEmpty(indexFooterAdapter.getIndexName())) {
            indexEntity = ConvertHelper.createIndexEntity(indexFooterAdapter.getIndexValue(), indexFooterAdapter.getIndexName());
            entities.add(0, indexEntity);
        }
        if(!TextUtils.isEmpty(indexFooterAdapter.getIndexValue())) {
            mIndexValueList.add(indexFooterAdapter.getIndexValue());
            mIndexValuePositionMap.put(indexFooterAdapter.getIndexValue(), getItemCount());
            mIndexFooterValueList.add(indexFooterAdapter.getIndexValue());
            mIndexFooterValuePositionMap.put(indexFooterAdapter.getIndexValue(), getItemCount());
        }
        mIndexFooterList.addAll(entities);
        notifyDataSetChanged();
    }

    /**
     * 更新数据列表后，重新将Footer添加到列表中
     * @param count
     */
    private void addAllIndexFooterAdapterData(int count) {

        if(mIndexFooterAdapters.size() > 0) {
            for(String indexValue : mIndexFooterValuePositionMap.keySet()) {
                mIndexFooterValuePositionMap.put(indexValue, mIndexFooterValuePositionMap.get(indexValue) + count);
            }
            mIndexValuePositionMap.putAll(mIndexFooterValuePositionMap);
            mIndexValueList.addAll(mIndexFooterValueList);
        }
    }

    /**
     * 当前位置是否为Footer区域
     * @param position
     * @return
     */
    private boolean isIndexFooterPosition(int position) {

        return position >= mList.size() + mIndexHeaderList.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {

        mOnItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {

        mOnItemLongClickListener = onItemLongClickListener;
    }

    /**------------ 待实现方法 ------------------**/

    public abstract RecyclerView.ViewHolder onCreateIndexViewHolder(ViewGroup parent);

    public abstract RecyclerView.ViewHolder onCreateContentViewHolder(ViewGroup parent);

    public abstract void onBindIndexViewHolder(RecyclerView.ViewHolder holder, int position, String indexName);

    public abstract void onBindContentViewHolder(RecyclerView.ViewHolder holder, int position, T itemData);


}
