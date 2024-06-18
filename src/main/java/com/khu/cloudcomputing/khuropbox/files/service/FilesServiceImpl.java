package com.khu.cloudcomputing.khuropbox.files.service;

import com.amazonaws.util.IOUtils;
import com.khu.cloudcomputing.khuropbox.apiPayload.GeneralException;
import com.khu.cloudcomputing.khuropbox.apiPayload.status.ErrorStatus;
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

    /**
     * 파일 ID로 파일을 찾습니다.
     *
     * @param id 파일 ID
     * @return 파일의 DTO
     * @throws GeneralException 파일을 찾을 수 없는 경우
     */
    @Override
    public FilesDTO findById(Integer id) {
        return new FilesDTO(filesRepository.findById(id)
                .orElseThrow(() -> new GeneralException(ErrorStatus._FILE_NOT_FOUND.getCode(), "File not found", HttpStatus.NOT_FOUND)));
    }

    /**
     * 사용자의 파일을 페이지 단위로 찾습니다.
     *
     * @param userId 사용자 ID
     * @param orderby 정렬 기준
     * @param pageNum 페이지 번호
     * @param sort 정렬 방향 (ASC 또는 DESC)
     * @return 페이지 단위로 파일 목록
     */
    @Override
    public Page<FilesDTO> findUserPage(String userId, String orderby, int pageNum, String sort, String search, Boolean isRecycleBin) {
        Pageable pageable = (sort.equals("ASC")) ?
                PageRequest.of(pageNum, 20, Sort.by(Sort.Direction.ASC, orderby))
                : PageRequest.of(pageNum, 20, Sort.by(Sort.Direction.DESC, orderby));

        return filesRepository.findAllByOwner_IdAndFileNameContainingAndIsRecycleBin(pageable, userId,search,isRecycleBin).map(FilesDTO::new);
    }

    @Override
    public Page<FilesDTO> findTeamFile(Integer teamId, String orderby, int pageNum, String sort, String search,Boolean isRecycleBin) {
        Pageable pageable = (sort.equals("ASC")) ?
                PageRequest.of(pageNum, 20, Sort.by(Sort.Direction.ASC, orderby))
                : PageRequest.of(pageNum, 20, Sort.by(Sort.Direction.DESC, orderby));

        return filesRepository.findAllByTeamIdAndFileNameContainingAndIsRecycleBin(pageable, teamId,search,isRecycleBin).map(FilesDTO::new);
    }
    /**
     * 파일 메타데이터를 업데이트합니다.
     *
     * @param fileUpdate 파일 업데이트 DTO
     * @throws GeneralException 파일을 찾을 수 없는 경우
     */
    @Override
    public void updateFile(FilesUpdateDTO fileUpdate) {
        Files file = this.filesRepository.findById(fileUpdate.getId())
                .orElseThrow(() -> new GeneralException(ErrorStatus._FILE_NOT_FOUND.getCode(), "File not found", HttpStatus.NOT_FOUND));
        file.update(fileUpdate.getFileName(), LocalDateTime.now(), fileUpdate.getTeamId());
        if(fileUpdate.getFileKey()!=null) {
            file.updateKey(fileUpdate.getFileKey());
        }
        this.filesRepository.save(file);
        FileHistoryEntity fileHistory = new FileHistoryEntity();
        fileHistory.updateFileHistory(file, fileUpdate.getChangeDescription());
        fileHistoryRepository.save(fileHistory);
    }
    @Override
    public void recycleBinFile(Integer id){
        Files file = this.filesRepository.findById(id)
                .orElseThrow(() -> new GeneralException(ErrorStatus._FILE_NOT_FOUND.getCode(), "File not found", HttpStatus.NOT_FOUND));
        file.recycleBin();
        this.filesRepository.save(file);
    }
    @Override
    public void restoreFile(Integer id){
        Files file = this.filesRepository.findById(id)
                .orElseThrow(() -> new GeneralException(ErrorStatus._FILE_NOT_FOUND.getCode(), "File not found", HttpStatus.NOT_FOUND));
        file.restore();
        this.filesRepository.save(file);
    }
    @Override
    public void deleteFile(Integer id) {//파일 삭제 메서드
        filesRepository.deleteById(id);
    }
    /**
     * S3에서 파일을 삭제합니다.
     *
     * @param filePath 파일 경로
     * @throws GeneralException 파일 삭제 실패 시
     */
    @Override
    public void deleteAtS3(String filePath) {
        try {
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(filePath)
                    .build();

            s3Client.deleteObject(request);
            log.info(String.format("[%s] deletion complete", filePath));
        } catch (S3Exception e) {
            log.error(e.awsErrorDetails().errorMessage());
            throw new GeneralException(ErrorStatus._FILE_DELETE_FAILED.getCode(), "File delete failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @Override
    public Integer insertFile(FilesDTO file) {//파일 업로드 메서드
        LocalDateTime now=LocalDateTime.now();
        file.setCreatedAt(now);
        file.setUpdatedAt(now);
        file.setIsRecycleBin(false);
        return filesRepository.save(file.toEntity()).getId();
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
    public Boolean isExistFileKey(String userId, String fileKey){
        List<String> fileKeyList=filesRepository.findFileKey(userId);
        return fileKeyList.contains(fileKey);
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

    /**
     * 파일을 이동합니다.
     *
     * @param source 원본 파일 경로
     * @param target 대상 파일 경로
     * @throws IOException 파일 이동 중 예외 발생 시
     * @throws GeneralException 파일 이동 중 예외 발생 시
     */
    @Override
    public void moveFile(String source, String target) throws IOException {
        try {
            if (source == null || target == null) {
                throw new GeneralException(ErrorStatus._BAD_REQUEST.getCode(), "Source or target must not be null", HttpStatus.BAD_REQUEST);
            }

            CopyObjectRequest copyObjectRequest = CopyObjectRequest.builder()
                    .sourceBucket(bucket)
                    .sourceKey(source)
                    .destinationBucket(bucket)
                    .destinationKey(target)
                    .build();
            s3Client.copyObject(copyObjectRequest);
            log.info("File copied successfully from {} to {}", source, target);

            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucket)
                    .key(source)
                    .build();
            s3Client.deleteObject(deleteObjectRequest);
            log.info("Original file {} deleted successfully", source);
        } catch (S3Exception e) {
            log.error("Error moving file from {} to {}", source, target, e);
            throw new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR.getCode(), "Failed to move file: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
