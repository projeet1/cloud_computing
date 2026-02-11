package uk.ac.ed.inf.acpCSW.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/acp")
public class S3Controller {

    @GetMapping(value = "/all/s3/{bucket}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Object>> getAllFromS3(@PathVariable String bucket) {
        return ResponseEntity.ok(List.of());
    }

    @GetMapping(value = "/single/s3/{bucket}/{key}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getSingleFromS3(@PathVariable String bucket, @PathVariable String key) {
        return ResponseEntity.status(404).build();
    }
}
