package com.project.kafka.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MessageKafkaDTO {
	
	private final String message;
    private final int identifier;
    
	public MessageKafkaDTO(@JsonProperty("message") final String message, 
			@JsonProperty("identifier") final int identifier) {
		super();
		this.message = message;
		this.identifier = identifier;
	}
	
	public String getMessage() {
		return message;
	}
	public int getIdentifier() {
		return identifier;
	}

	@Override
	public String toString() {
		return "MessageKafkaDTO [message=" + message + ", identifier=" + identifier + "]";
	}

}
