package com.infy.facebook_group3.dto;

import lombok.Data;

@Data
public class JwtResponse {
	private String token;
	private long userId;
	
	
	public JwtResponse(String token, long userId) {
		this.token=token;
		this.userId=userId;
	}
}
