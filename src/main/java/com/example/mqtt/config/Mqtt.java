package com.example.mqtt.config;

import org.eclipse.paho.client.mqttv3.*;

public class Mqtt {

    private static final String MQTT_PUBLISHER_ID = "spring-server";
    private static final String MQTT_SERVER_ADDRESS = "tcp://localhost:1883";
    private static IMqttClient instance;

    public Mqtt() {
    }

    public static IMqttClient getInstance() {

        try {
            if (instance == null) {
                instance = new MqttClient(MQTT_SERVER_ADDRESS, MQTT_PUBLISHER_ID);
            }

            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(10);
            MqttTopic topic = new MqttTopic("myTopic", null);
            options.setWill(topic,"Hey guys, I am out!".getBytes(),1,false);

            if (!instance.isConnected()) {
                instance.connect(options);
            }

        } catch (MqttException ex) {
            ex.printStackTrace();
        }

        return instance;
    }
}
