package richtexteditor.quillproj.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    //https://upload-bucket-quillhtml.s3.ap-northeast-2.amazonaws.com/default-avatar.png
    @Value("${aws.s3.bucket:upload-bucket-quillhtml}")
    private String bucket;

    @Value("${aws.s3.region:ap-northeast-2}")
    private String region;

    @Value("${aws.s3.directory:uploads/}")
    private String dir;


    private S3Client client() {
        return S3Client.builder()
                .region(Region.of(region))
                .build();
    }

    /** Multipart 업로드(이미지 등) */
    public String upload(MultipartFile file) throws IOException {
        String key = dir + UUID.randomUUID() + "-" + file.getOriginalFilename();

        /*
        PutObjectRequest req = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(file.getContentType())
                .acl(ObjectCannedACL.PUBLIC_READ)   // 버킷 정책에 따라 제거 가능
                .build();*/

        PutObjectRequest req = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(file.getContentType())
                .build();

        client().putObject(req, RequestBody.fromBytes(file.getBytes()));
        return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + key;
    }

    /** 로컬 Path(File) 업로드 */
    public String upload(Path path) throws IOException {
        //String key = dir + UUID.randomUUID() + "-" + path.getFileName();
        String key = dir + path.getFileName();

        PutObjectRequest req = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(Files.probeContentType(path))
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build();

        log.info("key = {}", key);
        client().putObject(req, RequestBody.fromFile(path));
        return "https://" + bucket + ".s3." + region + ".amazonaws.com/" + key;
    }
}
