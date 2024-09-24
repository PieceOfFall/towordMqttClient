package com.toword.towordmqttclient.handler;

import com.toword.towordmqttclient.util.MqttUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.mqtt.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;


@Component
@RequiredArgsConstructor
public class MqttMsgHandler  extends SimpleChannelInboundHandler<MqttMessage> {

    private final MqttUtil mqttUtil;

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MqttMessage msg){
        if (msg.fixedHeader().messageType() == MqttMessageType.CONNACK) {
            System.out.println("Connected to MQTT broker");
            mqttUtil.subscribe(channelHandlerContext,"123");
        } else if (msg.fixedHeader().messageType() == MqttMessageType.PUBLISH) {
            MqttPublishMessage publishMessage = (MqttPublishMessage) msg;
            ByteBuf payload = publishMessage.payload();
            String payloadStr = payload.toString(payload.readerIndex(),payload.readableBytes(), StandardCharsets.UTF_8);
            System.out.println(payloadStr);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        // 发送连接请求
        MqttConnectMessage connectMessage = MqttMessageBuilders.connect()
                .clientId("123")
                .cleanSession(true)
                .keepAlive(60)
                .build();

        ctx.writeAndFlush(connectMessage);
    }

}
