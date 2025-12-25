package com.infy.facebook_group3.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostPrivacyDTO {
	private Long privacyId;
	private Long postId;
	private Long userId;
}
