package ru.romanov.weyland.yutani.synthetic.rest.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.romanov.weyland.yutani.synthetic.dto.response.ErrorResponseDTO;
import ru.romanov.weyland.yutani.synthetic.exception.CommandExecutionException;
import ru.romanov.weyland.yutani.synthetic.exception.CommandQueueOverflowException;
import ru.romanov.weyland.yutani.synthetic.exception.CommandValidationException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(
                "VALIDATION_ERROR",
                "Ошибка валидации команды",
                errors,
                LocalDateTime.now()
        );

        log.warn("Ошибка валидации команды: {}", errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseDTO);
    }

    @ExceptionHandler(CommandQueueOverflowException.class)
    public ResponseEntity<ErrorResponseDTO> handleCommandQueueOverflow(
            CommandQueueOverflowException ex) {

        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(
                "QUEUE_OVERFLOW",
                ex.getMessage(),
                null,
                LocalDateTime.now()
        );

        log.error("Переполнение очереди команд: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponseDTO);
    }

    @ExceptionHandler(CommandExecutionException.class)
    public ResponseEntity<ErrorResponseDTO> handleCommandExecution(
            CommandExecutionException ex) {

        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(
                "EXECUTION_ERROR",
                ex.getMessage(),
                null,
                LocalDateTime.now()
        );

        log.error("Ошибка выполнения команды: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponseDTO);
    }

    @ExceptionHandler(CommandValidationException.class)
    public ResponseEntity<ErrorResponseDTO> handleCommandValidation(
            CommandValidationException ex) {

        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(
                "COMMAND_VALIDATION_ERROR",
                ex.getMessage(),
                null,
                LocalDateTime.now()
        );

        log.warn("Ошибка валидации команды: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponseDTO);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception ex) {

        ErrorResponseDTO errorResponseDTO = new ErrorResponseDTO(
                "INTERNAL_ERROR",
                "Внутренняя ошибка системы синтетиков",
                null,
                LocalDateTime.now()
        );

        log.error("Неожиданная ошибка в системе синтетиков: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponseDTO);
    }
}
