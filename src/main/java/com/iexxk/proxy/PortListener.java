package com.iexxk.proxy;

import com.iexxk.events.MasterSwitchEventBus;
import com.iexxk.upstream.Upstream;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class PortListener {

    public PortListener(int port, Upstream upstream) {
        EventLoopGroup boss = new NioEventLoopGroup(1);
        EventLoopGroup worker = new NioEventLoopGroup();

        ServerBootstrap b = new ServerBootstrap();
        b.group(boss, worker)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new L4ProxyFrontend(upstream));
                    }
                });

        b.bind(port).syncUninterruptibly();

        MasterSwitchEventBus.register(upstream, this::closeAllConnections);
    }

    private final Set<Channel> connections = ConcurrentHashMap.newKeySet();

    public void addConnection(Channel clientChannel) {
        connections.add(clientChannel);
    }

    public void closeAllConnections() {
        for (Channel c : connections) {
            c.close();
        }
        connections.clear();
    }
}
