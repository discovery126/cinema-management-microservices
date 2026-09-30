package com.github.discovery126.bookingservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicsConfig {
    @Bean
    public NewTopic bookingCreatedTopic(@Value("${kafka.topics.booking-created}")
                                            String bookingCreatedTopic) {
        return TopicBuilder.name(bookingCreatedTopic)
                    .partitions(1)
                    .replicas(1)
                    .build();
        }
}
