package com.tecvify.springsecuriitytutorial.repository;

import com.tecvify.springsecuriitytutorial.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationRepository extends JpaRepository<VerificationToken, Long> {
    VerificationToken findByToken(String token);
}
