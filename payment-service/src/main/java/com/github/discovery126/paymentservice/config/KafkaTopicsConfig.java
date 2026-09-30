package com.github.discovery126.paymentservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicsConfig {
    @Bean
    public NewTopic paymentCompletedTopic(@Value("${kafka.topics.payment-completed}")
                                              String paymentCompletedTopic
    ) {
        return TopicBuilder.name(paymentCompletedTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }
    @Bean
    public NewTopic paymentFailedTopic(@Value("${kafka.topics.payment-failed}")
                                           String paymentFailedTopic) {
        return TopicBuilder.name(paymentFailedTopic)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
