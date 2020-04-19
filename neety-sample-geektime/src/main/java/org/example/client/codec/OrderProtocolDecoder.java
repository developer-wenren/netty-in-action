package org.example.client.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.example.study.common.ResponseMessage;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * 协议二次解码器
 *
 * @author one
 * @date 2020/04/12
 */
public class OrderProtocolDecoder extends MessageToMessageDecoder<ByteBuf> {
    /**
     * Decode from one message to an other. This method will be called for each written message that can be handled
     * by this decoder.
     *
     * @param ctx the {@link ChannelHandlerContext} which this {@link MessageToMessageDecoder} belongs to
     * @param msg the message to decode to an other one
     * @param out the {@link List} to which decoded messages should be added
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) {
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.decode(msg);
        out.add(responseMessage);
    }
}
