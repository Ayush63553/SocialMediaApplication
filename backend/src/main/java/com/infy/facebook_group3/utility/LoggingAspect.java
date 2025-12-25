package com.infy.facebook_group3.utility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.infy.facebook_group3.exception.FacebookException;

@Component
@Aspect
public class LoggingAspect {
	
	private static final Logger LOGGER = LogManager.getLogger(LoggingAspect.class); 
	
	@AfterThrowing(pointcut = "execution(* com.infy.facebook_group3.service.CommentServiceImpl.*(..))", throwing="exception")
	public void logServiceExceptionComment(FacebookException exception) {
		LOGGER.error(exception.getMessage(), exception);
	}
	
	@AfterThrowing(pointcut = "execution(* com.infy.facebook_group3.service.FriendRequestServiceImpl.*(..))", throwing="exception")
	public void logServiceExceptionFriendRequest(FacebookException exception) {
		LOGGER.error(exception.getMessage(), exception);
	}
	
	@AfterThrowing(pointcut = "execution(* com.infy.facebook_group3.service.LikeServiceImpl.*(..))", throwing="exception")
	public void logServiceExceptionLike(FacebookException exception) {
		LOGGER.error(exception.getMessage(), exception);
	}
	
	@AfterThrowing(pointcut = "execution(* com.infy.facebook_group3.service.PostServiceImpl.*(..))", throwing="exception")
	public void logServiceExceptionPost(FacebookException exception) {
		LOGGER.error(exception.getMessage(), exception);
	}
	
	@AfterThrowing(pointcut = "execution(* com.infy.facebook_group3.service.UserServiceImpl.*(..))", throwing="exception")
	public void logServiceExceptionUser(FacebookException exception) {
		LOGGER.error(exception.getMessage(), exception);
	}
	
	
	
	
}
