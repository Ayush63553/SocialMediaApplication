package com.infy.facebook_group3.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlockedUsersDTO {
	
	private Long blockedId;
	
	private Long userId;
	
	private Long blockedUserId;
	
	private LocalDateTime createdAt;
}
