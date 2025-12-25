package com.infy.facebook_group3.dto;

import java.time.LocalDateTime;

//import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
	
	
	private Long userId;
	
	@NotBlank(message="{user.firstName.notBlank}")
	@Size(min=2, max=50, message="{user.firstName.size}")
	@Pattern(regexp="^[a-zA-Z]*$", message="{user.firstName.pattern}")
	private String firstName;
	
	@NotBlank(message="{user.lastName.notBlank}")
	@Size(min=2, max=50, message="{user.lastName.size}")
	@Pattern(regexp="^[a-zA-Z]*$", message="{user.lastName.pattern}")
	private String lastName;
	
	@Email(message="{user.email.invalid}")
	@NotBlank(message="{user.email.notBlank}")
	private String email;
	
	
	private String username;	
	@NotBlank(message="{user.password.notBlank}")
	@Size(min=8, max=20, message="{user.password.size}")
	@Pattern(regexp="^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!])(?!.*\\s).{8,20}$", message="{user.password.pattern}")
	private String password;
	
	private String dateOfBirth;
	private String gender;
	private String bio;
	private String profilePictureUrl;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
