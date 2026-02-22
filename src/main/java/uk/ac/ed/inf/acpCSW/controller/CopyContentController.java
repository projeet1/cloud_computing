package uk.ac.ed.inf.acpCSW.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.ac.ed.inf.acpCSW.configuration.AcpConfig;
import uk.ac.ed.inf.acpCSW.service.DynamoService;
import uk.ac.ed.inf.acpCSW.service.PostgresService;
import uk.ac.ed.inf.acpCSW.service.S3Service;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/acp")
public class CopyContentController {

    private final PostgresService postgresService;
    private final DynamoService dynamoService;
    private final S3Service s3Service;
    private final AcpConfig acpConfig;

    public CopyContentController(PostgresService postgresService,
                                 DynamoService dynamoService,
                                 S3Service s3Service,
                                 AcpConfig acpConfig) {
        this.postgresService = postgresService;
        this.dynamoService = dynamoService;
        this.s3Service = s3Service;
        this.acpConfig = acpConfig;
    }

    @PostMapping("/copy-content/dynamo/{table}")
    public ResponseEntity<Void> copyPostgresToDynamo(@PathVariable String table) {
        try {
            var rows = postgresService.getAllRows(table);


            String dynTable = acpConfig.getSid(); // spec: Dynamo table = SID

            for (var row : rows) {
                String key = UUID.randomUUID().toString();
                dynamoService.putObject(dynTable, key, row);
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(404).build();
        }
    }

    @PostMapping("/copy-content/S3/{table}")
    public ResponseEntity<Void> copyPostgresToS3(@PathVariable String table) {
        try {
            var rows = postgresService.getAllRows(table);


            String bucket = acpConfig.getSid(); // spec: bucket = SID

            for (var row : rows) {
                String key = UUID.randomUUID().toString();
                s3Service.putJson(bucket, key, row);
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(404).build();
        }
    }
}
