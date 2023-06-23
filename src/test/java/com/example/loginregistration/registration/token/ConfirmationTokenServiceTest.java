package com.example.loginregistration.registration.token;

import com.example.loginregistration.appUser.AppUser;
import com.example.loginregistration.appUser.AppUserRepository;
import com.example.loginregistration.appUser.AppUserRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DataJpaTest
@DirtiesContext
class ConfirmationTokenServiceTest {

    @Mock
    private ConfirmationTokenRepository confirmationTokenRepository;
    @Mock
    private AppUserRepository appUserRepository;
    private ConfirmationTokenService confirmationTokenService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        confirmationTokenService = new ConfirmationTokenService(confirmationTokenRepository);
    }

    @AfterEach
    void tearDown() {
        appUserRepository.deleteAll();
        confirmationTokenRepository.deleteAll();
    }

    @Test
    void canSaveConfirmationToken() {
        // give
        AppUser appUser =  new AppUser(
                "Jan",
                "Kowalski",
                "jan.kowalski@email.com",
                "password",
                AppUserRole.USER);
        appUserRepository.save(appUser);
        ConfirmationToken confirmationToken = new ConfirmationToken(
                "123",
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                appUser
        );
        // when
        confirmationTokenService.saveConfirmationToken(confirmationToken);
        // then
        verify(confirmationTokenRepository).save(confirmationToken);

    }

    @Test
    void canGetToken() {
        // given
        String email = "jan.kowalski@emial.com";
        AppUser appUser = new AppUser(
                "Jan",
                "Kowalski",
                email,
                "password",
                AppUserRole.USER
        );
        appUserRepository.save(appUser);
        String token = "123";
        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                appUser
        );
        confirmationTokenRepository.save(confirmationToken);
        // when
        when(confirmationTokenRepository.findByToken(token)).thenReturn(Optional.of(confirmationToken));
        Optional<ConfirmationToken> expected = confirmationTokenService.getToken(token);
        // then
        assertTrue(expected.isPresent());
        assertEquals(confirmationToken, expected.orElse(null));
    }

    @Test
    void canNotGetToken() {
        // when
        when(confirmationTokenRepository.findByToken("token")).thenReturn(Optional.empty());
        Optional<ConfirmationToken> expected = confirmationTokenService.getToken("token");
        // then
        assertEquals(Optional.empty(), expected);
    }

    @Test
    void canNotSetConfirmedAt() {
        //when
        when(confirmationTokenRepository.updateConfirmedAt("token", LocalDateTime.now())).thenReturn(0);
        int expected = confirmationTokenService.setConfirmedAt("token");
        //then
        assertEquals(0, expected);
    }
}