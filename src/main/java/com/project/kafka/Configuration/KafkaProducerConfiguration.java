package com.project.kafka.Configuration;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
public class KafkaProducerConfiguration {
	
	@Value("${spring.kafka.template.reply-topic}")
    private String replyTopic;
	
	@Autowired
	private KafkaProperties kafkaProperties;
	
	@Bean
	public ProducerFactory<String, Object> producerFactory() {
		Map<String, Object> prop = new HashMap<>(kafkaProperties.buildProducerProperties());
		prop.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		prop.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
		
		return new DefaultKafkaProducerFactory<>(prop);
	}
	
	@Primary
	@Bean
	public KafkaTemplate<String, Object> kafkaTemplate() {
		return new KafkaTemplate<>(producerFactory());
	}
	  
	  
	  
	
//	@Bean
//	public ReplyingKafkaTemplate<String, Object, Object> replyingKafkaTemplate(ProducerFactory<String, Object> pfRequest,
//			ConcurrentKafkaListenerContainerFactory<String, Object> pfResult) {
//		ConcurrentMessageListenerContainer<String, Object> replyContainer = pfResult.createContainer(replyTopic);
//		replyContainer.getContainerProperties().setMissingTopicsFatal(false);
//		replyContainer.getContainerProperties().setGroupId(kafkaProperties.getConsumer().getGroupId());
//		
//		return new ReplyingKafkaTemplate<>(pfRequest, replyContainer);
//	}
//	
//	@Bean
//	public KafkaTemplate<String, Object> replyKafkaTemplate(ProducerFactory<String, Object> pfRequest,
//			ConcurrentKafkaListenerContainerFactory<String, Object> pfResult) {
//		KafkaTemplate<String, Object> replyTemplate = new KafkaTemplate<>(pfRequest);
//		pfResult.getContainerProperties().setMissingTopicsFatal(false);
//		pfResult.setReplyTemplate(replyTemplate);
//		
//		return replyTemplate;
//	}

}
