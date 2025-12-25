package com.infy.facebook_group3.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.infy.facebook_group3.dto.LikeDTO;
import com.infy.facebook_group3.entity.Like;
import com.infy.facebook_group3.entity.Post;
import com.infy.facebook_group3.entity.User;
import com.infy.facebook_group3.exception.FacebookException;
import com.infy.facebook_group3.repository.LikeRepository;
import com.infy.facebook_group3.repository.PostRepository;
import com.infy.facebook_group3.repository.UserRepository;

@Service(value="likeService")
@Transactional
public class LikeServiceImpl implements LikeService {
	
	Logger log=LogManager.getLogger(LikeServiceImpl.class);

	@Autowired
	 private  LikeRepository likeRepository;
	@Autowired
	    private  ModelMapper modelMapper;
	@Autowired
	private PostRepository postRepository;
	@Autowired
	private UserRepository userRepository;


	@Override
	public LikeDTO likePost(LikeDTO dto)throws FacebookException {
	    // check if already liked
	    if (likeRepository.findByPost_PostIdAndUser_UserId(dto.getPostId(), dto.getUserId()).isPresent()) {
	        throw new FacebookException("Service.USER_ALREADY_LIKED");
	    }

	    // fetch post
	    Post post = postRepository.findById(dto.getPostId())
	            .orElseThrow(() -> new FacebookException("Service.POST_NOT_FOUND" + dto.getPostId()));

	    // fetch user
	    User user = userRepository.findById(dto.getUserId())
	            .orElseThrow(() -> new FacebookException("Service.USER_NOT_FOUND" + dto.getUserId()));

	    // create like
	    Like like = new Like();
	    like.setPost(post);
	    like.setUser(user);
	    like.setCreatedAt(LocalDateTime.now());
	    likeRepository.save(like);
	    post.setLikeCount(post.getLikeCount()+1);
	    postRepository.save(post);
	    
	    LikeDTO likee=new LikeDTO();
	    likee.setFullName(user.getUsername());
	    likee.setLikeCount(post.getLikeCount());
	    likee.setPostId(post.getPostId());
	    likee.setLikeId(like.getLikeId());
	    likee.setUserId(user.getUserId());
	    likee.setCreatedAt(like.getCreatedAt());
	    // convert back to DTO
	    return likee;
	}
	    public void unlikePost(Long postId, Long userId)throws FacebookException {
	        Optional<Like> optLike = likeRepository.findByPost_PostIdAndUser_UserId(postId, userId);
	        
	        if(optLike.isPresent()) {
	        	Like like = optLike.get();
	        	likeRepository.delete(like);
	        	Post post=like.getPost();
	        	post.setLikeCount(Math.max(0, post.getLikeCount()-1));
	        	postRepository.save(post);
	        } else {
	        	log.error("User not liked yet");
	        }
	    }

	    public List<LikeDTO> getLikesByPost(Long postId) throws FacebookException{
	    	
	    	Post post=postRepository.findById(postId).orElseThrow(()->
	    	new FacebookException("Service.POST_NOT_FOUND"));
	    	
	    	List<Like>likes=likeRepository.findByPost_PostId(postId);
	    	
	        return likes.stream().map(a->{LikeDTO dto=modelMapper.map(a, LikeDTO.class);
	         dto.setFullName(a.getUser().getFirstName()+" "+a.getUser().getLastName());
	         dto.setLikeCount(post.getLikeCount());
	         return dto;
	         }).toList();	    }
}
