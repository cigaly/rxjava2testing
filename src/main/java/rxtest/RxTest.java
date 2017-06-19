package rxtest;

import io.reactivex.Observable;

import java.util.Date;
import java.util.Timer;

public class RxTest {

    private static final int WAIT_SECONDS = 7;

    public static void main(String[] args) {
        TestSubscriber subscriber1 = new TestSubscriber("#1");
        TestSubscriber subscriber2 = new TestSubscriber("#2");

        Timer timer = new Timer("Test Timer", true);

        TestPublisher publisher = new TestPublisher();
        Observable<Long> observable = Observable.fromPublisher(publisher);

        /*
        TestObservablOnSubscribe publisher = new TestObservablOnSubscribe();
        Observable<Long> observable = Observable.create(publisher);
        */

        observable.subscribe(subscriber1);
        observable.subscribe(subscriber2);

        timer.schedule(new TestTimerTask(timer, publisher, 5), new Date(System.currentTimeMillis() + 1000), 1000);
        Object w = new Object();
        long waitMillis = 5000;
        while (!subscriber1.isDisposed() || !subscriber2.isDisposed()) {
            synchronized (w) {
                try {
                    w.wait(waitMillis);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Wake up @ " + System.currentTimeMillis());
                waitMillis = 500;
            }
        }
    }

}
