package com.dbms.project.controller;

import com.dbms.project.dto.MessageResponse;
import com.dbms.project.model.Index;
import com.dbms.project.model.Operation;
import com.dbms.project.model.Table;
import com.dbms.project.repository.TableRepository;
import com.dbms.project.service.TableService;
import jakarta.validation.Valid;
import org.json.simple.JSONArray;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api")
public class TableController {
    private final TableRepository tableRepository;
    private final TableService tableService;

    public TableController(TableRepository tableRepository, TableService tableService) {
        this.tableRepository = tableRepository;
        this.tableService = tableService;
    }

    @GetMapping("/databases/{databaseName}/tables")
    public Map<?, ?> getAllTablesForDatabase(@Valid @PathVariable String databaseName) throws Exception {
        Map<String, JSONArray> response = new HashMap<>();
        response.put("tables", tableRepository.getAllTables(databaseName));

        return response;
    }

    @PostMapping("/databases/{databaseName}/tables/{tableName}/select")
    public Map<?, ?> getRecordsFromTable(@Valid @PathVariable String databaseName,
                                         @Valid @PathVariable String tableName,
                                         @Valid @RequestBody SelectBody selectBody) throws Exception {
        Map<String, List<?>> response = new HashMap<>();
        response.put("records", tableService.getRecordsBasedOnCriteria(
                databaseName, tableName, selectBody.projection(),
                selectBody.conditions(), selectBody.isDistinct()));
        return response;
    }

    @PostMapping("/databases/{databaseName}/tables/select/join")
    public Map<?, ?> getRecordsFromTable(@Valid @PathVariable String databaseName,
                                         @Valid @RequestBody SelectBodyJoin selectBodyJoin) throws Exception {
        Map<String, List<?>> response = new HashMap<>();
        response.put("records", tableService.getAllRecordsBasedOnJoinAndCriteria(
                databaseName, selectBodyJoin.initialTableName, selectBodyJoin));
        return response;
    }

    @PostMapping("/databases/{databaseName}/tables")
    public ResponseEntity<?> createTable(@Valid @PathVariable String databaseName,
                                         @Valid @RequestBody Table table) throws Exception {
        boolean created = tableRepository.createTable(databaseName, table);

        if (created) {
            return new ResponseEntity<>(new MessageResponse("Table created successfully"), HttpStatus.CREATED);
        }
        return new ResponseEntity<>(new MessageResponse("Could not create table"), HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/databases/{databaseName}/tables/{tableName}/index")
    public ResponseEntity<?> createIndex(@Valid @PathVariable String databaseName,
                                         @Valid @PathVariable String tableName,
                                         @Valid @RequestBody Index index) throws Exception {
        boolean created = tableRepository.createIndex(databaseName, tableName, index);

        if (created) {
            return new ResponseEntity<>(new MessageResponse("Table index created successfully"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new MessageResponse("Could not create index for table"), HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/databases/{databaseName}/tables/{tableName}")
    public ResponseEntity<?> deleteTable(@Valid @PathVariable String databaseName,
                                         @Valid @PathVariable String tableName) throws Exception {
        tableService.deleteTable(databaseName, tableName);
        return new ResponseEntity<>(new MessageResponse("Table deleted successfully"), HttpStatus.OK);
    }

    @GetMapping("/databases/{databaseName}/tables/{tableName}/rows")
    public ResponseEntity<?> getRow(@Valid @PathVariable String databaseName,
                                       @Valid @PathVariable String tableName) throws Exception {
        List<?> rows = tableService.getAllRows(databaseName, tableName);
        Map<String, List<?>> records = new HashMap<>();
        records.put("records", rows);
        return new ResponseEntity<>(records, HttpStatus.OK);
    }

    @PostMapping("/databases/{databaseName}/tables/{tableName}/insert")
    public ResponseEntity<?> insertRow(@Valid @PathVariable String databaseName,
                                       @Valid @PathVariable String tableName,
                                       @Valid @RequestBody Map<String, String> values) {
        tableService.insertRow(databaseName, tableName, values);
        return new ResponseEntity<>(new MessageResponse("Row has been inserted successfully"), HttpStatus.OK);
    }

    @DeleteMapping("/databases/{databaseName}/tables/{tableName}/delete/{rowId}")
    public ResponseEntity<?> deleteRow(@Valid @PathVariable String databaseName,
                                       @Valid @PathVariable String tableName,
                                       @Valid @PathVariable String rowId) {
        tableService.deleteRow(databaseName, tableName, rowId);
        return new ResponseEntity<>(new MessageResponse("Row has been deleted successfully"), HttpStatus.OK);
    }

    public record Condition(String columnName, Operation operation, String value) { }

    public record JoinCondition(String columnName, String tableName) { }

    public record SelectBody(List<String> projection, List<Condition> conditions, Boolean isDistinct) { }

    public record SelectBodyJoin(List<String> projection,
                                 List<Condition> conditions,
                                 Boolean isDistinct,
                                 String initialTableName,
                                 List<JoinCondition> joinConditions) { }
}
