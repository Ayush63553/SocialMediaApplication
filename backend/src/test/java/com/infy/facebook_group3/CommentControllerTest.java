package com.infy.facebook_group3;

import com.infy.facebook_group3.api.CommentController;
import com.infy.facebook_group3.dto.CommentDTO;

import com.infy.facebook_group3.service.CommentService;

import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;

import org.mockito.Mock;

import org.mockito.MockitoAnnotations;

import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;

import java.util.Arrays;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

class CommentControllerTest {

    @InjectMocks

    private CommentController commentController;

    @Mock

    private CommentService commentService;

    private CommentDTO commentDTO;

    @BeforeEach

    void setUp() {

        MockitoAnnotations.openMocks(this);

        commentDTO = new CommentDTO();

        commentDTO.setCommentId(1L);

        commentDTO.setPostId(10L);

        commentDTO.setUserId(100L);

        commentDTO.setContent("Sample Comment");

    }

    @Test

    void testAddComment_success() {

        when(commentService.addComment(commentDTO)).thenReturn(commentDTO);

        ResponseEntity<CommentDTO> response = commentController.addComment(commentDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals(commentDTO, response.getBody());

        verify(commentService, times(1)).addComment(commentDTO);

    }

    @Test

    void testAddComment_exception() {

        when(commentService.addComment(commentDTO)).thenThrow(new RuntimeException("Add failed"));

        assertThrows(RuntimeException.class, () -> commentController.addComment(commentDTO));

        verify(commentService, times(1)).addComment(commentDTO);

    }

    @Test

    void testGetCommentsByPost_success() {

        List<CommentDTO> expected = Arrays.asList(commentDTO);

        when(commentService.getCommentsByPost(10L)).thenReturn(expected);

        ResponseEntity<List<CommentDTO>> response = commentController.getCommentsByPost(10L);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals(expected, response.getBody());

        verify(commentService, times(1)).getCommentsByPost(10L);

    }

    @Test

    void testGetCommentsByPost_exception() {

        when(commentService.getCommentsByPost(10L)).thenThrow(new RuntimeException("Not found"));

        assertThrows(RuntimeException.class, () -> commentController.getCommentsByPost(10L));

        verify(commentService, times(1)).getCommentsByPost(10L);

    }

    @Test

    void testGetCommentsByUser_success() {

        List<CommentDTO> expected = Arrays.asList(commentDTO);

        when(commentService.getCommentsByUser(100L)).thenReturn(expected);

        ResponseEntity<List<CommentDTO>> response = commentController.getCommentsByUser(100L);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals(expected, response.getBody());

        verify(commentService, times(1)).getCommentsByUser(100L);

    }

    @Test

    void testGetCommentsByUser_exception() {

        when(commentService.getCommentsByUser(100L)).thenThrow(new RuntimeException("Error"));

        assertThrows(RuntimeException.class, () -> commentController.getCommentsByUser(100L));

        verify(commentService, times(1)).getCommentsByUser(100L);

    }

    @Test

    void testUpdateComment_success() {

        when(commentService.updateComment(1L, 100L, "Updated")).thenReturn(commentDTO);

        ResponseEntity<CommentDTO> response = commentController.updateComment(1L, 100L, "Updated");

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals(commentDTO, response.getBody());

        verify(commentService, times(1)).updateComment(1L, 100L, "Updated");

    }

    @Test

    void testUpdateComment_exception() {

        when(commentService.updateComment(1L, 100L, "Updated")).thenThrow(new RuntimeException("Update failed"));

        assertThrows(RuntimeException.class, () -> commentController.updateComment(1L, 100L, "Updated"));

        verify(commentService, times(1)).updateComment(1L, 100L, "Updated");

    }

    @Test

    void testDeleteComment_success() {

        doNothing().when(commentService).deleteComment(1L, 100L);

        ResponseEntity<Void> response = commentController.deleteComment(1L, 100L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        assertNull(response.getBody());

        verify(commentService, times(1)).deleteComment(1L, 100L);

    }

    @Test

    void testDeleteComment_exception() {

        doThrow(new RuntimeException("Delete failed")).when(commentService).deleteComment(1L, 100L);

        assertThrows(RuntimeException.class, () -> commentController.deleteComment(1L, 100L));

        verify(commentService, times(1)).deleteComment(1L, 100L);

    }

}