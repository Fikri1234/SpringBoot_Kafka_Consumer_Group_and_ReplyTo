package com.project.kafka.Configuration;

import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Configuration
@EnableKafka
public class KafkaConsumerConfiguration {
	
	@Autowired
	private KafkaProperties kafkaProperties;
	
	@Value("${spring.kafka.template.reply-topic}")
    private String replyTopic;
	
	@Autowired
	private KafkaTemplate<String, Object> template;
     
//    // 1. Consume string data from Kafka Non Group
//    @Bean
//	public ConsumerFactory<String, String> consumerFactory() {
//		Map<String, Object> prop = new HashMap<>();
//		prop.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
//		prop.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//		prop.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//		prop.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
//		return new DefaultKafkaConsumerFactory<>(prop);
//	}
// 
//    @Bean
//    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
//        ConcurrentKafkaListenerContainerFactory<String, String> factory 
//            = new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(consumerFactory());
//        return factory;
//    }
//    
//    // 2. Consume object data from Kafka
//  	@Bean
//	public ConsumerFactory<String, Object> consumerFactoryObject() {
//		Map<String, Object> prop = new HashMap<>();
//		final JsonDeserializer<Object> jsonDeserializer = new JsonDeserializer<>();
//		jsonDeserializer.addTrustedPackages("*");
//
//		prop.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
//		prop.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//		prop.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, jsonDeserializer);
//		return new DefaultKafkaConsumerFactory<>(prop);
//	}
//
//	@Bean
//	public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerFactoryObject() {
//		ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
//		factory.setConsumerFactory(consumerFactoryObject());
//
//		return factory;
//	}
//	
//	// 3. Consume byte array data from Kafka
//	@Bean
//	public ConsumerFactory<String, byte[]> ConsumerFactoryByteArray() {
//		Map<String, Object> prop = new HashMap<>();
//
//		prop.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
//		prop.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//		prop.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, new ByteArrayDeserializer());
//		prop.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
//		return new DefaultKafkaConsumerFactory<>(kafkaProperties.buildConsumerProperties(), new StringDeserializer(),
//				new ByteArrayDeserializer());
//	}
//
//    @Bean
//	public ConcurrentKafkaListenerContainerFactory<String, byte[]> kafkaListenerFactoryByteArray() {
//		ConcurrentKafkaListenerContainerFactory<String, byte[]> factory = new ConcurrentKafkaListenerContainerFactory<>();
//		factory.setConsumerFactory(ConsumerFactoryByteArray());
//		
//		return factory;
//	}
	
	// 4. Consumer group string data from Kafka
	@Bean
    public ConsumerFactory<String, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
                kafkaProperties.buildConsumerProperties(), new StringDeserializer(), new StringDeserializer()
        );
    }

    @Bean
	public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory());

		return factory;
	}
	
	// 5. Consumer group object data from Kafka
    @Bean
	public ConsumerFactory<String, Object> consumerFactoryGroupObject() {
		final JsonDeserializer<Object> jsonDeserializer = new JsonDeserializer<>();
		jsonDeserializer.addTrustedPackages("*");
		return new DefaultKafkaConsumerFactory<>(kafkaProperties.buildConsumerProperties(), new StringDeserializer(),
				jsonDeserializer);
	}

    @Bean
	public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerFactoryGroupObject() {
		ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactoryGroupObject());

		return factory;
	}

	// 6. Consumer group byte array data from Kafka
	@Bean
	public ConsumerFactory<String, byte[]> ConsumerFactoryGroupByteArray() {
		return new DefaultKafkaConsumerFactory<>(kafkaProperties.buildConsumerProperties(), new StringDeserializer(),
				new ByteArrayDeserializer());
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, byte[]> kafkaListenerFactoryGroupByteArray() {
		ConcurrentKafkaListenerContainerFactory<String, byte[]> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(ConsumerFactoryGroupByteArray());
		return factory;
	}
	
	// Consumer reply to another topic
	@Bean
	public ReplyingKafkaTemplate<String, Object, Object> replyingKafkaTemplate(ProducerFactory<String, Object> pf,
			KafkaMessageListenerContainer<String, Object> container) {
		return new ReplyingKafkaTemplate<>(pf, container);

	}
	
	@Bean
	public KafkaMessageListenerContainer<String, Object> replyContainer(ConsumerFactory<String, Object> cf) {
		ContainerProperties containerProperties = new ContainerProperties(replyTopic);
		return new KafkaMessageListenerContainer<>(cf, containerProperties);
	}

	@Bean
	public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, Object>> kafkaListenerFactoryGroupObjectReplay() {
		ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactoryGroupObject());
		factory.setReplyTemplate(template);
		return factory;
	}
	
//	@Bean
//    public KafkaTemplate<String, Object> kafkaTemplateReply(ProducerFactory<String, Object> pf) {
//        return new KafkaTemplate<>(pf);
//    }
//	
//	@Bean
//	public KafkaListenerContainerFactory<?> kafkaListenerFactoryGroupObjectReplay(ProducerFactory<String, Object> pf) {
//		ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
//		factory.setConsumerFactory(consumerFactoryGroupObject());
//		factory.setReplyTemplate(kafkaTemplateReply(pf));
//		return factory;
//	}
	
	
	
	
//	@Bean
//    public ReplyingKafkaTemplate<String, Object, Object> replyingKafkaTemplate(
//            ProducerFactory<String, Object> pf,
//            ConcurrentMessageListenerContainer<String, Object> repliesContainer) {
//
//        return new ReplyingKafkaTemplate<>(pf, repliesContainer);
//    }
//
//    @Bean
//    public ConcurrentMessageListenerContainer<String, Object> kafkaListenerFactoryGroupObjectReplay(
//            ConcurrentKafkaListenerContainerFactory<String, Object> containerFactory) {
//
//        ConcurrentMessageListenerContainer<String, Object> repliesContainer =
//                containerFactory.createContainer(replyTopic);
////        repliesContainer.getContainerProperties().setGroupId("repliesGroup");
//        repliesContainer.setAutoStartup(false);
//        return repliesContainer;
//    }

}
