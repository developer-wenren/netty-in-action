package org.example.client.codec;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * 订单帧解码
 *
 * @author one
 * @date 2020/04/12
 */
public class OrderFrameDecoder extends LengthFieldBasedFrameDecoder {
    public OrderFrameDecoder() {
        this(Integer.MAX_VALUE, 0, 2, 0, 2);
    }

    /**
     * Creates a new instance.
     *
     * @param maxFrameLength      the maximum length of the frame.  If the length of the frame is
     *                            greater than this value
     * @param lengthFieldOffset   the offset of the length field
     * @param lengthFieldLength   the length of the length field
     * @param lengthAdjustment    the compensation value to add to the value of the length field
     * @param initialBytesToStrip
     */
    public OrderFrameDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }
}
