package com.infy.facebook_group3.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequest {
	
	@NotNull(message="{password.reset.userId.notNull}")
	private Long userId;
	
	@NotBlank(message="{password.reset.newPassword.notBlank}")
	@Size(min=8, max=20, message="{password.reset.newPassword.size}")
	@Pattern(regexp="^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!])(?!.*\\s).{8,20}$", message="{password.reset.newPassword.pattern}")
	private String newPassword;
}
