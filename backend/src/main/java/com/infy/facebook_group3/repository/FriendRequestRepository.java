package com.infy.facebook_group3.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.infy.facebook_group3.entity.FriendRequest;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest,Long> {
	List<FriendRequest> findByReceiver_UserIdAndStatus(Long receiverId,FriendRequest.Status status);
	
	List<FriendRequest> findBySender_UserIdAndStatus(Long senderId,FriendRequest.Status status);
	
	List<FriendRequest> findBySender_UserId(Long senderId);
	
	Optional<FriendRequest> findBySender_UserIdAndReceiver_UserId(Long senderId,Long receiverId);
	
	Boolean existsBySender_UserIdAndReceiver_UserId(Long senderId, Long receiverId);
}
