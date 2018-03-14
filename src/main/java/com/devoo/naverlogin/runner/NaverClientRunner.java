package com.devoo.naverlogin.runner;

import com.devoo.naverlogin.NaverClient;
import com.devoo.naverlogin.ParallelNaverClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Runs a NaverClient with an injected function.
 *
 * @param <I>
 * @param <R>
 */
public class NaverClientRunner<I, R> implements Callable {
    private static final Logger log = LoggerFactory.getLogger(ParallelNaverClient.class);

    public final String NAME;
    private final ClientAction<I, R> function;
    private final BlockingQueue<R> outputQueue;
    private NaverClient naverClient = new NaverClient();
    private BlockingQueue<I> queue;
    private AtomicInteger lock = new AtomicInteger(0);

    public NaverClientRunner(BlockingQueue<I> queue, String name, ClientAction<I, R> function,
                             BlockingQueue<R> outputQueue) {
        this.queue = queue;
        this.NAME = name;
        this.function = function;
        this.outputQueue = outputQueue;
    }

    public boolean lockIfAvailable() throws InterruptedException {
        return lock.compareAndSet(0, 1);
    }

    public void unlock() {
        log.debug("{} is unlocked.", NAME);
        lock.compareAndSet(1, 0);
    }

    public void terminate() {
        naverClient.getWebDriver().close();
        log.debug("{} terminated.", NAME);
    }

    /**
     * Polls an item from the input queue and processes with the given ClientAction.
     * and offers an result item into the output queue.
     * Note: in case that no item is offered from the input queue in 3 seconds, null is returned.
     * @return
     * @throws Exception
     */
    @Override
    public R call() throws Exception {
        I item = queue.poll(3, TimeUnit.SECONDS);
        if (item == null) {
            log.debug("{} : no item to consume", NAME);
            unlock();
            return null;
        }
        R result = function.apply(item, naverClient);
        outputQueue.offer(result);
        log.debug("{} :  {}: {}", Thread.currentThread().getName(), NAME, item.toString());
        unlock();
        return result;
    }
}