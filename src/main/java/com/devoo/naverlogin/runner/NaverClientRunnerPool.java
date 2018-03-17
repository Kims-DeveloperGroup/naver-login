package com.devoo.naverlogin.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;

/**
 * Initializes the multiple number of NaverClientRunner and offers a NaverClientRunner pool.
 *
 * @param <I> type of input items
 * @param <R> type of output items
 */
public class NaverClientRunnerPool<I, R> {
    private static final Logger log = LoggerFactory.getLogger(NaverClientRunnerPool.class);

    private LinkedList<NaverClientRunner<I, R>> clientRunners = new LinkedList<>();

    /**
     * @param clientRunnerCount the number of NaverClientRunner in the pool
     * @param outputQueue       a output items queue to be stored after processing.
     */
    public NaverClientRunnerPool(int clientRunnerCount,
                                 BlockingQueue<R> outputQueue) {
        for (int count = 0; count < clientRunnerCount; count++) {
            this.clientRunners.add(new NaverClientRunner("runner-" + count, outputQueue));
        }
        log.debug("NaverClientRunnerPool initialized");
    }

    /**s
     * Offers an available NaverClientRunner from the pool.
     * @return an available NaverClientRunner
     * @throws InterruptedException
     */
    public NaverClientRunner pollAvailableClientRunner() throws InterruptedException {
        log.debug("waiting for runner");
        while (true) {
            for (NaverClientRunner runner : clientRunners) {
                if (runner.lockIfAvailable()) {
                    log.debug("{} is available.", runner.NAME);
                    return runner;
                }
            }
            continue;
        }
    }

    /**
     * Terminates all of NaverClientRunner in the pool
     */
    public void terminate() {
        clientRunners.forEach(runner -> runner.terminate());
    }

    public LinkedList<NaverClientRunner<I, R>> getClientRunners() {
        return this.clientRunners;
    }

    public void setInputQueue(BlockingQueue<I> inputQueue) {
        this.clientRunners.forEach(runner -> runner.setInputQueue(inputQueue));
    }

    public void setClientAction(ClientAction<I, R> clientAction) {
        this.clientRunners.forEach(runner -> runner.setClientAction(clientAction));
    }
}