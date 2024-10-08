package com.toword.towordmqttclient.callback;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.toword.towordmqttclient.client.TcpClient;
import com.toword.towordmqttclient.vo.TouchMsg;
import io.github.netty.mqtt.client.callback.MqttCallback;
import io.github.netty.mqtt.client.callback.MqttReceiveCallbackResult;
import io.netty.buffer.ByteBuf;
import jakarta.annotation.PostConstruct;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Component
@ConfigurationProperties(prefix = "toword")
public class CtrlMqttCallback implements MqttCallback {

    private final ObjectMapper objectMapper;
    private final TcpClient tcpClient;

    @Autowired
    public CtrlMqttCallback(ObjectMapper objectMapper,TcpClient tcpClient){
        this.tcpClient = tcpClient;
        this.objectMapper = objectMapper;
    }

    @Setter
    private  List<String> pcList;
    @Setter
    private Integer shutdownPort;
    @Setter
    private String lightIp;
    @Setter
    private String lightOnCmd;
    @Setter
    private String lightOffCmd;
    @Setter
    private String mediaIp;
    @Setter
    private String mediaOnCmd;
    @Setter
    private Integer lightPort;
    @Setter
    private Integer mediaPort;
    @Setter
    private Integer screenPort;
    @Setter
    private List<String> screenList;
    @Setter
    private String screenPoweronCommand;
    @Setter
    private String screenPoweroffCommand;
    @Setter
    private Integer sequentialPort;
    @Setter
    private String sequentialIp;
    @Setter
    private List<String> sequentialPoweronCommandList;
    @Setter
    private List<String> sequentialPoweroffCommandList;

    private Integer sequentialTime;
    private static long lastStopMills;

    @PostConstruct
    void p(){
        System.out.println(screenList);
    }


    @Override
    public void messageReceiveCallback(MqttReceiveCallbackResult receiveCallbackResult) {
        String msg = new String(receiveCallbackResult.getPayload(), StandardCharsets.UTF_8);
        try {
            TouchMsg touchMsg = objectMapper.readValue(msg, TouchMsg.class);
            String lightOperation = touchMsg.getLight();

            String lightCmd = "1".equals(lightOperation) ?
                    lightOnCmd : "0".equals(lightOperation) ?
                    lightOffCmd : null;

            if(lightCmd != null) {
                tcpClient.sendMsg(lightIp,lightPort,TcpClient.hexStringToByteBuf(lightCmd));
            }

            if("0".equals(touchMsg.getMedia())) {
                lastStopMills = System.currentTimeMillis();
                for (String ip : pcList) {
                        tcpClient.sendMsg(ip,shutdownPort,"shutdown");
                }
                for (String ip : screenList) {
                    tcpClient.sendMsg(ip,screenPort,TcpClient.hexStringToByteBuf(screenPoweroffCommand));
                }

                tcpClient.sendHexStrList(sequentialIp,sequentialPort,sequentialPoweroffCommandList,sequentialTime);

            } else if("1".equals(touchMsg.getMedia()))  {
                long currentMill = System.currentTimeMillis();
                if(currentMill - lastStopMills > 30 * 1000) {
                    String mediaCmd = mediaOnCmd;
                    tcpClient.sendMsg(mediaIp,mediaPort,TcpClient.hexStringToByteBuf(mediaCmd));
                } else {
                    log.info("wait for shutdown," + (30 - (currentMill - lastStopMills)/1000) + " sec left..." );
                }

                for (String ip : screenList) {
                    tcpClient.sendMsg(ip,screenPort,TcpClient.hexStringToByteBuf(screenPoweronCommand));
                }

                tcpClient.sendHexStrList(sequentialIp,sequentialPort,sequentialPoweronCommandList,sequentialTime);
            }

        } catch (JsonProcessingException e) {
            log.error("消息序列化错误:\n {}",e.getMessage());
        }
        MqttCallback.super.messageReceiveCallback(receiveCallbackResult);
    }
}
