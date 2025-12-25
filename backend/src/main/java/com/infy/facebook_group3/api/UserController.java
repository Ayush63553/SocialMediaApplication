package com.infy.facebook_group3.api;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.infy.facebook_group3.dto.UserDTO;
import com.infy.facebook_group3.entity.User;
import com.infy.facebook_group3.exception.FacebookException;
import com.infy.facebook_group3.repository.UserRepository;
import com.infy.facebook_group3.service.UserServiceImpl;

import jakarta.validation.Valid;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200")
@Validated
public class UserController {

	@Autowired
    private final UserServiceImpl userServiceImpl;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ModelMapper modelMapper;

    public UserController(UserServiceImpl userServiceImpl) {

        this.userServiceImpl = userServiceImpl;

    }

    @PostMapping
    public ResponseEntity<Map<String,Object>> registerUser(@RequestBody @Valid UserDTO dto) throws FacebookException{
    	Long userId=userServiceImpl.createUser(dto);
    	
        return  ResponseEntity.status(HttpStatus.CREATED).body(Map.of("userId",userId,"message","User Created"));

    }

    
    @PutMapping("/{id}")
    public ResponseEntity<Map<String,Object>> updateUserWithFile(
           @PathVariable Long id,
           @RequestParam(value = "username", required = false) String username,
           @RequestParam(value = "dateOfBirth", required = false) String dateOfBirth,
           @RequestParam(value = "bio", required = false) String bio,
           @RequestParam(value = "file", required = false) MultipartFile file
    ) {
       try {
           String profilePictureUrl;

           // Handle file upload
           if (file != null && !file.isEmpty()) {
               String uploadDir = "uploads/";
               String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
               Path filePath = Paths.get(uploadDir, fileName);
               Files.createDirectories(filePath.getParent());
               Files.write(filePath, file.getBytes());

               profilePictureUrl = "http://localhost:8765/uploads/" + fileName;
           }else {
        	   profilePictureUrl="https://tse1.mm.bing.net/th/id/OIP.WafwdUOq5b5T7RVq_rUJoQHaHa?rs=1&pid=ImgDetMain&o=7&rm=3";
           }

           // Create DTO safely
           UserDTO dto = new UserDTO();
           if (username != null && !"null".equalsIgnoreCase(username) && !username.isBlank()) {
               dto.setUsername(username);
           }
           if (dateOfBirth != null && !"null".equalsIgnoreCase(dateOfBirth) && !dateOfBirth.isBlank()) {
               dto.setDateOfBirth(dateOfBirth);
           }
           if (bio != null && !"null".equalsIgnoreCase(bio) && !bio.isBlank()) {
               dto.setBio(bio);
           }
           if (profilePictureUrl != null) {
               dto.setProfilePictureUrl(profilePictureUrl);
           }

           // Update user
           Long updatedUserId = userServiceImpl.updateUser(id, dto);

           return ResponseEntity.ok(Map.of(
                   "userId", updatedUserId,
                   "profilePictureUrl", profilePictureUrl
           ));
       } catch (Exception e) {
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                   .body(Map.of("errorMessage", e.getMessage(), "errorCode", 500));
       }
    }




    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) throws FacebookException{

        userServiceImpl.deleteUser(id);

        return new ResponseEntity<>("User Deleted",HttpStatus.OK);

    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long id) throws FacebookException{

        return new ResponseEntity<>(userServiceImpl.getUserById(id),HttpStatus.OK);

    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return new ResponseEntity<>(userServiceImpl.getAllUsers(),HttpStatus.OK);
    }
    
    @PutMapping("/profileUpdate/{id}/{profilePictureUrl}")
    public ResponseEntity<String> updateProfilePhoto(@PathVariable Long id, @PathVariable String profilePictureUrl){
    	userServiceImpl.updateProfilePhoto(id, profilePictureUrl);
    	return ResponseEntity.ok("Profile Photo Updated Successfully!!");
    }
    
    @DeleteMapping("/profilePhotoDelete/{userId}")
    public ResponseEntity<Map<String, String>> removeProfilePhoto(@PathVariable Long userId) {
        userServiceImpl.removeProfilePhoto(userId);
        return ResponseEntity.ok(Map.of("message", "Profile photo deleted successfully!!"));
    }
    
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getLoggedInUser(Authentication auth){
    	String username = auth.getName();
    	User user = userRepository.findByUsername(username).orElseThrow(() -> new FacebookException("User not found"));
    	return ResponseEntity.ok(modelMapper.map(user, UserDTO.class));
    }
    @GetMapping("/username/{username}")
    public ResponseEntity<UserDTO>getUserByUserName(@PathVariable String username){
    	User user=userServiceImpl.getUserByUsername(username);
    	return new ResponseEntity<>(modelMapper.map(user, UserDTO.class),HttpStatus.OK);
    }

}


