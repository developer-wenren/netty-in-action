package org.example.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.example.study.common.Operation;
import io.netty.example.study.common.OperationResult;
import io.netty.example.study.common.RequestMessage;
import io.netty.example.study.common.order.OrderOperation;
import io.netty.example.study.util.IdUtil;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.SneakyThrows;
import org.example.client.codec.OrderFrameDecoder;
import org.example.client.codec.OrderFrameEncoder;
import org.example.client.codec.OrderProtocolDecoder;
import org.example.client.codec.OrderProtocolEncoder;
import org.example.client.dispatcher.OperationResultFeature;
import org.example.client.dispatcher.RequestPendingCenter;
import org.example.client.handler.DispatcherHandler;

/**
 * @author one
 * @date 2020/04/12
 */
public class Client2 {

    @SneakyThrows
    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.group(new NioEventLoopGroup());
        RequestPendingCenter center = new RequestPendingCenter();
        bootstrap.handler(new ChannelInitializer<NioSocketChannel>() {
            @Override
            protected void initChannel(NioSocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new OrderFrameDecoder());
                pipeline.addLast(new OrderProtocolDecoder());
                pipeline.addLast(new DispatcherHandler(center));
                pipeline.addLast(new LoggingHandler(LogLevel.INFO));
                pipeline.addLast(new OrderFrameEncoder());
                pipeline.addLast(new OrderProtocolEncoder());
            }
        });
        ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 8090);
        // 必须等待连接就绪，才能写数据
        channelFuture.sync();
        Operation operation = new OrderOperation(101, "table");
        long streamId = IdUtil.nextId();
        RequestMessage obj = new RequestMessage(streamId, operation);
        OperationResultFeature operationResultFeature = new OperationResultFeature();
        center.add(1L, operationResultFeature);
        channelFuture.channel().writeAndFlush(obj);
        OperationResult operationResult = operationResultFeature.get();
        System.out.println(operationResult);
        channelFuture.channel().closeFuture().get();
    }
}
