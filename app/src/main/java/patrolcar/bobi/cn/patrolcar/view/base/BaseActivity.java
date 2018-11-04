package patrolcar.bobi.cn.patrolcar.view.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import patrolcar.bobi.cn.patrolcar.util.LogUtil;

public abstract class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.setLevel(LogUtil.getVERBOSE());
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

}
