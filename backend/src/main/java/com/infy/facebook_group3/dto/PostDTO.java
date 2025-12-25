package com.infy.facebook_group3.dto;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO {

	private Long postId;
	
	@NotNull(message="{post.userId.notNull}")
	private Long userId;
	
	@NotBlank(message="{post.content.notBlank}")
	@Size(max=500, message="{post.content.size}")
	private String content;
	
	private String mediaUrl;
	
	@Pattern(regexp="IMAGE|VIDEO", message="{post.mediaType.pattern}")
	private String mediaType;
	
	@NotBlank(message="{post.privacy.notBlank}")
	@Pattern(regexp="PUBLIC|FRIENDS|PRIVATE|CUSTOM", message="{post.privacy.pattern}")
	private String privacy;
	
	private Integer likeCount;
	private String username;
	private List<String>likedByUsers;
	private LocalDateTime createdAt = LocalDateTime.now();
	private LocalDateTime updatedAt = LocalDateTime.now();
	
	private List<CommentDTO> comments;
	
	private String profilePictureUrl;
}
