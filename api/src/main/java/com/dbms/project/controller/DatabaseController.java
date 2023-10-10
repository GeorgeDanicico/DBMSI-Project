package com.dbms.project.controller;

import com.dbms.project.dto.MessageResponse;
import com.dbms.project.model.Database;
import com.dbms.project.repository.DatabaseRepository;
import jakarta.validation.Valid;
import org.json.simple.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api")
public class DatabaseController {
    private final DatabaseRepository databaseRepository;

    public DatabaseController(DatabaseRepository databaseRepository) {
        this.databaseRepository = databaseRepository;
    }

    @GetMapping("/databases")
    public JSONObject getAllDatabases() throws Exception {
        JSONObject databases = databaseRepository.getAllDatabases();
//        Map<String, List<Database>> response = new HashMap<>();
//        response.put("databases", databases);

        return databases;
    }

    @PostMapping("/databases")
    public ResponseEntity<?> createDatabase(@Valid @RequestBody DatabaseRequest databaseRequest) throws Exception {
        boolean created = databaseRepository.createDatabase(databaseRequest.name());

        if (created) {
            return new ResponseEntity<>(new MessageResponse("Database created successfully"), HttpStatus.CREATED);
        }
        return new ResponseEntity<>(new MessageResponse("Could not create the database."), HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/databases/{databaseName}")
    public ResponseEntity<?> deleteDatabase(@Valid @PathVariable String databaseName) throws Exception {
        boolean deleted = databaseRepository.deleteDatabase(databaseName);

        if (deleted) {
            return new ResponseEntity<>(new MessageResponse("Database deleted successfully"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new MessageResponse("Could not delete the database"), HttpStatus.BAD_REQUEST);
    }

    public record DatabaseRequest(String name) {}
}
