package com.xqw.module;

import com.xqw.common.BaseMsg;
import com.xqw.common.MsgType;
import com.xqw.common.NettyChannelMap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;

/**
 * 登录验证类型的消息
 */
public class LoginMsg extends BaseMsg {
    private String userName;
    private String password;
    private String group;

    private SocketChannel socketChannel;

    public LoginMsg() {
        super();
        setType(MsgType.LOGIN);
    }

    @Override
    public void serverHandelMsg() {
        LoginMsg loginMsg = this;
        if ("xqw".equals(loginMsg.getUserName()) && "123".equals(loginMsg.getPassword())) {
            //登录成功,把channel存到服务端的map中
            NettyChannelMap.add(loginMsg.getGroup(), loginMsg.getClientId(), socketChannel);
            System.out.println("client" + loginMsg.getClientId() + " 登录成功");
        }
    }

    @Override
    public void clientHandelMsg(ChannelHandlerContext channelHandlerContext) {
        //向服务器发起登录
        LoginMsg loginMsg = new LoginMsg();
        loginMsg.setPassword("123");
        loginMsg.setUserName("xqw");
        channelHandlerContext.writeAndFlush(loginMsg);
    }

    public void setSocketChannel(SocketChannel socketChannel) {
        this.socketChannel = socketChannel;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
