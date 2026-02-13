package uk.ac.ed.inf.acpCSW.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.ac.ed.inf.acpCSW.configuration.AcpConfig;
import uk.ac.ed.inf.acpCSW.service.PostgresService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/acp")
public class PostgresController {

    private final PostgresService postgresService;
    private final AcpConfig acpConfig;

    public PostgresController(PostgresService postgresService, AcpConfig acpConfig) {
        this.postgresService = postgresService;
        this.acpConfig = acpConfig;
    }

    @GetMapping(value = "/all/postgres/{table}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Map<String, Object>>> getAllFromPostgres(@PathVariable String table) {
        try {
            String qualified = acpConfig.getSid() + "." + table;
            var rows = postgresService.getAllRows(qualified);
            return ResponseEntity.ok(rows); // OK even if empty
        } catch (org.springframework.jdbc.BadSqlGrammarException e) {
            return ResponseEntity.status(404).build();
        } catch (Exception e) {
            return ResponseEntity.status(404).build();
        }
    }


}

