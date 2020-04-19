package org.example.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.example.study.common.Operation;
import io.netty.example.study.common.OperationResult;
import io.netty.example.study.common.RequestMessage;
import io.netty.example.study.common.ResponseMessage;

/**
 * @author one
 * @date 2020/04/12
 */
public class OrderServerProcessHandler extends SimpleChannelInboundHandler<RequestMessage> {
    /**
     * Is called for each message of type
     *
     * @param ctx the {@link ChannelHandlerContext} which this {@link SimpleChannelInboundHandler}
     *            belongs to
     * @param msg the message to handle
     * @throws Exception is thrown if an error occurred
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RequestMessage msg) throws Exception {
        Operation messageBody = msg.getMessageBody();
        OperationResult execute = messageBody.execute();
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setMessageHeader(msg.getMessageHeader());
        responseMessage.setMessageBody(execute);
        ctx.writeAndFlush(responseMessage);
    }
}
