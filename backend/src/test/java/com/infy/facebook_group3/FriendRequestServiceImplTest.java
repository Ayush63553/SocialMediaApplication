package com.infy.facebook_group3;

import com.infy.facebook_group3.dto.UserDTO;
import com.infy.facebook_group3.entity.Friend;
import com.infy.facebook_group3.entity.FriendRequest;
import com.infy.facebook_group3.entity.User;
import com.infy.facebook_group3.exception.FacebookException;
import com.infy.facebook_group3.repository.FriendRepository;
import com.infy.facebook_group3.repository.FriendRequestRepository;
import com.infy.facebook_group3.repository.UserRepository;
import com.infy.facebook_group3.service.FriendRequestServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FriendRequestServiceImplTest {

    @Mock private FriendRequestRepository friendRequestRepository;
    @Mock private UserRepository userRepository;
    @Mock private FriendRepository friendRepository;

    @InjectMocks private FriendRequestServiceImpl service;

    private User user1, user2;
    private FriendRequest request;

    @BeforeEach
    void setUp() {
        user1 = new User();
        user1.setUserId(1L);
        user1.setFirstName("John");
        user1.setLastName("Doe");
        user1.setEmail("john@example.com");
        user1.setUsername("john");

        user2 = new User();
        user2.setUserId(2L);
        user2.setFirstName("Jane");
        user2.setLastName("Smith");
        user2.setEmail("jane@example.com");
        user2.setUsername("jane");

        request = new FriendRequest();
        request.setRequestId(10L);
        request.setSender(user1);
        request.setReceiver(user2);
        request.setStatus(FriendRequest.Status.PENDING);
    }

    // sendRequest
    @Test void sendRequestSuccess() throws FacebookException {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(friendRequestRepository.findBySender_UserIdAndReceiver_UserId(1L, 2L)).thenReturn(Optional.empty());
        when(friendRequestRepository.findBySender_UserIdAndReceiver_UserId(2L, 1L)).thenReturn(Optional.empty());
        when(friendRequestRepository.save(any())).thenReturn(request);

        FriendRequest result = service.sendRequest(1L, 2L);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(FriendRequest.Status.PENDING, result.getStatus());
    }
    @Test void sendRequestSenderNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(FacebookException.class, () -> service.sendRequest(1L, 2L));
    }
    @Test void sendRequestReceiverNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        Assertions.assertThrows(FacebookException.class, () -> service.sendRequest(1L, 2L));
    }
    @Test void sendRequestToSelf() {
        Assertions.assertThrows(FacebookException.class, () -> service.sendRequest(1L, 1L));
    }
    @Test void sendRequestAlreadyExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(friendRequestRepository.findBySender_UserIdAndReceiver_UserId(1L, 2L)).thenReturn(Optional.of(request));
        Assertions.assertThrows(FacebookException.class, () -> service.sendRequest(1L, 2L));
    }
    @Test void sendRequestReverseExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(friendRequestRepository.findBySender_UserIdAndReceiver_UserId(1L, 2L)).thenReturn(Optional.empty());
        when(friendRequestRepository.findBySender_UserIdAndReceiver_UserId(2L, 1L)).thenReturn(Optional.of(request));
        Assertions.assertThrows(FacebookException.class, () -> service.sendRequest(1L, 2L));
    }

    // acceptRequest
    @Test void acceptRequestSuccess() throws FacebookException {
        when(friendRequestRepository.findBySender_UserIdAndReceiver_UserId(1L, 2L)).thenReturn(Optional.of(request));
        when(friendRequestRepository.findBySender_UserIdAndReceiver_UserId(2L, 1L)).thenReturn(Optional.empty());
        when(friendRequestRepository.save(any())).thenReturn(request);

        FriendRequest result = service.acceptRequest(1L, 2L);
        Assertions.assertEquals(FriendRequest.Status.ACCEPTED, result.getStatus());
    }
    @Test void acceptRequestNotFound() {
        when(friendRequestRepository.findBySender_UserIdAndReceiver_UserId(1L, 2L)).thenReturn(Optional.empty());
        Assertions.assertThrows(FacebookException.class, () -> service.acceptRequest(1L, 2L));
    }
    @Test void acceptRequestReverseExists() {
        when(friendRequestRepository.findBySender_UserIdAndReceiver_UserId(1L, 2L)).thenReturn(Optional.of(request));
        when(friendRequestRepository.findBySender_UserIdAndReceiver_UserId(2L, 1L)).thenReturn(Optional.of(request));
        Assertions.assertThrows(FacebookException.class, () -> service.acceptRequest(1L, 2L));
    }
    @Test void acceptRequestAlreadyDeclined() {
        request.setStatus(FriendRequest.Status.DECLINED);
        when(friendRequestRepository.findBySender_UserIdAndReceiver_UserId(1L, 2L)).thenReturn(Optional.of(request));
        Assertions.assertThrows(FacebookException.class, () -> service.acceptRequest(1L, 2L));
    }
    @Test void acceptRequestAlreadyAccepted() {
        request.setStatus(FriendRequest.Status.ACCEPTED);
        when(friendRequestRepository.findBySender_UserIdAndReceiver_UserId(1L, 2L)).thenReturn(Optional.of(request));
        Assertions.assertThrows(FacebookException.class, () -> service.acceptRequest(1L, 2L));
    }

    // rejectRequest
    @Test void rejectRequestSuccess() throws FacebookException {
        when(friendRequestRepository.findBySender_UserIdAndReceiver_UserId(1L, 2L)).thenReturn(Optional.of(request));
        when(friendRequestRepository.findBySender_UserIdAndReceiver_UserId(2L, 1L)).thenReturn(Optional.empty());
        when(friendRequestRepository.save(any())).thenReturn(request);

        FriendRequest result = service.rejectRequest(1L, 2L);
        Assertions.assertEquals(FriendRequest.Status.DECLINED, result.getStatus());
    }
    @Test void rejectRequestNotFound() {
        when(friendRequestRepository.findBySender_UserIdAndReceiver_UserId(1L, 2L)).thenReturn(Optional.empty());
        Assertions.assertThrows(FacebookException.class, () -> service.rejectRequest(1L, 2L));
    }
    @Test void rejectRequestReverseExists() {
        when(friendRequestRepository.findBySender_UserIdAndReceiver_UserId(1L, 2L)).thenReturn(Optional.of(request));
        when(friendRequestRepository.findBySender_UserIdAndReceiver_UserId(2L, 1L)).thenReturn(Optional.of(request));
        Assertions.assertThrows(FacebookException.class, () -> service.rejectRequest(1L, 2L));
    }
    @Test void rejectRequestAlreadyAccepted() {
        request.setStatus(FriendRequest.Status.ACCEPTED);
        when(friendRequestRepository.findBySender_UserIdAndReceiver_UserId(1L, 2L)).thenReturn(Optional.of(request));
        Assertions.assertThrows(FacebookException.class, () -> service.rejectRequest(1L, 2L));
    }
    @Test void rejectRequestAlreadyDeclined() {
        request.setStatus(FriendRequest.Status.DECLINED);
        when(friendRequestRepository.findBySender_UserIdAndReceiver_UserId(1L, 2L)).thenReturn(Optional.of(request));
        Assertions.assertThrows(FacebookException.class, () -> service.rejectRequest(1L, 2L));
    }

    // cancelRequest
    @Test void cancelRequestSuccess() throws FacebookException {
        when(friendRequestRepository.findBySender_UserIdAndReceiver_UserId(1L, 2L)).thenReturn(Optional.of(request));
        when(friendRequestRepository.findBySender_UserIdAndReceiver_UserId(2L, 1L)).thenReturn(Optional.empty());

        service.cancelRequest(1L, 2L);
        verify(friendRequestRepository).delete(request);
    }
    @Test void cancelRequestToSelf() {
        Assertions.assertThrows(FacebookException.class, () -> service.cancelRequest(1L, 1L));
    }
        @Test void cancelRequestNotFound() {
        when(friendRequestRepository.findBySender_UserIdAndReceiver_UserId(1L, 2L)).thenReturn(Optional.empty());
        when(friendRequestRepository.findBySender_UserIdAndReceiver_UserId(2L, 1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(FacebookException.class, () -> service.cancelRequest(1L, 2L));
    }
    @Test void cancelRequestAlreadyAccepted() {
        request.setStatus(FriendRequest.Status.ACCEPTED);
        when(friendRequestRepository.findBySender_UserIdAndReceiver_UserId(1L, 2L)).thenReturn(Optional.of(request));
        Assertions.assertThrows(FacebookException.class, () -> service.cancelRequest(1L, 2L));
    }

    // getReceivedRequests / getSentRequests
    @Test void getReceivedRequestsReturnsList() throws FacebookException {
        when(friendRequestRepository.findByReceiver_UserIdAndStatus(2L, FriendRequest.Status.PENDING)).thenReturn(List.of(request));
        Assertions.assertEquals(1, service.getReceivedRequests(2L).size());
    }
    @Test void getSentRequestsReturnsList() throws FacebookException {
        when(friendRequestRepository.findBySender_UserIdAndStatus(1L, FriendRequest.Status.PENDING)).thenReturn(List.of(request));
        Assertions.assertEquals(1, service.getSentRequests(1L).size());
    }
    // unFriend
    @Test void unFriendSuccess() throws FacebookException {
        Friend f = new Friend(); f.setUserId(1L); f.setFriendId(2L);
        when(friendRepository.findByUserIdAndFriendId(1L, 2L)).thenReturn(Optional.of(f));
        when(friendRequestRepository.findBySender_UserIdAndReceiver_UserId(1L, 2L)).thenReturn(Optional.of(request));

        service.unFriend(1L, 2L);
        verify(friendRepository).delete(f);
        verify(friendRequestRepository).delete(request);
    }
    @Test void unFriendNotExists() {
        when(friendRepository.findByUserIdAndFriendId(1L, 2L)).thenReturn(Optional.empty());
        when(friendRepository.findByUserIdAndFriendId(2L, 1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(FacebookException.class, () -> service.unFriend(1L, 2L));
    }

    
    @Test void findMutualFriendsSameId() {
        Assertions.assertThrows(FacebookException.class, () -> service.findMutualFriends(1L, 1L));
    }

    // getProfile
    @Test void getProfileSuccess() throws FacebookException {
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        UserDTO dto = service.getProfile(2L);
        Assertions.assertEquals("jane", dto.getUsername());
    }
    @Test void getProfileNotFound() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        Assertions.assertThrows(FacebookException.class, () -> service.getProfile(2L));
    }

    // sendStatus
    @Test void sendStatusFriend() throws FacebookException {
        when(friendRepository.findByUserIdAndFriendId(1L, 2L)).thenReturn(Optional.of(new Friend()));
        Assertions.assertEquals("FRIEND", service.sendStatus(1L, 2L));
    }
    @Test void sendStatusRequestSent() throws FacebookException {
        when(friendRepository.findByUserIdAndFriendId(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(friendRequestRepository.findBySender_UserIdAndReceiver_UserId(1L, 2L)).thenReturn(Optional.of(request));
        Assertions.assertEquals("REQUEST_SENT", service.sendStatus(1L, 2L));
    }
    @Test void sendStatusRequestReceived() throws FacebookException {
        when(friendRepository.findByUserIdAndFriendId(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(friendRequestRepository.findBySender_UserIdAndReceiver_UserId(1L, 2L)).thenReturn(Optional.empty());
        when(friendRequestRepository.findBySender_UserIdAndReceiver_UserId(2L, 1L)).thenReturn(Optional.of(request));
        Assertions.assertEquals("REQUEST_RECEIVED", service.sendStatus(1L, 2L));
    }
    @Test void sendStatusNotFriend() throws FacebookException {
        when(friendRepository.findByUserIdAndFriendId(anyLong(), anyLong())).thenReturn(Optional.empty());
        when(friendRequestRepository.findBySender_UserIdAndReceiver_UserId(anyLong(), anyLong())).thenReturn(Optional.empty());
        Assertions.assertEquals("NOT_FRIEND", service.sendStatus(1L, 2L));
    }
}