package com.iexxk.proxy;

import com.iexxk.upstream.HostPort;
import com.iexxk.upstream.Upstream;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class L4ProxyFrontend extends ChannelInboundHandlerAdapter {

    private final Upstream upstream;
    private Channel backend;
    private Channel client;

    public L4ProxyFrontend(Upstream upstream) {
        this.upstream = upstream;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        this.client = ctx.channel();

        HostPort master = upstream.getCurrentMaster();

        Bootstrap bs = new Bootstrap();
        bs.group(client.eventLoop())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new L4ProxyBackend(client));
                    }
                });

        backend = bs.connect(master.host, master.port).syncUninterruptibly().channel();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        backend.writeAndFlush(msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (backend != null) backend.close();
    }
}
