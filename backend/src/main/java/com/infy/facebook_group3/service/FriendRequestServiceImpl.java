package com.infy.facebook_group3.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.infy.facebook_group3.dto.BlockedUsersDTO;
import com.infy.facebook_group3.dto.UserDTO;
import com.infy.facebook_group3.entity.BlockedUsers;
import com.infy.facebook_group3.entity.Friend;
import com.infy.facebook_group3.entity.FriendRequest;
import com.infy.facebook_group3.entity.User;
import com.infy.facebook_group3.exception.FacebookException;
import com.infy.facebook_group3.repository.BlockedUsersRepository;
import com.infy.facebook_group3.repository.FriendRepository;
import com.infy.facebook_group3.repository.FriendRequestRepository;
import com.infy.facebook_group3.repository.UserRepository;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FriendRequestServiceImpl implements FriendRequestService {
	
	private static final String USER_NOT_FOUND="Service.USER_NOT_FOUND";
	private static final String REQUEST_ACCEPTED="Service.Request_Accepted";
	
	@Autowired
    private FriendRequestRepository friendRequestRepository;

	@Autowired
    private UserRepository userRepository;
	@Autowired
	private FriendRepository friendRepository;
	@Autowired
	private BlockedUsersRepository blockedUsersRepository;
	
    // Send a friend request
    public FriendRequest sendRequest(Long senderId, Long receiverId) throws FacebookException{
    	if(Boolean.TRUE.equals(blockedUsersRepository.existsByUser_UserIdAndBlockedUser_UserId(receiverId, senderId))) throw new FacebookException(USER_NOT_FOUND);
    	if(Boolean.TRUE.equals(blockedUsersRepository.existsByUser_UserIdAndBlockedUser_UserId(senderId,receiverId))) throw new FacebookException("User Blocked"); //change
       
    	User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new FacebookException("Service.SENDER_NOT_FOUND"));

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new FacebookException("Service.RECEIVER_NOT_FOUND"));
        // Prevent duplicate requests
        if (senderId.equals(receiverId)) throw new FacebookException("Service.CANT_SEND_FRIEND_REQUEST");
        if (friendRequestRepository.findBySender_UserIdAndReceiver_UserId(senderId, receiverId).isPresent()) throw new FacebookException("Service.FRIEND_REQUEST_EXISTS");
        if (friendRequestRepository.findBySender_UserIdAndReceiver_UserId(receiverId, senderId).isPresent()) throw new FacebookException("Service.FRIEND_REQUEST_EXISTS"); 
        FriendRequest request = new FriendRequest();
        request.setSender(sender);
        request.setReceiver(receiver);
        request.setStatus(FriendRequest.Status.PENDING);
        request.setCreatedAt(LocalDateTime.now());
        request.setUpdatedAt(LocalDateTime.now());
        
        return friendRequestRepository.save(request);
    }

    // Accept a friend request
    public FriendRequest acceptRequest(Long senderId, Long receiverId) throws FacebookException {
        FriendRequest request = friendRequestRepository.findBySender_UserIdAndReceiver_UserId(senderId, receiverId)
                .orElseThrow(() -> new FacebookException("Request not found"));
        if(friendRequestRepository.findBySender_UserIdAndReceiver_UserId(receiverId,senderId).isPresent()) throw new FacebookException("Service.Conflict");
        if(request.getStatus().equals(FriendRequest.Status.DECLINED)) throw new FacebookException("Service.Request_Declined");
        if(request.getStatus().equals(FriendRequest.Status.ACCEPTED)) throw new FacebookException(REQUEST_ACCEPTED);
        Friend friend=new Friend();
        friend.setCreatedAt(LocalDateTime.now());
        friend.setUserId(request.getSender().getUserId());
        friend.setFriendId(request.getReceiver().getUserId());
        friendRepository.save(friend);
        
        Friend user=new Friend();
        user.setCreatedAt(LocalDateTime.now());
        user.setUserId(request.getReceiver().getUserId());
        user.setFriendId(request.getSender().getUserId());
        friendRepository.save(user);
        
        request.setStatus(FriendRequest.Status.ACCEPTED);
        request.setUpdatedAt(LocalDateTime.now());
        return friendRequestRepository.save(request);
    }

    // Reject a friend request
    public FriendRequest rejectRequest(Long senderId, Long receiverId) throws FacebookException {    	
        FriendRequest request = friendRequestRepository.findBySender_UserIdAndReceiver_UserId(senderId, receiverId)
                .orElseThrow(() -> new FacebookException("Service.Request_Not_Found"));
        if(friendRequestRepository.findBySender_UserIdAndReceiver_UserId(receiverId,senderId).isPresent()) throw new FacebookException("Service.Conflict");
        if(request.getStatus().equals(FriendRequest.Status.ACCEPTED)) throw new FacebookException(REQUEST_ACCEPTED);
        if(request.getStatus().equals(FriendRequest.Status.DECLINED)) throw new FacebookException("Service.Request_Declined");
        
        request.setStatus(FriendRequest.Status.DECLINED);
        request.setUpdatedAt(LocalDateTime.now());
        return friendRequestRepository.save(request);
    }

    // Cancel a friend request (sender withdraws)
    public void cancelRequest(Long senderId, Long receiverId) throws FacebookException {
        if(senderId.equals(receiverId))
        	throw new FacebookException("Service.SameId");
    	Optional<FriendRequest> op1 = friendRequestRepository.findBySender_UserIdAndReceiver_UserId(senderId, receiverId);       
        Optional<FriendRequest> op2 = friendRequestRepository.findBySender_UserIdAndReceiver_UserId(receiverId,senderId);
        if(op2.isPresent()) throw new FacebookException("Service.OnlySenderRequest");
        FriendRequest request=op1.orElseThrow(()->new FacebookException("Service.Request_Not_Found"));
        if(request.getStatus().equals(FriendRequest.Status.ACCEPTED)) throw new FacebookException(REQUEST_ACCEPTED);
        friendRequestRepository.delete(request);
    }

    // Get received friend requests (pending)

    public List<FriendRequest> getReceivedRequests(Long userId) throws FacebookException {
        return friendRequestRepository.findByReceiver_UserIdAndStatus(userId, FriendRequest.Status.PENDING);
    }

    // Get sent friend requests (pending)

    public List<FriendRequest> getSentRequests(Long userId) throws FacebookException {
        return friendRequestRepository.findBySender_UserIdAndStatus(userId, FriendRequest.Status.PENDING);
    }
    
    public List<UserDTO> getFriends(Long userId) throws FacebookException{
    	List<Friend> ans=friendRepository.findByUserId(userId);
    	return ans.stream()
    			.map(friend->userRepository.findById(friend.getFriendId()).orElseThrow(()->new FacebookException(USER_NOT_FOUND)))
    			.map(user->new UserDTO(user.getUserId(),user.getFirstName(),user.getLastName(),user.getEmail(),user.getUsername(),null,user.getDateOfBirth(),
    			user.getGender(),user.getBio(),user.getProfilePictureUrl(),null,null)).toList();
    }
    
    public void unFriend(Long userId, Long friendId) throws FacebookException{  	    	
    	Optional<Friend> op1=friendRepository.findByUserIdAndFriendId(userId, friendId);
    	Optional<Friend> op2=friendRepository.findByUserIdAndFriendId(friendId, userId);
    	Friend friend1 = op1.orElse(null);
    	Friend friend2 = op2.orElse(null);
    	
    	if(friend1==null || friend2==null) throw new FacebookException("No such friend exists");
    	
    	FriendRequest op3=friendRequestRepository.findBySender_UserIdAndReceiver_UserId(userId, friendId).orElse(null);
    	FriendRequest op4=friendRequestRepository.findBySender_UserIdAndReceiver_UserId(friendId, userId).orElse(null);
    	
    	if(op3!=null) friendRequestRepository.delete(op3);
    	else if(op4!=null) friendRequestRepository.delete(op4);
    	friendRepository.delete(friend1);
    	friendRepository.delete(friend2);
    }
    
    public List<UserDTO> findFriends(Long userId) throws FacebookException{
    	List<User> users=userRepository.findAll();
    	List<User> friends=getFriends(userId).stream().map(x->userRepository.findById(x.getUserId()).orElse(null)).filter(x->x!=null).toList();
    	users.removeAll(friends);
    	List<User> blockedUsers=blockedUsersRepository.findByUser_UserId(userId).stream().map(blockedUser->userRepository.findById(blockedUser.getBlockedUser().getUserId()).orElse(null)).filter(x->x!=null).toList();
    	users.removeAll(blockedUsers);
    	List<User> blockedByUsers=blockedUsersRepository.findByBlockedUser_UserId(userId).stream().map(blockedUser->userRepository.findById(blockedUser.getUser().getUserId()).orElse(null)).filter(x->x!=null).toList();
    	users.removeAll(blockedByUsers);
    	users.remove(userRepository.findById(userId).orElse(null));
    	List<FriendRequest> fr=getSentRequests(userId);
    	users.removeAll(fr.stream().map(x->x.getReceiver()).toList());
    	fr=getReceivedRequests(userId);
    	users.removeAll(fr.stream().map(x->x.getSender()).toList());
    	return users.stream().map(user->new UserDTO(user.getUserId(),user.getFirstName(),user.getLastName(),user.getEmail(),user.getUsername(),null,user.getDateOfBirth(),
    			user.getGender(),user.getBio(),user.getProfilePictureUrl(),null,null)).toList();
    }

    public List<UserDTO> findMutualFriends(Long userId,Long friendId) throws FacebookException{
    	if(userId.equals(friendId)) throw new FacebookException("Service.SameId");  	
    	return getFriends(userId).stream().filter(getFriends(friendId)::contains).toList();
    }
    
    public UserDTO getProfile(Long userId, Long friendId) throws FacebookException{
    	if(Boolean.TRUE.equals(blockedUsersRepository.existsByUser_UserIdAndBlockedUser_UserId(friendId,userId))) throw new FacebookException(USER_NOT_FOUND);
    	User user=userRepository.findById(friendId).orElseThrow(()->new FacebookException(USER_NOT_FOUND));
    	UserDTO userDTO=new UserDTO();
    	userDTO.setBio(user.getBio());
    	userDTO.setEmail(user.getEmail());
    	userDTO.setFirstName(user.getFirstName());
    	userDTO.setLastName(user.getLastName());
    	userDTO.setGender(user.getGender());
    	userDTO.setDateOfBirth(user.getDateOfBirth());
    	userDTO.setUserId(user.getUserId());
    	userDTO.setProfilePictureUrl(user.getProfilePictureUrl());
    	userDTO.setUsername(user.getUsername());
    	return userDTO;
    }
    
    
    public String sendStatus(Long userId,Long friendId) throws FacebookException{
    	if(friendRepository.findByUserIdAndFriendId(userId, friendId).isPresent() || friendRepository.findByUserIdAndFriendId(friendId, userId).isPresent()) {
    		return "FRIEND";
    	}
    	else if(friendRequestRepository.findBySender_UserIdAndReceiver_UserId(userId, friendId).isPresent()){
    		return "REQUEST_SENT";
    	}
    	else if(friendRequestRepository.findBySender_UserIdAndReceiver_UserId(friendId, userId).isPresent()) {
    		return "REQUEST_RECEIVED";
    	}
    	return "NOT_FRIEND";
    }
    
    public List<UserDTO> getBlockedUsers(Long userId) throws FacebookException{
    	return blockedUsersRepository.findByUser_UserId(userId).stream().map(blockedUser->userRepository.findById(blockedUser.getBlockedUser().getUserId()).orElseThrow(()->new FacebookException(USER_NOT_FOUND))).map(user->new UserDTO(user.getUserId(),user.getFirstName(),user.getLastName(),user.getEmail(),user.getUsername(),null,user.getDateOfBirth(),
    			user.getGender(),user.getBio(),user.getProfilePictureUrl(),null,null)).toList();
    }
    
    public UserDTO getBlockedUser(Long userId, Long blockedUserId) throws FacebookException{
    	if(Boolean.TRUE.equals(blockedUsersRepository.existsByUser_UserIdAndBlockedUser_UserId(blockedUserId, userId))) throw new FacebookException(USER_NOT_FOUND);
    	if(Boolean.FALSE.equals(blockedUsersRepository.existsByUser_UserIdAndBlockedUser_UserId(userId, blockedUserId))) throw new FacebookException("User not in the blockList");
    	User user=userRepository.findById(blockedUserId).orElseThrow(()->new FacebookException(USER_NOT_FOUND));
    	return new UserDTO(user.getUserId(),user.getFirstName(),user.getLastName(),user.getEmail(),user.getUsername(),null,user.getDateOfBirth(),
    			user.getGender(),user.getBio(),user.getProfilePictureUrl(),null,null);
    }
    
    public BlockedUsersDTO blockUser(Long userId, Long blockUserId) throws FacebookException{    	
    	if(Boolean.TRUE.equals(blockedUsersRepository.existsByUser_UserIdAndBlockedUser_UserId(blockUserId,userId))) throw new FacebookException(USER_NOT_FOUND) ;
    	if(Boolean.TRUE.equals(blockedUsersRepository.existsByUser_UserIdAndBlockedUser_UserId(userId,blockUserId))) throw new FacebookException("User Already Blocked") ;
    	BlockedUsers blockedUser=new BlockedUsers();
    	blockedUser.setUser(userRepository.findById(userId).orElseThrow(()->new FacebookException(USER_NOT_FOUND)));
    	blockedUser.setBlockedUser(userRepository.findById(blockUserId).orElseThrow(()->new FacebookException(USER_NOT_FOUND)));
    	blockedUser.setCreatedAt(LocalDateTime.now());
    	blockedUsersRepository.save(blockedUser);
    	if(Boolean.TRUE.equals(friendRepository.existsByUserIdAndFriendId(userId, blockUserId))) unFriend(userId, blockUserId);
    	return new BlockedUsersDTO(blockedUser.getBlockedId(),blockedUser.getUser().getUserId(),blockedUser.getBlockedUser().getUserId(), blockedUser.getCreatedAt());
    }
    
    public void unBlockUser(Long userId, Long blockUserId) throws FacebookException{
    	if(Boolean.TRUE.equals(blockedUsersRepository.existsByUser_UserIdAndBlockedUser_UserId(blockUserId, userId))) throw new FacebookException(USER_NOT_FOUND);
    	BlockedUsers blockedUser= blockedUsersRepository.findByUser_UserIdAndBlockedUser_UserId(userId, blockUserId).orElseThrow(()-> new FacebookException("User not in the blockList"));
    	blockedUsersRepository.delete(blockedUser);
    }
    
    public Boolean checkIfBlockedUserExists(Long userId, Long blockedUserId) throws FacebookException{
    	return blockedUsersRepository.existsByUser_UserIdAndBlockedUser_UserId(userId, blockedUserId);
    }
    
}