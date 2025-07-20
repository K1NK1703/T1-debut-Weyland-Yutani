package ru.romanov.weyland.yutani.synthetic.dto.response;

import java.time.LocalDateTime;

public record CommandResponseDTO(String commandId,
                                 String status,
                                 String message,
                                 LocalDateTime timestamp
) {}
