package com.khu.cloudcomputing.khuropbox.stt.auth;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthTokenRepository extends JpaRepository<AuthTokenEntity, Long> {
}
