package com.infy.facebook_group3.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import com.infy.facebook_group3.dto.UserDTO;
import com.infy.facebook_group3.entity.User;
import com.infy.facebook_group3.exception.FacebookException;
import com.infy.facebook_group3.repository.UserRepository;

import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.List;

import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

	private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ModelMapper modelMapper;
	
	private static final String USER_NOT_FOUND_ERROR = "Service.USER_NOT_FOUND";

    
    @Override
    public Long createUser(UserDTO userDTO) throws FacebookException{

    	if(userRepository.findByEmail(userDTO.getEmail()).isPresent()) throw new FacebookException("user.email.already");
    	if(userRepository.findByUsername(userDTO.getUsername()).isPresent()) throw new FacebookException("user.username");
    	else{
    		User user =new User();
    		user.setFirstName(userDTO.getFirstName());
    		user.setLastName(userDTO.getLastName());
    		user.setEmail(userDTO.getEmail());
    		user.setUsername(userDTO.getUsername());
    		user.setDateOfBirth(userDTO.getDateOfBirth());
    		user.setGender(userDTO.getGender());
    		user.setBio(userDTO.getBio());
    		user.setProfilePictureUrl(userDTO.getProfilePictureUrl());
    		user.setPasswordHash(passwordEncoder.encode(userDTO.getPassword()));
    		user.setCreatedAt(LocalDateTime.now());
    		user.setUpdatedAt(LocalDateTime.now());
    		userRepository.save(user);
    		return user.getUserId();
    	}
        

    }
    
    @Override
    public Long updateUser(Long id, UserDTO userDTO) throws FacebookException{

        User user = userRepository.findById(id)

                .orElseThrow(() -> new FacebookException(USER_NOT_FOUND_ERROR));

        if(userRepository.findByUsername(userDTO.getUsername()).isPresent() && !user.getUsername().equals(userDTO.getUsername())) throw new FacebookException("user.username");
        
        if(userDTO.getUsername()!=null) user.setUsername(userDTO.getUsername());
        if(userDTO.getBio()!=null) user.setBio(userDTO.getBio());
        if(userDTO.getDateOfBirth()!=null) user.setDateOfBirth(userDTO.getDateOfBirth());
        
        if(userDTO.getProfilePictureUrl()!=null) user.setProfilePictureUrl(userDTO.getProfilePictureUrl());

        
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        
        return user.getUserId();

    }

    @Override
    public void deleteUser(Long id) throws FacebookException{

        if (!userRepository.existsById(id)) {

            throw new FacebookException(USER_NOT_FOUND_ERROR);

        }

        userRepository.deleteById(id);

    }

    @Override
    public UserDTO getUserById(Long id) throws FacebookException {

        return userRepository.findById(id)

                .map(user -> modelMapper.map(user, UserDTO.class))

                .orElseThrow(() -> new FacebookException(USER_NOT_FOUND_ERROR));

    }

    @Override
    public List<UserDTO> getAllUsers() {

        return userRepository.findAll()

                .stream()

                .map(user -> modelMapper.map(user, UserDTO.class))

                .toList();

    }

    @Override
    public Optional<UserDTO> getUserByEmail(String email) {

        return userRepository.findByEmail(email)

                .map(user -> modelMapper.map(user, UserDTO.class));

    }
    public User getUserByUsername(String username) {
    	return userRepository.findByUsername(username).orElseThrow(()->new FacebookException(USER_NOT_FOUND_ERROR));
    }
    
    
    public void updateProfilePhoto(Long userId, String profilePictureUrl) {
    	User user = userRepository.findById(userId).orElseThrow(() -> new FacebookException(USER_NOT_FOUND_ERROR));
    	user.setProfilePictureUrl(profilePictureUrl);
    	userRepository.save(user);
    }

    public void removeProfilePhoto(Long userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new FacebookException(USER_NOT_FOUND_ERROR));
		user.setProfilePictureUrl(null);
		userRepository.save(user);
	}
}
