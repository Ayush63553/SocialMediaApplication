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
public class LikeDTO {

	private Long likeId;
	
	@NotNull(message="{like.postId.notNull}")
	private Long postId;
	
	@NotNull(message="{like.userId.notNull}")
	private Long userId;
	private String fullName;
	private Integer likeCount;
	private LocalDateTime createdAt;
	
	
}
