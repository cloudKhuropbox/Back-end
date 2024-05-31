package com.khu.cloudcomputing.khuropbox.stt.controller;

import com.khu.cloudcomputing.khuropbox.apiPayload.ApiResponse;
import com.khu.cloudcomputing.khuropbox.apiPayload.status.SuccessStatus;
import com.khu.cloudcomputing.khuropbox.configuration.AwsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping("/files")
public class RequestController {
    private final AwsService awsService;
    private final String apiGatewayUrl;

    public RequestController(AwsService awsService, @Value("${cloud.aws.lambda.url}")String apiGatewayUrl) {
        this.awsService = awsService;
        this.apiGatewayUrl= apiGatewayUrl;
    }

    @PostMapping("/upload-and-transcribe")
    public ResponseEntity<ApiResponse<String>> uploadAndTranscribe(@RequestBody Map<String, String> params) {
        String dirName = params.get("dirName");
        Integer id = Integer.valueOf(params.get("id"));
        String fileType = params.get("fileType");
        String callbackUrl = params.get("callbackUrl");

        // S3 키 생성
        String key = dirName + "/" + id + "." + fileType;

        // Presigned URL 생성
        String presignedUrl = awsService.generateDownloadPresignedUrl(key).toString();

        // Lambda 함수 호출 (API Gateway 엔드포인트 사용)

        String lambdaResponse = callLambdaFunction(apiGatewayUrl, presignedUrl, callbackUrl);

        return ResponseEntity.ok(new ApiResponse<>(SuccessStatus._OK, lambdaResponse));
    }


    private String callLambdaFunction(String apiGatewayUrl, String presignedUrl, String callbackUrl) {
        // HTTP 클라이언트를 사용하여 Lambda 함수 호출
        // 예제에서는 간단히 HttpURLConnection을 사용
        try {
            URL url = new URL(apiGatewayUrl + "?presignedUrl=" + presignedUrl + "&callbackUrl=" + callbackUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            InputStream responseStream = connection.getInputStream();

            return new String(responseStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}
