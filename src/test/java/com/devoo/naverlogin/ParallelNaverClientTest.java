package com.devoo.naverlogin;

import org.junit.Test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;

import static org.junit.Assert.fail;

public class ParallelNaverClientTest {

    @Test
    public void shouldClientDequeAllItemsFromInputs_andEnqueueToOutputs() throws Exception {
        //Given
        BlockingQueue<String> inputs = new LinkedBlockingQueue<>();
        BlockingQueue<String> outputs = new LinkedBlockingQueue<>();

        for (int i = 0; i < 1000; i++) {
            inputs.add(String.valueOf(i));
        }
        Function<String, String> function = s -> s;

        //When
        ParallelNaverClient<String, String> parallelNaverClient = new ParallelNaverClient<>(3, inputs,
                function, outputs);
        parallelNaverClient.start();

        //Then
        if (!inputs.isEmpty()) {
            fail("all items are not consumed.");
        }
        if (outputs.size() != 1000) {
            fail("all items are not produced.");
        }
    }
}