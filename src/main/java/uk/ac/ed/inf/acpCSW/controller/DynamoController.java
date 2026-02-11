package uk.ac.ed.inf.acpCSW.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/acp")
public class DynamoController {

    @GetMapping(value = "/all/dynamo/{table}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Object>> getAllFromDynamo(@PathVariable String table) {
        return ResponseEntity.ok(List.of());
    }

    @GetMapping(value = "/single/dynamo/{table}/{key}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getSingleFromDynamo(@PathVariable String table, @PathVariable String key) {
        return ResponseEntity.status(404).build();
    }
}
