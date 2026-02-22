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

    private String getPartitionKeyName(String tableName) {
        var res = dynamo.describeTable(DescribeTableRequest.builder().tableName(tableName).build());
        return res.table().keySchema().stream()
                .filter(k -> k.keyType() == KeyType.HASH)
                .findFirst()
                .orElseThrow()
                .attributeName();
    }

    private Map<String, Object> attributeMapToPlainMap(Map<String, AttributeValue> item) {
        Map<String, Object> out = new LinkedHashMap<>();
        for (var e : item.entrySet()) {
            out.put(e.getKey(), attributeValueToObject(e.getValue()));
        }
        return out;
    }

    private Object attributeValueToObject(AttributeValue av) {
        if (av.s() != null) return av.s();
        if (av.n() != null) return av.n();

        if (av.bool() != null) return av.bool();
        if (av.m() != null) {
            Map<String, Object> m = new LinkedHashMap<>();
            for (var e : av.m().entrySet()) m.put(e.getKey(), attributeValueToObject(e.getValue()));
            return m;
        }
        if (av.l() != null) {
            List<Object> l = new ArrayList<>();
            for (var x : av.l()) l.add(attributeValueToObject(x));
            return l;
        }
        if (av.ss() != null && !av.ss().isEmpty()) return av.ss();
        if (av.ns() != null && !av.ns().isEmpty()) return av.ns();
        return null;
    }

    public DynamoService(DynamoDbClient dynamo, ObjectMapper objectMapper) {
        this.dynamo = dynamo;
        this.objectMapper = objectMapper;
    }


    public void putObject(String tableName, String keyValue, Map<String, Object> jsonObject) {
        String content;
        try {
            content = objectMapper.writeValueAsString(jsonObject);
        } catch (Exception e) {
            content = "{}";
        }

        String pk = getPartitionKeyName(tableName);

        Map<String, AttributeValue> item = new LinkedHashMap<>();
        item.put(pk, AttributeValue.fromS(keyValue));
        item.put("content", AttributeValue.fromS(content));

        dynamo.putItem(PutItemRequest.builder()
                .tableName(tableName)
                .item(item)
                .build());
    }


    public Optional<Map<String, Object>> getObject(String tableName, String keyValue) {
        try {
            String pk = getPartitionKeyName(tableName);

            var res = dynamo.getItem(GetItemRequest.builder()
                    .tableName(tableName)
                    .key(Map.of(pk, AttributeValue.fromS(keyValue)))
                    .build());

            if (res.item() == null || res.item().isEmpty()) return Optional.empty();
            return Optional.of(attributeMapToPlainMap(res.item()));
        } catch (ResourceNotFoundException e) {
            return Optional.empty();
        } catch (Exception e) {
            return Optional.empty();
        }
    }


    public List<Map<String, Object>> scanAll(String tableName) {
        try {
            var res = dynamo.scan(ScanRequest.builder().tableName(tableName).build());
            List<Map<String, Object>> out = new ArrayList<>();
            for (var item : res.items()) {
                out.add(attributeMapToPlainMap(item));
            }
            return out;
        } catch (ResourceNotFoundException e) {
            return null;
        } catch (Exception e) {
            return List.of();
        }
    }


}
