package com.backend.warehouse.service;

import java.io.InputStream;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

/**
 * Service để tương tác với Cloudflare R2 (S3-compatible storage).
 * Thay thế lưu file local → cloud, tránh mất file khi Docker container restart.
 */
@Service
public class R2StorageService {

    private final S3Client s3Client;
    private final String bucket;

    public R2StorageService(
            @Value("${r2.account-id}") String accountId,
            @Value("${r2.access-key}") String accessKey,
            @Value("${r2.secret-key}") String secretKey,
            @Value("${r2.bucket}")     String bucket) {

        this.bucket = bucket;

        // Cloudflare R2 endpoint: https://<account-id>.r2.cloudflarestorage.com
        String endpoint = "https://" + accountId + ".r2.cloudflarestorage.com";

        this.s3Client = S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                // R2 chỉ dùng "auto" region (hoặc bất kỳ chuỗi nào)
                .region(Region.of("auto"))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }

    /**
     * Upload file lên R2, tự động đổi tên nếu key đã tồn tại.
     *
     * @param file MultipartFile từ HTTP request
     * @return key (tên object trong bucket) — lưu vào DB
     */
    public String uploadFile(MultipartFile file) throws java.io.IOException {
        String originalName = file.getOriginalFilename();
        String key = buildUniqueKey(originalName);

        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(file.getContentType())
                .contentLength(file.getSize())
                .build();

        s3Client.putObject(putRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        return key;
    }

    /**
     * Lấy InputStream của file từ R2 để stream về client.
     *
     * @param key object key lưu trong DB
     * @return InputStream
     */
    public InputStream downloadFile(String key) {
        GetObjectRequest getRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
        return s3Client.getObject(getRequest);
    }

    /**
     * Xóa file khỏi R2 (khi booking bị xóa).
     *
     * @param key object key lưu trong DB
     */
    public void deleteFile(String key) {
        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
        s3Client.deleteObject(deleteRequest);
    }

    /**
     * Kiểm tra key có tồn tại trong bucket không.
     */
    public boolean exists(String key) {
        try {
            HeadObjectResponse response = s3Client.headObject(
                    HeadObjectRequest.builder().bucket(bucket).key(key).build());
            return response != null;
        } catch (NoSuchKeyException e) {
            return false;
        }
    }

    // -----------------------------------------------------------------------
    // Private helpers
    // -----------------------------------------------------------------------

    private String buildUniqueKey(String originalName) {
        if (originalName == null || originalName.isBlank()) {
            originalName = "upload.csv";
        }
        // Nếu key đã tồn tại → thêm timestamp vào tên để tránh overwrite
        if (exists(originalName)) {
            int dot = originalName.lastIndexOf('.');
            String name = dot > 0 ? originalName.substring(0, dot) : originalName;
            String ext  = dot > 0 ? originalName.substring(dot)     : "";
            String ts   = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            return name + "_" + ts + ext;
        }
        return originalName;
    }
}
