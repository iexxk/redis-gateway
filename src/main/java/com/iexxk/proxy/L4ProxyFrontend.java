package com.iexxk.proxy;

import com.iexxk.upstream.Upstream;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * TCP 层透传客户端数据到 Redis
 */
public class L4ProxyFrontend extends ChannelInboundHandlerAdapter {

    private final Upstream upstream;
    private Channel backendChannel;

    public L4ProxyFrontend(Upstream upstream) {
        this.upstream = upstream;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        // 当客户端连接时，建立到后端 Redis 的连接
        StatefullRedisProxyBackend backendHandler = new StatefullRedisProxyBackend(ctx, upstream);
        backendChannel = backendHandler.getBackendChannel();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (backendChannel != null && backendChannel.isActive()) {
            // 直接写给后端 Redis
            backendChannel.writeAndFlush(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}