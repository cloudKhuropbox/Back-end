package com.khu.cloudcomputing.khuropbox.auth.security;

import com.khu.cloudcomputing.khuropbox.auth.model.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Service
public class TokenProvider {
    @Value("${spring.jwt.secret}")
    private String secretKeyPlain;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        secretKey=Keys.hmacShaKeyFor(secretKeyPlain.getBytes(StandardCharsets.UTF_8));
    }


    public String create(UserEntity userEntity) {
        Date expiryDate = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24);
        //하루 후 토큰 만료되도록 설정
        return Jwts.builder()
                .signWith(secretKey, Jwts.SIG.HS512)
                .subject(userEntity.getId())
                .issuer("demo app")
                .issuedAt(new Date())
                .expiration(expiryDate)
                .compact();
    }
    /*
    위 메서드는 jwt library를 사용해 jwt token을 생성한다.
    이 과정에서 임의로 지정한 secretkey를 개인키로 사용한다
    개인키의 노출을 막기 위해 application.properties에 작성하고 git ignore를 통해 git에 업로드하지 않았다

     */

    public String validateAndGetUserId(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }
    /*
    이 method는 토큰을 디코딩, 파싱, 위조여부확인 을 담당한다.
    해당 작업을 완료하고 원하는 subject=user의 id르 리턴한다.
     */
}
