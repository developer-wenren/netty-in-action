package org.example.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.example.study.common.Operation;
import io.netty.example.study.common.order.OrderOperation;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.SneakyThrows;
import org.example.client.codec.*;

/**
 * @author one
 * @date 2020/04/12
 */
public class Client {
    @SneakyThrows
    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(new NioEventLoopGroup());
        bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new OrderFrameDecoder());
                pipeline.addLast(new OrderFrameEncoder());
                pipeline.addLast(new OrderProtocolEncoder());
                pipeline.addLast(new OrderProtocolDecoder());
                pipeline.addLast(new OperationEncoder());
                pipeline.addLast(new LoggingHandler(LogLevel.INFO));
            }
        });
        ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 8090);
        // 必须等待连接就绪，才能写数据
        channelFuture.sync();
//        Operation operation = new OrderOperation(101, "table");
//        Object obj = new RequestMessage(IdUtil.nextId(), operation);
//        channelFuture.channel().writeAndFlush(obj);
        Operation milk = new OrderOperation(1, "milk");
        channelFuture.channel().writeAndFlush(milk);
        channelFuture.channel().closeFuture().get();
    }
}
