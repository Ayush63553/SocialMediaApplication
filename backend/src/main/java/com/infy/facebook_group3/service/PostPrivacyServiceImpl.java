package com.infy.facebook_group3.service;

import com.infy.facebook_group3.dto.PostPrivacyDTO;
import com.infy.facebook_group3.entity.Post;
import com.infy.facebook_group3.entity.PostPrivacy;
import com.infy.facebook_group3.entity.User;
import com.infy.facebook_group3.exception.FacebookException;
import com.infy.facebook_group3.repository.PostPrivacyRepository;
import com.infy.facebook_group3.repository.PostRepository;
import com.infy.facebook_group3.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class PostPrivacyServiceImpl implements PostPrivacyService{
	
	@Autowired
    private PostPrivacyRepository postPrivacyRepository;
	
	@Autowired
    private PostRepository postRepository;
	
	@Autowired
    private UserRepository userRepository;
	
	private static final String POST_NOT_FOUND_ERROR = "Service.POST_NOT_FOUND";
	private static final String USER_NOT_FOUND_ERROR = "Service.USER_NOT_FOUND";

    public void addPrivacyRule(Long postId, Long userId) throws FacebookException {

        Post post = postRepository.findById(postId)

                .orElseThrow(() -> new FacebookException(POST_NOT_FOUND_ERROR));

        User user = userRepository.findById(userId)

                .orElseThrow(() -> new FacebookException(USER_NOT_FOUND_ERROR));
        if(postPrivacyRepository.existsByPostAndUser(post, user)) throw new FacebookException("This rule already exists");
               	
        PostPrivacy rule = PostPrivacy.builder()
                .post(post)
                .user(user)
                .build();

        postPrivacyRepository.save(rule);
    }

    // Check if a user can view a post

    public boolean canUserViewPost(Long postId, Long viewerId) throws FacebookException{
    	
        Post post = postRepository.findById(postId)

                .orElseThrow(() -> new FacebookException(POST_NOT_FOUND_ERROR));

        User viewer = userRepository.findById(viewerId)

                .orElseThrow(() -> new FacebookException(USER_NOT_FOUND_ERROR));

        // First check global privacy

        return postPrivacyRepository.existsByPostAndUser(post, viewer); 
    }

    // List all custom privacy rules for a post

    public List<PostPrivacyDTO> getPrivacyRules(Long postId) throws FacebookException{

        Post post = postRepository.findById(postId)

                .orElseThrow(() -> new FacebookException(POST_NOT_FOUND_ERROR));
        List<PostPrivacy> pr=postPrivacyRepository.findByPost(post);
        return pr.stream().map(postPrivacy->new PostPrivacyDTO(postPrivacy.getPrivacyId(),postPrivacy.getPost().getPostId(),postPrivacy.getUser().getUserId())).toList();

    }
 
    public void deletePrivacyRules(Long postId) throws FacebookException{
    	Post post = postRepository.findById(postId)
    			
    			.orElseThrow(() -> new FacebookException(POST_NOT_FOUND_ERROR));
    	List<PostPrivacy> pr=postPrivacyRepository.findByPost(post);
    	if(pr.isEmpty()) return;
    	pr.stream().forEach(postPrivacy->postPrivacyRepository.delete(postPrivacy));
    }
    
    public List<PostPrivacyDTO> getByUser(Long userId) throws FacebookException{
    	User user = userRepository.findById(userId).orElseThrow(() -> new FacebookException(USER_NOT_FOUND_ERROR));
    	return postPrivacyRepository.findByUser(user).stream().map(postPrivacy->new PostPrivacyDTO(postPrivacy.getPrivacyId(),postPrivacy.getPost().getPostId(),postPrivacy.getUser().getUserId())).toList();
    
    }
}