package com.example.demo.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KafkaPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;


    public KafkaPublisher(KafkaTemplate<String, String> kafkaTemplate) { // Changed from KafkaPublisherService to KafkaPublisher
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String topic, String message) {
        kafkaTemplate.send(topic, message);
    }
}
