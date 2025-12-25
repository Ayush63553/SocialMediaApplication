package com.infy.facebook_group3;

import com.infy.facebook_group3.dto.PostPrivacyDTO;
import com.infy.facebook_group3.entity.Post;
import com.infy.facebook_group3.entity.PostPrivacy;
import com.infy.facebook_group3.entity.User;
import com.infy.facebook_group3.exception.FacebookException;
import com.infy.facebook_group3.repository.FriendRepository;
import com.infy.facebook_group3.repository.PostPrivacyRepository;
import com.infy.facebook_group3.repository.PostRepository;
import com.infy.facebook_group3.repository.UserRepository;
import com.infy.facebook_group3.service.PostPrivacyServiceImpl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class PostPrivacyServiceImplTest {

    @Mock
    private PostPrivacyRepository postPrivacyRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FriendRepository friendRepository;

    @InjectMocks
    private PostPrivacyServiceImpl postPrivacyService;

    private Post post;
    private User user;
    private PostPrivacy postPrivacy;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(1L);

        post = new Post();
        post.setPostId(101L);

        postPrivacy = new PostPrivacy();
        postPrivacy.setPrivacyId(1001L);
        postPrivacy.setPost(post);
        postPrivacy.setUser(user);
    }

    @Test
    void addPrivacyRuleSuccessTest() throws FacebookException {
        Mockito.when(postRepository.findById(101L)).thenReturn(Optional.of(post));
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(postPrivacyRepository.existsByPostAndUser(post, user)).thenReturn(false);

        postPrivacyService.addPrivacyRule(101L, 1L);

        Mockito.verify(postPrivacyRepository, Mockito.times(1)).save(Mockito.any(PostPrivacy.class));
    }

    @Test
    void addPrivacyRuleAlreadyExistsTest() {
        Mockito.when(postRepository.findById(101L)).thenReturn(Optional.of(post));
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(postPrivacyRepository.existsByPostAndUser(post, user)).thenReturn(true);

        Assertions.assertThrows(FacebookException.class,
                () -> postPrivacyService.addPrivacyRule(101L, 1L));
    }

    @Test
    void addPrivacyRulePostNotFoundTest() {
        Mockito.when(postRepository.findById(999L)).thenReturn(Optional.empty());

        Assertions.assertThrows(FacebookException.class,
                () -> postPrivacyService.addPrivacyRule(999L, 1L));
    }

    @Test
    void canUserViewPostTrueTest() throws FacebookException {
        Mockito.when(postRepository.findById(101L)).thenReturn(Optional.of(post));
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(postPrivacyRepository.existsByPostAndUser(post, user)).thenReturn(true);

        boolean result = postPrivacyService.canUserViewPost(101L, 1L);

        Assertions.assertTrue(result);
    }

    @Test
    void canUserViewPostFalseTest() throws FacebookException {
        Mockito.when(postRepository.findById(101L)).thenReturn(Optional.of(post));
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(postPrivacyRepository.existsByPostAndUser(post, user)).thenReturn(false);

        boolean result = postPrivacyService.canUserViewPost(101L, 1L);

        Assertions.assertFalse(result);
    }

    @Test
    void canUserViewPostUserNotFoundTest() {
        Mockito.when(postRepository.findById(101L)).thenReturn(Optional.of(post));
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(FacebookException.class,
                () -> postPrivacyService.canUserViewPost(101L, 1L));
    }

    @Test
    void getPrivacyRulesSuccessTest() throws FacebookException {
        List<PostPrivacy> list = new ArrayList<>();
        list.add(postPrivacy);

        Mockito.when(postRepository.findById(101L)).thenReturn(Optional.of(post));
        Mockito.when(postPrivacyRepository.findByPost(post)).thenReturn(list);

        List<PostPrivacyDTO> result = postPrivacyService.getPrivacyRules(101L);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(101L, result.get(0).getPostId());
        Assertions.assertEquals(1L, result.get(0).getUserId());
    }

    @Test
    void getPrivacyRulesPostNotFoundTest() {
        Mockito.when(postRepository.findById(999L)).thenReturn(Optional.empty());

        Assertions.assertThrows(FacebookException.class,
                () -> postPrivacyService.getPrivacyRules(999L));
    }

    @Test
    void deletePrivacyRulesSuccessTest() throws FacebookException {
        List<PostPrivacy> list = new ArrayList<>();
        list.add(postPrivacy);

        Mockito.when(postRepository.findById(101L)).thenReturn(Optional.of(post));
        Mockito.when(postPrivacyRepository.findByPost(post)).thenReturn(list);

        postPrivacyService.deletePrivacyRules(101L);

        Mockito.verify(postPrivacyRepository, Mockito.times(1)).delete(postPrivacy);
    }

    @Test
    void deletePrivacyRulesEmptyTest() throws FacebookException {
        List<PostPrivacy> list = new ArrayList<>();

        Mockito.when(postRepository.findById(101L)).thenReturn(Optional.of(post));
        Mockito.when(postPrivacyRepository.findByPost(post)).thenReturn(list);

        postPrivacyService.deletePrivacyRules(101L);

        Mockito.verify(postPrivacyRepository, Mockito.never()).delete(Mockito.any(PostPrivacy.class));
    }

    @Test
    void getByUserSuccessTest() throws FacebookException {
        List<PostPrivacy> list = new ArrayList<>();
        list.add(postPrivacy);

        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        Mockito.when(postPrivacyRepository.findByUser(user)).thenReturn(list);

        List<PostPrivacyDTO> result = postPrivacyService.getByUser(1L);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(1L, result.get(0).getUserId());
    }

    @Test
    void getByUserNotFoundTest() {
        Mockito.when(userRepository.findById(99L)).thenReturn(Optional.empty());

        Assertions.assertThrows(FacebookException.class,
                () -> postPrivacyService.getByUser(99L));
    }
}