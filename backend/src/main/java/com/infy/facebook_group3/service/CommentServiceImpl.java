package com.infy.facebook_group3.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.infy.facebook_group3.dto.CommentDTO;
import com.infy.facebook_group3.entity.Comment;
import com.infy.facebook_group3.entity.Post;
import com.infy.facebook_group3.entity.User;
import com.infy.facebook_group3.exception.FacebookException;
import com.infy.facebook_group3.repository.CommentRepository;
import com.infy.facebook_group3.repository.PostRepository;
import com.infy.facebook_group3.repository.UserRepository;

@Service
@Transactional
public class CommentServiceImpl implements CommentService {

	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private UserRepository userRepository;

	private ModelMapper modelMapper = new ModelMapper();

	@Override
	public CommentDTO addComment(CommentDTO dto) {
	    Optional<Post> optional = postRepository.findById(dto.getPostId());
	    Post post = optional.orElseThrow(() -> new FacebookException("post.content.notFound"));

	    Optional<User> optional2 = userRepository.findById(dto.getUserId());
	    User user = optional2.orElseThrow(() -> new FacebookException("user.notFound"));

	    Comment comment = new Comment();
	    comment.setPost(post);
	    comment.setUser(user);
	    comment.setContent(dto.getContent());
	    comment.setCreatedAt(LocalDateTime.now());
	    comment.setUpdatedAt(LocalDateTime.now());
	    comment.setUsername(user.getUsername());

	    Comment saved = commentRepository.save(comment);

	    CommentDTO response = modelMapper.map(saved, CommentDTO.class);
	    response.setUsername(saved.getUser().getUsername()); // <-- important
	    response.setProfilePictureUrl(saved.getUser().getProfilePictureUrl());
	    return response;
	}

	@Override
	public List<CommentDTO> getCommentsByPost(Long postId) {
		return commentRepository.findByPost_PostId(postId).stream().map(c ->{
			CommentDTO dto=modelMapper.map(c, CommentDTO.class);
			dto.setUsername(c.getUsername());
			dto.setProfilePictureUrl(c.getUser().getProfilePictureUrl());
			return dto;
		})
				.toList();
	}

	@Override
	public List<CommentDTO> getCommentsByUser(Long userId) {
		return commentRepository.findByUser_UserId(userId).stream().map(c -> {
			CommentDTO dto=modelMapper.map(c, CommentDTO.class);
			dto.setUsername(c.getUser().getUsername());
			dto.setProfilePictureUrl(c.getUser().getProfilePictureUrl());
			return dto;
		})
				.toList();
	}

	@Override
	public CommentDTO updateComment(Long commentId, Long userId, String newContent) {
	    Comment comment = commentRepository.findById(commentId)
	            .orElseThrow(() -> new FacebookException("comment.content.notFound"));
	    if (!comment.getUser().getUserId().equals(userId)) {
	        throw new FacebookException("comment.content.notEdit");
	    }

	    comment.setContent(newContent);
	    comment.setUpdatedAt(LocalDateTime.now());

	    Comment updated = commentRepository.save(comment);
	    CommentDTO dto = modelMapper.map(updated, CommentDTO.class);
	    dto.setUsername(updated.getUser().getUsername());
	    dto.setProfilePictureUrl(updated.getUser().getProfilePictureUrl());
	    return dto;
	}

	@Override
	public void deleteComment(Long commentId, Long userId) {
		Comment comment = commentRepository.findById(commentId)
				.orElseThrow(() -> new FacebookException("Comment not found"));
		boolean isCommentOwner = comment.getUser().getUserId().equals(userId);
		boolean isPostOwner = comment.getPost().getUser().getUserId().equals(userId);

		if (!isCommentOwner && !isPostOwner) {
			throw new FacebookException("comment.content.CannotDelete");
		}

		commentRepository.delete(comment);
	}

}
