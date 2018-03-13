package com.devoo.naverlogin;

import com.devoo.naverlogin.runner.ClientAction;
import org.junit.Test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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
        ClientAction<String, String> function = (s, client) -> s;

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

    @Test
    public void shouldClientStopBeforeAllItemsAreConsumed() throws Exception {
        //Given
        BlockingQueue<String> inputs = new LinkedBlockingQueue<>();
        BlockingQueue<String> outputs = new LinkedBlockingQueue<>();

        for (int i = 0; i < 1000; i++) {
            inputs.add(String.valueOf(i));
        }
        ClientAction<String, String> function = (s, client) -> s;

        //When
        ParallelNaverClient<String, String> parallelNaverClient = new ParallelNaverClient<>(1, inputs,
                function, outputs);
        parallelNaverClient.startAsyn();
        Thread.sleep(50);
        parallelNaverClient.stop();

        //Then
        if (inputs.isEmpty()) {
            fail("all items are consumed.");
        }
        if (outputs.size() == 1000) {
            fail("all items are produced.");
        }
    }
}