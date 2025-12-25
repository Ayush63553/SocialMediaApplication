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
public class FriendDTO {
	
	@NotNull(message="{friend.userId.notNull}")
	private Long userId;
	
	@NotNull(message="{friend.friendId.notNull}")
	private Long friendId;
	
	@NotNull(message="{friend.createdAt.notNull}")
	private LocalDateTime createdAt;
}
