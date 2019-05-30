package com.xqw.module;

import com.xqw.common.BaseMsg;
import com.xqw.common.MsgType;
import com.xqw.common.NettyChannelMap;
import io.netty.channel.ChannelHandlerContext;

/**
 * 心跳检测的消息类型
 */
public class PingMsg extends BaseMsg {
    public PingMsg() {
        super();
        setType(MsgType.PING);
    }

    @Override
    public void serverHandelMsg() {
        PingMsg replyPing = new PingMsg();
        NettyChannelMap.getChannel(this.getClientId()).writeAndFlush(replyPing);
    }

    @Override
    public void clientHandelMsg(ChannelHandlerContext channelHandlerContext) {
        System.out.println("receive ping from server----------");
    }
}
