package rxtest;

import static java.util.concurrent.TimeUnit.SECONDS;

import io.reactivex.Emitter;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

public class TestObservablOnSubscribe implements ObservableOnSubscribe<Long>, MyPublisher<Long> {

    private final ThreadPoolExecutor executor;

    private final List<Emitter<Long>> emitters = new ArrayList<>();

    public TestObservablOnSubscribe() {
        this.executor = new ThreadPoolExecutor(2, 5, 1, SECONDS, new LinkedBlockingQueue<>());
    }

    private void publishToEmitter(Emitter<Long> subscriber, Long value) {
        synchronized (subscriber) {
            System.out.println("Publish in thread " + Thread.currentThread().getName() + ", value " + value);
            subscriber.onNext(value);
        }
    }

    public void publish(Long value) {
        System.out.println("Publishing " + value);
        for (Emitter<Long> emitter : emitters) {
            executor.execute(() -> publishToEmitter(emitter, value));
        }
    }

    public void close() {
        System.out.println("Closing");
        for (Emitter<Long> emitter : emitters) {
            executor.execute(emitter::onComplete);
        }
        executor.shutdown();
    }

    @Override
    public void subscribe(ObservableEmitter<Long> e) throws Exception {
        System.out.println("subscribe(" + e.getClass() + ")");
        this.emitters.add(e);
    }

}
