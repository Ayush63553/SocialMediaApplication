package com.infy.facebook_group3;

import com.infy.facebook_group3.dto.CommentDTO;
import com.infy.facebook_group3.entity.Comment;
import com.infy.facebook_group3.entity.Post;
import com.infy.facebook_group3.entity.User;
import com.infy.facebook_group3.exception.FacebookException;
import com.infy.facebook_group3.repository.CommentRepository;
import com.infy.facebook_group3.repository.PostRepository;
import com.infy.facebook_group3.repository.UserRepository;
import com.infy.facebook_group3.service.CommentServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock private CommentRepository commentRepository;
    @Mock private PostRepository postRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private CommentServiceImpl commentService;

    private User user;
    private Post post;
    private Comment comment;
    private CommentDTO commentDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(1L);
        user.setUsername("testuser");
        user.setProfilePictureUrl("pic.jpg");

        post = new Post();
        post.setPostId(100L);
        post.setUser(user);

        comment = new Comment();
        comment.setCommentId(10L);
        comment.setContent("old content");
        comment.setUser(user);
        comment.setPost(post);

        commentDTO = new CommentDTO();
        commentDTO.setCommentId(10L);
        commentDTO.setContent("new content");
        commentDTO.setUserId(1L);
        commentDTO.setPostId(100L);
    }

    // addComment
    @Test void addCommentSuccess() {
        when(postRepository.findById(100L)).thenReturn(Optional.of(post));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDTO result = commentService.addComment(commentDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("testuser", result.getUsername());
    }
    @Test void addCommentPostNotFound() {
        when(postRepository.findById(100L)).thenReturn(Optional.empty());
        Assertions.assertThrows(FacebookException.class, () -> commentService.addComment(commentDTO));
    }
    @Test void addCommentUserNotFound() {
        when(postRepository.findById(100L)).thenReturn(Optional.of(post));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(FacebookException.class, () -> commentService.addComment(commentDTO));
    }
    @Test void getCommentsByPostEmpty() {
        when(commentRepository.findByPost_PostId(200L)).thenReturn(List.of());
        List<CommentDTO> result = commentService.getCommentsByPost(200L);
        Assertions.assertTrue(result.isEmpty());
    }

    // getCommentsByUser
    @Test void getCommentsByUserSuccess() {
        when(commentRepository.findByUser_UserId(1L)).thenReturn(List.of(comment));

        List<CommentDTO> result = commentService.getCommentsByUser(1L);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals("testuser", result.get(0).getUsername());
    }
    @Test void getCommentsByUserEmpty() {
        when(commentRepository.findByUser_UserId(2L)).thenReturn(List.of());
        List<CommentDTO> result = commentService.getCommentsByUser(2L);
        Assertions.assertTrue(result.isEmpty());
    }

    // updateComment
    @Test void updateCommentSuccess() {
        when(commentRepository.findById(10L)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDTO result = commentService.updateComment(10L, 1L, "updated");

        Assertions.assertEquals("updated", result.getContent());
    }
    @Test void updateCommentNotFound() {
        when(commentRepository.findById(10L)).thenReturn(Optional.empty());
        Assertions.assertThrows(FacebookException.class, () -> commentService.updateComment(10L, 1L, "abc"));
    }
    @Test void updateCommentUnauthorized() {
        User otherUser = new User();
        otherUser.setUserId(2L);
        comment.setUser(otherUser);
        when(commentRepository.findById(10L)).thenReturn(Optional.of(comment));

        Assertions.assertThrows(FacebookException.class, () -> commentService.updateComment(10L, 1L, "abc"));
    }

    // deleteComment
    @Test void deleteCommentByOwner() {
        when(commentRepository.findById(10L)).thenReturn(Optional.of(comment));

        commentService.deleteComment(10L, 1L);

        verify(commentRepository, times(1)).delete(comment);
    }
    @Test void deleteCommentByPostOwner() {
        User postOwner = new User();
        postOwner.setUserId(2L);
        post.setUser(postOwner);
        comment.setPost(post);

        when(commentRepository.findById(10L)).thenReturn(Optional.of(comment));

        commentService.deleteComment(10L, 2L);

        verify(commentRepository, times(1)).delete(comment);
    }
    @Test void deleteCommentNotFound() {
        when(commentRepository.findById(10L)).thenReturn(Optional.empty());
        Assertions.assertThrows(FacebookException.class, () -> commentService.deleteComment(10L, 1L));
    }
    @Test void deleteCommentUnauthorized() {
        User stranger = new User();
        stranger.setUserId(3L);
        Post strangersPost = new Post();
        strangersPost.setUser(stranger);
        comment.setUser(stranger);
        comment.setPost(strangersPost);

        when(commentRepository.findById(10L)).thenReturn(Optional.of(comment));

        Assertions.assertThrows(FacebookException.class, () -> commentService.deleteComment(10L, 1L));
    }
}