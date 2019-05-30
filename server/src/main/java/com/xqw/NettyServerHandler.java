package com.xqw;

import com.xqw.common.BaseMsg;
import com.xqw.module.LoginMsg;
import com.xqw.common.MsgType;
import com.xqw.common.NettyChannelMap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.ReferenceCountUtil;

public class NettyServerHandler extends SimpleChannelInboundHandler<BaseMsg> {
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        NettyChannelMap.remove((SocketChannel) ctx.channel());
    }

    @Override
    protected void messageReceived(ChannelHandlerContext channelHandlerContext, BaseMsg baseMsg) throws Exception {

        Channel channel = channelHandlerContext.channel();
        if (MsgType.LOGIN.equals(baseMsg.getType())) {
            LoginMsg loginMsg = (LoginMsg) baseMsg;
            loginMsg.setSocketChannel((SocketChannel) channel);
            baseMsg = loginMsg;
        } else if (NettyChannelMap.getChannel(baseMsg.getClientId()) == null) {
            //说明未登录，或者连接断了，服务器向客户端发起登录请求，让客户端重新登录
            LoginMsg loginMsg = new LoginMsg();
            channel.writeAndFlush(loginMsg);
        }
        //自定义处理消息
        baseMsg.serverHandelMsg();
        ReferenceCountUtil.release(baseMsg);
    }
}
