package br.com.batch.users.presentation.exception;

import br.com.batch.users.domain.exception.EmailInvalidException;
import br.com.batch.users.domain.exception.UserImportListIsNullException;
import br.com.batch.users.domain.exception.UserImportNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserImportNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleUserImportNotFound(final UserImportNotFoundException ex) {
        log.error("User import not found: {}", ex.getMessage());
        final var errorResponse = ErrorResponseDto.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(UserImportListIsNullException.class)
    public ResponseEntity<ErrorResponseDto> handleUserImportListIsNullException(final UserImportListIsNullException ex) {
        log.error("User import list is null: {}", ex.getMessage());
        final var errorResponse = ErrorResponseDto.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(EmailInvalidException.class)
    public ResponseEntity<ErrorResponseDto> handleEmailInvalidException(final EmailInvalidException ex) {
        log.error("Invalid email. Cause: {}", ex.getMessage());
        final var errorResponse = ErrorResponseDto.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponseDto> handleBadRequestException(final BadRequestException ex) {
        log.error("Bad request. Cause: ", ex);
        final var errorResponse = ErrorResponseDto.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGeneralException(final Exception ex) {
        log.error("An unexpected error occurred. Cause: ", ex);
        final var errorResponse = ErrorResponseDto.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message("An unexpected error occurred: " + ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
