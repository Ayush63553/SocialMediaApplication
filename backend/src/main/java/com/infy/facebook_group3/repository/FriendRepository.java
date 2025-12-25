package com.infy.facebook_group3.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.infy.facebook_group3.entity.Friend;

@Repository
public interface FriendRepository extends JpaRepository<Friend,Long> {
	List<Friend> findByUserId(Long userId);
	
	List<Friend> findByFriendId(Long friendId);
	
	Optional<Friend> findByUserIdAndFriendId(Long userId,Long friendId);
	
	Boolean existsByUserId(Long userId);
	Boolean existsByFriendId(Long friendId);
	Boolean existsByUserIdAndFriendId(Long userId, Long friendId);
	
	// Find all friend IDs for a given user
    @Query(value = "SELECT f.friend_id FROM friends f WHERE f.user_id = :userId " +
                   "UNION SELECT f.user_id FROM friends f WHERE f.friend_id = :userId",
           nativeQuery = true)
    List<Long> findFriendIdsOfUser(@Param("userId") Long userId);
}
