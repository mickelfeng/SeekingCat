package zqx.rj.com.seekingcat.search.ui.activity;

import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;

import org.litepal.LitePal;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import zqx.rj.com.model.entity.PageRsp;
import zqx.rj.com.seekingcat.R;
import zqx.rj.com.seekingcat.common.goods.model.bean.GoodsRsp;
import zqx.rj.com.seekingcat.common.goods.ui.activity.GoodsActivity;
import zqx.rj.com.seekingcat.search.contract.SearchContract;
import zqx.rj.com.seekingcat.search.model.adapter.HistoryAdapter;
import zqx.rj.com.seekingcat.search.model.db.Record;
import zqx.rj.com.seekingcat.search.presenter.SearchPresenter;
import zqx.rj.com.utils.Log;

public class SearchActivity extends GoodsActivity<SearchContract.Presenter> implements
        SearchContract.View {

    @BindView(R.id.ll_history)
    LinearLayout mLlHistory;

    @BindView(R.id.tv_clear_history)
    TextView mTvClear;

    @BindView(R.id.rv_history)
    RecyclerView mRvHistory;

    private int page = 0;
    private boolean isFirst = true;
    private SearchView mSearchView;
    private HistoryAdapter mHistoryAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    protected void initView() {
        super.initView();

        setEmptyViewTips(getString(R.string.search_result_empty_tips));

        // 初始化 搜索记录
        initHistory();
    }

    @Override
    protected void initData() {
        super.initData();

        // 设置 历史记录
        List<Record> records = LitePal.findAll(Record.class);

        // 倒序排列
        Collections.reverse(records);

        if (!records.isEmpty()) {
            hideHistory(false);
            mHistoryAdapter.setNewData(records);
        }
    }

    @Override
    protected String getGoodsTitle() {
        return getString(R.string.search);
    }

    private void initHistory() {
        mRvHistory.setLayoutManager(new LinearLayoutManager(this));
        mRvHistory.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        mHistoryAdapter = new HistoryAdapter(R.layout.history_item, null);
        mRvHistory.setAdapter(mHistoryAdapter);

        // 点击历史记录
        mHistoryAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                Record record = (Record) adapter.getItem(position);
                if (record != null) {
                    // 设置到 SearchView 输入框, 并进行搜索
                    mSearchView.setQuery(record.getRecord(), true);

                    // 由于搜索后会清除 关键词  所以在进行添加 关键词
                    mSearchView.setQuery(record.getRecord(), false);
                }
            }
        });


        mHistoryAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.iv_close:
                        // 删除 单条历史记录
                        Record record = (Record) adapter.getItem(position);
                        if (record != null) {
                            LitePal.delete(Record.class, record.getId());
                            mHistoryAdapter.remove(position);
                        }
                        break;
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.toolbar_search, menu);

        mSearchView = (SearchView) menu.findItem(R.id.ab_search).getActionView();
        initSearchView();
        return true;
    }

    private void initSearchView() {
        // 设置 默认打开搜索框
        mSearchView.setIconified(false);
        mSearchView.setQueryHint(getString(R.string.search_tips));

        // 搜索框 输入监听
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if (!query.isEmpty()) {
                    isFirst = true;

                    searchGoods(query);
                    // 由于搜索后会清除 关键词  所以在进行添加 关键词
                    mSearchView.setQuery(query, false);
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // 如果输入为空的话
                if (newText.isEmpty()) {
                    // 显示历史记录
                    hideHistory(false);
                    // 隐藏搜索结果
                    mRvGoods.setVisibility(View.GONE);
                }
                return false;
            }
        });
    }

    private void searchGoods(String query) {

        showLoading();
        mPresenter.searchGoods(0, query);

        // 添加历史搜索记录
        addHistoryRecord(query);
    }

    // 添加历史搜索记录
    private void addHistoryRecord(final String query) {

        /**
         *  1. 查询数据库是否存在
         *  2. 如果不存在则添加
         *  3. 否则就“移动” 到 top
         */

        // 异步查询数据库
        List<Record> recordList = LitePal.where("record = ?", query).find(Record.class);

        // 创建一条新记录
        Record record = new Record();
        record.setRecord(query);

        if (recordList.isEmpty()) {
            // 添加到 recyclerView
            mHistoryAdapter.addData(record);
        } else {

            List<Record> records = mHistoryAdapter.getData();
            int i;
            for (i = 0; i < records.size(); i++) {
                if (records.get(i).getRecord().equals(query)) {
                    break;
                }
            }

            mHistoryAdapter.remove(i);

            // 否则就 移动 到 top
            mHistoryAdapter.addData(0, recordList.get(0));

            // 删除之前数据, 并重新添加到数据库
            LitePal.delete(Record.class, recordList.get(0).getId());
        }

        // 保存到数据库
        record.save();
    }

    @OnClick(R.id.tv_clear_history)
    void onClearHistory() {
        toast("清除");
        // 清除数据库  record 表
        LitePal.deleteAll(Record.class);

        // 删除 RecyclerView 数据
        mHistoryAdapter.getData().clear();
        mHistoryAdapter.notifyDataSetChanged();
    }

    @Override
    protected SearchContract.Presenter bindPresenter() {
        return new SearchPresenter(this);
    }

    /**
     * 搜索成功
     */
    @Override
    public void searchSuccess(PageRsp<GoodsRsp> pageRsp) {
        hideLoading();

        if (isFirst) {
            // 如果第一次 添加数据 就设置新数据
            // 就是 keyword 变化了
            mGoodsAdapter.setNewData(pageRsp.getDatas());
            mGoodsAdapter.loadMoreComplete();

            isFirst = false;
        } else {
            // 否则 就直接添加数据 (keyword 不变)
            addData(pageRsp);
        }

        mRvGoods.setVisibility(View.VISIBLE);

        // 隐藏历史记录
        hideHistory(true);
    }

    // 是否隐藏历史记录
    private void hideHistory(boolean isHide) {
        if (isHide) {
            mLlHistory.setVisibility(View.GONE);
            mRvHistory.setVisibility(View.GONE);
            mTvClear.setVisibility(View.GONE);
        } else {
            mLlHistory.setVisibility(View.VISIBLE);
            mRvHistory.setVisibility(View.VISIBLE);
            mTvClear.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRefresh() {
        String query = mSearchView.getQuery().toString();
        if (!query.isEmpty()) {
            Log.d("LST", "onRefresh = " + query);
            mPresenter.searchGoods(0, query);
        }
    }

    @Override
    public void onLoadMoreRequested() {
        String query = mSearchView.getQuery().toString();
        if (!query.isEmpty()) {
            Log.d("LST", "onLoadMoreRequested = " + query);
            mPresenter.searchGoods(++page, query);
        }
    }
}
