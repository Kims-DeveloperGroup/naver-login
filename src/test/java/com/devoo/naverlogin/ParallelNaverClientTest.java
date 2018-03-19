package com.devoo.naverlogin;

import com.devoo.naverlogin.exception.NoMoreOutputException;
import com.devoo.naverlogin.runner.ClientAction;
import org.junit.Test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Stream;

public class ParallelNaverClientTest {


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
        Thread.sleep(3000L);
        parallelNaverClient.stop();
    }
}