package rxtest;

public interface MyPublisher<T> {

    void publish(T value);

    void close();
}
