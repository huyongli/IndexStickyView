package cn.ittiger.indexlist.demo;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.ittiger.indexlist.IndexStickyView;
import cn.ittiger.indexlist.adapter.IndexHeaderFooterAdapter;
import cn.ittiger.indexlist.adapter.IndexStickyViewAdapter;
import cn.ittiger.indexlist.demo.entity.CityEntity;
import cn.ittiger.indexlist.demo.entity.ContactEntity;
import cn.ittiger.indexlist.listener.OnItemClickListener;
import cn.ittiger.indexlist.listener.OnItemLongClickListener;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author: laohu on 2016/12/25
 * @site: http://ittiger.cn
 */
public class CityActivity extends AppCompatActivity implements OnItemClickListener<CityEntity>,
        OnItemLongClickListener<CityEntity> {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.indexStickyView)
    IndexStickyView mIndexStickyView;
    CityAdapter mAdapter;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_city);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("城市列表");

        mIndexStickyView.addItemDecoration(new IndexStickyViewDecoration(this));
        mAdapter = new CityAdapter(initCitys());
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnItemLongClickListener(this);
        mIndexStickyView.setAdapter(mAdapter);

        IndexHeaderFooterAdapter<CityEntity> hotCityHeaderAdapter = new IndexHeaderFooterAdapter<CityEntity>(
                "热", "热门城市", initHotCitys()
        ) {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {

                View view = LayoutInflater.from(mContext).inflate(android.R.layout.simple_list_item_1, parent, false);
                return new CityViewHolder(view);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, CityEntity itemData) {
                CityViewHolder cityViewHolder = (CityViewHolder) holder;
                cityViewHolder.mTextView.setText(itemData.getCityName());
            }
        };
        hotCityHeaderAdapter.setOnItemClickListener(this);
        hotCityHeaderAdapter.setOnItemLongClickListener(this);
        mIndexStickyView.addIndexHeaderAdapter(hotCityHeaderAdapter);

        IndexHeaderFooterAdapter<CityEntity> locationCityHeaderAdapter = new IndexHeaderFooterAdapter<CityEntity>(
                "定", "当前城市", initLocationCitys()
        ) {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent) {

                View view = LayoutInflater.from(mContext).inflate(android.R.layout.simple_list_item_1, parent, false);
                return new CityViewHolder(view);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, CityEntity itemData) {
                CityViewHolder cityViewHolder = (CityViewHolder) holder;
                cityViewHolder.mTextView.setText(itemData.getCityName());
            }
        };
        locationCityHeaderAdapter.setOnItemClickListener(this);
        locationCityHeaderAdapter.setOnItemLongClickListener(this);
        mIndexStickyView.addIndexHeaderAdapter(locationCityHeaderAdapter);

    }

    List<CityEntity> initCitys() {
        List<CityEntity> list = new ArrayList<>();
        // 初始化数据
        List<String> contactStrings = Arrays.asList(getResources().getStringArray(R.array.city_array));
        for (int i = 0; i < contactStrings.size(); i++) {
            CityEntity cityEntity = new CityEntity(contactStrings.get(i));
            list.add(cityEntity);
        }
        return list;
    }

    List<CityEntity> initHotCitys() {
        List<CityEntity> list = new ArrayList<>();
        list.add(new CityEntity("北京"));
        list.add(new CityEntity("上海"));
        list.add(new CityEntity("深圳"));
        list.add(new CityEntity("武汉"));
        return list;
    }

    List<CityEntity> initLocationCitys() {
        List<CityEntity> list = new ArrayList<>();
        list.add(new CityEntity("武汉"));
        return list;
    }

    private class CityAdapter extends IndexStickyViewAdapter<CityEntity> {

        public CityAdapter(List<CityEntity> originalList) {

            super(originalList);
        }

        @Override
        public RecyclerView.ViewHolder onCreateIndexViewHolder(ViewGroup parent) {

            View view = LayoutInflater.from(mContext).inflate(R.layout.indexsticky_item_index, parent, false);
            return new IndexViewHolder(view);
        }

        @Override
        public RecyclerView.ViewHolder onCreateContentViewHolder(ViewGroup parent) {

            View view = LayoutInflater.from(mContext).inflate(android.R.layout.simple_list_item_1, parent, false);
            return new CityViewHolder(view);
        }

        @Override
        public void onBindIndexViewHolder(RecyclerView.ViewHolder holder, int position, String indexName) {

            IndexViewHolder indexViewHolder = (IndexViewHolder) holder;
            indexViewHolder.mTextView.setText(indexName);
            indexViewHolder.mTextView.setTextColor(Color.RED);
        }

        @Override
        public void onBindContentViewHolder(RecyclerView.ViewHolder holder, int position, CityEntity itemData) {

            CityViewHolder cityViewHolder = (CityViewHolder) holder;
            cityViewHolder.mTextView.setText(itemData.getCityName());
        }
    }

    class CityViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;
        public CityViewHolder(View itemView) {

            super(itemView);
            mTextView = (TextView) itemView;
        }
    }

    class IndexViewHolder extends RecyclerView.ViewHolder {
        TextView mTextView;
        public IndexViewHolder(View itemView) {

            super(itemView);
            mTextView = (TextView) itemView.findViewById(R.id.tv_index);
        }
    }

    @Override
    public void onItemClick(View childView, int position, CityEntity item) {

        Toast.makeText(mContext, "点击：" + item.getCityName() + ",位置：" + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemLongClick(View childView, int position, CityEntity item) {

        Toast.makeText(mContext, "长按：" + item.getCityName() + ",位置：" + position, Toast.LENGTH_SHORT).show();
    }
}
