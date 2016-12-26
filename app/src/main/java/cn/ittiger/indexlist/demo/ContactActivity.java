package cn.ittiger.indexlist.demo;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.ittiger.indexlist.IndexStickyView;
import cn.ittiger.indexlist.adapter.IndexHeaderFooterAdapter;
import cn.ittiger.indexlist.adapter.IndexStickyViewAdapter;
import cn.ittiger.indexlist.demo.entity.ContactEntity;
import cn.ittiger.indexlist.demo.entity.MenuEntity;
import cn.ittiger.indexlist.demo.entity.UserEntity;
import cn.ittiger.indexlist.entity.BaseEntity;
import cn.ittiger.indexlist.listener.OnItemClickListener;
import cn.ittiger.indexlist.listener.OnItemLongClickListener;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author: laohu on 2016/12/25
 * @site: http://ittiger.cn
 */
public class ContactActivity extends AppCompatActivity implements
        Toolbar.OnMenuItemClickListener, OnItemClickListener<ContactEntity>, OnItemLongClickListener<ContactEntity> {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.indexStickyView)
    IndexStickyView mIndexStickyView;
    MyIndexStickyViewAdapter mAdapter;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_contact);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("联系人列表");
        mToolbar.setOnMenuItemClickListener(this);


        mAdapter = new MyIndexStickyViewAdapter(initDatas());
        mIndexStickyView.setAdapter(mAdapter);
        mIndexStickyView.addItemDecoration(new IndexStickyViewDecoration(this));

        //自定义添加头部收藏信息
        IndexHeaderFooterAdapter<UserEntity> favAdapter = new IndexHeaderFooterAdapter<UserEntity>("☆", "我的收藏", initFavDatas()) {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {

                View view = LayoutInflater.from(mContext).inflate(R.layout.indexsticky_item_contact, parent, false);
                return new ContentViewHolder(view);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, UserEntity itemData) {

                ContentViewHolder contentViewHolder = (ContentViewHolder) holder;
                contentViewHolder.mMobile.setText(itemData.getMobile());
                contentViewHolder.mName.setText(itemData.getName());
            }
        };
        favAdapter.setOnItemClickListener(new OnItemClickListener<UserEntity>() {
            @Override
            public void onItemClick(View childView, int position, UserEntity item) {
                Toast.makeText(mContext, "点击：" + item.getName() + ",位置：" + position, Toast.LENGTH_SHORT).show();
            }
        });
        mIndexStickyView.addIndexHeaderAdapter(favAdapter);


        //添加自定义头部菜单项
        IndexHeaderFooterAdapter<MenuEntity> menuAdapter = new IndexHeaderFooterAdapter<MenuEntity>("↑", null, initMenuDatas()) {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {

                View view = LayoutInflater.from(mContext).inflate(R.layout.indexsticky_item_contact, parent, false);
                return new ContentViewHolder(view);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, MenuEntity itemData) {

                ContentViewHolder contentViewHolder = (ContentViewHolder) holder;
                contentViewHolder.mMobile.setVisibility(View.GONE);
                contentViewHolder.mName.setText(itemData.getMenuTitle());
                contentViewHolder.mAvatar.setImageResource(itemData.getMenuIconRes());
            }
        };
        menuAdapter.setOnItemLongClickListener(new OnItemLongClickListener<MenuEntity>() {
            @Override
            public  void onItemLongClick(View childView, int position, MenuEntity item) {
                Toast.makeText(mContext, "长按：" + item.getMenuTitle() + ",位置：" + position, Toast.LENGTH_SHORT).show();
            }
        });
        mIndexStickyView.addIndexHeaderAdapter(menuAdapter);

        //添加一个长度为1的数据来作为普通视图的数据源
        UserEntity[] entitys = {new UserEntity("数据绑定普通HeaderView", "13312345654")};
        IndexHeaderFooterAdapter<UserEntity> normalAdapter = new IndexHeaderFooterAdapter<UserEntity>(null, null, Arrays.asList(entitys)) {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {

                View view = LayoutInflater.from(mContext).inflate(android.R.layout.simple_list_item_1, parent, false);
                return new MyViewHolder(view);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, UserEntity itemData) {

                ((MyViewHolder)holder).mTextView.setText(itemData.getName());
            }

            class MyViewHolder extends RecyclerView.ViewHolder {
                TextView mTextView;

                public MyViewHolder(View itemView) {
                    super(itemView);
                    mTextView = (TextView) itemView;
                }
            }
        };
        normalAdapter.setOnItemClickListener(new OnItemClickListener<UserEntity>() {
            @Override
            public void onItemClick(View childView, int position, UserEntity item) {
                Toast.makeText(mContext, "普通Header视图点击：" + item.getName() + ",位置：" + position, Toast.LENGTH_SHORT).show();
            }
        });
        mIndexStickyView.addIndexHeaderAdapter(normalAdapter);

        //自定义添加一个图片作为头部普通视图
        mIndexStickyView.addIndexHeaderAdapter(new IndexHeaderFooterAdapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {

                View view = LayoutInflater.from(mContext).inflate(R.layout.indexsticky_header_contact_banner, parent, false);
                ImageViewVH vh = new ImageViewVH(view);
                vh.img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(mContext, "头部普通图片视图点击", Toast.LENGTH_SHORT).show();
                    }
                });
                return vh;
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, BaseEntity itemData) {

            }

            class ImageViewVH extends RecyclerView.ViewHolder {
                ImageView img;

                public ImageViewVH(View itemView) {
                    super(itemView);
                    img = (ImageView) itemView.findViewById(R.id.img);
                }
            }
        });


        //添加一个底部自定义列表
        IndexHeaderFooterAdapter<UserEntity> footerAdapter = new IndexHeaderFooterAdapter<UserEntity>("尾", "Footer", initFavDatas()) {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {

                View view = LayoutInflater.from(mContext).inflate(R.layout.indexsticky_item_contact, parent, false);
                return new ContentViewHolder(view);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, UserEntity itemData) {
                ContentViewHolder contentViewHolder = (ContentViewHolder) holder;
                contentViewHolder.mMobile.setText(itemData.getMobile());
                contentViewHolder.mName.setText(itemData.getName());
            }
        };
        footerAdapter.setOnItemClickListener(new OnItemClickListener<UserEntity>() {
            @Override
            public void onItemClick(View childView, int position, UserEntity item) {
                Toast.makeText(mContext, "点击Footer：" + item.getName() + ",位置：" + position, Toast.LENGTH_SHORT).show();
            }
        });
        mIndexStickyView.addIndexFooterAdapter(footerAdapter);


        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnItemLongClickListener(this);
    }

    private List<ContactEntity> initDatas() {

        List<ContactEntity> list = new ArrayList<>();
        // 初始化数据
        List<String> contactStrings = Arrays.asList(getResources().getStringArray(R.array.contact_array));
        List<String> mobileStrings = Arrays.asList(getResources().getStringArray(R.array.mobile_array));
        for (int i = 0; i < contactStrings.size(); i++) {
            ContactEntity contactEntity = new ContactEntity(contactStrings.get(i), mobileStrings.get(i));
            list.add(contactEntity);
        }
        return list;
    }

    private List<UserEntity> initFavDatas() {
        List<UserEntity> list = new ArrayList<>();
        list.add(new UserEntity("张三", "13298449923"));
        list.add(new UserEntity("李四", "13298449923"));
        return list;
    }

    private List<MenuEntity> initMenuDatas() {
        List<MenuEntity> list = new ArrayList<>();
        list.add(new MenuEntity("圣诞组", R.drawable.christmas));
        list.add(new MenuEntity("雪人组", R.drawable.snowman));
        list.add(new MenuEntity("天使组", R.drawable.angel));
        list.add(new MenuEntity("铃铛组", R.drawable.bell));
        return list;
    }

    class MyIndexStickyViewAdapter extends IndexStickyViewAdapter<ContactEntity> {

        public MyIndexStickyViewAdapter(List<ContactEntity> list) {

            super(list);
        }

        @Override
        public RecyclerView.ViewHolder onCreateIndexViewHolder(ViewGroup parent) {

            View view = LayoutInflater.from(mContext).inflate(R.layout.indexsticky_item_index, parent, false);
            return new IndexViewHolder(view);
        }

        @Override
        public RecyclerView.ViewHolder onCreateContentViewHolder(ViewGroup parent) {

            View view = LayoutInflater.from(mContext).inflate(R.layout.indexsticky_item_contact, parent, false);
            return new ContentViewHolder(view);
        }

        @Override
        public void onBindIndexViewHolder(RecyclerView.ViewHolder holder, int position, String indexName) {

            IndexViewHolder indexViewHolder = (IndexViewHolder) holder;
            indexViewHolder.mTextView.setText(indexName);
        }

        @Override
        public void onBindContentViewHolder(RecyclerView.ViewHolder holder, int position, ContactEntity itemData) {

            ContentViewHolder contentViewHolder = (ContentViewHolder) holder;
            contentViewHolder.mMobile.setText(itemData.getMobile());
            contentViewHolder.mName.setText(itemData.getName());
        }
    }

    class IndexViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;

        public IndexViewHolder(View itemView) {

            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.tv_index);
        }
    }

    class ContentViewHolder extends RecyclerView.ViewHolder {
        TextView mName;
        TextView mMobile;
        ImageView mAvatar;

        public ContentViewHolder(View itemView) {

            super(itemView);
            mName = (TextView) itemView.findViewById(R.id.tv_name);
            mMobile = (TextView) itemView.findViewById(R.id.tv_mobile);
            mAvatar = (ImageView) itemView.findViewById(R.id.img_avatar);
        }
    }

    @Override
    public void onItemClick(View childView, int position, ContactEntity item) {

        Toast.makeText(mContext, "点击" + item.getName() + ",位置：" + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemLongClick(View childView, int position, ContactEntity item) {

        Toast.makeText(mContext, "长按：" + item.getName() + ",位置：" + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.toolbar_add:
                mAdapter.add(new ContactEntity("阿圆", "15525672987"));
                Toast.makeText(mContext, "添加成功", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return true;
    }
}
