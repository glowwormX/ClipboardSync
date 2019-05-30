package com.xqw;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

/**
 *
 */
public class NettyServerBootstrap {
    private int port;
    private SocketChannel socketChannel;

    public NettyServerBootstrap(int port) throws InterruptedException {
        this.port = port;
    }

    public ChannelFuture bind() throws InterruptedException {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap()
                .group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .option(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline p = socketChannel.pipeline();
                        p.addLast(new ObjectEncoder());
                        p.addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
                        p.addLast(new NettyServerHandler());
                    }
                });
        ChannelFuture f = bootstrap.bind(port).sync();
        if (f.isSuccess()) {
            System.out.println("server start---------------");
        }
        return f;
    }

    public static void main(String[] args) throws InterruptedException {
        ChannelFuture f = new NettyServerBootstrap(23146).bind();
        f.channel().closeFuture().sync();
    }
}
