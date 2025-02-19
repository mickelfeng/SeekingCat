package zqx.rj.com.seekingcat.mine.ui.activity;

import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.OnClick;
import zqx.rj.com.model.entity.PageRsp;
import zqx.rj.com.seekingcat.R;
import zqx.rj.com.seekingcat.common.goods.model.bean.GoodsRsp;
import zqx.rj.com.seekingcat.common.goods.ui.activity.GoodsEditActivity;
import zqx.rj.com.seekingcat.mine.contract.FollowContract;
import zqx.rj.com.seekingcat.mine.presenter.FollowPresenter;
import zqx.rj.com.utils.Log;

/**
 * author:  HyZhan
 * create:  2018/12/13 11:24
 * desc:    TODO
 */
public class FollowActivity extends GoodsEditActivity<FollowContract.Presenter>
        implements FollowContract.View {

    private int page = 0;

    @Override
    protected void initView() {
        super.initView();

        setEmptyViewTips(getString(R.string.follow_empty_tips));
    }

    @Override
    protected int getMenuId() {
        return R.menu.menu_toolbar_edit;
    }

    @Override
    protected void onItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                if (mGoodsAdapter.getData().isEmpty()) {
                    toast(getString(R.string.follow_empty_tips));
                    return;
                }
                showOrHideButton(item);
                break;
        }
    }

    @Override
    protected void initData() {
        super.initData();
        mPresenter.getFollow(page);
    }

    @Override
    public void onRefresh() {
        page = 0;
        mPresenter.getFollow(page);
    }

    @Override
    public void onLoadMoreRequested() {
        mPresenter.getFollow(++page);
    }

    @Override
    protected String getGoodsTitle() {
        return getString(R.string.my_follow);
    }

    @Override
    public void getFollowSuc(PageRsp<GoodsRsp> pageRsp) {
        addData(pageRsp);
    }

    @Override
    protected FollowContract.Presenter bindPresenter() {
        return new FollowPresenter(this);
    }

    @OnClick(R.id.btn_delete)
    void onClickDelete() {

        //  批量删除 id
        List<Integer> goodsIdList = new ArrayList<>();
        for (GoodsRsp goodsRsp : mGoodsAdapter.getData()) {
            // 获取 选中的 item
            if (goodsRsp.getChoose()) {
                goodsIdList.add(goodsRsp.getId());
            }
        }

        if (goodsIdList.isEmpty()) {
            toast(getString(R.string.delete_tips));
            return;
        }

        Log.d("id = " + goodsIdList);
        mPresenter.deleteMyFollow(goodsIdList);
    }

    @Override
    public void deleteSuc() {
        // 获取原始数据
        List<GoodsRsp> goodsRspList = mGoodsAdapter.getData();

        List<Integer> positionList = new ArrayList<>();
        for (int i = 0; i < goodsRspList.size(); i++) {
            // 判断是否是要删除的item
            if (goodsRspList.get(i).getChoose()) {
                // 收集要删除item 的 position
                // 直接删除  position 会错乱
                positionList.add(i);
            }
        }

        // 先排序
        Collections.sort(positionList);
        // 然后在反序
        Collections.reverse(positionList);
        // 最后在删除 (从后面往前面删除, 就不会数据错乱了)
        for (Integer id : positionList) {
            mGoodsAdapter.remove(id);
        }
    }
}
