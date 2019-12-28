package com.xqw.module;

import com.xqw.common.BaseMsg;
import com.xqw.common.Constants;
import com.xqw.common.MsgType;
import com.xqw.common.NettyChannelMap;
import com.xqw.utils.SysClipboardUtil;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.datatransfer.Transferable;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

/**
 * 请求类型的消息
 */
public class AskReplyMsg extends BaseMsg {
    private static final Logger logger = LoggerFactory.getLogger(AskReplyMsg.class);
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
                        logger.info(String.format("AskMsg to remoteAddress:%s , contenct:%s ", NettyChannelMap.getChannel(clientId).remoteAddress(), body));
                        NettyChannelMap.getChannel(clientId).writeAndFlush(replyMsg);
                    }
                }
            }
        }
    }

    @Override
    public void clientHandelMsg(ChannelHandlerContext channelHandlerContext) {
        if (body != null) {
            Constants.lastClipboardContent = body;
            if (body instanceof String) {
                Transferable trans = SysClipboardUtil.setSysClipboardText((String) body);
                SysClipboardUtil.clipListener.setSyncContent(trans);
            }
            if (body instanceof byte[]) {
                ByteArrayInputStream bin = new ByteArrayInputStream((byte[])body);
                BufferedImage image = null;
                try {
                    image = ImageIO.read(bin);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Transferable trans = SysClipboardUtil.setClipboardImage(image);
                SysClipboardUtil.clipListener.setSyncContent(trans);
            }
        }
        logger.info("receive server msg: " + body);
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
