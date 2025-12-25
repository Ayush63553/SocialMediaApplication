package com.infy.facebook_group3.service;

import java.util.List;

import com.infy.facebook_group3.dto.PostDTO;
import com.infy.facebook_group3.exception.FacebookException;

public interface PostService  {
	
	public PostDTO createPost(Long userId, PostDTO postDTO)throws FacebookException;
	 
	 public List<PostDTO> getPostsByUser(Long userId)throws FacebookException ;
	 
	 public List<PostDTO> getAllPosts();
	 
	 public void deletePost(Long id);
	 
	 public PostDTO updatePost(Long postId,PostDTO postDTO)throws FacebookException; 
	 
	 public PostDTO getPostById(Long postId)throws FacebookException;
	 
	 public PostDTO getPostWithLikes(Long postId) throws FacebookException;

	 public List<PostDTO> getFeedForUser(Long userId) throws FacebookException;
	 
	 public List<PostDTO> getPostsByProfile(Long userId, Long friendId) throws FacebookException;
}
