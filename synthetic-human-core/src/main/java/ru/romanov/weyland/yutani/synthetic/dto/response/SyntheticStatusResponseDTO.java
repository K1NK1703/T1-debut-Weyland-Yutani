package ru.romanov.weyland.yutani.synthetic.dto.response;

import java.time.LocalDateTime;

public record SyntheticStatusResponseDTO(String name,
                                         String status,
                                         String version,
                                         int queueSize,
                                         int totalCommands,
                                         long activeCommands,
                                         LocalDateTime timestamp
) {}
