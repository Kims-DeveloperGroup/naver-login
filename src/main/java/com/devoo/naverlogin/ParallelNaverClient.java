package com.devoo.naverlogin;

import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.Executors.newFixedThreadPool;

public class ParallelNaverClient<T, R> implements Runnable {
    private final ExecutorService executorService;
    private final NaverClientRunners runners;
    private BlockingQueue<T> inputQueue;

    private boolean stop = false;

    public ParallelNaverClient(int parallel, BlockingQueue<T> inputQueue) {
        executorService = newFixedThreadPool(parallel);
        this.inputQueue = inputQueue;
        this.runners = new NaverClientRunners(parallel, this.inputQueue);
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

    private class NaverClientRunners {
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
                        System.out.println(runner.name + " is available.");
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

    private class NaverClientRunner<T, R> implements Callable {
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
            if (lock.compareAndSet(1, 0)) {
                System.out.println(name + " is unlocked.");
            } else {
                System.out.println(name + " is already unlocked. wtf");
            }
        }

        public void terminate() {
            naverClient.getWebDriver().close();
            System.out.println(name + " terminated.");
        }

        @Override
        public R call() throws Exception {
            T item = queue.poll(3, TimeUnit.SECONDS);
            if (item == null) {
                System.out.println(name + ": no item to consume");
            }
            System.out.println(Thread.currentThread().getName() + " : " + name + ": item: " + item.toString());
            unlock();
            return null;
        }
    }
}