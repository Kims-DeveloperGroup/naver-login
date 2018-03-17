package com.devoo.naverlogin;

import com.devoo.naverlogin.exception.NaverLoginFailException;
import com.devoo.naverlogin.exception.NoMoreOutputException;
import com.devoo.naverlogin.runner.ClientAction;
import com.devoo.naverlogin.runner.NaverClientRunner;
import com.devoo.naverlogin.runner.NaverClientRunnerPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static java.util.concurrent.Executors.newFixedThreadPool;

/**
 * Runs NaverClientRunnerPool and executes NaverClientRunner in multiple threads.
 *
 * @param <I>
 * @param <R>
 */
public class ParallelNaverClient<I, R> implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(ParallelNaverClient.class);
    private final ExecutorService executorService;
    private final NaverClientRunnerPool<I, R> clientRunnerPool;
    private BlockingQueue<I> inputQueue;
    private final BlockingQueue<R> outputQueue;

    private boolean stop = false;

    public ParallelNaverClient(int parallel) {
        executorService = newFixedThreadPool(parallel);
        this.inputQueue = new LinkedBlockingQueue<>();
        this.outputQueue = new LinkedBlockingQueue<>();
        this.clientRunnerPool = new NaverClientRunnerPool(parallel, outputQueue);
    }

    public void tryToLogin(String userId, String password) {
        this.clientRunnerPool.getClientRunners().forEach(runner -> {
            try {
                runner.getNaverClient().tryLogin(userId, password);
            } catch (NaverLoginFailException e) {
                log.error("Login fail exception : {}", e);
            }
        });
    }

    /**
     * Starts this ParallelNaverClient synchronously.
     * @throws Exception
     */
    public BlockingQueue<R> start(ClientAction<I, R> clientAction, BlockingQueue<I> inputQueue) throws Exception {
        init(inputQueue, clientAction);
        this.run();
        return this.outputQueue;
    }

    /**
     * Starts this ParallelNaverClient asynchronously and returns stream of outputs.
     * @return
     */
    public Stream<R> startAsynchronously(ClientAction<I, R> clientAction, BlockingQueue<I> inputQueue) {
        init(inputQueue, clientAction);
        new Thread(this).start();
        return Stream.generate(() -> {
            try {
                R item = this.outputQueue.poll(3L, TimeUnit.SECONDS);
                if (item == null) {
                    log.debug("No more item to supply");
                    this.stop();
                    throw new NoMoreOutputException();
                }
                return item;
            } catch (InterruptedException e) {
                log.error("Exception occurred while consuming items : {}", e);
            }
            return null;
        });
    }

    private void init(BlockingQueue<I> inputQueue, ClientAction<I, R> clientAction) {
        this.inputQueue = inputQueue;
        clientRunnerPool.setClientAction(clientAction);
        clientRunnerPool.setInputQueue(inputQueue);
    }

    public void stop() throws InterruptedException {
        if (this.stop) {
            log.debug("Stop request has already been sent.");
            return;
        }
        this.stop = true;
        log.info("Sent stop request.");
        log.info("Stopping....{} items remain", this.inputQueue.size());
        clientRunnerPool.terminate();
        executorService.awaitTermination(3, TimeUnit.SECONDS);
    }

    @Override
    public void run() {
        long start = System.currentTimeMillis();
        while (true) {
            try {
                NaverClientRunner naverClientRunner = clientRunnerPool.pollAvailableClientRunner();
                executorService.submit(naverClientRunner);
                if (this.inputQueue.isEmpty()) {
                    log.debug("Input Queue is empty");
                    stop();
                    break;
                }
            } catch (InterruptedException e) {
                log.error("Exception occurred while running NaverClientRunnerPool: ", e);
            }
        }
        long end = System.currentTimeMillis();
        long duration = (end - start) / 1000;
        log.info("시간: {} 초", duration);
    }
}