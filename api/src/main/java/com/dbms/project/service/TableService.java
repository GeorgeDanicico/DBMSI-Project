package com.dbms.project.service;

import com.dbms.project.controller.TableController;
import com.dbms.project.db.TableUtils;
import com.dbms.project.exceptions.DBMSException;
import com.dbms.project.model.*;
import com.dbms.project.model.validators.Validator;
import com.dbms.project.repository.TableRepository;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TableService {
    private final static Logger logger = LoggerFactory.getLogger(TableService.class);
    private final TableRepository tableRepository;

    public TableService(TableRepository tableRepository) {
        this.tableRepository = tableRepository;
    }


    public List<?> getAllRecordsBasedOnCriteria(String databaseName, String tableName, TableController.SelectBody selectBody) throws Exception {
        Table table = Table.fromJSON(TableUtils.getTableFromDatabase(databaseName, tableName));
        PrimaryKey tablePrimaryKey = table.getPrimaryKey();
        List<Index> indexes = table.getIndexes();
        List<String> uniqueKeys = table.getUniqueKeys();
        List<ForeignKey> foreignKeys = table.getForeignKeys();

        List<String> projection = selectBody.projection();
        List<TableController.Condition> conditions = selectBody.conditions();
        Boolean isDistinct = selectBody.isDistinct();
        List<Map<String, String>> records = new ArrayList<>();

        List<String> conditionsAttributes = conditions.stream().map(TableController.Condition::columnName).toList();
        Optional<ForeignKey> usedForeignKey = foreignKeys.stream().filter(key -> new HashSet<>(key.getReferencedAttributes()).containsAll(conditionsAttributes)).findFirst();
        Optional<String> usedUniqueKey = uniqueKeys.stream().filter(key -> new HashSet<>(List.of(key)).containsAll(conditionsAttributes)).findFirst();
        Optional<Index> usedIndex = indexes.stream().filter(index -> new HashSet<>(index.getIndexAttributes()).containsAll(conditionsAttributes)).findFirst();

        // There exists a manually created unique/non unique index that contains all the attributes in the where clause
        if (usedIndex.isPresent()) {
            var index = usedIndex.get();
            var indexPrimaryKey = new PrimaryKey(new ArrayList<>());
            if (conditionsAttributes.size() == 1) {
                indexPrimaryKey.addAttribute(conditions.get(0).value());
                records = tableRepository.getAllRecordsWithCondition(databaseName, table, index.getIndexTableName(table.getTableName()),
                        indexPrimaryKey.getPk(), conditions.get(0).operation());
            } else if (usedIndex.get().getIndexAttributes().size() == conditionsAttributes.size()) {
                conditions.forEach(condition -> indexPrimaryKey.addAttribute(condition.value()));
                records = tableRepository.getAllRecordsWithCondition(databaseName, table, index.getIndexTableName(table.getTableName()), indexPrimaryKey.getPk(), Operation.EQUAL);
            } else {
                for (int i = 0; i < usedIndex.get().getIndexAttributes().size(); i++) {
                    var indexAttribute = usedIndex.get().getIndexAttributes().get(i);
                    if (conditionsAttributes.contains(indexAttribute)) {
                        indexPrimaryKey.addAttribute(conditions.get(conditionsAttributes.indexOf(indexAttribute)).value());
                    } else {
                        indexPrimaryKey.addAttribute(".*?");
                    }
                }
                records = tableRepository.getAllRecordsWithCondition(databaseName, table, index.getIndexTableName(table.getTableName()), indexPrimaryKey.getPk(), Operation.LIKE);
                records = records.stream()
                        .filter(record -> conditions.stream()
                                .allMatch(condition -> record.get(condition.columnName()).equals(condition.value())))
                        .toList();
            }
        } // There exists a  foreign key that contains all the attributes in the where clause
        else if (usedForeignKey.isPresent()) {
            var fk = usedForeignKey.get();
            var foreignKeyIndexName = String.format("FK_%s_%s", table.getTableName(), String.join("-", fk.getReferencedAttributes()));
            var indexPrimaryKey = new PrimaryKey(new ArrayList<>());
            if (conditionsAttributes.size() == 1) {
                indexPrimaryKey.addAttribute(conditions.get(0).value());
                records = tableRepository.getAllRecordsWithCondition(databaseName, table, foreignKeyIndexName,
                        indexPrimaryKey.getPk(), conditions.get(0).operation());
            } else if (fk.getReferencedAttributes().size() == conditionsAttributes.size()) {
                conditions.forEach(condition -> indexPrimaryKey.addAttribute(condition.value()));
                records = tableRepository.getAllRecordsWithCondition(databaseName, table, foreignKeyIndexName, indexPrimaryKey.getPk(), Operation.EQUAL);
            } else {
                for (int i = 0; i < fk.getReferencedAttributes().size(); i++) {
                    var fkAttribute = fk.getReferencedAttributes().get(i);
                    if (conditionsAttributes.contains(fkAttribute)) {
                        indexPrimaryKey.addAttribute(conditions.get(conditionsAttributes.indexOf(fkAttribute)).value());
                    } else {
                        indexPrimaryKey.addAttribute(".*?");
                    }
                }
                records = tableRepository.getAllRecordsWithCondition(databaseName, table, foreignKeyIndexName, indexPrimaryKey.getPk(), Operation.LIKE);
                records = records.stream()
                        .filter(record -> conditions.stream()
                                .allMatch(condition -> record.get(condition.columnName()).equals(condition.value())))
                        .toList();
            }
        } // There exists a manually created unique key that contains all the attributes in the where clause
        else if (usedUniqueKey.isPresent()) {
            var uniqueKey = usedUniqueKey.get();
            var uniqueKeyIndexName = String.format("UK_%s_%s", table.getTableName(), uniqueKey);
            var indexPrimaryKey = new PrimaryKey(new ArrayList<>());
            indexPrimaryKey.addAttribute(conditions.get(0).value());
            records = tableRepository.getAllRecordsWithCondition(databaseName, table, uniqueKeyIndexName,
                    indexPrimaryKey.getPk(), conditions.get(0).operation());
        } // Get all the data from the existing indexes and join the results
        else {
            List<Map<String, String>> previousRecords = null;
            for (var condition : conditions) {
                var conditionAttribute = condition.columnName();
                var conditionValue = condition.value();
                var conditionOperation = condition.operation();
                var conditionIndex = indexes.stream().filter(index -> List.of(conditionAttribute).containsAll(index.getIndexAttributes()))
                        .map((index) -> index.getIndexTableName(table.getTableName())).findFirst();
                if (conditionIndex.isEmpty()) {
                    conditionIndex = uniqueKeys.stream().filter(key -> List.of(conditionAttribute).containsAll(List.of(key)))
                            .map((key) -> String.format("UK_%s_%s", table.getTableName(), key)).findFirst();
                }
                if (conditionIndex.isEmpty()) {
                    conditionIndex = foreignKeys.stream().filter(key -> List.of(conditionAttribute).containsAll(key.getReferencedAttributes()))
                            .map((key) -> String.format("FK_%s_%s", table.getTableName(), String.join("-", key.getReferencedAttributes()))).findFirst();
                }

                if (conditionIndex.isPresent()) {
                    var indexName = conditionIndex.get();
                    var indexPrimaryKey = new PrimaryKey(new ArrayList<>());
                    indexPrimaryKey.addAttribute(conditionValue);
                    var newRecords = tableRepository.getAllRecordsWithCondition(databaseName, table, indexName,
                            indexPrimaryKey.getPk(), conditionOperation);
                    if (previousRecords != null) {
                        var finalPreviousRecords = previousRecords;
                        previousRecords = newRecords.stream()
                                .filter(record -> finalPreviousRecords.stream()
                                        .anyMatch(previousRecord -> tablePrimaryKey.getPkAttributes().stream()
                                                .filter(attr -> previousRecord.get(attr).equals(record.get(attr)))
                                                .toList().size() == tablePrimaryKey.getPkAttributes().size()))
                                .toList();
                    } else {
                        previousRecords = newRecords;
                    }
                } else {
                    var rows = tableRepository.getAllRows(databaseName, table);
                    rows = selectRecordsFromCriteria(conditionAttribute, conditionValue, rows, conditionOperation.getOperation());

                    if (previousRecords != null) {
                        var finalPreviousRecords = previousRecords;
                        previousRecords = rows.stream()
                                .filter(record -> finalPreviousRecords.stream()
                                        .anyMatch(previousRecord -> tablePrimaryKey.getPkAttributes().stream()
                                                .filter(attr -> previousRecord.get(attr).equals(record.get(attr)))
                                                .toList().size() == tablePrimaryKey.getPkAttributes().size()))
                                .toList();
                    } else {
                        previousRecords = rows;
                    }
                }
            }
            records = previousRecords;
        }

        var returnedRecords = records.stream()
                .map(record -> record.entrySet().stream()
                        .filter(entry -> projection.contains(entry.getKey()))
                        .toList().stream()
                        .collect(HashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), Map::putAll))
                .toList();

        if (isDistinct) {
            return getDistinctMaps(returnedRecords);
        }

        return returnedRecords;
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
            tableRepository.insertRow(databaseName, tableName, primaryKey, row);
            updateUniqueKeys(databaseName, table, primaryKey, values, "insert");
            updateForeignKeys(databaseName, table, primaryKey, values, "insert");
            updateIndexes(databaseName, table, primaryKey, values, "insert");
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

    private void updateUniqueKeys(String databaseName, Table table, PrimaryKey
            primaryKey, Map<String, String> values, String operation) {
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

    private List<Map<String, String>> selectRecordsFromCriteria(String columnName, String actualValue,
                                           List<Map<String, String>> values, String operation) {
        return values.stream()
                .filter(record -> {
                    var value = record.get(columnName);
                    return switch (operation) {
                        case "EQUAL" -> value.equals(actualValue);
                        case "LESS_THAN" -> value.compareTo(actualValue) == 0;
                        case "GREATER_THAN" -> value.compareTo(actualValue) > 0;
                        case "LIKE" -> value.matches(actualValue);
                        default -> true;
                    };
                })
                .toList();

    }

    private void updateForeignKeys(String databaseName, Table table, PrimaryKey
            primaryKey, Map<String, String> values, String operation) {
        table.getForeignKeys().forEach(key -> {
            String foreignKeyIndexName = String.format("FK_%s_%s", table.getTableName(), String.join("-", key.getReferencedAttributes()));
            try {
                PrimaryKey indexPrimaryKey = new PrimaryKey(new ArrayList<>());
                key.getReferencedAttributes().forEach(attr -> indexPrimaryKey.addAttribute(values.get(attr)));
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

    private void updateIndexes(String databaseName, Table table, PrimaryKey
            primaryKey, Map<String, String> values, String operation) {
        table.getIndexes().forEach(index -> {
            String indexName = String.format("%s_%s_%s", index.getIsUnique() == 1 ? "UK" : "NUK", table.getTableName(), index.getIndexName());
            try {
                PrimaryKey indexPrimaryKey = new PrimaryKey(new ArrayList<>());
                index.getIndexAttributes().forEach(attr -> indexPrimaryKey.addAttribute(values.get(attr)));
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

    public boolean areMapsDistinct(Map<Object, Object> map1, Map<Object, Object> map2) {
        for (Object key : map1.keySet()) {
            if (!Objects.equals(map1.get((String) key), map2.get((String) key))) {
                return true; // Maps are distinct
            }
        }
        return false; // Maps are not distinct
    }

    public List<Map<Object, Object>> getDistinctMaps(List<HashMap<Object, Object>> maps) {
        Set<Map<Object, Object>> distinctMaps = new HashSet<Map<Object, Object>>() {
            @Override
            public boolean add(Map<Object, Object> map) {
                return !containsAny(map) && super.add(map);
            }

            private boolean containsAny(Map<Object, Object> map) {
                for (Map<Object, Object> existingMap : this) {
                    if (!areMapsDistinct(existingMap, map)) {
                        return true;
                    }
                }
                return false;
            }
        };

        distinctMaps.addAll(maps);

        return new ArrayList<>(distinctMaps);
    }
}
