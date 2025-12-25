package com.infy.facebook_group3.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.infy.facebook_group3.entity.BlockedUsers;

public interface BlockedUsersRepository extends JpaRepository<BlockedUsers, Long>{
	Optional<BlockedUsers> findByUser_UserIdAndBlockedUser_UserId(Long userId, Long blockedUserId);
	
	List<BlockedUsers> findByUser_UserId(Long userId);
	List<BlockedUsers> findByBlockedUser_UserId(Long blockedUserId);
	
	Boolean existsByUser_UserIdAndBlockedUser_UserId(Long userId, Long blockedUserId);
	Boolean existsByUser_UserId(Long userId);
	Boolean existsByBlockedUser_UserId(Long blockedUserId);
	
}
