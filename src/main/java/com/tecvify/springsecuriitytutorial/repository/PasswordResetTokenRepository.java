package com.tecvify.springsecuriitytutorial.repository;

import com.tecvify.springsecuriitytutorial.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    PasswordResetToken findByToken(String token);
}
