package com.infy.facebook_group3.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.infy.facebook_group3.entity.Post;
import com.infy.facebook_group3.entity.PostPrivacy;
import com.infy.facebook_group3.entity.User;

public interface PostPrivacyRepository extends JpaRepository<PostPrivacy, Long>{
	
	List<PostPrivacy> findByPost(Post post);
	Optional<PostPrivacy> findByPostAndUser(Post post,User user);
	List<PostPrivacy> findByUser(User user);
	boolean existsByPostAndUser(Post post, User user);
}
