package org.example.server.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.example.study.common.Operation;
import io.netty.example.study.common.RequestMessage;
import io.netty.example.study.common.auth.AuthOperation;
import io.netty.example.study.common.auth.AuthOperationResult;
import lombok.extern.slf4j.Slf4j;

/**
 * @author one
 * @date 2020/04/19
 */
@Slf4j
@ChannelHandler.Sharable
public class AuthHandler extends SimpleChannelInboundHandler<RequestMessage> {

    /**
     * @param ctx the {@link ChannelHandlerContext} which this {@link SimpleChannelInboundHandler}
     *            belongs to
     * @param msg the message to handle
     * @throws Exception is thrown if an error occurred
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RequestMessage msg) throws Exception {
        Operation messageBody = msg.getMessageBody();
        try {
            if (messageBody instanceof AuthOperation) {
                AuthOperation authOperation = AuthOperation.class.cast(messageBody);
                AuthOperationResult authOperationResult = authOperation.execute();
                if (authOperationResult.isPassAuth()) {
                    log.info("授权通过");
                } else {
                    log.error("授权失败");
                    ctx.close();
                }
            } else {
                log.error("首次请求非授权消息");
                ctx.close();
            }
        } catch (Exception e) {
            log.error("授权异常", e);
            ctx.close();
        } finally {
            ctx.pipeline().remove(this);
        }
    }
}
