package com.project.kafka.Configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfiguration {
	
	@Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;
 
    @Value(value = "${spring.kafka.template.default-topic}")
    private String topicName;
 
    @Bean
    public NewTopic generalTopic() {
    	return TopicBuilder
    			.name(topicName)
    			.partitions(3)
    			.replicas(2)
    			.build();
    }

}
