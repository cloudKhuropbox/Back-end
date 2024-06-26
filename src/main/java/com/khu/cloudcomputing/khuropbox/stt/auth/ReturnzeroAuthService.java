package com.khu.cloudcomputing.khuropbox.stt.auth;

import com.khu.cloudcomputing.khuropbox.apiPayload.GeneralException;
import com.khu.cloudcomputing.khuropbox.apiPayload.status.ErrorStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Scanner;

@Service
public class ReturnzeroAuthService {

    private final String clientId;
    private final String clientSecret;
    private final String Auth_URL;

    private final AuthTokenRepository authTokenRepository;

    public ReturnzeroAuthService(@Value("${returnzero.clientid}") String clientId, @Value("${returnzero.clientSecret}") String clientSecret, @Value("${returnzero.authUrl}") String auth_URL, AuthTokenRepository authTokenRepository) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.Auth_URL = auth_URL;
        this.authTokenRepository = authTokenRepository;
    }

    // 비동기적으로 토큰을 요청하고 저장하는 메서드
    public Mono<Void> retrieveAndStoreToken() {
        return Mono.defer(() -> {
            try {
                HttpURLConnection httpConn = setupConnection();
                String response = sendRequest(httpConn);
                storeToken(response);
                return Mono.empty();
            } catch (IOException | JSONException e) {
                return Mono.error(new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR.getCode(), "Failed to retrieve and store token", ErrorStatus._INTERNAL_SERVER_ERROR.getHttpStatus()));
            }
        });
    }

    private HttpURLConnection setupConnection() throws IOException {
        URL url = new URL(Auth_URL);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.setRequestMethod("POST");
        httpConn.setRequestProperty("accept", "application/json");
        httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        httpConn.setDoOutput(true);
        return httpConn;
    }

    private String sendRequest(HttpURLConnection httpConn) throws IOException {
        String data = "client_id=" + clientId + "&client_secret=" + clientSecret + "&grant_type=authorization_code";
        byte[] out = data.getBytes(StandardCharsets.UTF_8);

        try (OutputStream stream = httpConn.getOutputStream()) {
            stream.write(out);
        }

        int responseCode = httpConn.getResponseCode();
        InputStream responseStream = responseCode / 100 == 2 ? httpConn.getInputStream() : httpConn.getErrorStream();

        try (Scanner s = new Scanner(responseStream).useDelimiter("\\A")) {
            return s.hasNext() ? s.next() : "";
        }
    }

    private void storeToken(String response) throws JSONException {
        JSONObject json = new JSONObject(response);
        if (json.has("access_token") && json.has("expire_at")) {
            String accessToken = json.getString("access_token");
            long expireAt = json.getLong("expire_at");
            Instant expiresAt = Instant.ofEpochSecond(expireAt);
            saveOrUpdateToken(accessToken, expiresAt);
        } else {
            throw new IllegalStateException("Invalid token response: " + response);
        }
    }

    private void saveOrUpdateToken(String accessToken, Instant expiresAt) {
        AuthTokenEntity tokenStorage = authTokenRepository.findById(1L).orElse(new AuthTokenEntity(accessToken, expiresAt));
        authTokenRepository.save(tokenStorage);
    }

    // 비동기적으로 토큰을 확인하고 필요하면 갱신하는 메서드
    public Mono<String> checkValidToken() {
        return Mono.defer(() -> {
                    AuthTokenEntity token = getToken();
                    if (token == null || token.getExpiresAt().isBefore(Instant.now())) {
                        return retrieveAndStoreToken().then(Mono.fromCallable(() -> getToken().getAccessToken()));
                    }
                    return Mono.just(token.getAccessToken());
                }).onErrorMap(IOException.class, e -> new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR.getCode(), "Failed to check valid token", ErrorStatus._INTERNAL_SERVER_ERROR.getHttpStatus()))
                .onErrorMap(JSONException.class, e -> new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR.getCode(), "Failed to parse token response", ErrorStatus._INTERNAL_SERVER_ERROR.getHttpStatus()));
    }

    private AuthTokenEntity getToken() {
        return authTokenRepository.findById(1L).orElse(null);
    }
}
