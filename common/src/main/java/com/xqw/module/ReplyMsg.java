//package com.xqw.module;
//
//import com.xqw.common.BaseMsg;
//import com.xqw.common.Constants;
//import com.xqw.common.MsgType;
//import com.xqw.utils.SysClipboardUtil;
//import io.netty.channel.ChannelHandlerContext;
//
///**
// *
// */
//public class ReplyMsg extends BaseMsg {
//    private String body;
//
//    public ReplyMsg() {
//        super();
//        setType(MsgType.REPLY);
//    }
//
//    @Override
//    public void serverHandelMsg() {
//        //服务端接收响应不做处理
//        logger.info("receive client msg: " + this.getBody());
//    }
//
//    @Override
//    public void clientHandelMsg(ChannelHandlerContext channelHandlerContext) {
//        String text = this.getBody();
//        if (text.length() > 0) {
//            Constants.lastClipboardText = text;
//            SysClipboardUtil.setSysClipboardText(text);
//        }
//        logger.info("receive server msg: " + text);
//    }
//
//    public String getBody() {
//        return body;
//    }
//
//    public void setBody(String body) {
//        this.body = body;
//    }
//}
