package uk.ac.ed.inf.acpCSW.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.ac.ed.inf.acpCSW.service.PostgresService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/acp")
public class PostgresController {

    private final PostgresService postgresService;

    public PostgresController(PostgresService postgresService) {
        this.postgresService = postgresService;
    }
    @GetMapping(value = "/all/postgres/{table}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Map<String, Object>>> getAllFromPostgres(@PathVariable String table) {
        try {
            var rows = postgresService.getAllRows(table);
            if (rows.isEmpty()) return ResponseEntity.status(404).build();
            return ResponseEntity.ok(rows);
        } catch (Exception e) {
            return ResponseEntity.status(404).build();
        }
    }
    @PostMapping(value = "/process/postgres/{table}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> processPostgres(@PathVariable String table, @RequestBody Map<String, Object> body) {
        String urlPath = body.get("urlPath") == null ? null : body.get("urlPath").toString();
        var drones = processService.processDump(urlPath);
        if (drones.isEmpty()) return ResponseEntity.status(404).build();

        try {
            for (var drone : drones) {
                postgresService.upsertDroneRow(table, drone);
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(404).build();
        }
    }


}
