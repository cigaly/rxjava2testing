package rxtest;

import static java.util.concurrent.TimeUnit.SECONDS;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import java.util.Date;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

public class RxTest {

    private static final int WAIT_SECONDS = 7;

    private static boolean running;

    public static void main(String[] args) {
        Timer timer = new Timer("Test Timer", true);

        TestPublisher publisher = new TestPublisher();
        /*Observable<Long> observable = Observable.fromPublisher(publisher);*/
        Flowable<Long> observable = Flowable.fromPublisher(publisher);
        //ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 5, 1, SECONDS, new LinkedBlockingQueue<>());
        ExecutorService executor = Executors.newSingleThreadExecutor();

        /*
        TestObservablOnSubscribe publisher = new TestObservablOnSubscribe();
        Observable<Long> observable = Observable.create(publisher);
        */

        /*
        TestSubscriber subscriber1 = new TestSubscriber("#1");
        TestSubscriber subscriber2 = new TestSubscriber("#2");
        */

        TestFlowableSubscriber subscriber1 = new TestFlowableSubscriber("#1");
        TestFlowableSubscriber subscriber2 = new TestFlowableSubscriber("#2");
        observable.subscribe(subscriber1);
        observable.subscribe(subscriber2);

        //observable.doOnNext(x -> System.out.println("For each running in thread " + Thread.currentThread().getName() + " on next value " + x));
        running = true;
        //executor.execute(() -> runIt(observable));
//        new Thread(() -> runIt(observable)).run();
        observable
            .doFinally(() -> {
                running = false;
                System.out.println("That's all folks!");
            })
            .doOnComplete(() -> System.out.println("Completed - That's all folks!"))
            .forEach(x -> executor.execute(() -> System.out.println("For each running in thread " + Thread.currentThread().getName() + " on next value " + x)));
//        .blockingSubscribe(x -> System.out.println("For each running in thread " + Thread.currentThread().getName() + " on next value " + x));

        timer.schedule(new TestTimerTask(timer, publisher, 5), new Date(System.currentTimeMillis() + 1000), 1000);
        Object w = new Object();
        long waitMillis = 2000;
        //while (!subscriber1.isDisposed() || !subscriber2.isDisposed()) {
        while (running) {
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
        executor.shutdown();
    }

//    private static void runIt(Flowable<Long> observable) {
//        System.out.println("Run!");
//        observable
//            .doFinally(() -> running = false)
//            .doOnComplete(() -> System.out.println("Completed - That's all folks!"))
//            .forEach(x -> System.out.println("For each running in thread " + Thread.currentThread().getName() + " on next value " + x));
//            //.blockingSubscribe(x -> System.out.println("For each running in thread " + Thread.currentThread().getName() + " on next value " + x));
//    }

}
