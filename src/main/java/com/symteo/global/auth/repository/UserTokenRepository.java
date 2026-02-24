package com.symteo.global.auth.repository;

import com.symteo.domain.user.entity.UserTokens;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserTokenRepository extends JpaRepository<UserTokens, Long> {
    Optional<UserTokens> findByRefreshToken(String refreshToken);

    Optional<UserTokens> findByUser_IdAndDeviceId(Long userId, String deviceId);

    void deleteByUser_IdAndDeviceId(Long userId, String deviceId);

    void deleteAllByUser_Id(Long userId);
}