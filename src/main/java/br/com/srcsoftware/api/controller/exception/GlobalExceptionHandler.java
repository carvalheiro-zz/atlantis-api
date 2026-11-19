package br.com.srcsoftware.api.controller.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import br.com.srcsoftware.api.atlantis.exceptions.ApplicationException;
import br.com.srcsoftware.api.controller.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Erro nao tratado capturado globalmente: ", ex);

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "Erro Interno do Servidor");
        body.put("message", ex.getMessage());
        body.put("path", request.getRequestURI());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, Object> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Erro de Validacao nos Campos");
        body.put("errors", errors);
        body.put("path", request.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
    
    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<Map<String, Object>> handleApplicationException(ApplicationException ex, HttpServletRequest request) {
        log.error("Erro de negócio capturado: {}", ex.getMessage());

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("success", false);
        body.put("error", "Erro de Regra de Negócio");
        body.put("message", ex.getMessage());
        body.put("path", request.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
    
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNoResourceFoundException(NoResourceFoundException ex, HttpServletRequest request) {
        log.warn("Recurso não encontrado (404): {}", request.getRequestURI());

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("success", false);
        body.put("error", "Não Encontrado");
        body.put("message", "O endpoint ou recurso solicitado não existe.");
        body.put("path", request.getRequestURI());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }
    
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolation(
            DataIntegrityViolationException ex, HttpServletRequest request) {
        
        String causeMsg = ex.getMostSpecificCause().getMessage();
        log.error("Erro de integridade de dados detectado: {}", causeMsg);

        String mensagem = "Registro duplicado ou violação de integridade no banco de dados.";

        // 1. Tenta capturar pelo padrão padrão (PostgreSQL/MySQL): Key (campo1, campo2)=...
        Matcher matcherKey = Pattern.compile("Key \\((.*?)\\)=", Pattern.CASE_INSENSITIVE).matcher(causeMsg);
        if (matcherKey.find()) {
            String campos = matcherKey.group(1);
            mensagem = String.format("Já existe um registro cadastrado com estes mesmos valores para o(s) campo(s): [%s].", campos);
        } 
        // 2. Tenta capturar pelo formato do H2: ... on table(coluna1, coluna2) ...
        else {
            Matcher matcherH2Table = Pattern.compile("\\(([^)]+)\\)\\s+VALUES", Pattern.CASE_INSENSITIVE).matcher(causeMsg);
            if (matcherH2Table.find()) {
                Matcher colMatcher = Pattern.compile("on\\s+\\S+\\(([^)]+)\\)", Pattern.CASE_INSENSITIVE).matcher(causeMsg);
                if (colMatcher.find()) {
                    String campos = colMatcher.group(1).replaceAll("(?i)\\s+nulls\\s+first|nulls\\s+last", "").trim();
                    mensagem = String.format("Já existe um registro cadastrado com estes mesmos valores para o(s) campo(s): [%s].", campos);
                } else {
                    Matcher matcherUk = Pattern.compile("(?i)(uk_[a-zA-Z0-9_]+)").matcher(causeMsg);
                    if (matcherUk.find()) {
                        mensagem = String.format("Já existe um registro cadastrado violando a restrição de unicidade [%s].", matcherUk.group(1));
                    }
                }
            } else {
                // 3. Fallback genérico para o nome da constraint UK_
                Matcher matcherUk = Pattern.compile("(?i)(uk_[a-zA-Z0-9_]+)").matcher(causeMsg);
                if (matcherUk.find()) {
                    mensagem = String.format("Já existe um registro cadastrado violando a restrição de unicidade [%s].", matcherUk.group(1));
                }
            }
        }

        ApiResponse<Void> response = ApiResponse.success(
            HttpStatus.BAD_REQUEST.value(),
            mensagem,
            request.getRequestURI()
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}