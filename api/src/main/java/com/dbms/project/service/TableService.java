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
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TableService {
    private final static Logger logger = LoggerFactory.getLogger(TableService.class);
    private final TableRepository tableRepository;

    public TableService(TableRepository tableRepository) {
        this.tableRepository = tableRepository;
    }

    public void deleteTable(String databaseName, String tableName) throws Exception {
        Table table = Table.fromJSON(TableUtils.getTableFromDatabase(databaseName, tableName));
        deleteIndexTables(databaseName, table);
        deleteUniqueKeysTables(databaseName, table);
        deleteForeignKeyTables(databaseName, table);
        checkIfTableIsReferenced(databaseName, tableName);
        tableRepository.deleteTable(databaseName, tableName);
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

        validateForeignKeyConstraint(databaseName, table, values);
        validateUniqueKeys(databaseName, table, values);
        validateIndexes(databaseName, table, values);

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
        try {
            updateUniqueKeys(databaseName, table, primaryKey, values, "insert");
            updateForeignKeys(databaseName, table, primaryKey, values, "insert");
            updateIndexes(databaseName, table, primaryKey, values, "insert");
            tableRepository.insertRow(databaseName, tableName, primaryKey, row);
        } catch (Exception e) {
            throw new DBMSException("400", e.getMessage());
        }
    }

    public void deleteRow(String databaseName, String tableName, String rowId) {
        JSONObject tbl = TableUtils.getTableFromDatabase(databaseName, tableName);
        if (tbl == null) {
            logger.error("Invalid table name.");
            throw new DBMSException("400", "Invalid table name. There is no table with this name.");
        }
        Table table = Table.fromJSON(tbl);
        checkIfRowIsReferenced(databaseName, table, rowId);
        Map<String, String> rowValues = tableRepository.getRow(databaseName, table, rowId);
        PrimaryKey primaryKey = new PrimaryKey(table.getAttributes().stream()
                .filter(Attribute::getIsPrimaryKey)
                .map(attr -> rowValues.get(attr.getAttributeName()))
                .toList());

        try {
            updateUniqueKeys(databaseName, table, primaryKey, rowValues, "delete");
            updateForeignKeys(databaseName, table, primaryKey, rowValues, "delete");
            updateIndexes(databaseName, table, primaryKey, rowValues, "delete");
            tableRepository.deleteRow(databaseName, tableName, rowId);
        } catch (Exception e) {

            throw new DBMSException("400", "Invalid row id");
        }
    }

    private void updateUniqueKeys(String databaseName, Table table, PrimaryKey primaryKey, Map<String, String> values, String operation) {
        table.getUniqueKeys().forEach(key -> {
            String uniqueKeyIndexName = String.format("UK_%s_%s", table.getTableName(), key);
            try {
                PrimaryKey indexPrimaryKey = new PrimaryKey();
                indexPrimaryKey.setPkAttributes(new ArrayList<>(List.of(values.get(key))));
                if (operation.equals("delete")) {
                    tableRepository.deleteRowForIndex(databaseName, uniqueKeyIndexName, indexPrimaryKey.getPk(), null, true);
                } else {
                    tableRepository.insertRow(databaseName, uniqueKeyIndexName, indexPrimaryKey, primaryKey.getPk());
                }
            } catch (Exception e) {
                logger.error("Error while updating unique key index: {}", uniqueKeyIndexName);
                throw new DBMSException("400", "Violating Unique Key Constraint");
            }
        });
    }

    private void updateForeignKeys(String databaseName, Table table, PrimaryKey primaryKey, Map<String, String> values, String operation) {
        table.getForeignKeys().forEach(key -> {
            String foreignKeyIndexName = String.format("FK_%s_%s", table.getTableName(), String.join("-", key.getReferencedAttributes()));
            try {
                PrimaryKey indexPrimaryKey = new PrimaryKey();
                key.getReferencedAttributes().forEach(attr -> indexPrimaryKey.setPkAttributes(new ArrayList<>(List.of(values.get(attr)))));
                if (operation.equals("delete")) {
                    tableRepository.deleteRowForIndex(databaseName, foreignKeyIndexName, indexPrimaryKey.getPk(), primaryKey.getPk(), false);
                } else {
                    tableRepository.insertRowInIndex(databaseName, foreignKeyIndexName, indexPrimaryKey, primaryKey.getPk(), false);
                }
            } catch (Exception e) {
                logger.error("Error while updating foreign key index: {}", foreignKeyIndexName);
                throw new DBMSException("400", "Invalid foreign key");
            }
        });
    }

    private void updateIndexes(String databaseName, Table table, PrimaryKey primaryKey, Map<String, String> values, String operation) {
        table.getIndexes().forEach(index -> {
            String indexName = String.format("%s_%s_%s", index.getIsUnique() == 1 ? "UK" : "NUK", table.getTableName(), index.getIndexName());
            try {
                PrimaryKey indexPrimaryKey = new PrimaryKey();
                index.getIndexAttributes().forEach(attr -> indexPrimaryKey.setPkAttributes(new ArrayList<>(List.of(values.get(attr)))));
                if (operation.equals("delete")) {
                    tableRepository.deleteRowForIndex(databaseName, indexName, indexPrimaryKey.getPk(), primaryKey.getPk(), index.getIsUnique() == 1);
                } else {
                    tableRepository.insertRowInIndex(databaseName, indexName, indexPrimaryKey, primaryKey.getPk(), index.getIsUnique() == 1);
                }
            } catch (Exception e) {
                logger.error("Error while updating  unique index: {}", indexName);
                throw new DBMSException("400", "Invalid foreign key");
            }
        });
    }

    public List<?> getAllRows(String databaseName, String tableName) throws Exception {
        JSONObject tbl = TableUtils.getTableFromDatabase(databaseName, tableName);
        Table table = Table.fromJSON(tbl);
        List<?> rows = tableRepository.getAllRows(databaseName, table);
        return rows;
    }

    private void deleteUniqueKeysTables(String databaseName, Table table) {
        table.getUniqueKeys().forEach(key -> {
            String uniqueKeyIndexName = String.format("UK_%s_%s", table.getTableName(), key);
            try {
                tableRepository.deleteIndexTables(databaseName, uniqueKeyIndexName);
            } catch (Exception e) {
                logger.error("Error while deleting unique key index: {}", uniqueKeyIndexName);
                throw new DBMSException("400", "Invalid unique key");
            }
        });
    }

    private void deleteIndexTables(String databaseName, Table table) {
        table.getIndexes().forEach(index -> {
            String indexName = String.format("%s_%s_%s", index.getIsUnique() == 1 ? "UK" : "NUK", table.getTableName(), index.getIndexName());
            try {
                tableRepository.deleteIndexTables(databaseName, indexName);
            } catch (Exception e) {
                logger.error("Error while deleting index: {}", indexName);
                throw new DBMSException("400", "Invalid index");
            }
        });
    }

    private void deleteForeignKeyTables(String databaseName, Table table) {
        table.getForeignKeys().forEach(key -> {
            String foreignKeyIndexName = String.format("FK_%s_%s", table.getTableName(), String.join("-", key.getReferencedAttributes()));
            try {
                tableRepository.deleteIndexTables(databaseName, foreignKeyIndexName);
            } catch (Exception e) {
                logger.error("Error while deleting foreign key index: {}", foreignKeyIndexName);
                throw new DBMSException("400", "Invalid foreign key");
            }
        });
    }

    private void validateForeignKeyConstraint(String databaseName, Table table, Map<String, String> values) {
        table.getForeignKeys().forEach(key -> {
            var referencedTable = Table.fromJSON(TableUtils.getTableFromDatabase(databaseName, key.getReferencedTable()));
            var referencedAttributes = key.getReferencedAttributes();
            Validator.validateForeignKeyConstraint(referencedTable, referencedAttributes);
            var referencedValues = String.join(",", key.getReferencedAttributes().stream()
                    .map(values::get)
                    .toList());
            tableRepository.checkIfForeignKeyExists(databaseName, referencedTable.getTableName(), referencedValues);
        });
    }

    private void validateUniqueKeys(String databaseName, Table table, Map<String, String> values) {
        table.getUniqueKeys().forEach(key -> {
            var uniqueKeyIndexName = String.format("UK_%s_%s", table.getTableName(), key);
            tableRepository.checkUniqueKey(databaseName, uniqueKeyIndexName, key);
        });
    }

    private void validateIndexes(String databaseName, Table table, Map<String, String> values) {
        table.getIndexes().forEach(index -> {
            if (index.getIsUnique() == 1) {
                var indexName = String.format("%s_%s_%s", "UK", table.getTableName(), index.getIndexName());
                PrimaryKey indexPrimaryKey = new PrimaryKey();
                index.getIndexAttributes().forEach(attr -> indexPrimaryKey.setPkAttributes(new ArrayList<>(List.of(values.get(attr)))));
                tableRepository.checkUniqueIndex(databaseName, indexName, indexPrimaryKey.getPk());
            }
        });
    }

    private void checkIfTableIsReferenced(String database, String tableName) {
        var tables = TableUtils.getAllTablesForDatabase(database);
        tables.forEach(tbl -> {
            var table = Table.fromJSON((JSONObject) tbl);
            table.getForeignKeys().forEach(key -> {
                if (key.getReferencedTable().equals(tableName)) {
                    logger.error("Table is referenced by another table");
                    throw new DBMSException("400", "Table is referenced by another table");
                }
            });
        });
    }

    private void checkIfRowIsReferenced(String databaseName, Table table, String rowId) {
        var tables = TableUtils.getAllTablesForDatabase(databaseName);
        tables.forEach(tbl -> {
            var dbTable = Table.fromJSON((JSONObject) tbl);
            dbTable.getForeignKeys().forEach(key -> {
                if (key.getReferencedTable().equals(table.getTableName())) {
                    var foreignKeyIndexName = String.format("FK_%s_%s", dbTable.getTableName(), String.join("-", key.getReferencedAttributes()));
                    try {
                        tableRepository.checkIfForeignKeyReferenced(databaseName, foreignKeyIndexName, rowId);
                    } catch (Exception e) {
                        logger.error("Error while checking if row is referenced: {}", foreignKeyIndexName);
                        throw new DBMSException("400", e.getMessage());
                    }
                }
            });
        });
    }
}
