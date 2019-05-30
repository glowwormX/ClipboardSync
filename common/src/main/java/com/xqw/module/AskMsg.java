package com.xqw.module;

import com.xqw.common.BaseMsg;
import com.xqw.common.MsgType;
import com.xqw.common.NettyChannelMap;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

/**
 * 请求类型的消息
 */
public class AskMsg extends BaseMsg {
    private String group;
    private String text;

    public AskMsg() {
        super();
        setType(MsgType.ASK);
    }

    @Override
    public void serverHandelMsg() {
        List<String> clientIds = NettyChannelMap.getClientIds(this.getGroup());
        if (clientIds != null && clientIds.contains(this.getClientId())) {
            String text = this.getText();
            if (text != null) {
                for (String clientId : clientIds) {
                    if (!clientId.equals(this.getClientId())) {
                        ReplyMsg replyMsg = new ReplyMsg();
                        replyMsg.setBody(text);
                        NettyChannelMap.getChannel(clientId).writeAndFlush(replyMsg);
                    }
                }
            }
        }
    }

    @Override
    public void clientHandelMsg(ChannelHandlerContext channelHandlerContext) {
        ReplyMsg replyMsg = new ReplyMsg();
        replyMsg.setBody("client info **** !!!");
        channelHandlerContext.writeAndFlush(replyMsg);
    }


    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
