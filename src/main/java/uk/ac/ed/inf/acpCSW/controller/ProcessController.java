package uk.ac.ed.inf.acpCSW.controller;

import uk.ac.ed.inf.acpCSW.configuration.AcpConfig;
import uk.ac.ed.inf.acpCSW.service.DynamoService;
import uk.ac.ed.inf.acpCSW.service.PostgresService;
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
    private final PostgresService postgresService;
    private final AcpConfig acpConfig;

    public ProcessController(
            ProcessService processService,
            DynamoService dynamoService,
            S3Service s3Service,
            PostgresService postgresService,
            AcpConfig acpConfig
    ) {
        this.processService = processService;
        this.dynamoService = dynamoService;
        this.s3Service = s3Service;
        this.postgresService = postgresService;
        this.acpConfig = acpConfig;
    }

    @PostMapping(value = "/process/dump", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Map<String, Object>>> processDump(@RequestBody Map<String, Object> body) {
        String urlPath = body.get("urlPath") == null ? null : body.get("urlPath").toString();
        var res = processService.processDump(urlPath);
        return ResponseEntity.ok(res);
    }

    @PostMapping(value = "/process/dynamo", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> processDynamo(@RequestBody Map<String, Object> body) {
        String urlPath = body.get("urlPath") == null ? null : body.get("urlPath").toString();
        var drones = processService.processDump(urlPath);

        String table = acpConfig.getSid(); // spec: table = SID

        for (var drone : drones) {
            Object nameObj = drone.get("name");
            if (nameObj == null) continue;
            dynamoService.putObject(table, nameObj.toString(), drone);
        }

        return ResponseEntity.ok().build();
    }


    @PostMapping(value = "/process/s3", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> processS3(@RequestBody Map<String, Object> body) {
        String urlPath = body.get("urlPath") == null ? null : body.get("urlPath").toString();
        var drones = processService.processDump(urlPath);

        String bucket = acpConfig.getSid();
        for (var drone : drones) {
            Object nameObj = drone.get("name");
            if (nameObj == null) continue;
            s3Service.putJson(bucket, nameObj.toString(), drone);
        }
        return ResponseEntity.ok().build();
    }


    @PostMapping(value = "/process/postgres/{table}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> processPostgres(@PathVariable String table, @RequestBody Map<String, Object> body) {
        String urlPath = body.get("urlPath") == null ? null : body.get("urlPath").toString();
        var drones = processService.processDump(urlPath);

        for (var drone : drones) {
            postgresService.upsertDroneRow(table, drone);
        }
        return ResponseEntity.ok().build();
    }


}
