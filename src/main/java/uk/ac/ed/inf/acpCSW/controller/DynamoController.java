package uk.ac.ed.inf.acpCSW.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uk.ac.ed.inf.acpCSW.service.DynamoService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/acp")
public class DynamoController {

    private final DynamoService dynamoService;

    public DynamoController(DynamoService dynamoService) {
        this.dynamoService = dynamoService;
    }

    @GetMapping(value = "/all/dynamo/{table}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Map<String, Object>>> getAll(@PathVariable String table) {
        var items = dynamoService.scanAll(table);
        if (items.isEmpty()) return ResponseEntity.status(404).build();
        return ResponseEntity.ok(items);
    }

    @GetMapping(value = "/single/dynamo/{table}/{key}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getOne(@PathVariable String table, @PathVariable String key) {
        var item = dynamoService.getObject(table, key);
        return item.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).build());
    }
}
