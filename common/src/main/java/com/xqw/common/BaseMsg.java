package com.xqw.common;

import io.netty.channel.ChannelHandlerContext;

import java.io.Serializable;

/**
 *
 * 必须实现序列,serialVersionUID 一定要有
 */

public abstract class BaseMsg  implements Serializable {
    private static final long serialVersionUID = 1L;
    private MsgType type;
    //必须唯一，否者会出现channel调用混乱
    private String clientId;

    //初始化客户端id
    public BaseMsg() {
        this.clientId = Constants.getClientId();
    }

    /**
     * 服务端处理消息方法
     */
    public abstract void serverHandelMsg();

    /**
     * 客户端处理消息方法
     */
    public abstract void clientHandelMsg(ChannelHandlerContext channelHandlerContext);

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public MsgType getType() {
        return type;
    }

    public void setType(MsgType type) {
        this.type = type;
    }
}
