package com.krylov.petpassport.service.impl;

import com.krylov.petpassport.exception.TokenRefreshException;
import com.krylov.petpassport.exception.UserNotFoundException;
import com.krylov.petpassport.model.RefreshToken;
import com.krylov.petpassport.repository.RefreshTokenRepository;
import com.krylov.petpassport.repository.UserRepository;
import com.krylov.petpassport.service.RefreshTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenService {

    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Autowired
    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {

        log.info("In findByToken: token " + token + " has been found");

        return refreshTokenRepository.findByToken(token);
    }

    @Override
    public RefreshToken createRefreshToken(Long userId) {
        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUser(userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("User by id " + userId + " was not found")));
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString());

        refreshTokenRepository.save(refreshToken);

        log.info("In createRefreshToken: token for user " + userId + " has been successfully created");

        return refreshToken;
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(token.getToken(), "Refresh token was expired");
        }

        log.info("In verifyExpiration: token " + token + "has been verified");

        return token;
    }

    @Override
    @Transactional
    public void deleteByUserId(Long userId) {
        boolean exists = userRepository.existsById(userId);

        if (!exists) {
            throw new UserNotFoundException("User with id " + userId + "does not exist");
        }

        refreshTokenRepository.deleteByUser(userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("User by id " + userId + " was not found")));

        log.info("In deleteByUserId: token successfully deleted by user id: " + userId);
    }
}
