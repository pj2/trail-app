package uk.co.prenderj.trail.util;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Asynchronously handles the completion of a future.
 * @author Joshua Prendergast
 * @param <V> the future's return type
 */
public abstract class Callback<V> implements Runnable {
    private Thread thread;
    private Future<V> future;
    private long timeout;
    private TimeUnit unit;

    public Callback(Future<V> future, long timeout, TimeUnit unit) {
        this.future = future;
        this.timeout = timeout;
        this.unit = unit;
    }

    public void start() {
        thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
    }

    public void run() {
        try {
            onGet(future.get(timeout, unit));
        } catch (Exception e) {
            onFailure(e);
        }
    }

    public abstract void onGet(V value) throws Exception;

    public void onFailure(Exception e) {
        // Do nothing
    }
}
