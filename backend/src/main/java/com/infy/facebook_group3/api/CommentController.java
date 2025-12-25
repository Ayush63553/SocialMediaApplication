package com.infy.facebook_group3.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.infy.facebook_group3.dto.CommentDTO;
import com.infy.facebook_group3.service.CommentService;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins="http://localhost:4200")
public class CommentController {
	
	@Autowired
	private CommentService commentService;
	
	@PostMapping
	public ResponseEntity<CommentDTO> addComment(@RequestBody CommentDTO commentDTO) {
		return ResponseEntity.ok(commentService.addComment(commentDTO));
	}
	
	@GetMapping("/post/{postId}")
	public ResponseEntity<List<CommentDTO>> getCommentsByPost(@PathVariable Long postId){
		return ResponseEntity.ok(commentService.getCommentsByPost(postId));
	}
	
	@GetMapping("/user/{userId}")
	public ResponseEntity<List<CommentDTO>> getCommentsByUser(@PathVariable Long userId){
		return ResponseEntity.ok(commentService.getCommentsByUser(userId));
	}
	
	@PutMapping("/{commentId}")
	public ResponseEntity<CommentDTO> updateComment(@PathVariable Long commentId, @RequestParam Long userId, @RequestParam String content){
		return ResponseEntity.ok(commentService.updateComment(commentId, userId, content));
	}
	
	@DeleteMapping("/{commentId}")
	public ResponseEntity<Void> deleteComment(@PathVariable Long commentId, @RequestParam Long userId){
		commentService.deleteComment(commentId, userId);
		return ResponseEntity.noContent().build();
	}
}