package uk.ac.ed.inf.acpCSW.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/acp")
public class PostgresController {

    @GetMapping(value = "/all/postgres/{table}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Map<String, Object>>> getAllFromPostgres(@PathVariable String table) {
        return ResponseEntity.ok(List.of());
    }
}
