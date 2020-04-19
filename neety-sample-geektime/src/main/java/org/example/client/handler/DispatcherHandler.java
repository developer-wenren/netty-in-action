package org.example.client.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.example.study.common.ResponseMessage;
import org.example.client.dispatcher.RequestPendingCenter;

/**
 * @author one
 * @date 2020/04/12
 */
public class DispatcherHandler extends SimpleChannelInboundHandler<ResponseMessage> {
    private final RequestPendingCenter center;

    public DispatcherHandler(RequestPendingCenter center) {
        this.center = center;
    }

    /**
     * Is called for each message of type {@link I}.
     *
     * @param ctx the {@link ChannelHandlerContext} which this {@link SimpleChannelInboundHandler}
     *            belongs to
     * @param msg the message to handle
     * @throws Exception is thrown if an error occurred
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ResponseMessage msg) throws Exception {
        center.set(msg.getMessageHeader().getStreamId(), msg.getMessageBody());
//        ctx.writeAndFlush(msg);
    }
}
