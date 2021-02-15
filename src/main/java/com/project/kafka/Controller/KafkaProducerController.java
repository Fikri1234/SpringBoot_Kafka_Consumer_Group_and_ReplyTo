package com.project.kafka.Controller;


import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.IntStream;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.project.kafka.DTO.RequestMathDTO;

@RestController
public class KafkaProducerController {
	
	Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private KafkaTemplate<String, Object> template;
	
	@Autowired
	private ReplyingKafkaTemplate<String, Object, Object> replyingKafkaTemplate;
	
	@Value(value = "${spring.kafka.template.default-topic}")
    private String topicName;
	
	@Value("${spring.kafka.template.reply-topic}")
    private String replyTopic;
	
	@PostMapping(value = "send-group/")
	public ResponseEntity<?> send(@RequestBody RequestMathDTO dto) throws Exception {
		
		DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(ZoneId.from(ZoneOffset.UTC));
		Instant instant = Instant.now();
		String dateStr = formatter.format( instant );
		
		dto.setDateStr(dateStr);
		
		// loop produce to consumer n times
		IntStream.range(0, 9)
				.forEach(i -> this.template.send(topicName, String.valueOf(i), dto));

		return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
	}
	
	@PostMapping(value = "send/")
	public ResponseEntity<?> sendGroup(@RequestBody RequestMathDTO dto) throws Exception {
		
		DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(ZoneId.from(ZoneOffset.UTC));
		Instant instant = Instant.now();
		String dateStr = formatter.format( instant );
		
		dto.setDateStr(dateStr);
		ListenableFuture<SendResult<String, Object>> future = this.template.send(topicName, dto);
		
		future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
	        @Override
	        public void onSuccess(SendResult<String, Object> result) {
	            log.info("Sent message: " + dto.getMessage() + " with offset: " + result.getRecordMetadata().offset());
	        }

	        @Override
	        public void onFailure(Throwable ex) {
	            log.error("Unable to send message : " + dto.getMessage(), ex);
	        }
		});
		return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
	}
	
	@PostMapping("send-reply/")
    public ResponseEntity<?> getObject(@RequestBody RequestMathDTO dto) {
		
		try {
			
			DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME.withZone(ZoneId.from(ZoneOffset.UTC));
			Instant instant = Instant.now();
			String dateStr = formatter.format( instant );
			
			dto.setDateStr(dateStr);
			ProducerRecord<String, Object> record = new ProducerRecord<String, Object>(topicName, dto);
			// set reply topic in header
			record.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, replyTopic.getBytes()));
			// post in kafka topic
			RequestReplyFuture<String, Object, Object> sendAndReceive = replyingKafkaTemplate.sendAndReceive(record);

			// confirm if producer produced successfully
			SendResult<String, Object> sendResult = sendAndReceive.getSendFuture().get(10, TimeUnit.SECONDS);
			log.info("sendResult: {}",sendResult.getRecordMetadata());
			
			//print all headers
			sendResult.getProducerRecord().headers().forEach(header -> System.out.println(header.key() + ":" + header.value().toString()));
			
			// get consumer record
			ConsumerRecord<String, Object> response = sendAndReceive.get(10, TimeUnit.SECONDS);
			log.info("response: {} topic: {}",response.value(), response.topic());
			
			// return consumer value
	        return new ResponseEntity<>(response.value(), HttpStatus.OK);
	        
		} catch (InterruptedException e) {
			e.printStackTrace();
			log.info("InterruptedException: {}",e.getMessage());
			return new ResponseEntity<>("gagal InterruptedException", HttpStatus.OK);
		} catch (ExecutionException e) {
			e.printStackTrace();
			log.info("ExecutionException: {}",e.getMessage());
			return new ResponseEntity<>("gagal ExecutionException", HttpStatus.OK);
		} catch (TimeoutException e) {
			e.printStackTrace();
			log.info("TimeoutException: {}",e.getMessage());
			return new ResponseEntity<>("gagal TimeoutException", HttpStatus.OK);
		}
    }

}
