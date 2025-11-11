package com.iexxk.proxy;

import com.iexxk.upstream.Upstream;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * TCP 层透传后端 Redis
 */
public class StatefullRedisProxyBackend {

    private final Channel backendChannel;

    public StatefullRedisProxyBackend(ChannelHandlerContext clientCtx, Upstream upstream) {
        NioEventLoopGroup group = new NioEventLoopGroup();

        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        // 将 Redis 返回透传给客户端
                        ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                            @Override
                            public void channelRead(ChannelHandlerContext ctx, Object msg) {
                                clientCtx.writeAndFlush(msg);
                            }

                            @Override
                            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
                                cause.printStackTrace();
                                ctx.close();
                            }
                        });
                    }
                });

        try {
            ChannelFuture f = b.connect(upstream.getHost(), upstream.getPort()).sync();
            backendChannel = f.channel();
        } catch (InterruptedException e) {
            throw new RuntimeException("Failed to connect to Redis backend", e);
        }
    }

    public Channel getBackendChannel() {
        return backendChannel;
    }
}