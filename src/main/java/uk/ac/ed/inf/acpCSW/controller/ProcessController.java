package uk.ac.ed.inf.acpCSW.controller;

import uk.ac.ed.inf.acpCSW.service.DynamoService;
import uk.ac.ed.inf.acpCSW.service.ProcessService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/acp")
public class ProcessController {

    private final ProcessService processService;
    private final DynamoService dynamoService;

    public ProcessController(ProcessService processService, DynamoService dynamoService) {
        this.processService = processService;
        this.dynamoService = dynamoService;
    }

    public ProcessController(ProcessService processService) {
        this.processService = processService;
    }


    @PostMapping(value = "/process/dump", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Map<String, Object>>> processDump(@RequestBody Map<String, Object> body) {
        Object p = body.get("urlPath");
        String urlPath = (p == null) ? null : p.toString();

        var res = processService.processDump(urlPath);

        // Spec only allows 200 or 404.
        // If nothing returned, treat as not found.
        if (res.isEmpty()) {
            return ResponseEntity.status(404).build();
        }
        return ResponseEntity.ok(res);
    }


    @PostMapping(value = "/process/dynamo", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> processDynamo(@RequestBody Map<String, Object> body) {
        String urlPath = body.get("urlPath") == null ? null : body.get("urlPath").toString();
        var drones = processService.processDump(urlPath);
        if (drones.isEmpty()) return ResponseEntity.status(404).build();

        String table = "drones";
        dynamoService.ensureTableExists(table);

        for (var drone : drones) {
            Object nameObj = drone.get("name");
            if (nameObj == null) continue;
            dynamoService.putObject(table, nameObj.toString(), drone);
        }

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
