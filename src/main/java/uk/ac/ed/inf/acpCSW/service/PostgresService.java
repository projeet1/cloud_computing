package uk.ac.ed.inf.acpCSW.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PostgresService {

    private final JdbcTemplate jdbc;

    public PostgresService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // Allow either: table OR schema.table (SID schema is common in marking)
    private String sanitizeTable(String table) {
        if (table == null) throw new IllegalArgumentException("table null");
        if (!table.matches("[a-zA-Z0-9_]+(\\.[a-zA-Z0-9_]+)?")) {
            throw new IllegalArgumentException("bad table");
        }
        return table;
    }

    @SuppressWarnings("unchecked")
    public void upsertDroneRow(String table, Map<String, Object> drone) {
        String t = sanitizeTable(table);

        Object nameObj = drone.get("name");
        if (nameObj == null) throw new IllegalArgumentException("missing name");
        String name = nameObj.toString();

        String id = drone.get("id") == null ? null : drone.get("id").toString();
        Double costPer100Moves = toDouble(drone.get("costPer100Moves"));

        Map<String, Object> cap = drone.get("capability") instanceof Map
                ? (Map<String, Object>) drone.get("capability")
                : Map.of();

        Boolean cooling = toBool(cap.get("cooling"));
        Boolean heating = toBool(cap.get("heating"));
        Double capacity = toDouble(cap.get("capacity"));
        Integer maxMoves = toInt(cap.get("maxMoves"));
        Double costPerMove = toDouble(cap.get("costPerMove"));
        Double costInitial = toDouble(cap.get("costInitial"));
        Double costFinal = toDouble(cap.get("costFinal"));

        String sql = "INSERT INTO " + t + " " +
                "(name, id, cooling, heating, capacity, maxMoves, costPerMove, costInitial, costFinal, costPer100Moves) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (name) DO UPDATE SET " +
                "id = EXCLUDED.id, " +
                "cooling = EXCLUDED.cooling, " +
                "heating = EXCLUDED.heating, " +
                "capacity = EXCLUDED.capacity, " +
                "maxMoves = EXCLUDED.maxMoves, " +
                "costPerMove = EXCLUDED.costPerMove, " +
                "costInitial = EXCLUDED.costInitial, " +
                "costFinal = EXCLUDED.costFinal, " +
                "costPer100Moves = EXCLUDED.costPer100Moves";

        jdbc.update(sql, name, id, cooling, heating, capacity, maxMoves, costPerMove, costInitial, costFinal, costPer100Moves);
    }

    public List<Map<String, Object>> getAllRows(String table) {
        String t = sanitizeTable(table);
        return jdbc.queryForList("SELECT * FROM " + t);
    }

    private static Double toDouble(Object v) {
        if (v == null) return null;
        if (v instanceof Number n) return n.doubleValue();
        try { return Double.parseDouble(v.toString()); } catch (Exception e) { return null; }
    }

    private static Integer toInt(Object v) {
        if (v == null) return null;
        if (v instanceof Number n) return n.intValue();
        try { return Integer.parseInt(v.toString()); } catch (Exception e) { return null; }
    }

    private static Boolean toBool(Object v) {
        if (v == null) return null;
        if (v instanceof Boolean b) return b;
        return Boolean.parseBoolean(v.toString());
    }
}
