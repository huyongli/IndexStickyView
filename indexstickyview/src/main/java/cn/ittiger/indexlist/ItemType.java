package cn.ittiger.indexlist;

/**
 * @author: laohu on 2016/12/17
 * @site: http://ittiger.cn
 */

public class ItemType {
    /**
     * 列表中普通数据项类型，例如联系人列表中的：联系人信息项
     */
    public static final int ITEM_TYPE_CONTENT = 1000000;
    /**
     * 列表中索引项类型，例如联系人列表中的：A,B,C...等索引数据
     */
    public static final int ITEM_TYPE_INDEX = 2000000;

    /**
     * 列表中增加头部索引数据(如自定义的常用联系人)
     */
    public static final int ITEM_TYPE_INDEX_HEADER = 3000000;
    /**
     * 列表中增加底部索引数据
     */
    public static final int ITEM_TYPE_INDEX_FOOTER = 4000000;
}
