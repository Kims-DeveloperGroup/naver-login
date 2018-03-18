package com.devoo.naverlogin;

import com.devoo.naverlogin.exception.NoMoreOutputException;
import com.devoo.naverlogin.runner.ClientAction;
import org.junit.Test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.fail;

public class ParallelNaverClientTest {

    @Test
    public void shouldClientDequeAllItemsFromInputs_andEnqueueToOutputs() throws Exception {
        //Given
        BlockingQueue<String> inputs = new LinkedBlockingQueue<>();
        BlockingQueue<String> outputs;

        for (int i = 0; i < 1000; i++) {
            inputs.add(String.valueOf(i));
        }
        ClientAction<String, String> clientAction = (s, client) -> s;

        //When
        ParallelNaverClient<String, String> parallelNaverClient = new ParallelNaverClient<>(3);
        outputs = parallelNaverClient.start(clientAction, inputs);

        //Then
        if (!inputs.isEmpty()) {
            fail("all items are not consumed.");
        }
        if (outputs.size() != 1000) {
            fail("all items are not produced.");
        }
    }

    @Test(expected = NoMoreOutputException.class)
    public void shouldClientThrowNoMoreItemException_whenRunningAsynchronously() throws Exception {
        //Given
        BlockingQueue<String> inputs = new LinkedBlockingQueue<>();

        for (int i = 0; i < 1000; i++) {
            inputs.add(String.valueOf(i));
        }
        ClientAction<String, String> clientAction = (s, client) -> s;
        ParallelNaverClient<String, String> parallelNaverClient = new ParallelNaverClient<>(3);

        //When
        Stream<String> stringStream = parallelNaverClient.startAsynchronously(clientAction, inputs);
        stringStream.collect(Collectors.toList());

        //Then
        if (!inputs.isEmpty()) {
            fail("all items are consumed.");
        }
    }
}