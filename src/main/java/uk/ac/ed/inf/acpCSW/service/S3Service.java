package uk.ac.ed.inf.acpCSW.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class S3Service {

    private final S3Client s3;
    private final ObjectMapper objectMapper;

    public S3Service(S3Client s3, ObjectMapper objectMapper) {
        this.s3 = s3;
        this.objectMapper = objectMapper;
    }

    public void ensureBucketExists(String bucket) {
        try {
            s3.headBucket(HeadBucketRequest.builder().bucket(bucket).build());
        } catch (NoSuchBucketException e) {
            s3.createBucket(CreateBucketRequest.builder().bucket(bucket).build());
        } catch (S3Exception e) {
            // If LocalStack returns 404-like errors via S3Exception, create bucket
            if (e.statusCode() == 404) {
                s3.createBucket(CreateBucketRequest.builder().bucket(bucket).build());
            } else {
                throw e;
            }
        }
    }

    public void putJson(String bucket, String key, Map<String, Object> obj) {
        String json;
        try {
            json = objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            json = "{}";
        }

        s3.putObject(
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .contentType("application/json")
                        .build(),
                RequestBody.fromString(json)
        );
    }

    public Map<String, Object> getJson(String bucket, String key) {
        ResponseBytes<GetObjectResponse> bytes = s3.getObjectAsBytes(
                GetObjectRequest.builder().bucket(bucket).key(key).build()
        );

        String json = bytes.asUtf8String();
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> parsed = objectMapper.readValue(json, Map.class);
            return parsed;
        } catch (Exception e) {
            return Map.of();
        }
    }

    public List<Map<String, Object>> getAllJson(String bucket) {
        ListObjectsV2Response list = s3.listObjectsV2(
                ListObjectsV2Request.builder().bucket(bucket).build()
        );

        List<Map<String, Object>> out = new ArrayList<>();
        for (S3Object o : list.contents()) {
            if (o.key() == null || o.key().isBlank()) continue;
            out.add(getJson(bucket, o.key()));
        }
        return out;
    }
}
