package com.khu.cloudcomputing.khuropbox.files.service;

import com.amazonaws.util.IOUtils;
import com.khu.cloudcomputing.khuropbox.files.dto.FileHistoryDTO;
import com.khu.cloudcomputing.khuropbox.files.dto.FilesDTO;
import com.khu.cloudcomputing.khuropbox.files.dto.FilesUpdateDTO;
import com.khu.cloudcomputing.khuropbox.files.entity.FileHistoryEntity;
import com.khu.cloudcomputing.khuropbox.files.entity.Files;
import com.khu.cloudcomputing.khuropbox.files.repository.FileHistoryRepository;
import com.khu.cloudcomputing.khuropbox.files.repository.FilesRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FilesServiceImpl implements FilesService {
    private final FilesRepository filesRepository;
    private final S3Client s3Client;
    private final FileHistoryRepository fileHistoryRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    @Override
    public FilesDTO findById(Integer id) {//id를 이용하여 찾는 메서드
        return new FilesDTO(filesRepository.findById(id).orElseThrow());
    }

    @Override
    public Page<FilesDTO> findUserPage(String userId, String orderby, int pageNum, String sort) {
        Pageable pageable = (sort.equals("ASC")) ?
                PageRequest.of(pageNum, 20, Sort.by(Sort.Direction.ASC, orderby))
                : PageRequest.of(pageNum, 20, Sort.by(Sort.Direction.DESC, orderby));

        return filesRepository.findAllByOwner_Id(pageable, userId).map(FilesDTO::new);
    }

    @Override
    public Page<FilesDTO> findTeamFile(Integer teamId, String orderby, int pageNum, String sort) {
        Pageable pageable = (sort.equals("ASC")) ?
                PageRequest.of(pageNum, 20, Sort.by(Sort.Direction.ASC, orderby))
                : PageRequest.of(pageNum, 20, Sort.by(Sort.Direction.DESC, orderby));

        return filesRepository.findAllByTeamId(pageable, teamId).map(FilesDTO::new);
    }
    @Override
    public void updateFile(FilesUpdateDTO fileUpdate) {//파일이름 갱신 메서드
        Files file = this.filesRepository.findById(fileUpdate.getId()).orElseThrow();
        file.update(fileUpdate.getFileName(), fileUpdate.getFileLink(), LocalDateTime.now(), fileUpdate.getTeamId());
        this.filesRepository.save(file);

        //변경이력 기록
        FileHistoryEntity fileHistory = new FileHistoryEntity();
        fileHistory.updateFileHistory(file, fileUpdate.getChangeDescription());
        fileHistoryRepository.save(fileHistory);
    }
    @Override
    public void updateLink(Integer id, String fileLink){
        Files file=this.filesRepository.findById(id).orElseThrow();
        file.updateLink(fileLink);
        this.filesRepository.save(file);
    }
    @Override
    public void deleteFile(Integer id) {//파일 삭제 메서드
        filesRepository.deleteById(id);
    }
    @Override
    public void deleteAtS3(String filePath) {
        try {
            // S3에서 삭제 요청
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(filePath)
                    .build();

            s3Client.deleteObject(request);
            log.info(String.format("[%s] deletion complete", filePath));
        } catch (S3Exception e) {
            log.error(e.awsErrorDetails().errorMessage());
        }
    }
    @Override
    public Integer insertFile(FilesDTO file) {//파일 업로드 메서드
        file.setCreatedAt(LocalDateTime.now());
        file.setUpdatedAt(LocalDateTime.now());
        return filesRepository.save(file.toEntity()).getId();
    }
    @Override
    public String upload(MultipartFile multipartFile, String dirName, Integer id, String fileType) throws IOException { // dirName의 디렉토리가 S3 Bucket 내부에 생성됨
        File uploadFile = convert(multipartFile)
                .orElseThrow(() -> new IllegalArgumentException("MultipartFile -> File 전환 실패"));
        return upload(uploadFile, dirName, id, fileType);
    }
    @Override
    public ResponseEntity<byte[]> download(String fileUrl) throws IOException {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(fileUrl)
                .build();

        try (ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest)) {
            byte[] bytes = IOUtils.toByteArray(s3Object);

            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(contentType(fileUrl));
            httpHeaders.setContentLength(bytes.length);
            String[] arr = fileUrl.split("/");
            String type = arr[arr.length - 1];
            String fileName = URLEncoder.encode(type, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            httpHeaders.setContentDispositionFormData("attachment", fileName);

            return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
        }
    }
    private String upload(File uploadFile, String dirName, Integer id, String fileType) {
        String fileName = dirName + id+"."+fileType;
        String uploadImageUrl = putS3(uploadFile, fileName);
        removeNewFile(uploadFile);  // convert()함수로 인해서 로컬에 생성된 File 삭제 (MultipartFile -> File 전환 하며 로컬에 파일 생성됨)
        return uploadImageUrl;      // 업로드된 파일의 S3 URL 주소 반환
    }
    private void removeNewFile(File targetFile) {
        if(targetFile.delete()) {
            log.info("파일이 삭제되었습니다.");
        }else {
            log.info("파일이 삭제되지 못했습니다.");
        }
    }
    private String putS3(File uploadFile, String fileName) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(fileName)
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromFile(uploadFile));
        return String.format("https://%s.s3.amazonaws.com/%s", bucket, fileName);
    }
    private Optional<File> convert(MultipartFile file) throws IOException {
        File convertFile = new File(file.getOriginalFilename()); // 업로드한 파일의 이름
        if (convertFile.createNewFile()) {
            try (FileOutputStream fos = new FileOutputStream(convertFile)) {
                fos.write(file.getBytes());
            }
            return Optional.of(convertFile);
        }
        return Optional.empty();
    }
    private MediaType contentType(String keyname) {
        String[] arr = keyname.split("\\.");
        String type = arr[arr.length - 1];
        return switch (type) {
            case "txt" -> MediaType.TEXT_PLAIN;
            case "png" -> MediaType.IMAGE_PNG;
            case "jpg" -> MediaType.IMAGE_JPEG;
            default -> MediaType.APPLICATION_OCTET_STREAM;
        };
    }

    //파일 히스토리 관리
    public List<FileHistoryDTO> getFileChangeHistory(Integer id) {
       List<FileHistoryEntity> fileHistoryEntities = fileHistoryRepository.findAllByFileIdOrderByChangeDateDesc(id);
       Files file = filesRepository.findById(id).orElseThrow(() -> new RuntimeException("File not found with id: " + id));

       return fileHistoryEntities.stream()
               .map(history->mapToDTO(file,history))
               .collect(Collectors.toList());
    }

    @Override
    public FileHistoryDTO mapToDTO(Files fileEntity,FileHistoryEntity historyEntity) {
        return new FileHistoryDTO(fileEntity, historyEntity);
    }

    @Override
    public void createDirectory(String currentDir, String newDirName) {
        try {
            // Ensure the parent directory path ends with a slash
            if (!currentDir.endsWith("/")) {
                currentDir += "/";
            }

            String fullPath = currentDir + newDirName + "/";

            // Create an empty object to represent the directory
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(fullPath)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(new byte[0]));
            log.info("Directory created successfully: {}", fullPath);
        } catch (S3Exception e) {
            log.error("Error creating directory in S3", e);
            throw new RuntimeException("Failed to create directory in S3: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error occurred", e);
            throw new RuntimeException("Unexpected error: " + e.getMessage(), e);
        }
    }

    @Override
    public void moveFile(String source, String target) throws IOException {
        try {
            // Check input validity
            if (source == null || target == null) {
                throw new IllegalArgumentException("Source or target must not be null");
            }

            // Copy the file within the bucket
            CopyObjectRequest copyObjectRequest = CopyObjectRequest.builder()
                    .sourceBucket(bucket)
                    .sourceKey(source)
                    .destinationBucket(bucket)
                    .destinationKey(target)
                    .build();
            s3Client.copyObject(copyObjectRequest);
            log.info("File copied successfully from {} to {}", source, target);

            // Delete the original file
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(source)
                    .build();
            s3Client.deleteObject(deleteObjectRequest);
            log.info("Original file {} deleted successfully", source);
        } catch (S3Exception e) {
            log.error("Error moving file from {} to {}", source, target, e);
            throw new IOException("Failed to move file: " + e.getMessage(), e);
        }
    }
}
