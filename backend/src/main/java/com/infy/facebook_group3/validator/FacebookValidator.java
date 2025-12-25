package com.infy.facebook_group3.validator;

import com.infy.facebook_group3.exception.FacebookException;

public class FacebookValidator {
	
	
	
     private FacebookValidator() {
		
	}


	public static void validateFirstName(String firstName) throws FacebookException {
		
   	 String regex = "^[A-Z][a-z]{1,50}$";
   	 
		if(firstName == null || !firstName.matches(regex)) {
			throw new FacebookException("Invalid first name: First name must contain only letters and be between 2 and 50 characters long.");
		}	
		
	}


   public static void validateLastName(String lastName) throws FacebookException {
	
   	String regex = "^[A-Z][a-z]{1,50}$";
   	
	if(lastName == null || !lastName.matches(regex)) {
		throw new FacebookException("Invalid last name: Last name must contain only letters and be between 2 and 50 characters long.");
	}	
}
	
	public static void validatePassword(String password) throws FacebookException {
		
		String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!])(?!.*\\s).{8,20}$";
		
		if(password == null || !password.matches(regex)) {
			throw new FacebookException("Password must contain at least one uppercase letter, one lowercase letter, one digit, one special character, and should be between 8 and 20 characters long. It should not contain spaces.");
		}		
		
	}
	
    
    public static void validateEmail(String email) throws FacebookException {
    	
    	String regex = "^[\\w.%+-]+@[\\w.-]+\\.[A-Za-z]{2,7}$";

    	if(email == null || !email.matches(regex)) {
    		throw new FacebookException("Invalid email format.");
    	}	
    }

}
