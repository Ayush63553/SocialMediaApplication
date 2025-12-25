package com.infy.facebook_group3.api;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.infy.facebook_group3.dto.PostDTO;
import com.infy.facebook_group3.exception.FacebookException;
import com.infy.facebook_group3.service.PostService;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins="http://localhost:4200")
/***
 *Making the post
 */

public class PostController {
	
	Logger log=LogManager.getLogger(PostController.class);
	
	@Autowired
	private PostService postService;

	/***create the post of the particular user
	 */
	@PostMapping("/userpost/{userId}")
	public ResponseEntity<PostDTO> createPost(
	        @PathVariable final Long userId,
	        @RequestParam("content") final String content,
	        @RequestParam("privacy") final String privacy,
	        @RequestParam(value = "file", required = false) MultipartFile file
	) throws FacebookException {

	    String fileUrl = null;
	    String mediaType = null;

	    if (file != null && !file.isEmpty()) {
	        try {
	            // Save file locally
	            final String uploadDir = "uploads/";
	            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
	            Path filePath = Paths.get(uploadDir, fileName);
	            Files.createDirectories(filePath.getParent());
	            Files.write(filePath, file.getBytes());

	            fileUrl = "http://localhost:8765/uploads/" + fileName; 
	            mediaType = file.getContentType();
	        } catch (IOException e) {
	        	log.error("Error fetching user",e);
	            throw new FacebookException( e.getMessage());
	        }
	    }

	    PostDTO dto = new PostDTO();
	    dto.setUserId(userId);
	    dto.setContent(content);
	    dto.setPrivacy(privacy);
	    dto.setMediaUrl(fileUrl);
	    dto.setMediaType(mediaType);

	    return new ResponseEntity<>(postService.createPost(userId, dto), HttpStatus.CREATED);
	}
	/***get the post id  of the particular user
	 */
	@GetMapping("{postId}")
	public ResponseEntity<PostDTO>getPostById(@PathVariable final Long postId)throws FacebookException{
		PostDTO dto=postService.getPostById(postId);
		return new ResponseEntity<>(dto,HttpStatus.OK);
	}

	/***get the post of the particular user
	 */
	@GetMapping("/user/{userId}")
	public ResponseEntity<?> getPostsByUser(@PathVariable final  Long userId) {
	    try {
	        List<PostDTO> posts = postService.getPostsByUser(userId);

	        if (posts == null || posts.isEmpty()) {
	            return ResponseEntity.ok(List.of()); // ✅ safe empty list
	        }

	        posts.forEach(a -> {
	            if (a.getMediaUrl() != null && !a.getMediaUrl().startsWith("http")) {
	                a.setMediaUrl("http://localhost:8765" + a.getMediaUrl());
	            }
	        });

	        return ResponseEntity.ok(posts);
	    } catch (Exception e) {
	        
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(Map.of("error", e.getMessage()));
	    }
	}

	/***delete the post of the particular user
	 */
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deletePost(@PathVariable final Long id) {
		postService.deletePost(id);
		return new ResponseEntity<>("Deleted",HttpStatus.OK);
	}
	/***update the post of the particular user
	 */
	@PutMapping("/{postId}")
	public ResponseEntity<PostDTO> updatePost(
	        @PathVariable Long postId,
	        @RequestParam("content") String content,
	        @RequestParam(value = "privacy", defaultValue = "public") String privacy,
	        @RequestParam(value = "file", required = false) MultipartFile file
	) throws IOException {

	    String uploadDir = "uploads/";
	    String mediaUrl = null;
	    String mediaType = null;

	    if (file != null && !file.isEmpty()) {
	        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
	        Path path = Paths.get(uploadDir, fileName);
	        Files.createDirectories(path.getParent());
	        Files.write(path, file.getBytes());

	        mediaUrl = "http://localhost:8765/uploads/" + fileName; // store only path
	        mediaType = file.getContentType();
	    }

	    PostDTO dto = new PostDTO();
	    dto.setContent(content);
	    dto.setPrivacy(privacy);
	    dto.setMediaUrl(mediaUrl);
	    dto.setMediaType(mediaType);

	    PostDTO updatedPost = postService.updatePost(postId, dto);
	    return ResponseEntity.ok(updatedPost);
	}
	
	/***get the post with likes of the particular user
	 */
	@GetMapping("/post/{postId}/withLikes")
	public ResponseEntity<PostDTO> getPostWithLikes(@PathVariable final Long postId){
		PostDTO postDTO = postService.getPostWithLikes(postId);
		return new ResponseEntity<>(postDTO, HttpStatus.OK);
	}
	
	/***get all the post of the particular user
	 */
	@GetMapping
	public ResponseEntity<List<PostDTO>> getAllPosts() {
		return ResponseEntity.ok(postService.getAllPosts());
	}
	
	/***get the feed of the particular user
	 */
	@GetMapping("/feed/{userId}")
	public ResponseEntity<List<PostDTO>> getFeed(@PathVariable final Long userId) throws FacebookException {
		return ResponseEntity.ok(postService.getFeedForUser(userId));
	}
	
	
	@GetMapping("/profile")
	public ResponseEntity<List<PostDTO>> getPostByProfile(@RequestParam final Long userId, @RequestParam final Long friendId) throws FacebookException{
		return new ResponseEntity<>(postService.getPostsByProfile(userId, friendId),HttpStatus.OK);
	}
	
}
