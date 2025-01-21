package com.tien.truyen247be.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserById(Long id);

    Optional<User> findByEmail(String username);

    User findByResetToken(String resetToken);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    List<User> findByPremiumTrueAndPremiumExpiryDateBefore(LocalDate expiryDate);
}
