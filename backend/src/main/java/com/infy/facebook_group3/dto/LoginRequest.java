package com.infy.facebook_group3.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {
	
	@NotBlank(message="{login.username.notBlank}")
	private String username;
	
	@NotBlank(message="{login.password.notBlank}")
	@Size(min=8, max=20, message="{login.password.size}")
	private String password;
}
