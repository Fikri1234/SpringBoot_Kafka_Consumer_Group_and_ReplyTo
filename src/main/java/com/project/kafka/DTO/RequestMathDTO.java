package com.project.kafka.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RequestMathDTO {
	
	private String message;
	private int grade;
	private int value;
	private String dateStr;

}
