package org.example.server.codec;

import io.netty.handler.codec.LengthFieldPrepender;

/**
 * @author one
 * @date 2020/04/12
 */
public class OrderFrameEncoder extends LengthFieldPrepender {

    public OrderFrameEncoder() {
        this(2);
    }

    /**
     * Creates a new instance.
     *
     * @param lengthFieldLength the length of the prepended length field.
     *                          Only 1, 2, 3, 4, and 8 are allowed.
     * @throws IllegalArgumentException if {@code lengthFieldLength} is not 1, 2, 3, 4, or 8
     */
    public OrderFrameEncoder(int lengthFieldLength) {
        super(lengthFieldLength);
    }
}
