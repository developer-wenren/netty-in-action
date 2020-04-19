package org.example.client.handler;

import io.netty.handler.timeout.IdleStateHandler;

public class ClientIdleCheckHandler extends IdleStateHandler {

    public ClientIdleCheckHandler() {
        // 客户端写5秒空闲时触发
        super(0, 5, 0);
    }

}
