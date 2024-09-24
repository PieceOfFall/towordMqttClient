package com.toword.towordmqttclient.util;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.mqtt.MqttMessageBuilders;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.codec.mqtt.MqttSubscribeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MqttUtil {

    @Value("${mqtt.qos}")
    private Integer qos;

    public void subscribe(ChannelHandlerContext ctx, String topic) {
        // 发送订阅请求
        MqttSubscribeMessage subscribeMessage = MqttMessageBuilders.subscribe()
                .messageId(1)
                .addSubscription(MqttQoS.valueOf(qos),topic)
                .build();
        ctx.writeAndFlush(subscribeMessage);
    }
}
