package rxtest;

import io.reactivex.observers.DefaultObserver;
import io.reactivex.observers.DisposableObserver;

public class TestSubscriber extends DisposableObserver<Long> {

    private final String name;

    public TestSubscriber(String name) {
        this.name = name;
    }

    @Override
    public void onNext(Long aLong) {
        System.out.println("Subscriber[" + name + "] running in thread " + Thread.currentThread().getName() + " on next value " + aLong);
    }

    @Override
    public void onComplete() {
        System.out.println("Subscriber[" + name + "] closing ...");
        dispose();
    }

    @Override
    public void onError(Throwable throwable) {
        System.out.println("Subscriber[" + name + "] received error " + throwable.toString());
    }

}
