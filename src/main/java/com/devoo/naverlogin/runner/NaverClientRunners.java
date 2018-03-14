package com.devoo.naverlogin.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;

public class NaverClientRunners<I, R> {
    private static final Logger log = LoggerFactory.getLogger(NaverClientRunners.class);

    private LinkedList<NaverClientRunner<I, R>> clientRunners = new LinkedList<>();

    public NaverClientRunners(int clientRunnerCount, BlockingQueue<I> inputQueue,
                              ClientAction<I, R> function,
                              BlockingQueue<R> outputQueue) {
        for (int count = 0; count < clientRunnerCount; count++) {
            this.clientRunners.add(new NaverClientRunner(inputQueue,
                    "runner-" + count, function, outputQueue));
        }
        log.debug("NaverClientRunner initialized");
    }

    public NaverClientRunner pollAvailableClient() {
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

    public void terminate() {
        clientRunners.forEach(runner -> runner.terminate());
    }
}