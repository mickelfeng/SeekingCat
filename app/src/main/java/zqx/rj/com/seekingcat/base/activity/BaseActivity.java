package zqx.rj.com.seekingcat.base.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.yatoooon.screenadaptation.ScreenAdapterTools;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import zqx.rj.com.seekingcat.base.mvp.BasePresenter;
import zqx.rj.com.seekingcat.base.mvp.IBaseView;
import zqx.rj.com.seekingcat.common.utils.ScreenTools;
import zqx.rj.com.seekingcat.common.utils.ToastUtil;

/**
 * 项目名：  SeekingCat
 * 包名：    zqx.rj.com.seekingcat.base.activity
 * 文件名：  BaseActivity
 * 创建者：  ZQX
 * 创建时间：2018/6/27 14:42
 * 描述：    基类 Activity
 */

public abstract class BaseActivity<T extends BasePresenter> extends AppCompatActivity
    implements IBaseView{

    protected T mPresenter;
    protected Activity mContext;
    private Unbinder mUnbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(setLayout());

        //如果希望android7.0分屏也适配的话,加上这句
        //ScreenTools.adapterAndroid7(this);

        //在setContentView();后面加上适配语句
        //ScreenTools.activity(this);

        mUnbinder = ButterKnife.bind(this);
        mContext = this;

        initPresenter();
        if (mPresenter != null){
            mPresenter.attachView(this);
        }

        ActivityManager.addActivity(this);

        ToastUtil.init(this);

        // 初始化控件
        initView();
        // 初始化数据
        initData();

    }

    protected abstract void initData();

    protected abstract void initView();

    protected abstract void initPresenter();

    protected abstract int setLayout();

    public void toast(String text){
        ToastUtil.show(text);
    }

    public void toast(int resId){
        ToastUtil.show(String.valueOf(resId));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null){
            mPresenter.detachView();
        }

        mUnbinder.unbind();

        ActivityManager.removeActivity(this);
    }


}
