package com.revature.revplay.controller;

import com.revature.revplay.service.SongService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StreamControllerTest {

    @Mock
    private SongService songService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private StreamController controller;

    @Test
    void shouldRecordStreamWithAuthenticatedUser() {

        when(authentication.getName()).thenReturn("pooja");

        ResponseEntity<String> response =
                controller.incrementStream(1L, authentication);

        assertEquals("Stream recorded cleanly.", response.getBody());

        verify(songService).recordPlay(1L, "pooja");
    }

    @Test
    void shouldRecordStreamWithAnonymousUser() {

        ResponseEntity<String> response =
                controller.incrementStream(1L, null);

        assertEquals("Stream recorded cleanly.", response.getBody());

        verify(songService).recordPlay(1L, null);
    }
}