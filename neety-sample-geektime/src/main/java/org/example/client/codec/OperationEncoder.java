package org.example.client.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.example.study.common.Operation;
import io.netty.example.study.common.RequestMessage;
import io.netty.example.study.util.IdUtil;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

/**
 * @author one
 * @date 2020/04/12
 */
public class OperationEncoder extends MessageToMessageEncoder<Operation> {
    /**
     * Encode from one message to an other. This method will be called for each written message that can be handled
     * by this encoder.
     *
     * @param ctx       the {@link ChannelHandlerContext} which this {@link MessageToMessageEncoder} belongs to
     * @param operation the message to encode to an other one
     * @param out       the {@link List} into which the encoded msg should be added
     *                  needs to do some kind of aggregation
     * @throws Exception is thrown if an error occurs
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, Operation operation, List<Object> out) throws Exception {
        ByteBuf buffer = ctx.alloc().buffer();
        RequestMessage requestMessage = new RequestMessage(IdUtil.nextId(), operation);
        requestMessage.encode(buffer);
        out.add(buffer);
    }
}
