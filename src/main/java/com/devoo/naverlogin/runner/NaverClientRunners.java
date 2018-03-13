package com.devoo.naverlogin.runner;

import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.function.Function;

public class NaverClientRunners<I, R> {
    private final BlockingQueue<R> outputQueue;
    private LinkedList<NaverClientRunner<I, R>> clientRunners = new LinkedList<>();

    public NaverClientRunners(int clientRunnerCount, BlockingQueue<I> inputQueue,
                              Function<I, R> function,
                              BlockingQueue<R> outputQueue) {
        this.outputQueue = outputQueue;
        for (int count = 0; count < clientRunnerCount; count++) {
            this.clientRunners.add(new NaverClientRunner(inputQueue,
                    "runner-" + count, function, outputQueue));
        }
        System.out.println("NaverClientRunner initialized");
    }

    public NaverClientRunner pollAvailableClient() {
        System.out.println("waiting for runner");
        while (true) {
            for (NaverClientRunner runner : clientRunners) {
                if (runner.lockIfAvailable()) {
                    System.out.println(runner.NAME + " is available.");
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