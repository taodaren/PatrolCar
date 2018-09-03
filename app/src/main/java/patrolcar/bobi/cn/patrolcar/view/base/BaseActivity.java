package patrolcar.bobi.cn.patrolcar.view.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.ButterKnife;
import patrolcar.bobi.cn.patrolcar.R;

public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 子类不再需要设置布局 ID，也不再需要使用 ButterKnife.BindView()
        setContentView(getActivityLayout());
        ButterKnife.bind(this);
        initView();
        initData();
        initListener();
    }

    /** 由子类实现 @return 当前界面的布局文件 id */
    protected abstract int getActivityLayout();

    public void initView() {
    }

    public void initData() {
    }

    public void initListener() {
    }

    /**
     * 设置 Toolbar
     *
     * @param title           标题
     * @param titleVisibility 标题控件是否显示
     * @param menu            右侧菜单文字
     * @param menuVisibility  右侧控件是否显示
     */
    public void setToolbar(String title, int titleVisibility, String menu, int menuVisibility) {
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // 隐藏 Toolbar 左侧导航按钮
            actionBar.setDisplayHomeAsUpEnabled(false);
            // 隐藏 Toolbar 自带标题栏
            actionBar.setDisplayShowTitleEnabled(false);
        }

        // 设置标题
        TextView textTitle = findViewById(R.id.tv_title_toolbar);
        textTitle.setVisibility(titleVisibility);
        textTitle.setText(title);
        // 设置返回按钮
        ImageView imgTitleBack = findViewById(R.id.img_back_toolbar);
        imgTitleBack.setVisibility(View.VISIBLE);
        imgTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // 设置右侧菜单按钮
        TextView textMenu = findViewById(R.id.tv_menu_toolbar);
        textMenu.setVisibility(menuVisibility);
        textMenu.setText(menu);
    }

}
