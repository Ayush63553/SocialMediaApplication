package com.infy.facebook_group3.service;

import java.util.List;

import com.infy.facebook_group3.dto.BlockedUsersDTO;
import com.infy.facebook_group3.dto.UserDTO;
import com.infy.facebook_group3.entity.FriendRequest;
import com.infy.facebook_group3.exception.FacebookException;

public interface FriendRequestService {
	
	public FriendRequest sendRequest(Long senderId, Long receiverId) throws FacebookException;

    public FriendRequest acceptRequest(Long senderId,Long receiverId) throws FacebookException;

    public FriendRequest rejectRequest(Long senderId, Long receiverId) throws FacebookException;
    
    public void cancelRequest(Long senderId, Long receiverId) throws FacebookException;
    
    public List<FriendRequest> getReceivedRequests(Long userId) throws FacebookException;

    public List<FriendRequest> getSentRequests(Long userId) throws FacebookException;
    
    public List<UserDTO> getFriends(Long userId) throws FacebookException;
    
    public void unFriend(Long userId, Long friendId) throws FacebookException;
    
    public List<UserDTO> findFriends(Long userId) throws FacebookException;

    public List<UserDTO> findMutualFriends(Long userId,Long friendId) throws FacebookException;
    
    public UserDTO getProfile(Long userId, Long friendId) throws FacebookException;
    
    public String sendStatus(Long userId, Long friendId) throws FacebookException;
    
    public List<UserDTO> getBlockedUsers(Long userId) throws FacebookException;
    
    public UserDTO getBlockedUser(Long userId, Long blockedUserId) throws FacebookException;
    
    public BlockedUsersDTO blockUser(Long userId, Long blockedUserId) throws FacebookException;
    
    public void unBlockUser(Long userId, Long blockedUserId) throws FacebookException;
    
    public Boolean checkIfBlockedUserExists(Long userId, Long blockedUserId) throws FacebookException;
    
}
