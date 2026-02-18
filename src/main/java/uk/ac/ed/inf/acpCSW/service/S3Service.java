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

    public Object getJson(String bucket, String key) {
        ResponseBytes<GetObjectResponse> bytes = s3.getObjectAsBytes(
                GetObjectRequest.builder().bucket(bucket).key(key).build()
        );

        String json = bytes.asUtf8String();
        try {
            return objectMapper.readValue(json, Object.class); // <-- supports object/array/string/number/bool/null
        } catch (Exception e) {
            throw new RuntimeException("Invalid JSON in S3 object: " + key, e);
        }
    }


    public List<Object> getAllJson(String bucket) {
        ListObjectsV2Response list = s3.listObjectsV2(
                ListObjectsV2Request.builder().bucket(bucket).build()
        );

        List<Object> out = new ArrayList<>();
        for (S3Object o : list.contents()) {
            if (o.key() == null || o.key().isBlank()) continue;
            out.add(getJson(bucket, o.key()));
        }
        return out;
    }

}
