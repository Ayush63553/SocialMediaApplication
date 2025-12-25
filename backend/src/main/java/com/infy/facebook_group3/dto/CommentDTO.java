package com.infy.facebook_group3.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDTO {

	private Long commentId;
	
	@NotNull(message="{comment.postId.notNull}")
	private Long postId;
	
	@NotNull(message="{comment.userId.notNull}")
	private Long userId;
	
	@NotBlank(message="{comment.content.notBlank}")
	@Size(max=255, message="{comment.content.size}")
	private String content;
	
	private String username;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	
	private String profilePictureUrl;
}
