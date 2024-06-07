package com.khu.cloudcomputing.khuropbox.configuration.aws;


import com.khu.cloudcomputing.khuropbox.files.dto.FileMultipartUploadUrlDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.BytesWrapper;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class AwsService {
    private final S3Client s3Client;
    private final S3AsyncClient s3AsyncClient;
    private final S3Presigner s3Presigner;
    private final String bucket;
    private final String region;

    public AwsService(S3Client s3Client,S3AsyncClient s3AsyncClient,S3Presigner s3Presigner, @Value("${cloud.aws.s3.bucket}") String bucket, @Value("${cloud.aws.region.static}") String region) {
        this.s3Client = s3Client;
        this.s3AsyncClient = s3AsyncClient;
        this.s3Presigner = s3Presigner;
        this.bucket = bucket;
        this.region = region;
    }

    public CreateMultipartUploadResponse createMultipartUpload(String key) {
        return s3Client.createMultipartUpload(CreateMultipartUploadRequest.builder()
                .bucket(bucket)
                .key(key)
                .build());
    }

    public URL generateDownloadPresignedUrl(String fileUrl) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(fileUrl)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .getObjectRequest(getObjectRequest)
                .build();

        return s3Presigner.presignGetObject(presignRequest).url();
    }

    public String generateWriteOnlyMultipartPresignedUrl(String key, Duration duration, String uploadId, int partNumber) {
        return s3Presigner.presignUploadPart(request -> request
                        .signatureDuration(duration)
                        .uploadPartRequest(uploadPartRequest -> uploadPartRequest
                                .bucket(bucket)
                                .key(key)
                                .partNumber(partNumber)
                                .uploadId(uploadId)))
                .url().toString();
    }

    public List<FileMultipartUploadUrlDTO> generateWriteOnlyMultipartPresignedUrls(String key, Duration duration, String uploadId, int partSize) {
        List<FileMultipartUploadUrlDTO> multipartPresignedUrls = new ArrayList<>();
        for (int partNumber = 1; partNumber <= partSize; partNumber++) {
            multipartPresignedUrls.add(new FileMultipartUploadUrlDTO(
                    partNumber, generateWriteOnlyMultipartPresignedUrl(key, duration, uploadId, partNumber)
            ));
        }
        return multipartPresignedUrls;
    }

    public CompleteMultipartUploadResponse completeMultipartUpload(String key, String uploadId, List<CompletedPart> parts) {
        return s3Client.completeMultipartUpload(request -> request
                .bucket(bucket)
                .key(key)
                .uploadId(uploadId)
                .multipartUpload(CompletedMultipartUpload.builder().parts(parts).build()));
    }

    public AbortMultipartUploadResponse abortMultipartUpload(String key, String uploadId) {
        return s3Client.abortMultipartUpload(request -> request
                .bucket(bucket)
                .key(key)
                .uploadId(uploadId));
    }

    public int calculateMultipartCount(long originalFileSize, long requestCount) {
        final long minPartSize = 5242880; // 5 MB
        final long maxPartSize = 2147483648L; // 2 GB
        final long recommendedMinOriginalFileSize = 104857600; // 100 MB
        final long maxPartCount = 10000;
        long correctedPartCount = Math.min(requestCount, maxPartCount);

        if (originalFileSize < recommendedMinOriginalFileSize) return 1;
        if (originalFileSize / correctedPartCount < minPartSize) {
            return (int) (originalFileSize / minPartSize);
        }
        if (originalFileSize / correctedPartCount > maxPartSize) {
            return (int) (originalFileSize / maxPartSize);
        }

        return (int) correctedPartCount;
    }

    public Mono<String> readFileAsString(String keyName) {
        return Mono.fromFuture(
                getObjectBytes(keyName)
                        .thenApply(String::new)
        );
    }

    public Mono<byte[]> readFileAsByteArray(String keyName) {
        return Mono.fromFuture(
                getObjectBytes(keyName)
        );
    }

    public CompletableFuture<byte[]> getObjectBytes(String keyName) {
        return s3AsyncClient.getObject(GetObjectRequest.builder()
                                .bucket(bucket)
                                .key(keyName)
                                .build(),
                        AsyncResponseTransformer.toBytes())
                .thenApply(BytesWrapper::asByteArray);
    }

    public Mono<String> uploadFile(String key, byte[] content) {
        return Mono.fromCallable(() -> {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(content));
            return key;
        });
    }

}
