package com.example.loginregistration.appUser;

import com.example.loginregistration.registration.token.ConfirmationToken;
import com.example.loginregistration.registration.token.ConfirmationTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DataJpaTest
class AppUserServiceTest {

    @Mock
    private AppUserRepository appUserRepository;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private ConfirmationTokenService confirmationTokenService;
    @InjectMocks
    private AppUserService appUserService;
    @Captor
    private ArgumentCaptor<AppUser> appUserCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        appUserService = new AppUserService(appUserRepository, bCryptPasswordEncoder, confirmationTokenService);
    }

    @Test
    void canLoadUserByUsername() {
        // given
        String email = "jan.kowalski@email.com";
        AppUser appUser = new AppUser(
                "Jan",
                "Kowalski",
                email,
                "password",
                AppUserRole.USER
        );
        appUserRepository.save(appUser);
        // when
        when(appUserRepository.findByEmail(email)).thenReturn(Optional.of(appUser));
        UserDetails userDetails = appUserService.loadUserByUsername(email);
        // then
        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
    }

    @Test
    void canNotLoadUserByUsername() {
        // given
        String email = "jan.kowalski@email.com";
        // when
        when(appUserRepository.findByEmail(email)).thenReturn(Optional.empty());
        // then
        assertThrows(UsernameNotFoundException.class, () -> appUserService.loadUserByUsername(email));
    }

    @Test
    void canSignUpUser() {
        // given
        String email = "jan.kowalski@email.com";
        AppUser appUser = new AppUser(
                "Jan",
                "Kowalski",
                email,
                "password",
                AppUserRole.USER
        );
        // when
        when(appUserRepository.findByEmail(appUser.getEmail())).thenReturn(Optional.empty());
        when(bCryptPasswordEncoder.encode(appUser.getPassword())).thenReturn("encodedPassword");

        String token = appUserService.signUpUser(appUser);

        // then
        assertNotNull(token);
        verify(appUserRepository).save(appUserCaptor.capture());
        AppUser capturedUser = appUserCaptor.getValue();
        assertEquals(email, capturedUser.getEmail());
        assertEquals("encodedPassword", capturedUser.getPassword());
        verify(confirmationTokenService).saveConfirmationToken(any(ConfirmationToken.class));

    }

    @Test
    void willThrowIllegalStateException() {
        // given
        String email = "jan.kowalski@email.com";
        AppUser appUser = new AppUser(
                "Jan",
                "Kowalski",
                email,
                "password",
                AppUserRole.USER
        );
        // when
        when(appUserRepository.findByEmail(email)).thenReturn(Optional.of(appUser));
        // then
        assertThrows(IllegalStateException.class, () -> appUserService.signUpUser(appUser));
        verify(appUserRepository, never()).save(any(AppUser.class));
        verify(confirmationTokenService, never()).saveConfirmationToken(any(ConfirmationToken.class));
    }

    @Test
    void canEnableAppUser() {
        // given
        String email = "jan.kowalski@email.com";
        // when
        when(appUserRepository.enableAppUser(email)).thenReturn(1);
        int expected = appUserService.enableAppUser(email);
        // then
        assertEquals(1, expected);
        verify(appUserRepository, times(1)).enableAppUser(email);
    }
}