package zqx.rj.com.seekingcat.home.ui.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.hugeterry.coordinatortablayout.CoordinatorTabLayout;
import zqx.rj.com.base.fragment.BaseFragment;
import zqx.rj.com.seekingcat.R;
import zqx.rj.com.seekingcat.home.model.adapter.HomeAdapter;
import zqx.rj.com.seekingcat.home.presenter.HomePresenter;

/**
 * 项目名：  SeekingCat
 * 包名：    zqx.rj.com.seekingcat.home.view
 * 文件名：  HomeFragment
 * 创建者：  ZQX
 * 创建时间：2018/7/10 14:15
 * 描述：    TODO
 */

public class HomeFragment extends BaseFragment {

    @BindView(R.id.coordinator)
    CoordinatorTabLayout mCoordinatorTabLayout;

    @BindView(R.id.vp_content)
    ViewPager mVpContent;

    public static HomeFragment getInstance() {
        return new HomeFragment();
    }

    @Override
    public void initView(View view) {

        int[] mImageArray = new int[]{
                R.drawable.bg1,
                R.drawable.bg2,
                R.drawable.bg3
        };

        mCoordinatorTabLayout.setTitle(getString(R.string.app_name))
                .setImageArray(mImageArray)
                .setupWithViewPager(mVpContent);

        // 设置 viewPage 预加载 2个页面, 默认从0开始
        mVpContent.setOffscreenPageLimit(2);
    }

    @Override
    public void initData() {
        super.initData();

        List<Fragment> fragments = initFragments();
        List<String> titles = initTabTitle();

        HomeAdapter adapter = new HomeAdapter(getChildFragmentManager(), titles, fragments);
        mVpContent.setAdapter(adapter);
    }

    private List<String> initTabTitle() {
        List<String> titles = new ArrayList<>();
        titles.add(getString(R.string.news));
        titles.add(getString(R.string.search_for_notices));
        titles.add(getString(R.string.lost_and_found));

        return titles;
    }

    private List<Fragment> initFragments() {

        List<Fragment> fragments = new ArrayList<>();

        fragments.add(HomeGoodsFragment.getInstance(HomePresenter.ALL));
        fragments.add(HomeGoodsFragment.getInstance(HomePresenter.SEEK));
        fragments.add(HomeGoodsFragment.getInstance(HomePresenter.LOSE));

        return fragments;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_home;
    }
}
