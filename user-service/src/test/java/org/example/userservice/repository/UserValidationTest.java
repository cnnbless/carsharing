package org.example.userservice.repository;

import org.springframework.dao.DataIntegrityViolationException;
import org.example.userservice.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.dao.*;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserValidationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void saveUser_ThrowsException_WhenNameIsNull() {
        User user = User.builder()
                .driverLicenseNumber("UA-123456")
                .isLicenseValid(true)
                .build();

        assertThrows(DataIntegrityViolationException.class,
                () -> userRepository.saveAndFlush(user));
    }

    @Test
    void saveUser_ThrowsException_WhenDriverLicenseIsNotUnique() {
        entityManager.persistAndFlush(User.builder()
                .name("First User")
                .driverLicenseNumber("DUPLICATE-LIC")
                .isLicenseValid(true)
                .build());

        User user2 = User.builder()
                .name("Second User")
                .driverLicenseNumber("DUPLICATE-LIC")
                .isLicenseValid(true)
                .build();

        assertThrows(DataIntegrityViolationException.class,
                () -> userRepository.saveAndFlush(user2));
    }
}