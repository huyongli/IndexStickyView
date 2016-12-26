package cn.ittiger.indexlist.demo;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class IndexStickyViewActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index_sticky_view);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
    }

    @OnClick({R.id.btnContact, R.id.btnCity})
    public void onClick(View view) {
        if(view.getId() == R.id.btnCity) {
            startActivity(new Intent(this, CityActivity.class));
        } else {
            startActivity(new Intent(this, ContactActivity.class));
        }
    }
}
