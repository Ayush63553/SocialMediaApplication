package com.infy.facebook_group3.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SecurityQuestionRequest {
	
	@NotNull(message="{security.question.userId.notNull}")
	private Long userId;
	
	@NotBlank(message="{security.question.question.notBlank}")
	private String question;
	
	@NotBlank(message="{security.question.answer.notBlank}")
	@Size(min=2, max=255, message="{security.question.answer.size}")
	private String answer;
}