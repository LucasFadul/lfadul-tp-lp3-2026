package py.edu.uc.lp3.rest.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import py.edu.uc.lp3.service.EntidadConflictException;
import py.edu.uc.lp3.service.EntidadNotFoundException;

@RestControllerAdvice
public class ApiExceptionHandler {
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Map<String, String>> badRequest(IllegalArgumentException error) {
		return ResponseEntity.badRequest().body(Map.of("error", error.getMessage()));
	}

	@ExceptionHandler(EntidadNotFoundException.class)
	public ResponseEntity<Map<String, String>> notFound(EntidadNotFoundException error) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", error.getMessage()));
	}

	@ExceptionHandler(EntidadConflictException.class)
	public ResponseEntity<Map<String, String>> conflict(EntidadConflictException error) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", error.getMessage()));
	}
}
