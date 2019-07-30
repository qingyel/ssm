package com.how2java.websocket;

import java.util.concurrent.CopyOnWriteArrayList;

public class ServerManager {

    private static CopyOnWriteArrayList<WsServer> servers = new CopyOnWriteArrayList();

    public static void broadCast(String msg) {
        for (WsServer bitCoinServer : servers) {
            bitCoinServer.sendMessage(msg);
        }
    }

    public static int getTotal() {
        return servers.size();
    }

    public static void add(WsServer server) {
        servers.add(server);
        System.out.println("有新连接加入！ 当前总连接数是：" + servers.size());
    }

    public static void remove(WsServer server) {
        System.out.println("有连接退出！ 当前总连接数是：" + servers.size());
        servers.remove(server);
    }


}
