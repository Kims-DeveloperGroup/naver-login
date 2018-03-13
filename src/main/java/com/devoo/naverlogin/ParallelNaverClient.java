package com.devoo.naverlogin;

import com.devoo.naverlogin.runner.NaverClientRunner;
import com.devoo.naverlogin.runner.NaverClientRunners;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static java.util.concurrent.Executors.newFixedThreadPool;

public class ParallelNaverClient<T, R> implements Runnable {
    private final ExecutorService executorService;
    private final NaverClientRunners<T, R> runners;
    private BlockingQueue<T> inputQueue;

    private boolean stop = false;

    public ParallelNaverClient(int parallel, BlockingQueue<T> inputQueue, Function<T, R> function,
                               BlockingQueue<R> outputQueue) {
        executorService = newFixedThreadPool(parallel);
        this.inputQueue = inputQueue;
        this.runners = new NaverClientRunners(parallel, this.inputQueue, function, outputQueue);
    }

    public void start() throws Exception {
        this.run();
    }

    public void startAsyn() {
        new Thread(this).start();
    }

    public void stop() {
        System.out.println("sent stop request.");
        this.stop = true;
    }

    @Override
    public void run() {
        long start = System.currentTimeMillis();
        while (true) {
            NaverClientRunner naverClientRunner = runners.pollAvailableClient();
            executorService.submit(naverClientRunner);
            if (this.inputQueue.isEmpty() || this.stop) {
                System.out.println("Stopping....:" + this.inputQueue.size() + " items remain");
                try {
                    executorService.awaitTermination(3, TimeUnit.SECONDS);
                    runners.terminate();
                } catch (InterruptedException e) {
                    break;
                }
                break;
            }
        }
        long end = System.currentTimeMillis();
        long duration = (end - start) / 1000;
        System.out.println("시간: " + duration + " 초");
    }
}