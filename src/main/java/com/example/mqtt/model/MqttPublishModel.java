package com.example.mqtt.model;


import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class MqttPublishModel {

    @NotNull
    @Size(min = 1, max = 255)
    private String topic;

    @NotNull
    @Size(min = 1, max = 255)
    private String message;

    @NotNull
    private Boolean retained;

    @NotNull(message = "qos should not be null")
    private Integer qos;
}
