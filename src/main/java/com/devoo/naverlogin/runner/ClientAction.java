package com.devoo.naverlogin.runner;

import com.devoo.naverlogin.NaverClient;

public interface ClientAction<I, R> {
    R apply(I input, NaverClient client);
}
