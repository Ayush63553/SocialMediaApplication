package com.infy.facebook_group3.service;

import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

import com.infy.facebook_group3.dto.ForgotPasswordRequest;
import com.infy.facebook_group3.dto.ResetPasswordRequest;
import com.infy.facebook_group3.dto.SecurityQuestionRequest;
import com.infy.facebook_group3.entity.SecurityQuestion;
import com.infy.facebook_group3.entity.User;
import com.infy.facebook_group3.repository.SecurityQuestionRepository;
import com.infy.facebook_group3.repository.UserRepository;

import jakarta.transaction.Transactional;

import java.util.List;

@Service
@Transactional
public class SecurityQuestionService {

    private final SecurityQuestionRepository questionRepo;

    private final UserRepository userRepo;

    private final PasswordEncoder passwordEncoder;

    public SecurityQuestionService(SecurityQuestionRepository questionRepo, UserRepository userRepo, PasswordEncoder passwordEncoder) {

        this.questionRepo = questionRepo;

        this.userRepo = userRepo;

        this.passwordEncoder = passwordEncoder;

    }

    public void addSecurityQuestion(SecurityQuestionRequest req) {

        User user = userRepo.findById(req.getUserId())

                .orElseThrow(() -> new RuntimeException("User not found"));

        SecurityQuestion question =	SecurityQuestion.builder()
        		.user(user)
                .question(req.getQuestion())
                .answerHash(passwordEncoder.encode(req.getAnswer()))
                .build();

        questionRepo.save(question);

    }

    public boolean verifySecurityAnswer(ForgotPasswordRequest req) {

        List<SecurityQuestion> questions = questionRepo.findByUser_UserId(req.getUserId());

        return questions.stream()

                .filter(q -> q.getQuestion().equals(req.getQuestion()))

                .anyMatch(q -> passwordEncoder.matches(req.getAnswer(), q.getAnswerHash()));

    }

    public void resetPassword(ResetPasswordRequest req) {

        User user = userRepo.findById(req.getUserId())

                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPasswordHash(passwordEncoder.encode(req.getNewPassword()));

        userRepo.save(user);

    }
    public List<SecurityQuestion>getQuestionByUserId(Long userId){
    	return questionRepo.findByUser_UserId(userId);
    }

}
