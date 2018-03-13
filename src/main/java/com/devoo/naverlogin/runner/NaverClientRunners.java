package com.devoo.naverlogin.runner;

import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;

public class NaverClientRunners<T> {
    private LinkedList<NaverClientRunner> clientRunners = new LinkedList<>();

    public NaverClientRunners(int clientRunnerCount, BlockingQueue<T> queue) {
        for (int count = 0; count < clientRunnerCount; count++) {
            this.clientRunners.add(new NaverClientRunner(queue, "runner-" + count));
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