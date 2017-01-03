package cn.ittiger.indexlist.helper;

import cn.ittiger.indexlist.ItemType;
import cn.ittiger.indexlist.adapter.IndexHeaderFooterAdapter;
import cn.ittiger.indexlist.entity.BaseEntity;
import cn.ittiger.indexlist.entity.IndexStickyEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 实体类转换器
 * 将用户要显示的数据转换成{@link android.support.v7.widget.RecyclerView}中展示的实体
 * @author: laohu on 2016/12/17
 * @site: http://ittiger.cn
 */
public class ConvertHelper {
    /**
     * 转换过程中，如果待索引字段信息为非字母串，则将其索引值设为：#
     */
    public static final String INDEX_SPECIAL = "#";

    public static class ConvertResult<T> {
        //转换后得到的实际展示数据列表，包括联系人数据+组名称数据(索引名称)
        private List<IndexStickyEntity<T>> mIndexStickyEntities = new ArrayList<>();
        //索引条中展示的数据列表
        private List<String> mIndexValueList = new ArrayList<>();
        //索引条中展示数据与对应组在列表中位置索引的一一映射
        private Map<String, Integer> mIndexValuePositionMap = new HashMap<>();

        public List<IndexStickyEntity<T>> getIndexStickyEntities() {

            return mIndexStickyEntities;
        }

        public List<String> getIndexValueList() {

            return mIndexValueList;
        }

        public Map<String, Integer> getIndexValuePositionMap() {

            return mIndexValuePositionMap;
        }
    }

    public static <T extends BaseEntity> ConvertResult<T> transfer(List<T> list) {

        ConvertResult<T> convertResult = new ConvertResult<T>();

        //使用TreeMap自动按照Key(字母索引值)进行排序
        TreeMap<String, List<IndexStickyEntity<T>>> treeMap = new TreeMap<>(ComparatorFactory.indexValueComparator());
        for(int i = 0; i < list.size(); i++) {
            IndexStickyEntity<T> entity = originalEntityToIndexEntity(list.get(i));

            if(treeMap.containsKey(entity.getIndexValue())) {//Map中已存在此索引值
                treeMap.get(entity.getIndexValue()).add(entity);
            } else {
                List<IndexStickyEntity<T>> indexStickyEntities = new ArrayList<>();
                indexStickyEntities.add(entity);
                treeMap.put(entity.getIndexValue(), indexStickyEntities);
            }
        }
        for(String indexValue : treeMap.keySet()) {
            IndexStickyEntity<T> indexValueEntity = createIndexEntity(indexValue, indexValue);

            //将索引值添加到索引值列表中
            convertResult.getIndexValueList().add(indexValue);
            //按顺序将索引实体添加到列表中
            convertResult.getIndexStickyEntities().add(indexValueEntity);
            //将索引值与索引值在结果列表中的位置进行映射
            convertResult.getIndexValuePositionMap().put(indexValue, convertResult.getIndexStickyEntities().size() - 1);

            //得到当前索引值下的索引数据实体
            List<IndexStickyEntity<T>> indexStickyEntities = treeMap.get(indexValue);
            //对数据实体按自然进行排序
            Collections.sort(indexStickyEntities, ComparatorFactory.<T>indexEntityComparator());
            //将排序后的实体列表按顺序加入到结果列表中
            convertResult.getIndexStickyEntities().addAll(indexStickyEntities);
        }

        return convertResult;
    }

    /**
     * 原始数据转换成展示的索引数据
     * @param originalEntity
     * @param <T>
     * @return
     */
    public static <T extends BaseEntity> IndexStickyEntity<T> originalEntityToIndexEntity(T originalEntity) {

        IndexStickyEntity<T> entity = new IndexStickyEntity<>();
        T item = originalEntity;
        String indexFieldName = item.getIndexField();
        String pinyin = PinYinHelper.getPingYin(indexFieldName);
        String indexValue = pinyin.substring(0, 1).toUpperCase();
        if(!PinYinHelper.isLetter(indexValue)) {//首字符如果非字母以#代替
            indexValue = INDEX_SPECIAL;
        }
        entity.setPinYin(pinyin);
        entity.setOriginalData(item);
        entity.setIndexValue(indexValue);
        entity.setIndexName(indexValue);
        return entity;
    }

    /**
     * 根据索引值创建索引实体对象
     * @param indexValue
     * @param <T>
     * @return
     */
    public static <T extends BaseEntity> IndexStickyEntity<T> createIndexEntity(String indexValue, String indexName) {

        //根据索引值创建索引实体对象
        IndexStickyEntity<T> indexValueEntity = new IndexStickyEntity<>();
        indexValueEntity.setIndexValue(indexValue);
        indexValueEntity.setPinYin(indexValue);
        indexValueEntity.setIndexName(indexName);
        indexValueEntity.setItemType(ItemType.ITEM_TYPE_INDEX);
        return indexValueEntity;
    }

    public static <T extends BaseEntity> List<IndexStickyEntity<T>> transferHeaderFooterData(IndexHeaderFooterAdapter<T> adapter, int itemType) {

        List<IndexStickyEntity<T>> entityList = new ArrayList<>();
        if(adapter.isNormalView()) {
            IndexStickyEntity entity = new IndexStickyEntity();
            entity.setPinYin("");
            entity.setIndexValue("");
            entity.setItemType(itemType);
            entityList.add(entity);
        } else {
            for(int i = 0; i < adapter.getOriginalList().size(); i++) {
                IndexStickyEntity<T> entity = new IndexStickyEntity<>();
                T item = adapter.getOriginalList().get(i);
                String indexFieldName = item.getIndexField();
                String pinyin = PinYinHelper.getPingYin(indexFieldName);
                entity.setPinYin(pinyin);
                entity.setOriginalData(item);
                entity.setIndexValue(adapter.getIndexValue());
                entity.setIndexName(adapter.getIndexName());
                entity.setItemType(itemType);
                entityList.add(entity);
            }
            Collections.sort(entityList, ComparatorFactory.<T>indexEntityComparator());
        }
        return entityList;
    }
}
