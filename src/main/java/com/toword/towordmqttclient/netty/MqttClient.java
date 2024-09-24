package com.toword.towordmqttclient.netty;

import com.toword.towordmqttclient.handler.MqttMsgHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.mqtt.MqttDecoder;
import io.netty.handler.codec.mqtt.MqttEncoder;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class MqttClient {
    @Value("${mqtt.ip}")
    private String ip;
    @Value("${mqtt.port}")
    private Integer port;

    private final MqttMsgHandler mqttMsgHandler;

    @PostConstruct
    public void connect() throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        DefaultEventLoopGroup handlerGroup = new DefaultEventLoopGroup(16);

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ch.pipeline().addLast(new MqttDecoder());
                        ch.pipeline().addLast(MqttEncoder.INSTANCE);
                        ch.pipeline().addLast(handlerGroup, mqttMsgHandler);
                    }
                });

        ChannelFuture future = bootstrap.connect(ip, port).sync();
        ChannelFuture closeFuture = future.channel().closeFuture();
        closeFuture.addListener(e -> {
            group.shutdownGracefully();
            handlerGroup.shutdownGracefully();
        });
    }
}

