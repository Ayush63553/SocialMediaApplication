package com.infy.facebook_group3.service;

import java.util.List;
import java.util.Optional;

import com.infy.facebook_group3.dto.UserDTO;
import com.infy.facebook_group3.exception.FacebookException;

public interface UserService {
	  public abstract Long createUser(UserDTO userDTO) throws FacebookException;
	  
	  public abstract Long updateUser(Long id, UserDTO userDTO) throws FacebookException;
	  
	  public abstract void deleteUser(Long id) throws FacebookException;
	  
	  public abstract UserDTO getUserById(Long id) throws FacebookException;
	  
	  public abstract List<UserDTO> getAllUsers();

	  public abstract Optional<UserDTO> getUserByEmail(String email);
	  
}
