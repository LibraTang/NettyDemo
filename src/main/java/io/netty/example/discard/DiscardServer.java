package io.netty.example.discard;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class DiscardServer {
    private int port;

    public DiscardServer(int port) {
        this.port = port;
    }

    public void run() throws Exception {
        //boss负责接受连接
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        //boss接受连接后会将连接注册到worker，worker处理这些连接
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            //ServerBootstrap是设置服务器的帮助类
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                     .channel(NioServerSocketChannel.class) //这里使用NioServerSocketChannel实例化一个Channel来接受连接
                     .childHandler(new ChannelInitializer<SocketChannel>() { //添加处理类，可添加多个
                         @Override
                         protected void initChannel(SocketChannel socketChannel) throws Exception {
                             socketChannel.pipeline().addLast(new DiscardServletHandler());
                         }
                     })
                     .option(ChannelOption.SO_BACKLOG, 128) //针对特定的Channel实现进行参数设置，此处是NioServerSocketChannel
                     .childOption(ChannelOption.SO_KEEPALIVE, true); //(存疑)用于ServerChannel的子Channel，此处是NioServerSocketChannel

            //绑定端口并开始接受连接
            ChannelFuture future = bootstrap.bind(port).sync();
            System.out.println("服务器启动...");

            // 等待server socket关闭
            future.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;
        new DiscardServer(port).run();
    }
}
