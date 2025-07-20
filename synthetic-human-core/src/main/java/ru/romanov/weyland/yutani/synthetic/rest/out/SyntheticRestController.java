package ru.romanov.weyland.yutani.synthetic.rest.out;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.romanov.weyland.yutani.synthetic.audit.WeylandWatchingYou;
import ru.romanov.weyland.yutani.synthetic.dto.response.CommandResponseDTO;
import ru.romanov.weyland.yutani.synthetic.dto.response.SyntheticMetricsResponseDTO;
import ru.romanov.weyland.yutani.synthetic.dto.response.SyntheticStatusResponseDTO;
import ru.romanov.weyland.yutani.synthetic.model.Command;
import ru.romanov.weyland.yutani.synthetic.service.CommandService;
import ru.romanov.weyland.yutani.synthetic.model.AuditLevel;

import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/synthetic")
public class SyntheticRestController {

    private final CommandService commandService;

    @GetMapping("/status")
    @WeylandWatchingYou(description = "Проверка статуса синтетика")
    public ResponseEntity<SyntheticStatusResponseDTO> getStatus() {
        SyntheticStatusResponseDTO status = new SyntheticStatusResponseDTO(
                "BISHOP-PROTOTYPE",
                "ONLINE",
                "Synthetic Human Core v1.0.0-OMEGA",
                commandService.getCurrentQueueSize(),
                commandService.getTotalCommandsCount(),
                commandService.getActiveCommandsCount(),
                LocalDateTime.now()
        );

        log.info("Запрос статуса синтетика: очередь={}, всего команд={}",
                status.queueSize(), status.totalCommands());

        return ResponseEntity.ok(status);
    }

    @PostMapping("/command")
    @WeylandWatchingYou(description = "Отправка команды синтетику", level = AuditLevel.HIGH)
    public ResponseEntity<CommandResponseDTO> sendCommand(@Valid @RequestBody Command command) {

        log.info("Получена команда от {}: {}", command.getAuthor(), command.getDescription());

        if (command.getTime() == null) {
            command.setTime(LocalDateTime.now());
        }

        try {
            String result = commandService.processCommand(command);

            CommandResponseDTO response = new CommandResponseDTO(
                    command.getId(),
                    "ACCEPTED",
                    result,
                    LocalDateTime.now()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            log.error("Ошибка обработки команды: {}", e.getMessage());
            throw e;
        }
    }

    @GetMapping("/command/{commandId}")
    @WeylandWatchingYou(description = "Получение информации о команде")
    public ResponseEntity<Command> getCommand(@PathVariable String commandId) {

        Command command = commandService.getCommand(commandId);

        if (command == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(command);
    }

    @GetMapping("/stats/authors")
    @WeylandWatchingYou(description = "Получение статистики команд по авторам")
    public ResponseEntity<Map<String, Integer>> getAuthorStats() {
        Map<String, Integer> stats = commandService.getCommandStatsByAuthor();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/metrics")
    @WeylandWatchingYou(description = "Получение метрик синтетика")
    public ResponseEntity<SyntheticMetricsResponseDTO> getMetrics() {
        SyntheticMetricsResponseDTO metrics = new SyntheticMetricsResponseDTO(
                commandService.getCurrentQueueSize(),
                commandService.getTotalCommandsCount(),
                commandService.getActiveCommandsCount(),
                commandService.getCommandStatsByAuthor()
        );

        return ResponseEntity.ok(metrics);
    }

    @PostMapping("/test/audit")
    @WeylandWatchingYou(
            description = "Тестовый метод для демонстрации аудита",
            level = AuditLevel.HIGH
    )
    public ResponseEntity<String> testAudit(@RequestBody Map<String, Object> testData) {
        log.info("Тестовый вызов аудита с данными: {}", testData);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String result = String.format("Тестовый аудит выполнен успешно для данных: %s", testData.keySet());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/emergency/shutdown")
    @WeylandWatchingYou(description = "ЭКСТРЕННАЯ ОСТАНОВКА СИНТЕТИКА", level = AuditLevel.CRITICAL)
    public ResponseEntity<String> emergencyShutdown(@RequestParam String reason) {
        log.error("ЭКСТРЕННАЯ ОСТАНОВКА СИНТЕТИКА. Причина: {}", reason);

        // В реальной системе здесь была бы логика остановки
        return ResponseEntity.ok(String.format("Синтетик готов к экстренной остановке. Причина: %s", reason));
    }
}
