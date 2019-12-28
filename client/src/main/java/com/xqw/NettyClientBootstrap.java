package com.xqw;

import com.xqw.common.Constants;
import com.xqw.common.SysClipboardMonitor;
import com.xqw.module.AskReplyMsg;
import com.xqw.module.LoginMsg;
import com.xqw.utils.SysClipboardUtil;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class NettyClientBootstrap {
    private static final Logger logger = LoggerFactory.getLogger(NettyClientBootstrap.class);

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
            logger.info("connect server  成功---------");
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

        /* 有问题
        一次性会发送两次
        */
        SysClipboardMonitor monitor = new SysClipboardMonitor((trans) -> {
            uploadClipboardText(trans, group, bootstrap);
            uploadClipboardImage(trans, group, bootstrap);
        });
        SysClipboardUtil.setMonitor(monitor);

        //TODO 存在问题
        //客户端锁屏后打开剪切板失败
        //断开连接 自动重连
        while (true) {
            Thread.sleep(1000);
        }
    }

    private static String login(String[] args, NettyClientBootstrap bootstrap) {
        String group = getParam(args, "-group");
        if (group == null) {
            logger.info("请输入group参数");
            System.exit(0);
        }
        LoginMsg loginMsg = new LoginMsg();
        loginMsg.setPassword("123");
        loginMsg.setUserName("xqw");
        loginMsg.setGroup(group);
        bootstrap.socketChannel.writeAndFlush(loginMsg);
        return group;
    }

    private static void uploadClipboardText(Transferable trans, String group, NettyClientBootstrap bootstrap) {
        if (trans.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            String text = SysClipboardUtil.getText(trans);
            if (!lastEquals(text)) {
                AskReplyMsg askReplyMsg = new AskReplyMsg();
                askReplyMsg.setGroup(group);
                askReplyMsg.setBody(text);
                bootstrap.socketChannel.writeAndFlush(askReplyMsg);
                logger.info("send AskMsg to server clipboardText:" + text);
            }
        }
    }

    private static boolean lastEquals(Object current) {
        boolean equals = false;
        if (Constants.lastClipboardContent.getClass().equals(current.getClass())) {
            if(current instanceof String) {
                equals = Constants.lastClipboardContent.equals(current);
            } else if(current instanceof byte[]) {
                equals = ((byte[])Constants.lastClipboardContent).length == ((byte[]) current).length;
            }
        }
        if (!equals) Constants.lastClipboardContent = current;
        return equals;
    }

    private static void uploadClipboardImage(Transferable trans, String group, NettyClientBootstrap bootstrap) {
        if (trans.isDataFlavorSupported(DataFlavor.imageFlavor)) {
            BufferedImage image = SysClipboardUtil.getImage(trans);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try {
                ImageIO.write(image, "png", out);
            } catch (IOException e) {
                e.printStackTrace();
            }
            byte[] bytes = out.toByteArray();
            if (!lastEquals(bytes)) {
                AskReplyMsg askReplyMsg = new AskReplyMsg();
                askReplyMsg.setGroup(group);
                askReplyMsg.setBody(bytes);
                bootstrap.socketChannel.writeAndFlush(askReplyMsg);
                logger.info("send AskMsg to server clipboardText:" + image);
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
            logger.info("参数不对");
            System.exit(0);
        }
        return res;
    }

    private static void showTips(String[] args) {
        List<String> argList = Arrays.asList(args);
        if (argList.contains("?") || argList.contains("-help")) {
            logger.info("-host       server地址");
            logger.info("-group      用户组，在该参数下所有客户端共享剪切板");
            System.exit(0);
        }
    }
}
