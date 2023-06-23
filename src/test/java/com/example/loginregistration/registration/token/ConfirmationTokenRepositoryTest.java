package com.example.loginregistration.registration.token;

import com.example.loginregistration.appUser.AppUser;
import com.example.loginregistration.appUser.AppUserRepository;
import com.example.loginregistration.appUser.AppUserRole;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ConfirmationTokenRepositoryTest {

    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final AppUserRepository appUserRepository;

    @Autowired
    ConfirmationTokenRepositoryTest(ConfirmationTokenRepository confirmationTokenRepository, AppUserRepository appUserRepository) {
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.appUserRepository = appUserRepository;
    }

    @AfterEach
    void tearDown() {
        confirmationTokenRepository.deleteAll();
    }

    @Test
    @DirtiesContext
    void canFindByToken() {
        // given
        String token = "123";
        AppUser appUser =  new AppUser("Jan", "Kowalski", "jan.kowalski@email.com", "password", AppUserRole.USER);
        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                appUser
        );
        appUserRepository.save(appUser);
        confirmationTokenRepository.save(confirmationToken);
        // when
        Optional<ConfirmationToken> expected = confirmationTokenRepository.findByToken(token);
        // then
        assertTrue(expected.isPresent());
        assertEquals(token, expected.get().getToken());
    }

    @Test
    @DirtiesContext
    void canNotFindByToken() {
        // given
        String token = "123";
        // when
        Optional<ConfirmationToken> expected = confirmationTokenRepository.findByToken(token);
        // then
        assertTrue(expected.isEmpty());
    }

    @Test
    @DirtiesContext
    void updateConfirmedAt() {
        // given
        String token = "123";
        AppUser appUser =  new AppUser("Jan", "Kowalski", "jan.kowalski@email.com", "password", AppUserRole.USER);
        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                appUser
        );
        appUserRepository.save(appUser);
        confirmationTokenRepository.save(confirmationToken);
        // when
        int expected = confirmationTokenRepository.updateConfirmedAt(token, LocalDateTime.now());
        // then
        assertEquals(1,expected);
    }
}