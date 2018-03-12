package com.devoo.naverlogin;

import org.junit.Test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ParallelNaverClientTest {

    @Test
    public void test() throws InterruptedException {
        BlockingQueue<String> queue = new LinkedBlockingQueue<>();
        for (int i = 0; i < 1000; i++) {
            queue.add(String.valueOf(i));
        }
        ParallelNaverClient<String, String> parallelNaverClient = new ParallelNaverClient<>(3, queue);
        parallelNaverClient.start();
    }

}