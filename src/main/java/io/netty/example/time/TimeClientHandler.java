package io.netty.example.time;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Date;

/**
 * 为了解决基于流的传输方式的问题（数据碎片化），有几种方式：
 * 1. 最简单的方式，创建一个内部缓冲区，积累到一定量的时候再处理
 * 但是这种方式会让handler变得复杂，后期难以维护
 * 2. 将handler的功能拆分出来，降低单个handler的复杂度，最后向ChannelPipeline添加多个handler
 */
public class TimeClientHandler extends ChannelInboundHandlerAdapter {
    private ByteBuf buf;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        buf = ctx.alloc().buffer(4);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        buf.release();
        buf = null;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        // 1. 所有收到的数据累加到buf中
        buf.writeBytes(in);
        in.release();

        // 2. 检查buf是否积累了足够的数据，在调用业务逻辑，否则会再次调用channelRead，直至累加到4个字节(此处)
        if(buf.readableBytes() >= 4) {
            long currentTimeMillis = (in.readUnsignedInt() - 2208988800L) * 1000L;
            System.out.println(new Date(currentTimeMillis));
            ctx.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
