package cn.ittiger.indexlist.entity;

import cn.ittiger.indexlist.ItemType;

/**
 * {@link RecyclerView}中展示的数据实体类
 * @author: laohu on 2016/12/17
 * @site: http://ittiger.cn
 */
public class IndexStickyEntity<T> {

    /**
     * 当前数据项的类型，自动转换赋值
     */
    private int mItemType = ItemType.ITEM_TYPE_CONTENT;
    /**
     * 当前数据的索引值，自动转换赋值
     */
    private String mIndexValue;
    /**
     * 索引视图显示的索引名称
     */
    private String mIndexName;
    /**
     * 原始数据，用户实际展示的数据
     * 当次值为null时，则表示此实体代表一个Index数据
     */
    private T mOriginalData;
    /**
     * 当前数据项的拼音
     */
    private String mPinYin;

    public int getItemType() {

        return mItemType;
    }

    public void setItemType(int itemType) {

        mItemType = itemType;
    }

    public String getIndexValue() {

        return mIndexValue;
    }

    public void setIndexValue(String indexValue) {

        mIndexValue = indexValue;
    }

    public T getOriginalData() {

        return mOriginalData;
    }

    public void setOriginalData(T originalData) {

        mOriginalData = originalData;
    }

    public String getPinYin() {

        return mPinYin;
    }

    public void setPinYin(String pinYin) {

        mPinYin = pinYin;
    }

    public String getIndexName() {

        return mIndexName;
    }

    public void setIndexName(String indexName) {

        mIndexName = indexName;
    }
}
