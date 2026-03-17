package com.example.clientmanager.exception;

import java.time.LocalDateTime;
import java.util.Map;

public class ApiError {
    private LocalDateTime timestamp;
    private String path;
    private int status;
    private String message;
    private Map<String, String> errors; // només per validacions, pot ser null

    public ApiError(LocalDateTime timestamp, String path, int status, String message) {
        this(timestamp, path, status, message, null);
    }

    public ApiError(LocalDateTime timestamp, String path, int status, String message, Map<String, String> errors) {
        this.timestamp = timestamp;
        this.path = path;
        this.status = status;
        this.message = message;
        this.errors = errors;
    }

    // Getters i setters
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Map<String, String> getErrors() { return errors; }
    public void setErrors(Map<String, String> errors) { this.errors = errors; }
}