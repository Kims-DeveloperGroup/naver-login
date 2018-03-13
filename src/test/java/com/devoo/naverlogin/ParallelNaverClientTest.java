package com.devoo.naverlogin;

import org.junit.Test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static org.junit.Assert.fail;

public class ParallelNaverClientTest {

    @Test
    public void shouldClientDequeAllItemsFromInputs() throws Exception {
        //Given
        BlockingQueue<String> inputs = new LinkedBlockingQueue<>();
        for (int i = 0; i < 1000; i++) {
            inputs.add(String.valueOf(i));
        }
        //When
        ParallelNaverClient<String, String> parallelNaverClient = new ParallelNaverClient<>(3, inputs);
        parallelNaverClient.start();

        //Then
        if (!inputs.isEmpty()) {
            fail("all items are not consumed.");
        }
    }
}