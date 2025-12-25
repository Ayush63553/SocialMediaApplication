package com.infy.facebook_group3.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.infy.facebook_group3.entity.Like;

@Repository
public interface LikeRepository extends JpaRepository<Like ,Long> {
	List<Like> findByPost_PostId(Long postId);
	List<Like> findByUser_UserId(Long userId);
	Optional<Like> findByPost_PostIdAndUser_UserId(Long postId,Long userId);
	Optional<Like>findByUser_UserIdAndPost_PostId(Long userId,Long postId);
}
