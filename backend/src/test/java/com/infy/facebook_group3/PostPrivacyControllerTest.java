package com.infy.facebook_group3;

import com.infy.facebook_group3.api.PostPrivacyController;
import com.infy.facebook_group3.dto.PostPrivacyDTO;

import com.infy.facebook_group3.exception.FacebookException;

import com.infy.facebook_group3.service.PostPrivacyService;

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

class PostPrivacyControllerTest {

    @InjectMocks

    private PostPrivacyController postPrivacyController;

    @Mock

    private PostPrivacyService postPrivacyService;

    private PostPrivacyDTO dto;

    @BeforeEach

    void setUp() {

        MockitoAnnotations.openMocks(this);

        dto = new PostPrivacyDTO();

        dto.setPostId(1L);

        dto.setUserId(101L);

    }

    @Test

    void testAddRule_success() throws FacebookException {

        doNothing().when(postPrivacyService).addPrivacyRule(1L, 101L);

        ResponseEntity<String> response = postPrivacyController.addRule(1L, 101L);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals("Added Rule", response.getBody());

        verify(postPrivacyService, times(1)).addPrivacyRule(1L, 101L);

    }

    @Test

    void testAddRule_exception() throws FacebookException {

        doThrow(new FacebookException("Cannot add rule"))

                .when(postPrivacyService).addPrivacyRule(1L, 101L);

        assertThrows(FacebookException.class, () -> postPrivacyController.addRule(1L, 101L));

        verify(postPrivacyService, times(1)).addPrivacyRule(1L, 101L);

    }

    @Test

    void testGetRules_success() throws FacebookException {

        List<PostPrivacyDTO> expected = Arrays.asList(dto);

        when(postPrivacyService.getPrivacyRules(1L)).thenReturn(expected);

        ResponseEntity<List<PostPrivacyDTO>> response = postPrivacyController.getRules(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals(expected, response.getBody());

        verify(postPrivacyService, times(1)).getPrivacyRules(1L);

    }

    @Test

    void testGetRules_exception() throws FacebookException {

        when(postPrivacyService.getPrivacyRules(1L))

                .thenThrow(new FacebookException("Not found"));

        assertThrows(FacebookException.class, () -> postPrivacyController.getRules(1L));

        verify(postPrivacyService, times(1)).getPrivacyRules(1L);

    }

    @Test

    void testCanView_success() throws FacebookException {

        when(postPrivacyService.canUserViewPost(1L, 202L)).thenReturn(true);

        ResponseEntity<Boolean> response = postPrivacyController.canView(1L, 202L);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertTrue(response.getBody());

        verify(postPrivacyService, times(1)).canUserViewPost(1L, 202L);

    }

    @Test

    void testCanView_exception() throws FacebookException {

        when(postPrivacyService.canUserViewPost(1L, 202L))

                .thenThrow(new FacebookException("Access denied"));

        assertThrows(FacebookException.class, () -> postPrivacyController.canView(1L, 202L));

        verify(postPrivacyService, times(1)).canUserViewPost(1L, 202L);

    }

    @Test

    void testDeleteRules_success() throws FacebookException {

        doNothing().when(postPrivacyService).deletePrivacyRules(1L);

        ResponseEntity<String> response = postPrivacyController.deleteRules(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals("Rules deleted", response.getBody());

        verify(postPrivacyService, times(1)).deletePrivacyRules(1L);

    }

    @Test

    void testDeleteRules_exception() throws FacebookException {

        doThrow(new FacebookException("Delete failed"))

                .when(postPrivacyService).deletePrivacyRules(1L);

        assertThrows(FacebookException.class, () -> postPrivacyController.deleteRules(1L));

        verify(postPrivacyService, times(1)).deletePrivacyRules(1L);

    }

    @Test

    void testGetRulesByUser_success() throws FacebookException {

        List<PostPrivacyDTO> expected = Arrays.asList(dto);

        when(postPrivacyService.getByUser(101L)).thenReturn(expected);

        ResponseEntity<List<PostPrivacyDTO>> response = postPrivacyController.getRulesByUser(101L);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals(expected, response.getBody());

        verify(postPrivacyService, times(1)).getByUser(101L);

    }

    @Test

    void testGetRulesByUser_exception() throws FacebookException {

        when(postPrivacyService.getByUser(101L))

                .thenThrow(new FacebookException("Error fetching rules"));

        assertThrows(FacebookException.class, () -> postPrivacyController.getRulesByUser(101L));

        verify(postPrivacyService, times(1)).getByUser(101L);

    }

}