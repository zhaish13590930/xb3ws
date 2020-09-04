package com.gw.xb3ws.server;

import com.gw.xb3ws.server.handler.WebSocketServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;

/**
 * @datetime:2020/9/4 13:09
 * @author: zhaish
 * @desc:
 **/
@Component
public class NettyWebSocketServer {
    @Value("${ws.port}")
    private int port;
    static final boolean SSL = System.getProperty("ssl") != null;

    public void start() throws CertificateException, SSLException {
        final SslContext sslCtx;
        if (SSL) {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        } else {
            sslCtx = null;
        }

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try{
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workGroup).channel(NioServerSocketChannel.class);
            b.option(ChannelOption.SO_BACKLOG, 1024);
            b.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);


            b.childOption(ChannelOption.SO_KEEPALIVE, true);////连接保活机制，2小时没数据，发送侦测报文保活
            b.childOption(ChannelOption.TCP_NODELAY, true);//不延迟，消息立即发送
            b.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
            b.childHandler(new WebSocketServerInitializer(sslCtx));
            Channel ch = b.bind(port).sync().channel();
            System.out.println("Open your web browser and navigate to " +
                    (SSL? "https" : "http") + "://127.0.0.1:" + port + '/');
            ch.closeFuture().sync();

        }catch (Throwable t){
            t.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
