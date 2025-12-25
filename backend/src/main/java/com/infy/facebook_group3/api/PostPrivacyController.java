package com.infy.facebook_group3.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import com.infy.facebook_group3.dto.PostPrivacyDTO;
import com.infy.facebook_group3.exception.FacebookException;
import com.infy.facebook_group3.service.PostPrivacyService;

import java.util.List;

@RestController

@RequestMapping("/api/post-privacy")

@CrossOrigin(origins="http://localhost:4200")

public class PostPrivacyController {
	@Autowired
    private PostPrivacyService postPrivacyService;

    @PostMapping("/{postId}/add-rule")

    public ResponseEntity<String> addRule(

            @PathVariable Long postId,

            @RequestParam Long userId

           ) throws FacebookException{
    		postPrivacyService.addPrivacyRule(postId, userId);
        return new ResponseEntity<>("Added Rule",HttpStatus.OK);

    }

    @GetMapping("/{postId}/rules")

    public ResponseEntity<List<PostPrivacyDTO>> getRules(@PathVariable Long postId) throws FacebookException{

        return ResponseEntity.ok(postPrivacyService.getPrivacyRules(postId));

    }

    @GetMapping("/{postId}/can-view")

    public ResponseEntity<Boolean> canView(

            @PathVariable Long postId,

            @RequestParam Long viewerId) throws FacebookException{

        return ResponseEntity.ok(postPrivacyService.canUserViewPost(postId, viewerId));

    }
    
    @DeleteMapping("/{postId}/delete")
    public ResponseEntity<String> deleteRules(@PathVariable Long postId)throws FacebookException{
    	postPrivacyService.deletePrivacyRules(postId);
    	return new ResponseEntity<>("Rules deleted",HttpStatus.OK);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostPrivacyDTO>> getRulesByUser(@PathVariable Long userId) throws FacebookException{
    	return new ResponseEntity<>(postPrivacyService.getByUser(userId),HttpStatus.OK);
    }

}