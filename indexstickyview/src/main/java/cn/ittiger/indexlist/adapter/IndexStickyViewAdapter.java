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

    /**------------ 设置Header自定义索引数据项适配器 -------------------------**/
    public void addIndexHeaderAdapter(IndexHeaderFooterAdapter<T> indexHeaderAdapter) {

        if(indexHeaderAdapter == null) {
            return;
        }
        //因为可能会添加多个Header，所以此处的ItemType按照Header个数进行变化
        indexHeaderAdapter.transfer(ItemType.ITEM_TYPE_INDEX_HEADER + mIndexHeaderAdapters.size());

        mIndexHeaderAdapters.put(indexHeaderAdapter.getItemType(), indexHeaderAdapter);

        int entitySize = indexHeaderAdapter.getEntityList().size();
        //更新索引值与位置的映射
        for(String indexValue : mIndexValuePositionMap.keySet()) {
            mIndexValuePositionMap.put(indexValue, mIndexValuePositionMap.get(indexValue) + entitySize);
        }
        //更新之前添加的Header索引值与位置的映射
        for(String indexValue : mIndexHeaderValuePositionMap.keySet()) {
            mIndexHeaderValuePositionMap.put(indexValue, mIndexHeaderValuePositionMap.get(indexValue) + entitySize);
        }
        //如果当前添加的Header索引值不为空则添加该索引到索引列表中
        if(!TextUtils.isEmpty(indexHeaderAdapter.getIndexValue())) {
            mIndexValueList.add(0, indexHeaderAdapter.getIndexValue());
            mIndexValuePositionMap.put(indexHeaderAdapter.getIndexValue(), 0);
            mIndexHeaderValueList.add(0, indexHeaderAdapter.getIndexValue());
            mIndexHeaderValuePositionMap.put(indexHeaderAdapter.getIndexValue(), 0);
        }
        mIndexHeaderList.addAll(0, indexHeaderAdapter.getEntityList());
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
        indexFooterAdapter.transfer(ItemType.ITEM_TYPE_INDEX_FOOTER + mIndexFooterAdapters.size());
        mIndexFooterAdapters.put(indexFooterAdapter.getItemType(), indexFooterAdapter);

        if(!TextUtils.isEmpty(indexFooterAdapter.getIndexValue())) {
            mIndexValueList.add(indexFooterAdapter.getIndexValue());
            mIndexValuePositionMap.put(indexFooterAdapter.getIndexValue(), getItemCount());
            mIndexFooterValueList.add(indexFooterAdapter.getIndexValue());
            mIndexFooterValuePositionMap.put(indexFooterAdapter.getIndexValue(), getItemCount());
        }
        mIndexFooterList.addAll(indexFooterAdapter.getEntityList());
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


    /**------------- 动态添加数据 -----------------**/

    public final void add(T originalEntity) {

        mOriginalList.add(originalEntity);
        refresh(1);
    }

    public final void add(List<T> originalList) {

        mOriginalList.addAll(originalList);
        refresh(originalList.size());
    }

    public final void reset(List<T> originalList) {

        if(originalList == null) {
            return;
        }
        int size = mOriginalList.size();
        mOriginalList = originalList;
        refresh(-size + originalList.size());
    }

    public final void remove(T originalEntity) {

        mOriginalList.remove(originalEntity);
        refresh(-1);
    }

    public final void remove(List<T> originalList) {

        mOriginalList.removeAll(originalList);
        refresh(-originalList.size());
    }

    /**
     * 删除所有要显示的数据，不包括Header和Footer
     */
    public final void removeAll() {

        int size = mOriginalList.size();
        mOriginalList.clear();
        refresh(-size);
    }

    /**
     * 清空所有数据，包括Header和Footer
     */
    public final void clear() {

        mOriginalList.clear();
        removeAllFooter(false);
        removeAllHeader(false);
        refresh(0);
    }

    /**
     * 删除所有Header数据
     */
    public final void removeAllHeader() {

        removeAllHeader(true);
    }

    /**
     * 移除某个Header
     * @param adapter
     */
    public final void removeHeader(IndexHeaderFooterAdapter adapter) {

        int idx = mIndexHeaderAdapters.indexOfKey(adapter.getItemType());//当前删除的是第几个Header
        if(idx < 0) {
            return;
        }
        if(!TextUtils.isEmpty(adapter.getIndexValue())) {//移除当前要删除的Header的索引位置映射关系
            mIndexHeaderValuePositionMap.remove(adapter.getIndexValue());
            mIndexValuePositionMap.remove(adapter.getIndexValue());

            mIndexHeaderValueList.remove(adapter.getIndexValue());
            mIndexValueList.remove(adapter.getIndexValue());
        }
        int size = adapter.getEntityList().size();
        String indexValue;
        for(int i = idx + 1; i < mIndexHeaderAdapters.size(); i++) {//更新其他Header的索引位置映射关系
            indexValue = mIndexHeaderAdapters.valueAt(i).getIndexValue();
            if(!TextUtils.isEmpty(indexValue)) {
                int position = mIndexHeaderValuePositionMap.get(indexValue);
                mIndexHeaderValuePositionMap.put(indexValue, position - size);
            }
        }
        for(String key : mIndexFooterValuePositionMap.keySet()) {//更新Footer的索引位置映射关系
            int position = mIndexFooterValuePositionMap.get(key);
            mIndexFooterValuePositionMap.put(key, position - size);
        }
        for(String key : mIndexValuePositionMap.keySet()) {//更新所有数据的索引位置映射关系
            int position = mIndexValuePositionMap.get(key) - size;
            if(mIndexHeaderValuePositionMap.containsKey(key)) {
                position = mIndexHeaderValuePositionMap.get(key);
            } else if(mIndexFooterValuePositionMap.containsKey(key)) {
                position = mIndexFooterValuePositionMap.get(key);
            }
            mIndexValuePositionMap.put(key, position);
        }
        mIndexHeaderList.removeAll(adapter.getEntityList());
        mIndexHeaderAdapters.removeAt(idx);
        IndexValueBus.getInstance().notifyDataSetChanged(mIndexValueList);
        notifyDataSetChanged();
    }

    /**
     * 删除所有Header数据
     */
    private void removeAllHeader(boolean isRefresh) {

        if(mIndexHeaderAdapters.size() > 0) {
            for(String indexValue : mIndexHeaderValuePositionMap.keySet()) {
                mIndexValuePositionMap.remove(indexValue);
            }
            mIndexHeaderValuePositionMap.clear();

            mIndexValueList.removeAll(mIndexHeaderValueList);
            mIndexHeaderValueList.clear();

            mIndexHeaderAdapters.clear();
            mIndexHeaderList.clear();

            if(isRefresh) {
                refresh(0);
            }
        }
    }

    /**
     * 删除所有Footer
     */
    public final void removeAllFooter() {

        removeAllFooter(true);
    }

    /**
     * 移除某个Footer
     * @param adapter
     */
    public final void removeFooter(IndexHeaderFooterAdapter adapter) {

        int idx = mIndexFooterAdapters.indexOfKey(adapter.getItemType());//当前删除的是第几个Footer
        if(idx < 0) {
            return;
        }
        if(!TextUtils.isEmpty(adapter.getIndexValue())) {//移除当前要删除的Footer的索引位置映射关系
            mIndexFooterValuePositionMap.remove(adapter.getIndexValue());
            mIndexValuePositionMap.remove(adapter.getIndexValue());

            mIndexFooterValueList.remove(adapter.getIndexValue());
            mIndexValueList.remove(adapter.getIndexValue());
        }
        int size = adapter.getEntityList().size();
        String indexValue;
        for(int i = idx + 1; i < mIndexFooterAdapters.size(); i++) {//更新其他Footer的索引位置映射关系
            indexValue = mIndexFooterAdapters.valueAt(i).getIndexValue();
            if(!TextUtils.isEmpty(indexValue)) {
                int position = mIndexFooterValuePositionMap.get(indexValue);
                mIndexFooterValuePositionMap.put(indexValue, position - size);
                mIndexValuePositionMap.put(indexValue, position - size);
            }
        }
        mIndexFooterList.removeAll(adapter.getEntityList());
        mIndexFooterAdapters.removeAt(idx);
        IndexValueBus.getInstance().notifyDataSetChanged(mIndexValueList);
        notifyDataSetChanged();
    }

    /**
     * 删除所有Footer
     */
    private final void removeAllFooter(boolean isRefresh) {

        if(mIndexHeaderAdapters.size() > 0) {
            for(String indexValue : mIndexFooterValuePositionMap.keySet()) {
                mIndexValuePositionMap.remove(indexValue);
            }
            mIndexFooterValuePositionMap.clear();

            mIndexValueList.removeAll(mIndexFooterValueList);
            mIndexHeaderValueList.clear();

            mIndexFooterAdapters.clear();
            mIndexFooterList.clear();

            if(isRefresh) {
                refresh(0);
            }
        }
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
