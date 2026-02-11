package uk.ac.ed.inf.acpCSW.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.*;

@Service
public class DynamoService {

    private final DynamoDbClient dynamo;
    private final ObjectMapper objectMapper;

    public DynamoService(DynamoDbClient dynamo, ObjectMapper objectMapper) {
        this.dynamo = dynamo;
        this.objectMapper = objectMapper;
    }

    public void ensureTableExists(String tableName) {
        try {
            dynamo.describeTable(DescribeTableRequest.builder().tableName(tableName).build());
        } catch (ResourceNotFoundException e) {
            dynamo.createTable(CreateTableRequest.builder()
                    .tableName(tableName)
                    .billingMode(BillingMode.PAY_PER_REQUEST)
                    .keySchema(KeySchemaElement.builder().attributeName("key").keyType(KeyType.HASH).build())
                    .attributeDefinitions(AttributeDefinition.builder().attributeName("key").attributeType(ScalarAttributeType.S).build())
                    .build());
            waitUntilActive(tableName);
        }
    }

    public void putObject(String tableName, String key, Map<String, Object> jsonObject) {
        String content;
        try {
            content = objectMapper.writeValueAsString(jsonObject);
        } catch (Exception e) {
            content = "{}";
        }

        dynamo.putItem(PutItemRequest.builder()
                .tableName(tableName)
                .item(Map.of(
                        "key", AttributeValue.fromS(key),
                        "content", AttributeValue.fromS(content)
                ))
                .build());
    }

    public Optional<Map<String, Object>> getObject(String tableName, String key) {
        try {
            var res = dynamo.getItem(GetItemRequest.builder()
                    .tableName(tableName)
                    .key(Map.of("key", AttributeValue.fromS(key)))
                    .build());

            if (res.item() == null || res.item().isEmpty()) return Optional.empty();
            var contentAttr = res.item().get("content");
            if (contentAttr == null || contentAttr.s() == null) return Optional.empty();

            return Optional.of(parseJsonObject(contentAttr.s()));
        } catch (ResourceNotFoundException e) {
            return Optional.empty(); // table not found -> 404
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public List<Map<String, Object>> scanAll(String tableName) {
        try {
            var res = dynamo.scan(ScanRequest.builder().tableName(tableName).build());
            List<Map<String, Object>> out = new ArrayList<>();

            for (var item : res.items()) {
                var contentAttr = item.get("content");
                if (contentAttr == null || contentAttr.s() == null) continue;
                out.add(parseJsonObject(contentAttr.s()));
            }
            return out;
        } catch (ResourceNotFoundException e) {
            return List.of(); // table not found -> controller can return 404
        } catch (Exception e) {
            return List.of();
        }
    }

    private Map<String, Object> parseJsonObject(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            return Map.of();
        }
    }

    private void waitUntilActive(String tableName) {
        for (int i = 0; i < 40; i++) {
            try {
                var res = dynamo.describeTable(DescribeTableRequest.builder().tableName(tableName).build());
                if (res.table().tableStatus() == TableStatus.ACTIVE) return;
                Thread.sleep(200);
            } catch (Exception ignored) {
            }
        }
    }
}
