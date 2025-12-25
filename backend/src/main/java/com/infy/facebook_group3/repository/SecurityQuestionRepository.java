package com.infy.facebook_group3.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.infy.facebook_group3.entity.SecurityQuestion;

public interface SecurityQuestionRepository extends JpaRepository<SecurityQuestion, Long>{
	List<SecurityQuestion> findByUser_UserId(Long userId);
	
}
