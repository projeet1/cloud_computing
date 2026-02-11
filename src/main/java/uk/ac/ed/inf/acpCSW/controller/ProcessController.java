package uk.ac.ed.inf.acpCSW.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/acp")
public class ProcessController {

    @PostMapping(value = "/process/dump", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Object>> processDump(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(List.of());
    }

    @PostMapping(value = "/process/dynamo", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> processDynamo(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/process/s3", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> processS3(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/process/postgres/{table}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> processPostgres(@PathVariable String table, @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/copy-content/dynamo/{table}")
    public ResponseEntity<Void> copyContentToDynamo(@PathVariable String table) {
        return ResponseEntity.ok().build();
    }

    // spec uses capital S3 in path
    @PostMapping("/copy-content/S3/{table}")
    public ResponseEntity<Void> copyContentToS3(@PathVariable String table) {
        return ResponseEntity.ok().build();
    }
}
