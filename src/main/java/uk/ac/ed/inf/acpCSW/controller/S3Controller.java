package uk.ac.ed.inf.acpCSW.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import uk.ac.ed.inf.acpCSW.service.S3Service;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/acp")
public class S3Controller {

    private final S3Service s3Service;

    public S3Controller(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @GetMapping(value = "/all/s3/{bucket}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Map<String, Object>>> getAll(@PathVariable String bucket) {
        try {
            var items = s3Service.getAllJson(bucket);
            return ResponseEntity.ok(items); // OK even if empty
        } catch (NoSuchBucketException e) {
            return ResponseEntity.status(404).build();
        } catch (Exception e) {
            return ResponseEntity.status(404).build();
        }
    }


    @GetMapping(value = "/single/s3/{bucket}/{key}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getOne(@PathVariable String bucket, @PathVariable String key) {
        try {
            var obj = s3Service.getJson(bucket, key);
            return ResponseEntity.ok(obj);
        } catch (NoSuchKeyException | NoSuchBucketException e) {
            return ResponseEntity.status(404).build();
        } catch (Exception e) {
            return ResponseEntity.status(404).build();
        }
    }

}
