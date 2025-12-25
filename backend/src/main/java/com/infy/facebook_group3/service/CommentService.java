package com.infy.facebook_group3.service;

import java.util.List;

import com.infy.facebook_group3.dto.CommentDTO;

public interface CommentService {

	public CommentDTO addComment(CommentDTO dto) ;
	
	public List<CommentDTO> getCommentsByPost(Long postId);
	
	public List<CommentDTO> getCommentsByUser(Long userId);
	
	CommentDTO updateComment(Long commentId, Long userId, String newContent);
	
	void deleteComment(Long commentId, Long userId);
}
