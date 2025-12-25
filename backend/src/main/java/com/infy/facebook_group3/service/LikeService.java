package com.infy.facebook_group3.service;

import java.util.List;

import com.infy.facebook_group3.dto.LikeDTO;
import com.infy.facebook_group3.exception.FacebookException;

public interface LikeService {

	public LikeDTO likePost(LikeDTO dto)throws FacebookException;
	
	public void unlikePost(Long postId, Long userId)throws FacebookException;
	
	public List<LikeDTO> getLikesByPost(Long postId)throws FacebookException;
	
	
}
