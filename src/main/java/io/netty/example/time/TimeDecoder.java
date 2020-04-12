package io.netty.example.time;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * ByteToMessageDecoder是方便用于处理数据碎片的类
 * 当有新的数据的时候，ByteToMessageDecoder会调用decode方法
 * 当数据量不够的时候，decode不会输出数据到list中。当有新数据的时候会再次调用decode
 * 数据量足够的时候，decode会将累积的数据输出到list中，ByteToMessageDecoder会丢弃缓冲区的部分
 */
public class TimeDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if(byteBuf.readableBytes() < 4) {
            return;
        }

//        list.add(byteBuf.readBytes(4));
        list.add(new UnixTime(byteBuf.readUnsignedInt()));
    }
}
