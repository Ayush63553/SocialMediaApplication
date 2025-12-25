package com.infy.facebook_group3.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import com.infy.facebook_group3.dto.UserDTO;
import com.infy.facebook_group3.entity.FriendRequest;
import com.infy.facebook_group3.exception.FacebookException;
import com.infy.facebook_group3.service.FriendRequestService;

import java.util.List;

@RestController

@RequestMapping("/api/friend-requests")

@CrossOrigin(origins ="http://localhost:4200")
public class FriendRequestController {

	@Autowired
    private FriendRequestService friendRequestService;

	// Send a new friend request

    @PostMapping("/send")
    public ResponseEntity<FriendRequest> sendRequest(@RequestParam Long senderId, @RequestParam Long receiverId) throws FacebookException { 
       	return new ResponseEntity<>(friendRequestService.sendRequest(senderId, receiverId),HttpStatus.OK);	
    }

    // Accept a friend request

    @PutMapping("/accept")

    public ResponseEntity<FriendRequest> acceptRequest(@RequestParam Long senderId,@RequestParam Long receiverId) throws FacebookException{

        return new ResponseEntity<>(friendRequestService.acceptRequest(senderId,receiverId),HttpStatus.OK);

    }

    // Reject a friend request

    @PutMapping("/reject")

    public ResponseEntity<FriendRequest> rejectRequest(@RequestParam Long senderId,@RequestParam Long receiverId) throws FacebookException{

        return new ResponseEntity<>(friendRequestService.rejectRequest(senderId,receiverId),HttpStatus.OK);

    }

    // Cancel a request (only by sender)

    @DeleteMapping("/cancel")
    public ResponseEntity<String> cancelRequest(@RequestParam Long senderId, @RequestParam Long receiverId) throws FacebookException {

        friendRequestService.cancelRequest(senderId, receiverId);

        return new ResponseEntity<>("Request Cancelled",HttpStatus.OK);

    }

    // Get pending requests received by a user

    @GetMapping("/received/{userId}")

    public ResponseEntity<List<FriendRequest>> getReceivedRequests(@PathVariable Long userId) throws FacebookException{

        return new ResponseEntity<>(friendRequestService.getReceivedRequests(userId),HttpStatus.OK);

    }

    // Get pending requests sent by a user

    @GetMapping("/sent/{userId}")

    public ResponseEntity<List<FriendRequest>> getSentRequests(@PathVariable Long userId) throws FacebookException{

        return new ResponseEntity<>(friendRequestService.getSentRequests(userId),HttpStatus.OK);

    }

    @GetMapping("/friends/{userId}")
    
    public ResponseEntity<List<UserDTO>> getFriends(@PathVariable Long userId) throws FacebookException{	
    	return new ResponseEntity<>(friendRequestService.getFriends(userId),HttpStatus.OK);
    }
    
    @DeleteMapping("/friends/delete")
    public ResponseEntity<String> unFriend(@RequestParam Long userId, @RequestParam Long friendId) throws FacebookException{
    	friendRequestService.unFriend(userId, friendId);
    	return new ResponseEntity<>("Friend Deleted",HttpStatus.OK);
    }
    
    @GetMapping("/friends/find/{userId}")
    public ResponseEntity<List<UserDTO>> findFriends(@PathVariable Long userId) throws FacebookException{	
    	return new ResponseEntity<>(friendRequestService.findFriends(userId),HttpStatus.OK);
    }
    
    @GetMapping("/friends/find")
    public ResponseEntity<List<UserDTO>> findMutualFriends(@RequestParam Long userId,@RequestParam Long friendId) throws FacebookException{	
    	return new ResponseEntity<>(friendRequestService.findMutualFriends(userId,friendId),HttpStatus.OK);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getProfile(@RequestParam Long userId, @RequestParam Long friendId) throws FacebookException{	
    	return new ResponseEntity<>(friendRequestService.getProfile(userId,friendId),HttpStatus.OK);
    }

    @GetMapping("/relationship-status")
    public ResponseEntity<String> sendStatus(@RequestParam Long userId, @RequestParam Long friendId) throws FacebookException{	
    	return new ResponseEntity<>(friendRequestService.sendStatus(userId, friendId),HttpStatus.OK);
    }
    @GetMapping("/notifications/{userId}")
    public ResponseEntity<List<String>> getNotifications(@PathVariable Long userId) throws FacebookException {
    	List<FriendRequest> requests = friendRequestService.getReceivedRequests(userId);
    	List<String> notification = requests.stream().map(req -> req.getSender().getUsername() + " sent you a friend request").toList();
    	return ResponseEntity.ok(notification);
    }
    
    @PostMapping("/blockUser")
    public ResponseEntity<String> blockUser(@RequestParam Long userId, @RequestParam Long blockedUserId) throws FacebookException{
    	friendRequestService.blockUser(userId, blockedUserId);
    	return new ResponseEntity<>("User blocked successfully",HttpStatus.OK);
    }
    
    @GetMapping("/blockedUsers/{userId}")
    public ResponseEntity<List<UserDTO>> getBlockedUsers(@PathVariable Long userId) throws FacebookException{
    	return new ResponseEntity<>(friendRequestService.getBlockedUsers(userId),HttpStatus.OK);
    }
    
    @GetMapping("/blockedUser")
    public ResponseEntity<UserDTO> getBlockedUser(@RequestParam Long userId, @RequestParam Long blockedUserId) throws FacebookException{
    	return new ResponseEntity<>(friendRequestService.getBlockedUser(userId, blockedUserId),HttpStatus.OK);
    }
    
    @DeleteMapping("/unBlockUser")
    public ResponseEntity<String> unBlockUser(@RequestParam Long userId, @RequestParam Long blockedUserId) throws FacebookException{
    	friendRequestService.unBlockUser(userId, blockedUserId);
    	return new ResponseEntity<>("User Unblocked successfully", HttpStatus.OK);
    }
    
    @GetMapping("/isBlocked")
    public ResponseEntity<Boolean> checkIfBlockedUserExists(@RequestParam Long userId, @RequestParam Long blockedUserId) throws FacebookException{
    	return new ResponseEntity<>(friendRequestService.checkIfBlockedUserExists(userId, blockedUserId),HttpStatus.OK);
    }
}