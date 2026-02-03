package uk.ac.ed.inf.acpTutorial.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/acp")
public class AcpController {

    @GetMapping("/all/s3/{bucket}")
    public ResponseEntity<?> getAllS3(@PathVariable String bucket) {
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/single/s3/{bucket}/{key}")
    public ResponseEntity<?> getSingleS3(@PathVariable String bucket,
                                         @PathVariable String key) {
        return ResponseEntity.ok(Map.of());
    }

    @GetMapping("/all/dynamo/{table}")
    public ResponseEntity<?> getAllDynamo(@PathVariable String table) {
        return ResponseEntity.ok(List.of());
    }

    @GetMapping("/single/dynamo/{table}/{key}")
    public ResponseEntity<?> getSingleDynamo(@PathVariable String table,
                                             @PathVariable String key) {
        return ResponseEntity.ok(Map.of());
    }

    @GetMapping("/all/postgres/{table}")
    public ResponseEntity<?> getAllPostgres(@PathVariable String table) {
        return ResponseEntity.ok(List.of());
    }

    @PostMapping("/process/dump")
    public ResponseEntity<?> processDump(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/process/dynamo")
    public ResponseEntity<?> processDynamo(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/process/s3")
    public ResponseEntity<?> processS3(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/process/postgres/{table}")
    public ResponseEntity<?> processPostgres(@PathVariable String table,
                                             @RequestBody Map<String, String> body) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/copy-content/dynamo/{table}")
    public ResponseEntity<?> copyToDynamo(@PathVariable String table) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/copy-content/s3/{table}")
    public ResponseEntity<?> copyToS3(@PathVariable String table) {
        return ResponseEntity.ok().build();
    }
}
