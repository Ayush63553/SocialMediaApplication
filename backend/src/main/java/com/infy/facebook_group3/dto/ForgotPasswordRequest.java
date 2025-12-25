package com.infy.facebook_group3.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ForgotPasswordRequest {
	
	@NotNull(message="{forgot.password.userId.notNull}")
	private Long userId;
	
	@NotBlank(message="{forgot.password.question.notBlank}")
	private String question;
	
	@NotBlank(message="{forgot.password.answer.notBlank}")
	private String answer;
}
