package com.khu.cloudcomputing.khuropbox.configuration.aws;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class AwsConfig {

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${cloud.aws.credentials.session-token}")
    private String sessionToken; // Added for session token support

    @Value("${cloud.aws.region.static}")
    private String region;

    @Bean
    public S3Client s3Client() {
        AwsSessionCredentials credentials = AwsSessionCredentials.create(accessKey, secretKey, sessionToken);

        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

    @Bean
    public S3Presigner s3Presigner() {
        AwsSessionCredentials credentials = AwsSessionCredentials.create(accessKey, secretKey, sessionToken);

        return S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

    @Bean
    public S3AsyncClient s3AsyncClient() {
        AwsSessionCredentials credentials = AwsSessionCredentials.create(accessKey, secretKey, sessionToken);

        return S3AsyncClient.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }
}