package com.dbms.project.service;

import com.dbms.project.db.TableUtils;
import com.dbms.project.exceptions.DBMSException;
import com.dbms.project.model.Attribute;
import com.dbms.project.model.PrimaryKey;
import com.dbms.project.model.Table;
import com.dbms.project.model.validators.Validator;
import com.dbms.project.repository.TableRepository;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TableService {
    private final static Logger logger = LoggerFactory.getLogger(TableService.class);
    private final TableRepository tableRepository;

    public TableService(TableRepository tableRepository) {
        this.tableRepository = tableRepository;
    }

    public void insertRow(String databaseName, String tableName, Map<String, String> values) {
        JSONObject tbl = TableUtils.getTableFromDatabase(databaseName, tableName);
        if (tbl == null) {
            logger.error("Invalid table name.");
            throw new DBMSException("400", "Invalid table name. There is no table with this name.");
        }
        Table table = Table.fromJSON(tbl);
        if (table.getAttributes().size() != values.keySet().size()) {
            throw new DBMSException("Invalid number of attributes");
        }

        table.getAttributes().forEach((attr) -> {
            if (!values.containsKey(attr.getAttributeName())) {
                throw new DBMSException("Invalid attributes");
            }
        });

        String row = table.getAttributes().stream()
                .filter(attr -> !attr.getIsPrimaryKey())
                .map(attr -> {
                    Validator.validate(values.get(attr.getAttributeName()), attr.getAttributeType());
                    return values.get(attr.getAttributeName());
                })
                .reduce("", (result, name) -> result + name + ";");
        PrimaryKey primaryKey = new PrimaryKey(table.getAttributes().stream()
                .filter(Attribute::getIsPrimaryKey)
                .map(attr -> values.get(attr.getAttributeName()))
                .toList());

        var uniqueAttributes = table.getUniqueKeys().stream()
                .map(attr -> Pair.of(attr, values.get(attr))).toList();

        try {
            tableRepository.insertRow(databaseName, tableName, primaryKey, row, uniqueAttributes);
        } catch (Exception e) {
            if (e.getClass().equals(DBMSException.class)) {
                throw new DBMSException(e.getMessage());
            }
        }
    }

    public void deleteRow(String databaseName, String tableName, String rowId) {
        JSONObject tbl = TableUtils.getTableFromDatabase(databaseName, tableName);
        if (tbl == null) {
            logger.error("Invalid table name.");
            throw new DBMSException("400", "Invalid table name. There is no table with this name.");
        }
        Table table = Table.fromJSON(tbl);

        try {
            tableRepository.deleteRow(databaseName, tableName, rowId);
        } catch (Exception e) {

        }

    }

    public List<?> getAllRows(String databaseName, String tableName) throws Exception {
        JSONObject tbl = TableUtils.getTableFromDatabase(databaseName, tableName);
        Table table = Table.fromJSON(tbl);
        List<?> rows = tableRepository.getAllRows(databaseName, table);
        return rows;
    }
}
