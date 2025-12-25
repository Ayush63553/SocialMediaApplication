package com.infy.facebook_group3;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

import java.util.Arrays;

import java.util.List;

import com.infy.facebook_group3.api.LikeController;
import com.infy.facebook_group3.dto.LikeDTO;

import com.infy.facebook_group3.exception.FacebookException;

import com.infy.facebook_group3.service.LikeServiceImpl;

import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;

import org.mockito.Mock;

import org.mockito.MockitoAnnotations;

import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;

class LikeControllerTest {

    @InjectMocks

    private LikeController likeController;

    @Mock

    private LikeServiceImpl likeService;

    private LikeDTO likeDTO;

    @BeforeEach

    void setUp() {

        MockitoAnnotations.openMocks(this);

        likeDTO = new LikeDTO();

        likeDTO.setPostId(1L);

        likeDTO.setUserId(101L);

    }

    @Test

    void testLikePost_success() throws FacebookException {

        // Arrange

        when(likeService.likePost(likeDTO)).thenReturn(likeDTO);

        // Act

        ResponseEntity<LikeDTO> response = likeController.likePost(likeDTO);

        // Assert

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        assertEquals(likeDTO, response.getBody());

        verify(likeService, times(1)).likePost(likeDTO);

    }

    @Test

    void testLikePost_exception() throws FacebookException {

        // Arrange

        when(likeService.likePost(likeDTO)).thenThrow(new FacebookException("Error liking post"));

        // Act & Assert

        assertThrows(FacebookException.class, () -> likeController.likePost(likeDTO));

        verify(likeService, times(1)).likePost(likeDTO);

    }

    @Test

    void testGetLikesByPost_success() throws FacebookException {

        // Arrange

        List<LikeDTO> expected = Arrays.asList(likeDTO);

        when(likeService.getLikesByPost(1L)).thenReturn(expected);

        // Act

        ResponseEntity<List<LikeDTO>> response = likeController.getLikesByPost(1L);

        // Assert

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals(expected, response.getBody());

        verify(likeService, times(1)).getLikesByPost(1L);

    }

    @Test

    void testGetLikesByPost_exception() throws FacebookException {

        // Arrange

        when(likeService.getLikesByPost(1L)).thenThrow(new FacebookException("Post not found"));

        // Act & Assert

        assertThrows(FacebookException.class, () -> likeController.getLikesByPost(1L));

        verify(likeService, times(1)).getLikesByPost(1L);

    }

    @Test

    void testUnlikePost_success() throws FacebookException {

        // Arrange

        doNothing().when(likeService).unlikePost(1L, 101L);

        // Act

        ResponseEntity<String> response = likeController.unlikePost(1L, 101L);

        // Assert

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals("Like Removed Successfully", response.getBody());

        verify(likeService, times(1)).unlikePost(1L, 101L);

    }

    @Test

    void testUnlikePost_exception() throws FacebookException {

        // Arrange

        doThrow(new FacebookException("Error unliking")).when(likeService).unlikePost(1L, 101L);

        // Act & Assert

        assertThrows(FacebookException.class, () -> likeController.unlikePost(1L, 101L));

        verify(likeService, times(1)).unlikePost(1L, 101L);

    }

}