package com.devoo.naverlogin.runner;

import com.devoo.naverlogin.NaverClient;
import com.devoo.naverlogin.ParallelNaverClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Runs a NaverClient with an injected clientAction.
 *
 * @param <I>
 * @param <R>
 */
public class NaverClientRunner<I, R> implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(ParallelNaverClient.class);

    public final String NAME;
    private ClientAction<I, R> clientAction;
    private final BlockingQueue<R> outputQueue;
    private NaverClient naverClient = new NaverClient();
    private BlockingQueue<I> inputQueue;
    private AtomicInteger lock = new AtomicInteger(0);

    public NaverClientRunner(String name, BlockingQueue<R> outputQueue) {
        this.NAME = name;
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
     *
     * @return
     * @throws Exception
     */
    @Override
    public void run() {
        I inputItem = null;
        try {
            inputItem = inputQueue.poll(10, TimeUnit.SECONDS);
            if (inputItem == null) {
                log.debug("{} : no item to consume", NAME);
                unlock();
                return;
            }
            R result = clientAction.apply(inputItem, naverClient);
            outputQueue.put(result);
            log.debug("{} processed {} outputQueue size: {}", NAME, inputItem.toString(), this.outputQueue.size());
            unlock();
        } catch (InterruptedException e) {
            return;
        }
    }

    public NaverClient getNaverClient() {
        return this.naverClient;
    }

    public void setInputQueue(BlockingQueue<I> inputQueue) {
        this.inputQueue = inputQueue;
    }

    public void setClientAction(ClientAction<I, R> clientAction) {
        this.clientAction = clientAction;
    }
}