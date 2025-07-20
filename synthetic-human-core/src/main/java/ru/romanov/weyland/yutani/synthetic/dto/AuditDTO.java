package ru.romanov.weyland.yutani.synthetic.dto;

import lombok.Builder;
import ru.romanov.weyland.yutani.synthetic.utils.AuditLevel;

import java.time.LocalDateTime;

@Builder
public record AuditDTO(
        LocalDateTime timestamp,
        String methodName,
        String description,
        String parameters,
        String result,
        String status,
        String errorMessage,
        long executionTimeMs,
        AuditLevel level
) {}