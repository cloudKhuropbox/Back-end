package com.khu.cloudcomputing.khuropbox.stt.auth;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@Entity
public class AuthTokenEntity {
    @Id
    private Long id = 1L;

    private String accessToken;
    private Instant expiresAt;

    protected AuthTokenEntity() {
        this.accessToken = null;
        this.expiresAt = null;
    }

    public AuthTokenEntity(String accessToken, Instant expiresAt) {
        this.accessToken = accessToken;
        this.expiresAt = expiresAt;
    }
}
