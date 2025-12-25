package com.infy.facebook_group3.service;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.infy.facebook_group3.dto.PostDTO;
import com.infy.facebook_group3.entity.Like;
import com.infy.facebook_group3.entity.Post;
import com.infy.facebook_group3.entity.User;
import com.infy.facebook_group3.exception.FacebookException;
import com.infy.facebook_group3.repository.FriendRepository;
import com.infy.facebook_group3.repository.LikeRepository;
import com.infy.facebook_group3.repository.PostPrivacyRepository;
import com.infy.facebook_group3.repository.PostRepository;
import com.infy.facebook_group3.repository.UserRepository;


@Service(value="postService")
@Transactional
public class PostServiceImpl implements PostService {
	
		@Autowired
		private  PostRepository postRepository;
		@Autowired
		private  UserRepository userRepository;
		@Autowired
		private LikeRepository likeRepository;
		@Autowired
	    private  ModelMapper modelMapper;
		@Autowired
		private PostPrivacyRepository postPrivacyRepository;
		@Autowired
		private FriendRepository friendRepository;
		@Autowired
		private PostPrivacyService postPrivacyService;
		
		private static final String CUSTOM="CUSTOM";
		private static final String USER_NOT_FOUND_ERROR = "Service.USER_NOT_FOUND";
		private static final String POST_NOT_FOUND_ERROR = "Service.POST_NOT_FOUND";

		

		public PostDTO createPost(Long userId, PostDTO postDTO)throws FacebookException {
	        User user = userRepository.findById(userId)
	                .orElseThrow(() -> new FacebookException(USER_NOT_FOUND_ERROR));
	        Post post = modelMapper.map(postDTO, Post.class);
	        post.setUsername(user.getUsername());
	        post.setUser(user);
	        
	        if(post.getLikeCount()==null) {
	        	post.setLikeCount(0);
	        }
	        post = postRepository.save(post);
	        return modelMapper.map(post, PostDTO.class);
	    }
		
	    // US08 - View Posts
	    public List<PostDTO> getPostsByUser(Long userId)throws FacebookException {
	        User user = userRepository.findById(userId)
	                .orElseThrow(() -> new FacebookException(USER_NOT_FOUND_ERROR));
	        List<Post> posts=postRepository.findByUser(user);
	        return posts
	                .stream()
	                .map(p -> {
	                	PostDTO dto=modelMapper.map(p, PostDTO.class);
	                	dto.setMediaUrl(p.getMediaUrl());
	                	dto.setMediaType(p.getMediaType());
	                	return dto;
	                })
	                .toList();
	    }

	    public List<PostDTO> getAllPosts() {
	        List<Post> posts = postRepository.findAll();
	        return posts.stream().map(p -> {
	            PostDTO dto = modelMapper.map(p, PostDTO.class);
	            if (p.getLikeCount() == null) dto.setLikeCount(0);
	            dto.setLikeCount(p.getLikeCount());
	            List<String> likeByUsers = likeRepository.findByPost_PostId(p.getPostId())
	                    .stream()
	                    .map(l -> l.getUser().getUsername())
	                    .toList();
	            dto.setLikedByUsers(likeByUsers);
	            return dto;
	        }).toList();
	    }
	    
	    public synchronized void deletePost(Long id)throws FacebookException {
	        Post post=postRepository.findById(id).orElseThrow(()->new FacebookException(POST_NOT_FOUND_ERROR));
	        postPrivacyService.deletePrivacyRules(id);
	        postRepository.delete(post);
	    }

		@Override
		public PostDTO updatePost(Long postId, PostDTO postDTO) throws FacebookException {
			Post post=postRepository.findById(postId).orElseThrow(()->
			new FacebookException("Post not found"));
			if(postDTO.getContent()!=null) {
				post.setContent(postDTO.getContent());
			}
			if(postDTO.getMediaUrl()!=null) {
				post.setMediaUrl(postDTO.getMediaUrl());
			}
			if(postDTO.getMediaType()!=null) {
				post.setMediaType(postDTO.getMediaType());
			}
			post.setPrivacy(postDTO.getPrivacy());
			Post updatedPost=postRepository.save(post);
			if(!postDTO.getPrivacy().equals(CUSTOM)) postPrivacyService.deletePrivacyRules(postId);
			return modelMapper.map(updatedPost, PostDTO.class);
		}

		@Override
		public PostDTO getPostById(Long postId) throws FacebookException {
			Post post=postRepository.findById(postId).orElseThrow(()->new FacebookException(POST_NOT_FOUND_ERROR));
			return modelMapper.map(post, PostDTO.class);
		}
		
		@Override
	    public PostDTO getPostWithLikes(Long postId) throws FacebookException {
	        Post post = postRepository.findById(postId)
	                .orElseThrow(() -> new FacebookException(POST_NOT_FOUND_ERROR));
	        List<Like> likes = likeRepository.findByPost_PostId(postId);
	        List<String> likeByUsers = likes.stream()
	                .map(l -> l.getUser().getFirstName() + " " + l.getUser().getLastName())
	                .toList();
	        PostDTO postDTO = modelMapper.map(post, PostDTO.class);
	        postDTO.setLikeCount(post.getLikeCount());
	        postDTO.setLikedByUsers(likeByUsers);
	        return postDTO;
	    }

	    @Override
	    public List<PostDTO> getFeedForUser(Long userId) throws FacebookException {
	        List<Post> posts = postRepository.findFeedPosts(userId);
	        User user=userRepository.findById(userId).orElseThrow(()-> new FacebookException(USER_NOT_FOUND_ERROR));
	        posts=posts.stream().filter(post->{
	        	if(post.getUser().getUserId().equals(userId) || post.getPrivacy().equals("PUBLIC")) return true;
	        	else if(post.getPrivacy().equals("PRIVATE")) {
	        		return false;
	        	}
	        	else if(post.getPrivacy().equals("FRIENDS")) {
	        		return friendRepository.findByUserIdAndFriendId(userId, post.getUser().getUserId()).isPresent() || friendRepository.findByUserIdAndFriendId(post.getUser().getUserId(), userId).isPresent();
	        	}
	        	else if(post.getPrivacy().equals(CUSTOM)){
	        		return postPrivacyRepository.existsByPostAndUser(post, user);
	        	}
	        	return false;
	        }).toList();
	        return posts.stream().map(p -> {
	            PostDTO dto = modelMapper.map(p, PostDTO.class);
	            dto.setLikeCount(p.getLikeCount() == null ? 0 : p.getLikeCount());
	            
	            if(p.getUser()!=null) {
	            	dto.setProfilePictureUrl(p.getUser().getProfilePictureUrl());
	            	
	            	dto.setUsername(p.getUser().getUsername());
	            }
	            List<String> likedByUsers = likeRepository.findByPost_PostId(p.getPostId())
	                    .stream()
	                    .map(l -> l.getUser().getUsername())
	                    .toList();
	            dto.setLikedByUsers(likedByUsers);
	            return dto;
	        }).toList();
	    }
	    
	    @Override
	    public List<PostDTO> getPostsByProfile(Long userId,Long friendId)throws FacebookException {
	        User friend = userRepository.findById(friendId)
	                .orElseThrow(() -> new FacebookException("Friend not found"));
	        User user = userRepository.findById(userId)
	        		.orElseThrow(() -> new FacebookException(USER_NOT_FOUND_ERROR));
	        List<Post> posts=postRepository.findByUser(friend);
	        posts=posts.stream().filter(post->{
	        	if(post.getPrivacy().equals("PRIVATE")) return false;
	        	else if(post.getPrivacy().equals("PUBLIC")) return true;
	        	else if((friendRepository.findByUserIdAndFriendId(userId, friendId).isPresent() || friendRepository.findByUserIdAndFriendId(friendId, userId).isPresent()) && post.getPrivacy().equals("FRIENDS")) return true;
	        	else if(post.getPrivacy().equals(CUSTOM)){
	        		if(postPrivacyRepository.existsByPostAndUser(post, user)) return true;
	        		return false;
	        	}
	        	return false;
	        }).toList();
	        return posts
	                .stream()
	                .map(p -> {
	                	PostDTO dto=modelMapper.map(p, PostDTO.class);
	                	dto.setMediaUrl(p.getMediaUrl());
	                	dto.setMediaType(p.getMediaType());
	                	return dto;
	                })
	                .toList();
	    }
}
