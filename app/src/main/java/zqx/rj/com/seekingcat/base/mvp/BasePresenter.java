package zqx.rj.com.seekingcat.base.mvp;

/**
 * 项目名：  SeekingCat
 * 包名：    zqx.rj.com.seekingcat.common.base
 * 文件名：  BasePresenter
 * 创建者：  ZQX
 * 创建时间：2018/6/27 13:21
 * 描述：    TODO
 */

public interface BasePresenter<T extends IBaseView> {

    void attachView(T view);

    void detachView();
}
