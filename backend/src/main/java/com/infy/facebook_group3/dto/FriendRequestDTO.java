package com.infy.facebook_group3.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FriendRequestDTO {

	private Long requestId;
	
	@NotNull(message="{friendRequest.senderId.notNull}")
	private Long senderId;
	
	@NotNull(message="{friendRequest.receiverId.notNull}")
	private Long receiverId;
	
	private String status;
	
	@NotNull(message="{friendRequest.createdAt.notNull}")
	private LocalDateTime createdAt;
	
	private LocalDateTime updatedAt;
}
