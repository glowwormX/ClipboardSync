package com.xqw.common;

import io.netty.channel.Channel;
import io.netty.channel.socket.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 */
public class NettyChannelMap {
    private static final Logger logger = LoggerFactory.getLogger(NettyChannelMap.class);
    private static Map<String, SocketChannel> map = new ConcurrentHashMap<String, SocketChannel>();
    private static Map<String, CopyOnWriteArrayList<String>> groupMap = new ConcurrentHashMap<String, CopyOnWriteArrayList<String>>();

    public static void add(String group, String clientId, SocketChannel socketChannel) {
        map.put(clientId, socketChannel);
        CopyOnWriteArrayList<String> clientIds = groupMap.get(group);
        if (clientIds == null) {
            clientIds = new CopyOnWriteArrayList<String>();
            clientIds.add(clientId);
            groupMap.put(group, clientIds);
        } else {
            clientIds.add(clientId);
        }
        logger.info("client " + clientId + " 登录成功");
        logger.info(clientIds.toString());
    }

    public static Channel getChannel(String clientId) {
        return map.get(clientId);
    }
    public static List<String> getClientIds(String group) {
        return groupMap.get(group);
    }

    public static void remove(SocketChannel socketChannel) {
        for (Map.Entry entry : map.entrySet()) {
            if (entry.getValue() == socketChannel) {
                logger.info(entry.getKey() + " 断开连接");
                map.remove(entry.getKey());

                for (Map.Entry<String, CopyOnWriteArrayList<String>> e : groupMap.entrySet()) {
                    e.getValue().remove(entry.getKey());
                }
            }
        }
    }
}
