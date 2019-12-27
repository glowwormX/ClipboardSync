package com.xqw.module;

import com.xqw.common.BaseMsg;
import com.xqw.common.MsgType;
import com.xqw.common.NettyChannelMap;
import com.xqw.utils.SysClipboardUtil;
import io.netty.channel.ChannelHandlerContext;

import java.awt.*;
import java.util.List;

/**
 * 请求类型的消息
 */
public class AskReplyMsg extends BaseMsg {
    private String group;
//    private String text;
//    private Image image;
    private Object body;

    public AskReplyMsg() {
        super();
        setType(MsgType.ASK);
    }

    @Override
    public void serverHandelMsg() {
        List<String> clientIds = NettyChannelMap.getClientIds(this.getGroup());
        if (clientIds != null && clientIds.contains(this.getClientId())) {
            Object body = this.getBody();
            if (body != null) {
                for (String clientId : clientIds) {
                    if (!clientId.equals(this.getClientId())) {
                        AskReplyMsg replyMsg = new AskReplyMsg();
                        replyMsg.setBody(body);
                        System.out.println(String.format("AskMsg to remoteAddress:%s , contenct:%s ", NettyChannelMap.getChannel(clientId).remoteAddress(), body));
                        NettyChannelMap.getChannel(clientId).writeAndFlush(replyMsg);
                    }
                }
            }
        }
    }

    @Override
    public void clientHandelMsg(ChannelHandlerContext channelHandlerContext) {
        if (body != null) {
//            Constants.lastClipboardText = text;
            if (body instanceof String) {
                SysClipboardUtil.setSysClipboardText((String) body);
            }
            if (body instanceof Image) {
                SysClipboardUtil.setClipboardImage((Image) body);
            }
        }
        System.out.println("receive server msg: " + body);
    }


    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

//    public String getText() {
//        return text;
//    }
//
//    public void setText(String text) {
//        this.text = text;
//    }
//
//    public Image getImage() {
//        return image;
//    }
//
//    public void setImage(Image image) {
//        this.image = image;
//    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }
}
