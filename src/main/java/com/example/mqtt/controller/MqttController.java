package com.example.mqtt.controller;

import com.example.mqtt.config.Mqtt;
import com.example.mqtt.exceptions.ExceptionMessages;
import com.example.mqtt.exceptions.MqttException;
import com.example.mqtt.model.MqttPublishModel;

import com.example.mqtt.model.MqttSubscribeModel;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(value = "/api/mqtt")
public class MqttController {

    @PostMapping("/publish")
    public void publishMessage(@RequestBody @Valid MqttPublishModel mqttPublishModel, BindingResult bindingResult)
            throws org.eclipse.paho.client.mqttv3.MqttException {
        if (bindingResult.hasErrors()) {
            throw new MqttException(ExceptionMessages.SOME_PARAMETERS_INVALID);
        }

        MqttMessage mqttMessage = new MqttMessage(mqttPublishModel.getMessage().getBytes());
        mqttMessage.setQos(mqttPublishModel.getQos());
        mqttMessage.setRetained(mqttPublishModel.getRetained());

        Mqtt.getInstance().publish(mqttPublishModel.getTopic(), mqttMessage);
    }

    @GetMapping("/subscribe")
    public List<MqttSubscribeModel> subscribeChannel(@RequestParam(value = "topic") String topic,
                                                     @RequestParam(value = "wait_millis") Integer waitMillis)
            throws InterruptedException, org.eclipse.paho.client.mqttv3.MqttException {
        List<MqttSubscribeModel> messages = new ArrayList<>();
        CountDownLatch countDownLatch = new CountDownLatch(10);
        Mqtt.getInstance().subscribeWithResponse(topic,
                new IMqttMessageListener() {
                    @Override
                    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                        MqttSubscribeModel mqttSubscribeModel = new MqttSubscribeModel();
                        mqttSubscribeModel.setId(mqttMessage.getId());
                        mqttSubscribeModel.setMessage(new String(mqttMessage.getPayload()));
                        mqttSubscribeModel.setQos(mqttMessage.getQos());
                        messages.add(mqttSubscribeModel);
                        countDownLatch.countDown();
                    }
                });

        countDownLatch.await(waitMillis, TimeUnit.MILLISECONDS);

        return messages;
    }
}
