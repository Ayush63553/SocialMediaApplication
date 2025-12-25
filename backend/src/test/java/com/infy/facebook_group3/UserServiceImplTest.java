package com.infy.facebook_group3;

import com.infy.facebook_group3.dto.UserDTO;
import com.infy.facebook_group3.entity.User;
import com.infy.facebook_group3.exception.FacebookException;
import com.infy.facebook_group3.repository.UserRepository;
import com.infy.facebook_group3.service.UserServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private ModelMapper modelMapper;

    @InjectMocks private UserServiceImpl userService;

    private User user;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(1L);
        user.setEmail("test@example.com");
        user.setUsername("testuser");

        userDTO = new UserDTO();
        userDTO.setUserId(1L);
        userDTO.setEmail("test@example.com");
        userDTO.setUsername("testuser");
        userDTO.setPassword("Password@123");
    }
    @Test void createUserEmailExists() {
        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.of(user));
        Assertions.assertThrows(FacebookException.class, () -> userService.createUser(userDTO));
    }
    @Test void createUserUsernameExists() {
        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByUsername(userDTO.getUsername())).thenReturn(Optional.of(user));
        Assertions.assertThrows(FacebookException.class, () -> userService.createUser(userDTO));
    }

    // updateUser
    @Test void updateUserSuccess() throws FacebookException {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername(userDTO.getUsername())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        Long result = userService.updateUser(1L, userDTO);

        Assertions.assertEquals(1L, result);
    }
    @Test void updateUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(FacebookException.class, () -> userService.updateUser(1L, userDTO));
    }
    @Test void updateUserUsernameConflict() {
        User anotherUser = new User();
        anotherUser.setUserId(2L);
        anotherUser.setUsername("other");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("other")).thenReturn(Optional.of(anotherUser));

        userDTO.setUsername("other");

        Assertions.assertThrows(FacebookException.class, () -> userService.updateUser(1L, userDTO));
    }

    // deleteUser
    @Test void deleteUserSuccess() throws FacebookException {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }
    @Test void deleteUserNotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);
        Assertions.assertThrows(FacebookException.class, () -> userService.deleteUser(1L));
    }

    // getUserById
    @Test void getUserByIdSuccess() throws FacebookException {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserDTO.class)).thenReturn(userDTO);

        UserDTO result = userService.getUserById(1L);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("testuser", result.getUsername());
    }
    @Test void getUserByIdNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(FacebookException.class, () -> userService.getUserById(1L));
    }

    // getAllUsers
    @Test void getAllUsersSuccess() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(modelMapper.map(user, UserDTO.class)).thenReturn(userDTO);

        List<UserDTO> result = userService.getAllUsers();

        Assertions.assertEquals(1, result.size());
    }

    // getUserByEmail
    @Test void getUserByEmailSuccess() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserDTO.class)).thenReturn(userDTO);

        Optional<UserDTO> result = userService.getUserByEmail("test@example.com");

        Assertions.assertTrue(result.isPresent());
    }
    @Test void getUserByEmailEmpty() {
        when(userRepository.findByEmail("nope@example.com")).thenReturn(Optional.empty());
        Optional<UserDTO> result = userService.getUserByEmail("nope@example.com");
        Assertions.assertTrue(result.isEmpty());
    }

    // getUserByUsername
    @Test void getUserByUsernameSuccess() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        User result = userService.getUserByUsername("testuser");
        Assertions.assertEquals("testuser", result.getUsername());
    }
    @Test void getUserByUsernameNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());
        Assertions.assertThrows(FacebookException.class, () -> userService.getUserByUsername("unknown"));
    }

    // updateProfilePhoto
    @Test void updateProfilePhotoSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.updateProfilePhoto(1L, "http://pic.jpg");

        Assertions.assertEquals("http://pic.jpg", user.getProfilePictureUrl());
    }
    @Test void updateProfilePhotoNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(FacebookException.class, () -> userService.updateProfilePhoto(1L, "url"));
    }

    // removeProfilePhoto
    @Test void removeProfilePhotoSuccess() {
        user.setProfilePictureUrl("pic.jpg");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.removeProfilePhoto(1L);

        Assertions.assertNull(user.getProfilePictureUrl());
    }
    @Test void removeProfilePhotoNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(FacebookException.class, () -> userService.removeProfilePhoto(1L));
    }
}