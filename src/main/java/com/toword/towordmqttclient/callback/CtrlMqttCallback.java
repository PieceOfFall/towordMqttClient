package com.toword.towordmqttclient.callback;

import io.github.netty.mqtt.client.callback.MqttCallback;
import io.github.netty.mqtt.client.callback.MqttReceiveCallbackResult;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class CtrlMqttCallback implements MqttCallback {
    @Override
    public void messageReceiveCallback(MqttReceiveCallbackResult receiveCallbackResult) {
        String msg = new String(receiveCallbackResult.getPayload(), StandardCharsets.UTF_8);
        System.out.println(msg);
        MqttCallback.super.messageReceiveCallback(receiveCallbackResult);
    }
}
