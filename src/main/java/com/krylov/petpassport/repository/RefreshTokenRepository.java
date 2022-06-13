package com.krylov.petpassport.repository;

import com.krylov.petpassport.model.RefreshToken;
import com.krylov.petpassport.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    @Override
    Optional<RefreshToken> findById(Long id);

    Optional<RefreshToken> findByToken(String token);

    @Modifying
    void deleteByUser(User user);
}
