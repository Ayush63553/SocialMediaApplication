package com.infy.facebook_group3.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserValidationResponse {

	private Long userId;
	private String username;
	private String email;
	
}
