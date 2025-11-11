package com.iexxk.proxy;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class L4ProxyBackend extends ChannelInboundHandlerAdapter {

    private final Channel client;

    public L4ProxyBackend(Channel client) {
        this.client = client;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        client.writeAndFlush(msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (client != null) client.close();
    }
}
