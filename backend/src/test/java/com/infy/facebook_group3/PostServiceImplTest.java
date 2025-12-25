package com.infy.facebook_group3;

import com.infy.facebook_group3.dto.PostDTO;
import com.infy.facebook_group3.entity.Like;
import com.infy.facebook_group3.entity.Post;
import com.infy.facebook_group3.entity.User;
import com.infy.facebook_group3.exception.FacebookException;
import com.infy.facebook_group3.repository.*;
import com.infy.facebook_group3.service.PostPrivacyService;
import com.infy.facebook_group3.service.PostServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock private PostRepository postRepository;
    @Mock private UserRepository userRepository;
    @Mock private LikeRepository likeRepository;
    @Mock private ModelMapper modelMapper;
    @Mock private PostPrivacyRepository postPrivacyRepository;
    @Mock private FriendRepository friendRepository;
    @Mock private PostPrivacyService postPrivacyService;

    @InjectMocks private PostServiceImpl postService;

    private User user;
    private User friend;
    private Post post;
    private PostDTO postDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(1L);
        user.setUsername("testuser");

        friend = new User();
        friend.setUserId(2L);
        friend.setUsername("frienduser");

        post = new Post();
        post.setPostId(100L);
        post.setUser(user);
        post.setPrivacy("PUBLIC");
        post.setLikeCount(5);
        post.setContent("Hello World");

        postDTO = new PostDTO();
        postDTO.setPostId(100L);
        postDTO.setUserId(1L);
        postDTO.setPrivacy("PUBLIC");
        postDTO.setContent("Hello World");
    }

    // createPost
    @Test
    void createPostSuccess() throws FacebookException {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(modelMapper.map(postDTO, Post.class)).thenReturn(post);
        when(postRepository.save(post)).thenReturn(post);
        when(modelMapper.map(post, PostDTO.class)).thenReturn(postDTO);

        PostDTO result = postService.createPost(1L, postDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("PUBLIC", result.getPrivacy());
    }

    @Test
    void createPostUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(FacebookException.class, () -> postService.createPost(1L, postDTO));
    }

    // getPostsByUser
    @Test
    void getPostsByUserSuccess() throws FacebookException {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(postRepository.findByUser(user)).thenReturn(List.of(post));
        when(modelMapper.map(post, PostDTO.class)).thenReturn(postDTO);

        List<PostDTO> result = postService.getPostsByUser(1L);

        Assertions.assertEquals(1, result.size());
    }

    @Test
    void getPostsByUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(FacebookException.class, () -> postService.getPostsByUser(1L));
    }

    // getAllPosts
    @Test
    void getAllPostsSuccess() {
        Post nullLikePost = new Post();
        nullLikePost.setPostId(200L);
        nullLikePost.setLikeCount(null);
        nullLikePost.setUser(user);

        when(postRepository.findAll()).thenReturn(List.of(post, nullLikePost));
        when(modelMapper.map(any(Post.class), eq(PostDTO.class)))
                .thenAnswer(inv -> {
                    Post p = inv.getArgument(0);
                    PostDTO dto = new PostDTO();
                    dto.setPostId(p.getPostId());
                    dto.setLikeCount(p.getLikeCount());
                    return dto;
                });
        when(likeRepository.findByPost_PostId(anyLong())).thenReturn(Collections.emptyList());

        List<PostDTO> result = postService.getAllPosts();

        Assertions.assertEquals(2, result.size());
    }

    // deletePost
    @Test
    void deletePostSuccess() throws FacebookException {
        when(postRepository.findById(100L)).thenReturn(Optional.of(post));
        doNothing().when(postPrivacyService).deletePrivacyRules(100L);
        doNothing().when(postRepository).delete(post);

        postService.deletePost(100L);

        verify(postRepository, times(1)).delete(post);
    }

    @Test
    void deletePostNotFound() {
        when(postRepository.findById(999L)).thenReturn(Optional.empty());
        Assertions.assertThrows(FacebookException.class, () -> postService.deletePost(999L));
    }

    // updatePost
    @Test
    void updatePostSuccess() throws FacebookException {
        PostDTO updateDTO = new PostDTO();
        updateDTO.setContent("Updated");
        updateDTO.setPrivacy("PUBLIC");
        updateDTO.setMediaUrl("url");
        updateDTO.setMediaType("image");

        when(postRepository.findById(100L)).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(modelMapper.map(post, PostDTO.class)).thenReturn(updateDTO);

        PostDTO result = postService.updatePost(100L, updateDTO);

        Assertions.assertEquals("Updated", result.getContent());
    }

    @Test
    void updatePostNotFound() {
        when(postRepository.findById(999L)).thenReturn(Optional.empty());
        Assertions.assertThrows(FacebookException.class, () -> postService.updatePost(999L, postDTO));
    }

    @Test
    void updatePostCustomPrivacyDeletesRules() throws FacebookException {
        PostDTO updateDTO = new PostDTO();
        updateDTO.setPrivacy("PRIVATE");

        when(postRepository.findById(100L)).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(modelMapper.map(post, PostDTO.class)).thenReturn(updateDTO);

        postService.updatePost(100L, updateDTO);

        verify(postPrivacyService, times(1)).deletePrivacyRules(100L);
    }

    // getPostById
    @Test
    void getPostByIdSuccess() throws FacebookException {
        when(postRepository.findById(100L)).thenReturn(Optional.of(post));
        when(modelMapper.map(post, PostDTO.class)).thenReturn(postDTO);

        PostDTO result = postService.getPostById(100L);

        Assertions.assertEquals(100L, result.getPostId());
    }

    @Test
    void getPostByIdNotFound() {
        when(postRepository.findById(999L)).thenReturn(Optional.empty());
        Assertions.assertThrows(FacebookException.class, () -> postService.getPostById(999L));
    }

    // getPostWithLikes
    @Test
    void getPostWithLikesSuccess() throws FacebookException {
        Like like = new Like();
        like.setUser(user);

        when(postRepository.findById(100L)).thenReturn(Optional.of(post));
        when(likeRepository.findByPost_PostId(100L)).thenReturn(List.of(like));
        when(modelMapper.map(post, PostDTO.class)).thenReturn(postDTO);

        PostDTO result = postService.getPostWithLikes(100L);

        Assertions.assertEquals(5, result.getLikeCount());
    }

    @Test
    void getPostWithLikesNotFound() {
        when(postRepository.findById(999L)).thenReturn(Optional.empty());
        Assertions.assertThrows(FacebookException.class, () -> postService.getPostWithLikes(999L));
    }

    @Test
    void getFeedForUserCoversAll() throws FacebookException {
        Post ownPost = new Post();
        ownPost.setUser(user);
        ownPost.setPrivacy("PRIVATE");
        ownPost.setPostId(1L);

        Post publicPost = new Post();
        publicPost.setUser(friend);
        publicPost.setPrivacy("PUBLIC");
        publicPost.setPostId(2L);

        Post privatePost = new Post();
        privatePost.setUser(friend);
        privatePost.setPrivacy("PRIVATE");
        privatePost.setPostId(3L);

        Post friendsPost = new Post();
        friendsPost.setUser(friend);
        friendsPost.setPrivacy("FRIENDS");
        friendsPost.setPostId(4L);

        Post customPost = new Post();
        customPost.setUser(friend);
        customPost.setPrivacy("CUSTOM");
        customPost.setPostId(5L);

        when(postRepository.findFeedPosts(1L))
                .thenReturn(List.of(ownPost, publicPost, privatePost, friendsPost, customPost));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // ✅ Use eq() for both sides
        when(friendRepository.findByUserIdAndFriendId(1L, 2L))
                .thenReturn(Optional.of(new com.infy.facebook_group3.entity.Friend()));

        // ✅ Wrap raw values in eq()
        when(postPrivacyRepository.existsByPostAndUser(customPost, user))
                .thenReturn(true);

        when(modelMapper.map(any(Post.class), eq(PostDTO.class)))
                .thenAnswer(inv -> {
                    Post p = inv.getArgument(0);
                    PostDTO dto = new PostDTO();
                    dto.setPostId(p.getPostId());
                    dto.setPrivacy(p.getPrivacy());
                    return dto;
                });

        when(likeRepository.findByPost_PostId(anyLong())).thenReturn(Collections.emptyList());

        List<PostDTO> result = postService.getFeedForUser(1L);

        Assertions.assertTrue(result.stream().anyMatch(dto -> dto.getPrivacy().equals("PUBLIC")));
    }

    @Test
    void getFeedForUserUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(FacebookException.class, () -> postService.getFeedForUser(1L));
    }

    // getPostsByProfile
    @Test
    void getPostsByProfileCoversAll() throws FacebookException {
        Post publicPost = new Post();
        publicPost.setUser(friend);
        publicPost.setPrivacy("PUBLIC");
        publicPost.setPostId(1L);

        Post friendsPost = new Post();
        friendsPost.setUser(friend);
        friendsPost.setPrivacy("FRIENDS");
        friendsPost.setPostId(2L);

        Post customPost = new Post();
        customPost.setUser(friend);
        customPost.setPrivacy("CUSTOM");
        customPost.setPostId(3L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findById(2L)).thenReturn(Optional.of(friend));
        when(postRepository.findByUser(friend)).thenReturn(List.of(publicPost, friendsPost, customPost));
        when(friendRepository.findByUserIdAndFriendId(1L, 2L)).thenReturn(Optional.of(new com.infy.facebook_group3.entity.Friend()));
        when(postPrivacyRepository.existsByPostAndUser(customPost, user)).thenReturn(true);
        when(modelMapper.map(any(Post.class), eq(PostDTO.class)))
                .thenAnswer(inv -> {
                    Post p = inv.getArgument(0);
                    PostDTO dto = new PostDTO();
                    dto.setPostId(p.getPostId());
                    dto.setPrivacy(p.getPrivacy());
                    return dto;
                });

        List<PostDTO> result = postService.getPostsByProfile(1L, 2L);

        Assertions.assertEquals(3, result.size());
    }

    @Test
    void getPostsByProfileFriendNotFound() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        Assertions.assertThrows(FacebookException.class, () -> postService.getPostsByProfile(1L, 2L));
    }

    @Test
    void getPostsByProfileUserNotFound() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(friend));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(FacebookException.class, () -> postService.getPostsByProfile(1L, 2L));
    }
}