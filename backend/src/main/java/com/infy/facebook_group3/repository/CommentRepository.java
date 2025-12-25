package com.infy.facebook_group3.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.infy.facebook_group3.entity.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
	List<Comment> findByPost_PostId(Long postId);

	List<Comment> findByUser_UserId(Long userId);

	// Delete a comment only if it belongs to the given user
	void deleteByCommentIdAndUser_UserId(Long commentId, Long userId);
}
