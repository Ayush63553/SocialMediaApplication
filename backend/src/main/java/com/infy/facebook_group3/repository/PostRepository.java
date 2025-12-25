package com.infy.facebook_group3.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.infy.facebook_group3.entity.Post;
import com.infy.facebook_group3.entity.User;

@Repository
public interface PostRepository extends JpaRepository<Post,Long>{
	
	List<Post> findByUser_UserId(Long userId);
	List<Post> findByPrivacy(String privacy);
	List<Post>findByUser(User user);
	List<Post>findByUserInOrderByCreatedAtDesc(List<User> friends);
//	@Query(value = "SELECT p.* FROM posts p " +
//            "WHERE p.user_id = :userId " +
//            "OR p.user_id IN (" +
//            "   SELECT f.friend_id FROM friends f WHERE f.user_id = :userId " +
//            "   UNION " +
//            "   SELECT f.user_id FROM friends f WHERE f.friend_id = :userId" +
//            ") " +
//            "ORDER BY p.created_at DESC",
	@Query(value = "SELECT p.* FROM posts p " +
            "WHERE p.user_id = :userId " +
            "OR p.user_id IN (" +
            "   SELECT u.user_id FROM users u" +
            ") " +
            "ORDER BY p.created_at DESC",
    nativeQuery = true)
	List<Post> findFeedPosts(@Param("userId") Long userId);
}
