package com.toword.towordmqttclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class TowordMqttClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(TowordMqttClientApplication.class, args);
    }

}
