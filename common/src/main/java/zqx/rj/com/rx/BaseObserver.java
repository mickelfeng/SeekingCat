package zqx.rj.com.rx;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import zqx.rj.com.utils.Log;

/**
 * 项目名：  SeekingCat
 * 包名：    zqx.rj.com.common.http.rx
 * 文件名：  BaseObserver
 * 创建者：  ZQX
 * 创建时间：2018/6/29 17:30
 * 描述：    TODO
 */

public class BaseObserver<T> implements Observer<T> {

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(T t) {

    }

    @Override
    public void onError(Throwable e) {
        Log.d("e ->" + e);
    }

    @Override
    public void onComplete() {

    }
}
