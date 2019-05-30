package com.xqw.module;

import com.xqw.common.BaseMsg;
import com.xqw.common.Constants;
import com.xqw.common.MsgType;
import com.xqw.utils.SysClipboardUtil;
import io.netty.channel.ChannelHandlerContext;

/**
 *
 */
public class ReplyMsg extends BaseMsg {
    private String body;

    public ReplyMsg() {
        super();
        setType(MsgType.REPLY);
    }

    @Override
    public void serverHandelMsg() {
        System.out.println("receive client msg: " + this.getBody());
    }

    @Override
    public void clientHandelMsg(ChannelHandlerContext channelHandlerContext) {
        String text = this.getBody();
        if (text.length() > 0) {
            Constants.lastClipboardText = text;
            SysClipboardUtil.setSysClipboardText(text);
        }
        System.out.println("receive server msg: " + text);
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
