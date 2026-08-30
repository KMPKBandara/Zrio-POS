package com.zrio.pos.system;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/system/database")
public class DatabaseCheckController {

    private final JdbcTemplate jdbcTemplate;

    public DatabaseCheckController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/ping")
    public String pingDatabase() {
        Integer result = jdbcTemplate.queryForObject(
                "SELECT 1",
                Integer.class
        );

        return result != null && result == 1
                ? "PostgreSQL connection is working"
                : "PostgreSQL connection failed";
    }
}