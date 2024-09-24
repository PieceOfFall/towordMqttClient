package com.toword.towordmqttclient.config;

import io.github.netty.mqtt.client.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqttConfig {
    @Bean
    public MqttClientFactory initClientFactory() {
        //创建MQTT全局配置器（也可以不创建）
        MqttConfiguration mqttConfiguration = new MqttConfiguration(2);
        //创建MQTT客户端工厂
        return new DefaultMqttClientFactory(mqttConfiguration);

    }

}
