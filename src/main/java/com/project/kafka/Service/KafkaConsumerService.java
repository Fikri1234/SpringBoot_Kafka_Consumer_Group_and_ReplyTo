package com.project.kafka.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.stream.StreamSupport;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaNull;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

import com.project.kafka.DTO.RequestMathDTO;
import com.project.kafka.DTO.ResultMathDTO;

@Service
public class KafkaConsumerService {
	
	Logger log = LoggerFactory.getLogger(getClass());
	
	private static String typeIdHeader(Headers headers) {
        return StreamSupport.stream(headers.spliterator(), false)
                .filter(header -> header.key().equals("__TypeId__"))
                .findFirst().map(header -> new String(header.value())).orElse("N/A");
    }
	
	@KafkaListener(topics = "${spring.kafka.template.default-topic}",
			groupId = "${spring.kafka.consumer.group-id}")
    public void consumeGroupAsString(ConsumerRecord<String, String> cr, @Payload String payload) {
        log.info("[String] received key {}: Type [{}] | Payload: {} | Record: {}", cr.key(),
                typeIdHeader(cr.headers()), payload, cr.toString());
    }
	
	@KafkaListener(topics = "${spring.kafka.template.default-topic}",
			groupId = "${spring.kafka.consumer.group-id}",
			containerFactory = "kafkaListenerFactoryGroupObject")
	public void consumeGroupAsObject(ConsumerRecord<String, RequestMathDTO> cr, @Payload RequestMathDTO dto) {
		log.info("[JSON] received key {}: Type [{}] | Payload: {} | Record: {}", cr.key(),
                typeIdHeader(cr.headers()), dto, cr.toString());
	}

    @KafkaListener(topics = "${spring.kafka.template.default-topic}",
    		groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerFactoryGroupByteArray")
    public void consumeGroupAsByteArray(ConsumerRecord<String, byte[]> cr, @Payload byte[] payload) {
        log.info("[ByteArray] received key {}: Type [{}] | Payload: {} | Record: {}", cr.key(),
                typeIdHeader(cr.headers()), payload, cr.toString());
    }
    
    @KafkaListener(topics = "${spring.kafka.template.default-topic}",
    		groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerFactoryGroupObjectReplay")
    @SendTo
    public Object handleReply(RequestMathDTO dto) {
    	log.info("replayy");
    	if (dto != null) {
    		DateTimeFormatter formatter =DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(ZoneId.from(ZoneOffset.UTC));
			Instant instant = Instant.now();
			String dateStr = formatter.format( instant );
			
	    	ResultMathDTO res = new ResultMathDTO();
	    	res.setMessage("Get request message ==> " + dto.getMessage());
	    	res.setGrade(dto.getGrade());
	    	res.setSum(dto.getValue() > dto.getGrade() ? dto.getValue() : dto.getValue() * 10);
	    	res.setDateStr(dateStr);
	    	return res;
    	} else {
    		return KafkaNull.INSTANCE;
    	}
    }

}
