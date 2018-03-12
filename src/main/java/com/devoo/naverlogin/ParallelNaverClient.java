package com.devoo.naverlogin;

import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.Executors.newFixedThreadPool;

public class ParallelNaverClient<T, R> {
    private final ExecutorService executorService;
    private final NaverClientRunners runners;
    private BlockingQueue<T> queue;

    private boolean stop = false;

    public ParallelNaverClient(int parallel, BlockingQueue<T> queue) {
        executorService = newFixedThreadPool(parallel);
        this.queue = queue;
        this.runners = new NaverClientRunners(parallel, this.queue);
    }

    public void start() throws InterruptedException {
        while (true) {
            NaverClientRunner naverClientRunner = runners.pollAvailableClient();
            executorService.submit(naverClientRunner);
            if (stop == true) {
                break;
            }
        }
    }

    private class NaverClientRunners {
        private LinkedList<NaverClientRunner> clientRunners = new LinkedList<>();

        public NaverClientRunners(int clientRunnerCount, BlockingQueue<T> queue) {
            for (int count = 0; count < clientRunnerCount; count++) {
                this.clientRunners.add(new NaverClientRunner(queue, "runner-" + count));
            }
        }

        public NaverClientRunner pollAvailableClient() {
            while (true) {
                for (NaverClientRunner runner : clientRunners) {
                    if (runner.lockIfAvailable()) {
                        return runner;
                    }
                }
                continue;
            }
        }
    }

    private class NaverClientRunner implements Callable<R> {
        private String name;
        private NaverClient naverClient = new NaverClient();
        private BlockingQueue<T> queue;
        private AtomicInteger lock = new AtomicInteger(0);

        public NaverClientRunner(BlockingQueue<T> queue, String name) {
            this.queue = queue;
            this.name = name;
        }

        public boolean isAvailable() {
            return lock.get() == 0;
        }

        public boolean lockIfAvailable() {
            return lock.compareAndSet(0, 1);
        }

        public void unlock() {
            lock.compareAndSet(1, 0);
        }

        @Override
        public R call() throws Exception {
            T item = queue.take();
            System.out.println(name + ": item: " + item.toString());
            unlock();
            return null;
        }
    }
}