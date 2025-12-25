package com.infy.facebook_group3.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.infy.facebook_group3.dto.ForgotPasswordRequest;
import com.infy.facebook_group3.dto.ResetPasswordRequest;
import com.infy.facebook_group3.dto.SecurityQuestionRequest;
import com.infy.facebook_group3.dto.UserValidationResponse;
import com.infy.facebook_group3.entity.SecurityQuestion;
import com.infy.facebook_group3.exception.FacebookException;
import com.infy.facebook_group3.repository.UserRepository;
import com.infy.facebook_group3.service.SecurityQuestionService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class PasswordResetController {

    private final SecurityQuestionService securityQuestionService;

    @Autowired
    private UserRepository userRepo;

    public PasswordResetController(SecurityQuestionService securityQuestionService) {
        this.securityQuestionService = securityQuestionService;
    }

    @PostMapping("/set-security-question")
    public ResponseEntity<String> setSecurityQuestion(@RequestBody SecurityQuestionRequest req) {
        securityQuestionService.addSecurityQuestion(req);
        return ResponseEntity.ok("Security question saved successfully.");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest req) {
        boolean valid = securityQuestionService.verifySecurityAnswer(req);
        if (!valid) {
        	throw new FacebookException("Security question/answer mismatch");
        }
        return ResponseEntity.ok("Verification successful. You may now reset your password.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest req) {
        securityQuestionService.resetPassword(req);
        return ResponseEntity.ok("Password reset successful.");
    }

    @GetMapping("/get-question/{userId}")
    public ResponseEntity<String> getSecurityQuestion(@PathVariable Long userId) {
        List<SecurityQuestion> questions = securityQuestionService.getQuestionByUserId(userId);
        if (questions.isEmpty()) {
            return ResponseEntity.status(404).body("No security Questions found");
        }
        return ResponseEntity.ok(questions.get(0).getQuestion());
    }

    @GetMapping("/validate-user")
    public ResponseEntity<UserValidationResponse> validateUser(@RequestParam String username, @RequestParam String email) {
        return userRepo.findByUsernameAndEmail(username, email)
                .map(user -> ResponseEntity.ok(new UserValidationResponse(
                        user.getUserId(), user.getUsername(), user.getEmail()
                )))
                .orElseGet(() -> ResponseEntity.status(404).body(
                        new UserValidationResponse(null, null, "User not found with given username and email")
                ));
    }
}