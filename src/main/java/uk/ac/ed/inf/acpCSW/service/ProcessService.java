package uk.ac.ed.inf.acpCSW.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import uk.ac.ed.inf.acpCSW.configuration.AcpConfig;

import java.util.List;
import java.util.Map;

@Service
public class ProcessService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final AcpConfig acpConfig;

    public ProcessService(RestTemplate restTemplate, ObjectMapper objectMapper, AcpConfig acpConfig) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.acpConfig = acpConfig;
    }

    public List<Map<String, Object>> processDump(String urlPath) {
        if (urlPath == null || urlPath.isBlank()) {
            return List.of();
        }

        String url = buildUrl(acpConfig.getIlpEndpoint(), urlPath);

        String raw;
        try {
            raw = restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            // keep to spec: don't throw 500
            return List.of();
        }

        if (raw == null || raw.isBlank()) {
            return List.of();
        }

        List<Map<String, Object>> drones;
        try {
            drones = objectMapper.readValue(raw, new TypeReference<>() {});
        } catch (Exception e) {
            return List.of();
        }

        for (Map<String, Object> drone : drones) {
            double costPer100Moves = 0.0;

            Object capObj = drone.get("capability");
            if (capObj instanceof Map<?, ?> cap) {
                double costInitial = safeDouble(cap.get("costInitial"));
                double costFinal = safeDouble(cap.get("costFinal"));
                double costPerMove = safeDouble(cap.get("costPerMove"));

                costPer100Moves = costInitial + costFinal + (costPerMove * 100.0);
            }

            drone.put("costPer100Moves", costPer100Moves);
        }

        return drones;
    }

    private static String buildUrl(String base, String urlPath) {
        // if caller passed full URL, use it
        String trimmed = urlPath.trim();
        if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            return trimmed;
        }

        if (base == null) base = "";
        if (base.endsWith("/") && trimmed.startsWith("/")) return base.substring(0, base.length() - 1) + trimmed;
        if (!base.endsWith("/") && !trimmed.startsWith("/")) return base + "/" + trimmed;
        return base + trimmed;
    }

    private static double safeDouble(Object v) {
        if (v == null) return 0.0;
        try {
            double d = Double.parseDouble(v.toString());
            if (Double.isNaN(d) || Double.isInfinite(d)) return 0.0;
            return d;
        } catch (Exception e) {
            return 0.0;
        }
    }
}
