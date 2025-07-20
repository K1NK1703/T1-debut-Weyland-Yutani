package ru.romanov.weyland.yutani.synthetic.dto.response;

import java.time.LocalDateTime;

public record ErrorResponseDTO(String code,
                               String message,
                               Object details,
                               LocalDateTime timestamp
) {}
