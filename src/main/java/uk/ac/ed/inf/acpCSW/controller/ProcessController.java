package uk.ac.ed.inf.acpCSW.controller;

import uk.ac.ed.inf.acpCSW.configuration.AcpConfig;
import uk.ac.ed.inf.acpCSW.service.DynamoService;
import uk.ac.ed.inf.acpCSW.service.ProcessService;
import uk.ac.ed.inf.acpCSW.service.S3Service;

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
    private final S3Service s3Service;
    private final AcpConfig acpConfig;

    public ProcessController(ProcessService processService, DynamoService dynamoService, S3Service s3Service, AcpConfig acpConfig) {
        this.processService = processService;
        this.dynamoService = dynamoService;
        this.s3Service = s3Service;
        this.acpConfig = acpConfig;
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
        System.out.println("Calling ILP with urlPath: " + urlPath);

        var drones = processService.processDump(urlPath);

        if (drones.isEmpty()) {
            System.out.println("No drones returned. Returning 404.");
            return ResponseEntity.status(404).build();
        }

        String table = acpConfig.getSid();
        System.out.println("Ensuring Dynamo table exists: " + table);

        try {
            dynamoService.ensureTableExists(table);

            System.out.println("Writing " + drones.size() + " drones to Dynamo...");

            for (var drone : drones) {
                Object nameObj = drone.get("name");
                if (nameObj == null) continue;
                String key = nameObj.toString();
                dynamoService.putObject(table, key, drone);
            }


            System.out.println("Dynamo write complete.");

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            System.out.println("Dynamo operation failed: " + e.getMessage());
            return ResponseEntity.status(404).build();
        }
    }



    @PostMapping(value = "/process/s3", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> processS3(@RequestBody Map<String, Object> body) {
        String urlPath = body.get("urlPath") == null ? null : body.get("urlPath").toString();
        var drones = processService.processDump(urlPath);
        if (drones.isEmpty()) return ResponseEntity.status(404).build();

        String bucket = acpConfig.getSid();

        try {
            s3Service.ensureBucketExists(bucket);

            for (var drone : drones) {
                Object nameObj = drone.get("name");
                if (nameObj == null) continue;
                s3Service.putJson(bucket, nameObj.toString(), drone);
            }

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(404).build();
        }
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
