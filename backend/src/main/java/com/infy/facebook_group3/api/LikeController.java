package com.infy.facebook_group3.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.infy.facebook_group3.dto.LikeDTO;
import com.infy.facebook_group3.exception.FacebookException;
import com.infy.facebook_group3.service.LikeServiceImpl;

@RestController
@RequestMapping("/api/likes")
@CrossOrigin(origins="http://localhost:4200")
public class LikeController {
	
	@Autowired
	private LikeServiceImpl likeService;
	
	@PostMapping
	public ResponseEntity<LikeDTO>likePost(@RequestBody LikeDTO dto) throws FacebookException{
		LikeDTO dt=likeService.likePost(dto);
		return new ResponseEntity<>(dt,HttpStatus.CREATED);
	}
	@GetMapping(value="/{postId}")
	public ResponseEntity<List<LikeDTO>>getLikesByPost(@PathVariable Long postId) throws FacebookException{
		List<LikeDTO>ans=likeService.getLikesByPost(postId);
		return new ResponseEntity<>(ans,HttpStatus.OK);
	}
	@DeleteMapping(value="/{postId}/user/{userId}")
	public ResponseEntity<String>unlikePost(@PathVariable Long postId,@PathVariable Long userId) throws FacebookException{
		likeService.unlikePost(postId, userId);
		String message="Like Removed Successfully";
		return new ResponseEntity<>(message,HttpStatus.OK);
	}

}
