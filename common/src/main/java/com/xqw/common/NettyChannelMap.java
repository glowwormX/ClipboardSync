package com.xqw.common;

import io.netty.channel.Channel;
import io.netty.channel.socket.SocketChannel;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 */
public class NettyChannelMap {
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
        System.out.println("client " + clientId + " 登录成功");
        System.out.println(clientIds);
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
                System.out.println(entry.getKey() + " 断开连接");
                map.remove(entry.getKey());

                for (Map.Entry<String, CopyOnWriteArrayList<String>> e : groupMap.entrySet()) {
                    e.getValue().remove(entry.getKey());
                }
            }
        }
    }
}
