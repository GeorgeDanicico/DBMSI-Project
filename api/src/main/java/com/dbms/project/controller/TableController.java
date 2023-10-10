package com.dbms.project.controller;

import com.dbms.project.dto.MessageResponse;
import com.dbms.project.model.Database;
import com.dbms.project.model.Index;
import com.dbms.project.model.Table;
import com.dbms.project.repository.DatabaseRepository;
import com.dbms.project.repository.TableRepository;
import jakarta.validation.Valid;
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

    public TableController(TableRepository tableRepository) {
        this.tableRepository = tableRepository;
    }

    @GetMapping("/databases/{databaseName}/tables")
    public Map<?, ?> getAllTablesForDatabase(@Valid @PathVariable String databaseName) throws Exception {

        return null;
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
        boolean deleted = tableRepository.deleteTable(databaseName, tableName);

        if (deleted) {
            return new ResponseEntity<>(new MessageResponse("Table deleted successfully"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new MessageResponse("Could not delete table"), HttpStatus.BAD_REQUEST);
    }
}
