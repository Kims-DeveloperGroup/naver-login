package com.devoo.naverlogin;

import com.devoo.naverlogin.runner.ClientAction;
import com.devoo.naverlogin.runner.NaverClientRunner;
import com.devoo.naverlogin.runner.NaverClientRunners;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.Executors.newFixedThreadPool;

public class ParallelNaverClient<T, R> implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(ParallelNaverClient.class);
    private final ExecutorService executorService;
    private final NaverClientRunners<T, R> runners;
    private BlockingQueue<T> inputQueue;

    private boolean stop = false;

    public ParallelNaverClient(int parallel, BlockingQueue<T> inputQueue, ClientAction<T, R> function,
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
        log.info("sent stop request.");
        this.stop = true;
    }

    @Override
    public void run() {
        long start = System.currentTimeMillis();
        while (true) {
            NaverClientRunner naverClientRunner = runners.pollAvailableClient();
            executorService.submit(naverClientRunner);
            if (this.inputQueue.isEmpty() || this.stop) {
                log.info("Stopping....{} items remain", this.inputQueue.size());
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
        log.info("시간: {} 초", duration);
    }
}