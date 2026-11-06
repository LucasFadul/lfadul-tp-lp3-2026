package py.edu.uc.lp3.rest.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import py.edu.uc.lp3.domain.exception.EntidadConflictException;
import py.edu.uc.lp3.domain.exception.EntidadNotFoundException;
import py.edu.uc.lp3.domain.exception.EntidadInvalidaException;
import py.edu.uc.lp3.domain.exception.EntidadSinVidaException;
import py.edu.uc.lp3.domain.exception.TntDetonadaException;
import py.edu.uc.lp3.domain.exception.TntNotFoundException;

@RestControllerAdvice
public class ApiExceptionHandler {
	@ExceptionHandler(TntDetonadaException.class)
	public ResponseEntity<Map<String, String>> tntDetonada(TntDetonadaException error) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", error.getMessage()));
	}

	@ExceptionHandler(TntNotFoundException.class)
	public ResponseEntity<Map<String, String>> tntNoEncontrada(TntNotFoundException error) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", error.getMessage()));
	}
	@ExceptionHandler(EntidadInvalidaException.class)
	public ResponseEntity<Map<String, String>> entidadInvalida(EntidadInvalidaException error) {
		return ResponseEntity.badRequest().body(Map.of("error", error.getMessage()));
	}

	@ExceptionHandler(EntidadSinVidaException.class)
	public ResponseEntity<Map<String, String>> entidadSinVida(EntidadSinVidaException error) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", error.getMessage()));
	}

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
