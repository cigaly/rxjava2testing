package rxtest;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class TestTimerTask extends TimerTask {

    private final Timer timer;

    private final MyPublisher publisher;

    private int counter;

    public TestTimerTask(Timer timer, MyPublisher publisher, int counter) {
        this.timer = timer;
        this.publisher = publisher;
        this.counter = counter;
    }

    @Override
    public void run() {
        publisher.publish(System.currentTimeMillis());
        if (--counter < 0) {
            timer.cancel();
            publisher.close();
        }
    }
}
