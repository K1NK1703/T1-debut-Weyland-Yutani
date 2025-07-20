package ru.romanov.weyland.yutani.synthetic.dto.response;

import java.util.Map;

public record SyntheticMetricsResponseDTO(int currentQueueSize,
                                          int totalCommandsProcessed,
                                          long activeCommands,
                                          Map<String, Integer> commandsByAuthor
) {}
