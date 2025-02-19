package zqx.rj.com.seekingcat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import zqx.rj.com.base.activity.BaseActivity;
import zqx.rj.com.base.fragment.BaseFragment;
import zqx.rj.com.constants.Constants;
import zqx.rj.com.seekingcat.search.ui.activity.SearchActivity;
import zqx.rj.com.seekingcat.home.ui.fragment.HomeFragment;
import zqx.rj.com.seekingcat.mine.ui.fragment.MineFragment;
import zqx.rj.com.seekingcat.publish.ui.activity.PublishActivity;
import zqx.rj.com.utils.Log;
import zqx.rj.com.utils.Preferences;

public class MainActivity extends BaseActivity {

    @BindView(R.id.main_bottom_navigation)
    BottomNavigationView mBottomNavigationView;

    @BindView(R.id.vp_main)
    ViewPager mViewPager;

    private List<BaseFragment> fragments;

    // 发布提示 dialog
    private AlertDialog mDialog;

    @Override
    protected void initView() {

        initBottomNavigationBar();
        initFragments();
        initViewPager();
    }

    private void initFragments() {

        fragments = new ArrayList<>();
        fragments.add(HomeFragment.getInstance());
        fragments.add(MineFragment.getInstance());
    }

    private void initViewPager() {

        ViewPagerAdapter mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mViewPagerAdapter);

        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mBottomNavigationView.getMenu().getItem(position).setChecked(true);
            }
        });
    }

    private void initBottomNavigationBar() {
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navigation_home:
                        mViewPager.setCurrentItem(0);
                        return true;
                    case R.id.navigation_message:
                        mViewPager.setCurrentItem(1);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar_search, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @OnClick(R.id.fab_publish)
    void onFloatButton() {
        // 获取头像路径
        String portraitPath = Preferences.getString(Constants.PORTRAIT_PATH, "");
        String nickName = Preferences.getString(Constants.NICK_NAME, "");

        if (portraitPath.isEmpty() || nickName.isEmpty()) {
            if (mDialog == null) {
                mDialog = initPublishDialog();
            }
            mDialog.show();

            Log.d("LST", "nickName = " + nickName);
        } else {
            startActivity(PublishActivity.class);
        }
    }

    private AlertDialog initPublishDialog() {
        return new AlertDialog.Builder(this)
                .setTitle(getString(R.string.reminder))
                .setMessage(getString(R.string.publish_tips))
                .setPositiveButton(getString(R.string.ok), null)
                .create();
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return fragments.get(i);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }
}
