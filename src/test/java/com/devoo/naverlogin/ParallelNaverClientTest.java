package com.devoo.naverlogin;

import com.devoo.naverlogin.exception.NoMoreOutputException;
import com.devoo.naverlogin.runner.ClientAction;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class ParallelNaverClientTest {
    private static Logger log = LoggerFactory.getLogger(ParallelNaverClientTest.class);


    @Test(expected = NoMoreOutputException.class)
    public void shouldClientThrowNoMoreItemException_whenRunningAsynchronously() throws Exception {
        //Given
        BlockingQueue<String> inputs = createTestItemQueue();
        ClientAction<String, String> clientAction = (s, client) -> s;
        ParallelNaverClient<String, String> parallelNaverClient = new ParallelNaverClient<>(3);

        //When
        Stream<String> stringStream = parallelNaverClient.startAsynchronously(clientAction, inputs);
        Thread.sleep(3000L);
        parallelNaverClient.stop();

        //Then
        //Exception thrown
    }

    @Test(expected = NoMoreOutputException.class)
    public void shouldBeOutputItemsStreamedOutAndShouldParallelClientStop_whenAllItemsAreConsumed() throws Exception {
        //Given
        BlockingQueue<String> inputs = createTestItemQueue();
        AtomicInteger count = new AtomicInteger(0);
        ClientAction<String, String> clientAction = (s, client) -> s;
        ParallelNaverClient<String, String> parallelNaverClient = new ParallelNaverClient<>(3);

        //When
        Stream<String> stringStream = parallelNaverClient.startAsynchronously(clientAction, inputs);
        stringStream.forEach(string -> {
            log.debug("consume count: {}", count.get());
            if (count.incrementAndGet() >= inputs.size()) {
                stopParalleClient(parallelNaverClient);
            }
        });

        //Then
        //ExceptionThrown
    }

    private void stopParalleClient(ParallelNaverClient<String, String> parallelNaverClient) {
        try {
            parallelNaverClient.stop();
        } catch (InterruptedException e) {
        }
    }

    private BlockingQueue<String> createTestItemQueue() {
        //Given
        BlockingQueue<String> inputs = new LinkedBlockingQueue<>();

        for (int i = 0; i < 1000; i++) {
            inputs.add(String.valueOf(i));
        }
        return inputs;
    }
}