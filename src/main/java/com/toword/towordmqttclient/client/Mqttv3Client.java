package com.toword.towordmqttclient.client;

import com.toword.towordmqttclient.callback.CtrlMqttCallback;
import io.github.netty.mqtt.client.MqttClient;
import io.github.netty.mqtt.client.MqttClientFactory;
import io.github.netty.mqtt.client.MqttConnectParameter;
import io.github.netty.mqtt.client.constant.MqttVersion;
import io.github.netty.mqtt.client.msg.MqttWillMsg;
import io.netty.handler.codec.mqtt.MqttQoS;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Mqttv3Client {
    private final MqttClientFactory mqttClientFactory;
    private final CtrlMqttCallback ctrlMqttCallback;

    @Value("${mqtt.client-id}")
    private String clientId;

    @Value("${mqtt.ip}")
    private String ip;

    @Value("${mqtt.port}")
    private Integer port;

    @Value("${mqtt.qos}")
    private Integer qos;

    @Value("${mqtt.subscribe}")
    private String subscribe;

    @PostConstruct
    public void connect() {
        //创建连接参数，设置客户端ID
        MqttConnectParameter mqttConnectParameter = new MqttConnectParameter(clientId);
        //设置客户端版本（默认为3.1.1）
        mqttConnectParameter.setMqttVersion(MqttVersion.MQTT_3_1_1);
        //是否自动重连
        mqttConnectParameter.setAutoReconnect(true);
        //Host
        mqttConnectParameter.setHost(ip);
        //端口
        mqttConnectParameter.setPort(port);
        //是否使用SSL/TLS
        mqttConnectParameter.setSsl(false);
        //遗嘱消息
        MqttWillMsg mqttWillMsg = new MqttWillMsg("backend-disconnect", new byte[]{}, MqttQoS.EXACTLY_ONCE);
        mqttConnectParameter.setWillMsg(mqttWillMsg);
        //是否清除会话
        mqttConnectParameter.setCleanSession(true);
        //心跳间隔
        mqttConnectParameter.setKeepAliveTimeSeconds(60);
        //连接超时时间
        mqttConnectParameter.setConnectTimeoutSeconds(30);
        //创建一个客户端
        MqttClient mqttClient = mqttClientFactory.createMqttClient(mqttConnectParameter);
        mqttClient.addMqttCallback(ctrlMqttCallback);

        mqttClient.connect();

        mqttClient.subscribe(subscribe,MqttQoS.valueOf(qos));
    }
}
