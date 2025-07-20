package ru.romanov.weyland.yutani.bishop.rest;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.romanov.weyland.yutani.bishop.service.BishopPrototypeService;
import ru.romanov.weyland.yutani.synthetic.audit.WeylandWatchingYou;
import ru.romanov.weyland.yutani.synthetic.exception.CommandQueueOverflowException;
import ru.romanov.weyland.yutani.synthetic.exception.CommandValidationException;
import ru.romanov.weyland.yutani.synthetic.service.CommandService;
import ru.romanov.weyland.yutani.synthetic.model.AuditLevel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RestController
@RequestMapping("/api/v1/bishop")
public class BishopRestController {

    BishopPrototypeService bishopService;
    CommandService commandService;

    @GetMapping("/info")
    @WeylandWatchingYou(description = "Получение информации о Bishop", level = AuditLevel.STANDARD)
    public ResponseEntity<Map<String, Object>> getBishopInfo() {
        Map<String, Object> info = Map.of(
                "name", "BISHOP",
                "model", "Synthetic Human Model 341-B",
                "designation", "Science Officer",
                "manufacturer", "Weyland-Yutani Corporation",
                "quote", "I prefer the term 'Artificial Person' myself",
                "primaryFunctions", List.of(
                        "Scientific Analysis",
                        "Biological Research",
                        "Mission Support",
                        "Emergency Response"
                ),
                "activatedAt", LocalDateTime.now()
        );

        return ResponseEntity.ok(info);
    }

    @PostMapping("/operations/power-analysis")
    @WeylandWatchingYou(description = "Анализ энергоблока корабля", level = AuditLevel.HIGH)
    public ResponseEntity<Map<String, Object>> analyzePowerCore(@RequestParam String shipName) {

        log.info("Bishop: Начинаю анализ энергоблока корабля {}", shipName);

        String result = bishopService.checkPowerCore(shipName);

        Map<String, Object> response = Map.of(
                "operation", "POWER_ANALYSIS",
                "shipName", shipName,
                "result", result,
                "timestamp", LocalDateTime.now(),
                "bishop", "Analysis complete"
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/operations/bio-scan")
    @WeylandWatchingYou(description = "Биологическое сканирование", level = AuditLevel.CRITICAL)
    public ResponseEntity<Map<String, Object>> performBioScan(@RequestParam(defaultValue = "5") int radius) {

        log.info("Bishop: Выполняю биологическое сканирование, радиус {} км", radius);

        String result = bishopService.scanForLifeForms(radius);

        Map<String, Object> response = Map.of(
                "operation", "BIOLOGICAL_SCAN",
                "scanRadius", radius,
                "result", result,
                "timestamp", LocalDateTime.now(),
                "bishop", "Scan complete"
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/operations/navigation")
    @WeylandWatchingYou(description = "Навигационные расчеты", level = AuditLevel.HIGH)
    public ResponseEntity<Map<String, Object>> calculateNavigation(
            @RequestParam String destination,
            @RequestParam double distance) {

        log.info("Bishop: Рассчитываю маршрут к {} ({} св. лет)", destination, distance);

        String result = bishopService.calculateNavigation(destination, distance);

        Map<String, Object> response = Map.of(
                "operation", "NAVIGATION_CALCULATION",
                "destination", destination,
                "distance", distance,
                "result", result,
                "timestamp", LocalDateTime.now(),
                "bishop", "Navigation complete"
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/test/audit")
    @WeylandWatchingYou(description = "Тестирование системы аудита Bishop", level = AuditLevel.HIGH)
    public ResponseEntity<Map<String, Object>> testAuditSystem(@RequestBody Map<String, Object> testData) {

        log.info("Bishop: Тестирую систему аудита с данными: {}", testData);

        String auditResult = bishopService.performAuditTest(testData);

        Map<String, Object> response = Map.of(
                "operation", "AUDIT_TEST",
                "testData", testData,
                "auditResult", auditResult,
                "timestamp", LocalDateTime.now(),
                "bishop", "Audit system functioning correctly"
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/metrics/integration")
    @WeylandWatchingYou(description = "Демонстрация интеграции метрик со стартером")
    public ResponseEntity<Map<String, Object>> getIntegratedMetrics() {

        log.info("Bishop: Получение метрик через интеграцию со стартером");

        Map<String, Object> metrics = Map.of(
                "starterMetrics", Map.of(
                        "queueSize", commandService.getCurrentQueueSize(),
                        "totalCommands", commandService.getTotalCommandsCount(),
                        "activeCommands", commandService.getActiveCommandsCount(),
                        "authorStats", commandService.getCommandStatsByAuthor()
                ),
                "integration", "SUCCESS",
                "timestamp", LocalDateTime.now(),
                "note", "Все метрики предоставляются Synthetic Human Core Starter"
        );

        return ResponseEntity.ok(metrics);
    }

    @PostMapping("/test/validation-error")
    @WeylandWatchingYou(description = "Тест ошибки валидации", level = AuditLevel.STANDARD)
    public ResponseEntity<String> testValidationError(@RequestParam String testType) {

        log.info("Bishop: Тестирую ошибку валидации типа {}", testType);

        switch (testType.toLowerCase()) {
            case "command" -> throw new CommandValidationException("Тест ошибки валидации команды от Bishop");
            case "parameter" -> throw new CommandValidationException(
                    String.format("Неверный параметр: %s", testType));
            default -> throw new CommandValidationException(
                    String.format("Неизвестный тип тестирования: %s", testType));
        }
    }

    @PostMapping("/test/queue-overflow")
    @WeylandWatchingYou(description = "Тест переполнения очереди", level = AuditLevel.CRITICAL)
    public ResponseEntity<String> testQueueOverflow() {

        log.error("Bishop: Симулирую переполнение очереди команд");

        throw new CommandQueueOverflowException("Очередь команд Bishop переполнена - тестовая ошибка");
    }

    @PostMapping("/test/general-error")
    @WeylandWatchingYou(description = "Тест общей ошибки системы", level = AuditLevel.HIGH)
    public ResponseEntity<String> testGeneralError() {

        log.error("Bishop: Симулирую общую ошибку системы");

        throw new RuntimeException("Критическая ошибка системы Bishop - тестовая ошибка");
    }

    @PostMapping("/test/comprehensive")
    @WeylandWatchingYou(description = "Комплексное тестирование всех модулей", level = AuditLevel.CRITICAL)
    public ResponseEntity<Map<String, Object>> comprehensiveTest(@RequestParam(defaultValue = "10") int iterations) {

        log.info("Bishop: Запуск комплексного тестирования всех модулей стартера");

        Map<String, Object> testResults = bishopService.runComprehensiveTest(iterations);

        Map<String, Object> response = Map.of(
                "operation", "COMPREHENSIVE_TEST",
                "iterations", iterations,
                "testResults", testResults,
                "timestamp", LocalDateTime.now(),
                "bishop", "All modules tested successfully"
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/integration/command-service")
    @WeylandWatchingYou(description = "Демонстрация интеграции с CommandService", level = AuditLevel.HIGH)
    public ResponseEntity<Map<String, Object>> demonstrateCommandServiceIntegration() {

        log.info("Bishop: Демонстрирую интеграцию с CommandService");

        Map<String, Object> integration;

        if (commandService != null) {
            integration = Map.of(
                    "commandServiceAvailable", true,
                    "currentQueueSize", commandService.getCurrentQueueSize(),
                    "totalCommands", commandService.getTotalCommandsCount(),
                    "activeCommands", commandService.getActiveCommandsCount(),
                    "authorStats", commandService.getCommandStatsByAuthor(),
                    "integration", "SUCCESS"
            );
        } else {
            integration = Map.of(
                    "commandServiceAvailable", false,
                    "currentQueueSize", 0,
                    "totalCommands", 0,
                    "activeCommands", 0L,
                    "authorStats", Map.of(),
                    "integration", "FAILED - CommandService not available"
            );
        }

        return ResponseEntity.ok(integration);
    }

    @PostMapping("/emergency/shutdown")
    @WeylandWatchingYou(description = "ЭКСТРЕННАЯ ОСТАНОВКА BISHOP", level = AuditLevel.CRITICAL)
    public ResponseEntity<Map<String, Object>> emergencyShutdown(@RequestParam String reason) {

        log.error("ЭКСТРЕННАЯ ОСТАНОВКА BISHOP! Причина: {}", reason);

        Map<String, Object> response = Map.of(
                "operation", "EMERGENCY_SHUTDOWN",
                "reason", reason,
                "status", "INITIATED",
                "timestamp", LocalDateTime.now(),
                "bishop", "Emergency protocols activated"
        );

        return ResponseEntity.ok(response);
    }
}
