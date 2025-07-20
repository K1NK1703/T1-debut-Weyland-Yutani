package ru.romanov.weyland.yutani.bishop.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.romanov.weyland.yutani.synthetic.audit.WeylandWatchingYou;
import ru.romanov.weyland.yutani.synthetic.model.Command;
import ru.romanov.weyland.yutani.synthetic.service.CommandService;
import ru.romanov.weyland.yutani.synthetic.model.AuditLevel;
import ru.romanov.weyland.yutani.synthetic.model.Priority;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Service
public class BishopPrototypeService {

    Random random = new Random();

    CommandService commandService;

    @WeylandWatchingYou(description = "Bishop: Диагностика энергоблока", level = AuditLevel.HIGH)
    public String checkPowerCore(String shipName) {
        log.info("Bishop начинает диагностику энергоблока корабля: {}", shipName);

        Command command = new Command(
                String.format("Диагностика энергоблока корабля %s", shipName),
                Priority.COMMON,
                "Bishop",
                LocalDateTime.now()
        );
        commandService.processCommand(command);

        simulateWork(2000, 4000);

        int powerLevel = 85 + random.nextInt(15);
        String status = powerLevel > 90 ? "ОПТИМАЛЬНЫЙ" : "СТАБИЛЬНЫЙ";

        String result = String.format("Энергоблок корабля %s: %s (%d%%)", shipName, status, powerLevel);
        log.info("Bishop завершил диагностику: {}", result);

        return result;
    }

    @WeylandWatchingYou(description = "Bishop: Биологическое сканирование", level = AuditLevel.CRITICAL)
    public String scanForLifeForms(int radius) {
        log.info("Bishop начинает биологическое сканирование в радиусе {} км", radius);

        Command command = new Command(
                String.format("Биологическое сканирование, радиус %s км", radius),
                Priority.CRITICAL,
                "Bishop",
                LocalDateTime.now()
        );
        commandService.processCommand(command);

        simulateWork(3000, 6000);

        List<String> possibleLifeForms = List.of(
                "Человеческие жизненные признаки",
                "Неизвестная биомасса",
                "Ксеноморфная активность",
                "Отсутствие жизненных форм",
                "Синтетические сигналы"
        );

        String detected = possibleLifeForms.get(random.nextInt(possibleLifeForms.size()));

        if (detected.contains("Ксеноморфная")) {
            log.warn("ВНИМАНИЕ! Bishop обнаружил потенциальную угрозу: {}", detected);
        }

        String result = String.format("Сканирование завершено. Обнаружено: %s", detected);
        log.info("Bishop: {}", result);

        return result;
    }

    @WeylandWatchingYou(description = "Bishop: Навигационные расчеты", level = AuditLevel.HIGH)
    public String calculateNavigation(String destination, double distance) {
        log.info("Bishop рассчитывает маршрут к: {} (расстояние: {} световых лет)", destination, distance);

        Command command = new Command(
                String.format("Навигационный расчет к %s (%.1f св. лет)", destination, distance),
                Priority.COMMON,
                "Bishop",
                LocalDateTime.now()
        );
        commandService.processCommand(command);

        simulateWork(2000, 5000);

        double fuelRequired = distance * 1.2 + random.nextDouble() * 0.3;
        int travelTime = (int)(distance * 365 + random.nextInt(100));

        String result = String.format(
                "Маршрут к %s рассчитан. Топливо: %.2f единиц, Время в пути: %d дней",
                destination, fuelRequired, travelTime
        );

        log.info("Bishop: {}", result);
        return result;
    }

    @WeylandWatchingYou(description = "Bishop: Тестирование системы аудита Weyland-Yutani", level = AuditLevel.HIGH)
    public String performAuditTest(Map<String, Object> testData) {
        log.info("Bishop выполняет тест системы аудита");

        simulateWork(1000, 2000);

        performLowLevelOperation();
        performStandardOperation();
        performHighLevelOperation();
        performCriticalOperation();

        String result = "Система аудита функционирует корректно. Все уровни протестированы.";
        log.info("Bishop: {}", result);

        return result;
    }

    @WeylandWatchingYou(description = "Операция низкого уровня", level = AuditLevel.LOW)
    private void performLowLevelOperation() {
        log.debug("Выполняется операция низкого уровня аудита");
    }

    @WeylandWatchingYou(description = "Стандартная операция", level = AuditLevel.STANDARD)
    private void performStandardOperation() {
        log.info("Выполняется стандартная операция аудита");
    }

    @WeylandWatchingYou(description = "Операция высокого уровня", level = AuditLevel.HIGH)
    private void performHighLevelOperation() {
        log.info("Выполняется операция высокого уровня аудита");
    }

    @WeylandWatchingYou(description = "Критическая операция", level = AuditLevel.CRITICAL)
    private void performCriticalOperation() {
        log.warn("Выполняется критическая операция аудита");
    }

    @WeylandWatchingYou(description = "Bishop: Комплексное тестирование модулей", level = AuditLevel.CRITICAL)
    public Map<String, Object> runComprehensiveTest(int iterations) {
        log.info("Bishop запускает комплексное тестирование {} итераций", iterations);

        long startTime = System.currentTimeMillis();
        int successfulCommands = 0;
        int auditedOperations = 0;

        for (int i = 0; i < iterations; i++) {
            try {
                Command testCommand = new Command(
                        String.format("Тестовая команда #%d", i + 1),
                        i % 3 == 0 ? Priority.CRITICAL : Priority.COMMON,
                        "Bishop Test",
                        LocalDateTime.now()
                );
                commandService.processCommand(testCommand);
                successfulCommands++;

                performTestOperation(i);
                auditedOperations++;

                Thread.sleep(100);

            } catch (Exception e) {
                log.error("Ошибка в итерации {}: {}", i, e.getMessage());
            }
        }

        long totalTime = System.currentTimeMillis() - startTime;

        Map<String, Object> results = Map.of(
                "iterations", iterations,
                "successfulCommands", successfulCommands,
                "auditedOperations", auditedOperations,
                "totalTimeMs", totalTime,
                "averageTimeMs", totalTime / iterations,
                "starterMetrics", Map.of(
                        "queueSize", commandService.getCurrentQueueSize(),
                        "totalCommands", commandService.getTotalCommandsCount(),
                        "activeCommands", commandService.getActiveCommandsCount(),
                        "authorStats", commandService.getCommandStatsByAuthor()
                )
        );

        log.info("Bishop завершил комплексное тестирование: {}", results);
        return results;
    }

    @WeylandWatchingYou(description = "Тестовая операция", level = AuditLevel.STANDARD)
    private void performTestOperation(int iteration) {
        log.debug("Выполняется тестовая операция #{}", iteration);
    }

    private void simulateWork(int minMs, int maxMs) {
        try {
            int workTime = minMs + random.nextInt(maxMs - minMs);
            Thread.sleep(workTime);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Работа Bishop прервана");
        }
    }
}
