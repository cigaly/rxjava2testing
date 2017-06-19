package rxtest;

import static java.util.concurrent.TimeUnit.SECONDS;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

public class TestPublisher implements Publisher<Long>, MyPublisher<Long> {

    //private final ThreadPoolExecutor executor;
    private final Executor executor;

    private final List<Subscriber<? super Long>> subscribers = new ArrayList<>();

    public TestPublisher() {
        //this.executor = new ThreadPoolExecutor(2, 5, 1, SECONDS, new LinkedBlockingQueue<>());
        this.executor = r -> r.run();
    }

    private void publishToSubscriber(Subscriber<? super Long> subscriber, Long value) {
        synchronized (subscriber) {
            System.out.println("Publish in thread " + Thread.currentThread().getName() + ", value " + value);
            subscriber.onNext(value);
        }
    }

    @Override
    public void subscribe(Subscriber<? super Long> subscriber) {
        System.out.println("Subscribing " + subscriber);
        subscribers.add(subscriber);
    }

    public void publish(Long value) {
        System.out.println("Publishing " + value);
        for (Subscriber<? super Long> subscriber : subscribers) {
            executor.execute(() -> publishToSubscriber(subscriber, value));
        }
    }

    public void close() {
        System.out.println("Closing");
        for (Subscriber<? super Long> subscriber : subscribers) {
            executor.execute(subscriber::onComplete);
        }
        /*executor.shutdown();*/
    }

}
