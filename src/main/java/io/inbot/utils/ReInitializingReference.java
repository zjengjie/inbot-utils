package io.inbot.utils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public class ReInitializingReference<V> {
    private volatile V instance;
    private volatile long lastInitialized=0;
    private final Supplier<V> initializer;
    private final ReentrantLock lock = new ReentrantLock();
    private final long expirationInMillis;

    public ReInitializingReference(Supplier<V> supplier, long expiration, TimeUnit unit) {
        this.initializer = supplier;
        this.expirationInMillis = unit.toMillis(expiration);
    }

    public V get() {
        if(needsReinitialization()) {
            lock.lock();
            try {
                if(needsReinitialization()) {
                    instance = initializer.get();
                    lastInitialized=System.currentTimeMillis();
                }
            } finally {
                lock.unlock();
            }
        }
        return instance;
    }

    /**
     * Forces reinitialization on next get()
     */
    public void reset() {
        lock.lock();
        try {
            lastInitialized=0;
        } finally {
            lock.unlock();
        }
    }

    private boolean needsReinitialization() {
        return instance==null || System.currentTimeMillis() -lastInitialized> expirationInMillis;
    }
}
