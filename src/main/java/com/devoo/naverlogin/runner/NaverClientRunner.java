package com.devoo.naverlogin.runner;

import com.devoo.naverlogin.NaverClient;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class NaverClientRunner<I, R> implements Callable {
    public final String NAME;
    private final ClientAction<I, R> function;
    private final BlockingQueue<R> outputQueue;
    private NaverClient naverClient = new NaverClient();
    private BlockingQueue<I> queue;
    private AtomicInteger lock = new AtomicInteger(0);

    public NaverClientRunner(BlockingQueue<I> queue, String NAME, ClientAction<I, R> function,
                             BlockingQueue<R> outputQueue) {
        this.queue = queue;
        this.NAME = NAME;
        this.function = function;
        this.outputQueue = outputQueue;
    }

    public boolean lockIfAvailable() {
        return lock.compareAndSet(0, 1);
    }

    public void unlock() {
        if (lock.compareAndSet(1, 0)) {
            System.out.println(NAME + " is unlocked.");
        } else {
            System.out.println(NAME + " is already unlocked. wtf");
        }
    }

    public void terminate() {
        naverClient.getWebDriver().close();
        System.out.println(NAME + " terminated.");
    }

    @Override
    public R call() throws Exception {
        I item = queue.poll(3, TimeUnit.SECONDS);
        if (item == null) {
            System.out.println(NAME + ": no item to consume");
            return null;
        }
        R result = function.apply(item, naverClient);
        outputQueue.offer(result);
        System.out.println(Thread.currentThread().getName() + " : " + NAME + ": item: " + item.toString());
        unlock();
        return result;
    }
}