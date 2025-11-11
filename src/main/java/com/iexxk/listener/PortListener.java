package com.iexxk.listener;


import com.iexxk.proxy.L4ProxyFrontend;
import com.iexxk.upstream.MasterSwitchEventBus;
import com.iexxk.upstream.Upstream;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class PortListener {
    private final int port;
    private final Upstream upstream;

    public PortListener(int port, Upstream upstream) {
        this.port = port;
        this.upstream = upstream;
    }

    public void start() {
        EventLoopGroup boss = new NioEventLoopGroup(1);
        EventLoopGroup worker = new NioEventLoopGroup();

        ServerBootstrap b = new ServerBootstrap();
        b.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new L4ProxyFrontend(upstream));
                    }
                });

        b.bind(port).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                System.out.println("Listening on port " + port);
            } else {
                System.err.println("Failed to bind port " + port);
            }
        });

        MasterSwitchEventBus.register(upstream, () -> {
            System.out.println("Master switched for upstream " + upstream.getHost() + ":" + upstream.getPort());
        });
    }
}