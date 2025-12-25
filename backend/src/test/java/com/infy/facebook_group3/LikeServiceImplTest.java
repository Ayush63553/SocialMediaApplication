package com.infy.facebook_group3;

import com.infy.facebook_group3.dto.LikeDTO;
import com.infy.facebook_group3.entity.Like;
import com.infy.facebook_group3.entity.Post;
import com.infy.facebook_group3.entity.User;
import com.infy.facebook_group3.exception.FacebookException;
import com.infy.facebook_group3.repository.LikeRepository;
import com.infy.facebook_group3.repository.PostRepository;
import com.infy.facebook_group3.repository.UserRepository;
import com.infy.facebook_group3.service.LikeServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceImplTest {

    @Mock
    private LikeRepository likeRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private LikeServiceImpl likeService;

    private Post post;
    private User user;
    private Like like;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        post = new Post();
        post.setPostId(1L);
        post.setLikeCount(0);

        user = new User();
        user.setUserId(100L);
        user.setUsername("adarsh");
        user.setFirstName("Adarsh");
        user.setLastName("Patel");

        like = new Like();
        like.setLikeId(500L);
        like.setUser(user);
        like.setPost(post);
        like.setCreatedAt(LocalDateTime.now());
    }

    // ✅ Test: likePost - success case
    @Test
    void testLikePost_Success() throws FacebookException {
        LikeDTO dto = new LikeDTO();
        dto.setPostId(1L);
        dto.setUserId(100L);

        when(likeRepository.findByPost_PostIdAndUser_UserId(1L, 100L))
                .thenReturn(Optional.empty());
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(userRepository.findById(100L)).thenReturn(Optional.of(user));
        when(likeRepository.save(any(Like.class))).thenReturn(like);

        LikeDTO result = likeService.likePost(dto);

        assertNotNull(result);
        assertEquals(1L, result.getPostId());
        assertEquals(100L, result.getUserId());
        assertEquals("adarsh", result.getFullName());
        verify(postRepository, times(1)).save(post);
    }

    // ✅ Test: likePost - already liked
    @Test
    void testLikePost_AlreadyLiked() {
        LikeDTO dto = new LikeDTO();
        dto.setPostId(1L);
        dto.setUserId(100L);

        when(likeRepository.findByPost_PostIdAndUser_UserId(1L, 100L))
                .thenReturn(Optional.of(like));

        assertThrows(FacebookException.class, () -> likeService.likePost(dto));
    }

    // ✅ Test: unlikePost - success
    @Test
    void testUnlikePost_Success() throws FacebookException {
        when(likeRepository.findByPost_PostIdAndUser_UserId(1L, 100L))
                .thenReturn(Optional.of(like));

        likeService.unlikePost(1L, 100L);

        verify(likeRepository, times(1)).delete(like);
        verify(postRepository, times(1)).save(post);
        assertEquals(0, post.getLikeCount());
    }

    // ✅ Test: unlikePost - like not found
    @Test
    void testUnlikePost_NotFound() throws FacebookException {
        when(likeRepository.findByPost_PostIdAndUser_UserId(1L, 100L))
                .thenReturn(Optional.empty());

        likeService.unlikePost(1L, 100L);

        verify(likeRepository, never()).delete(any());
        verify(postRepository, never()).save(any());
    }

    // ✅ Test: getLikesByPost - success
    @Test
    void testGetLikesByPost_Success() throws FacebookException {
        post.setLikeCount(1);

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(likeRepository.findByPost_PostId(1L)).thenReturn(List.of(like));
        when(modelMapper.map(any(Like.class), eq(LikeDTO.class))).thenAnswer(invocation -> {
            Like l = invocation.getArgument(0);
            LikeDTO dto = new LikeDTO();
            dto.setLikeId(l.getLikeId());
            dto.setPostId(l.getPost().getPostId());
            dto.setUserId(l.getUser().getUserId());
            return dto;
        });

        List<LikeDTO> likes = likeService.getLikesByPost(1L);

        assertNotNull(likes);
        assertEquals(1, likes.size());
        assertEquals("Adarsh Patel", likes.get(0).getFullName());
        assertEquals(1, likes.get(0).getLikeCount());
    }

    // ✅ Test: getLikesByPost - post not found
    @Test
    void testGetLikesByPost_PostNotFound() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(FacebookException.class, () -> likeService.getLikesByPost(1L));
    }
}