package com.xqw;

import com.xqw.module.AskMsg;
import com.xqw.common.Constants;
import com.xqw.module.LoginMsg;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class NettyClientBootstrap {
    private int port;
    private String host;
    private SocketChannel socketChannel;
    private static final EventExecutorGroup group = new DefaultEventExecutorGroup(20);

    public NettyClientBootstrap(int port, String host) throws InterruptedException {
        this.port = port;
        this.host = host;
    }

    public void start() throws InterruptedException {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap()
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .group(eventLoopGroup)
                .remoteAddress(host, port)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new IdleStateHandler(20, 10, 0));
                        socketChannel.pipeline().addLast(new ObjectEncoder());
                        socketChannel.pipeline().addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
                        socketChannel.pipeline().addLast(new NettyClientHandler());
                    }
                });
        ChannelFuture future = bootstrap.connect(host, port).sync();
        if (future.isSuccess()) {
            socketChannel = (SocketChannel) future.channel();
            System.out.println("connect server  成功---------");
        }
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        showTips(args);

        String host = getParam(args, "-host");
        if (host == null) {
            host = "localhost";
        }
        Constants.setClientId(UUID.randomUUID().toString());
        final NettyClientBootstrap bootstrap = new NettyClientBootstrap(23146, host);
        bootstrap.start();

        final String group = login(args, bootstrap);

        final Clipboard sysClip = Toolkit.getDefaultToolkit().getSystemClipboard();
//        uploadClipboardText(sysClip, finalGroup, bootstrap);
        sysClip.addFlavorListener(new FlavorListener() {
            @Override
            public void flavorsChanged(FlavorEvent flavorEvent) {
                uploadClipboardText(sysClip, group, bootstrap);
            }
        });
        while (true) {
            String str = new BufferedReader(new InputStreamReader(System.in)).readLine();
            if ("exit".equals(str)) {
                System.exit(0);
            }
        }
    }

    private static String login(String[] args, NettyClientBootstrap bootstrap) {
        String group = getParam(args, "-group");
        if (group == null) {
            System.out.println("请输入group参数");
            System.exit(0);
        }
        LoginMsg loginMsg = new LoginMsg();
        loginMsg.setPassword("123");
        loginMsg.setUserName("xqw");
        loginMsg.setGroup(group);
        bootstrap.socketChannel.writeAndFlush(loginMsg);
        return group;
    }

    private static void uploadClipboardText(Clipboard sysClip, String group, NettyClientBootstrap bootstrap) {
        Transferable trans = sysClip.getContents(null);
        if (trans.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            try {
                String clipboardText = (String) trans.getTransferData(DataFlavor.stringFlavor);
                if (Constants.lastClipboardText.equals(clipboardText)) {
                    return;
                }
                System.out.println("clipboardText:" + clipboardText);
                AskMsg askMsg = new AskMsg();
                askMsg.setGroup(group);
                askMsg.setText(clipboardText);
                bootstrap.socketChannel.writeAndFlush(askMsg);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private static String getParam(String[] args, String param) {
        String res = null;
        try {
            if (args.length > 0) {
                List<String> argList = Arrays.asList(args);
                int i = argList.indexOf(param);
                if (i > -1) {
                    res = args[i + 1];
                }
            }
        } catch (Exception e) {
            System.out.println("参数不对");
            System.exit(0);
        }
        return res;
    }

    private static void showTips(String[] args) {
        List<String> argList = Arrays.asList(args);
        if (argList.contains("?") || argList.contains("-help")) {
            System.out.println("-host       server地址");
            System.out.println("-group      用户组，在该参数下所有客户端共享剪切板");
            System.exit(0);
        }
    }
}
