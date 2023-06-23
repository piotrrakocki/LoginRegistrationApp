package com.example.loginregistration.appUser;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class AppUserRepositoryTest {

    private final AppUserRepository appUserRepository;

    @Autowired
    AppUserRepositoryTest(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @AfterEach
    void tearDown() {
        appUserRepository.deleteAll();
    }

    @Test
    @DirtiesContext
    void canFindByEmail() {
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
        Optional<AppUser> expected = appUserRepository.findByEmail(email);
        // then
        assertTrue(expected.isPresent());
        assertEquals(email, expected.get().getEmail());
    }

    @Test
    @DirtiesContext
    void canNotFindByEmail() {
        // given
        String email = "jan.kowalski@email.com";
        // when
        Optional<AppUser> expected = appUserRepository.findByEmail(email);
        // then
        assertTrue(expected.isEmpty());
    }

    @Test
    @DirtiesContext
    public void testEnableAppUser() {
        // Given
        String email = "jan.kowalski@email.com";
        AppUser appUser = new AppUser(
                "Jan",
                "Kowalski",
                email,
                "password",
                AppUserRole.USER
        );
        appUserRepository.save(appUser);
        // When
        int updatedRows = appUserRepository.enableAppUser(email);
        // Then
        assertEquals(1, updatedRows);
    }
}