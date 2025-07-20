package ru.romanov.weyland.yutani.synthetic.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.romanov.weyland.yutani.synthetic.audit.WeylandWatchingYou;
import ru.romanov.weyland.yutani.synthetic.exception.CommandQueueOverflowException;
import ru.romanov.weyland.yutani.synthetic.model.Command;
import ru.romanov.weyland.yutani.synthetic.model.AuditLevel;
import ru.romanov.weyland.yutani.synthetic.model.CommandStatus;
import ru.romanov.weyland.yutani.synthetic.model.Priority;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class CommandService {

    @Value("${spring.weyland.command.queue.max-size}")
    int maxQueueSize;

    @Value("${spring.weyland.command.executor.core-pool-size}")
    int corePoolSize;

    @Value("${spring.weyland.command.executor.max-pool-size}")
    int maxPoolSize;

    @Value("${spring.weyland.command.executor.keep-alive-time}")
    long keepAliveTime;

    ThreadPoolExecutor commandExecutor;
    final Map<String, Command> commandHistory = new ConcurrentHashMap<>();
    final Map<String, AtomicInteger> authorCommandCount = new ConcurrentHashMap<>();

    final AtomicInteger queueSize = new AtomicInteger(0);
    Counter completedCommandsCounter;
    Counter failedCommandsCounter;
    final MeterRegistry meterRegistry;

    public CommandService(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @PostConstruct
    public void init() {
        commandExecutor = new ThreadPoolExecutor(
                corePoolSize, maxPoolSize, keepAliveTime, TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(maxQueueSize)
        );

        completedCommandsCounter = Counter.builder("synthetic.commands.completed")
                .description("Количество выполненных команд")
                .register(meterRegistry);

        failedCommandsCounter = Counter.builder("synthetic.commands.failed")
                .description("Количество неудачных команд")
                .register(meterRegistry);

        Gauge.builder("synthetic.queue.size", this, CommandService::getCurrentQueueSize)
                .description("Текущий размер очереди команд")
                .register(meterRegistry);

        log.info("Synthetic Command Service initialized");
            log.info("Queue size: {}, Thread pool: {}-{}",
                maxQueueSize, corePoolSize, maxPoolSize);
    }

    @PreDestroy
    public void destroy() {
        if (commandExecutor != null) {
            commandExecutor.shutdown();
            try {
                if (!commandExecutor.awaitTermination(30, TimeUnit.SECONDS)) {
                    commandExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                commandExecutor.shutdown();
                Thread.currentThread().interrupt();
            }
        }
        log.info("Synthetic Command Service остановлен");
    }

    @WeylandWatchingYou(description = "Обработка команды синтетика", level = AuditLevel.HIGH)
    public String processCommand(Command command) {
        log.info("Получена команда: {}", command);

        commandHistory.put(command.getId(), command);

        authorCommandCount.computeIfAbsent(command.getAuthor(), _ -> new AtomicInteger(0))
                .incrementAndGet();

        if (command.getPriority() == Priority.CRITICAL) {
            return executeCriticalCommand(command);
        }
        return enqueueCommand(command);
    }

    @WeylandWatchingYou(description = "Выполнение критической команды", level = AuditLevel.CRITICAL)
    private String executeCriticalCommand(Command command) {
        log.warn("КРИТИЧЕСКАЯ КОМАНДА выполняется немедленно: {}", command.getDescription());

        command.setStatus(CommandStatus.EXECUTING);

        try {
            Thread.sleep(100);

            String result = String.format("КРИТИЧЕСКАЯ КОМАНДА ВЫПОЛНЕНА: %s", command.getDescription());
            command.setStatus(CommandStatus.COMPLETED);
            command.setExecutedAt(LocalDateTime.now());
            command.setExecutionResult(result);

            completedCommandsCounter.increment();

            log.info("Критическая команда выполнена: {}", command.getId());
            return result;

        } catch (Exception e) {
            command.setStatus(CommandStatus.FAILED);
            command.setExecutionResult(String.format("ОШИБКА: %s", e.getMessage()));
            failedCommandsCounter.increment();

            log.error("Ошибка выполнения критической команды: {}", e.getMessage());
            throw new RuntimeException("Ошибка выполнения критической команды", e);
        }
    }

    @WeylandWatchingYou(description = "Добавление команды в очередь")
    private String enqueueCommand(Command command) {
        try {
            commandExecutor.submit(() -> executeCommand(command));
            queueSize.incrementAndGet();

            log.info("Команда добавлена в очередь: {}", command.getId());
            return String.format("Команда добавлена в очередь выполнения: %s", command.getId());

        } catch (Exception e) {
            if (e.getCause() instanceof RejectedExecutionException) {
                throw new CommandQueueOverflowException("Очередь команд выполнена");
            }
            throw new RuntimeException("Ошибка при добавлении команды в очередь", e);
        }
    }

    @WeylandWatchingYou(description = "Выполнение команды из очереди")
    private void executeCommand(Command command) {
        try {
            log.info("Выполняется команда: {}", command.getDescription());

            command.setStatus(CommandStatus.EXECUTING);

            Thread.sleep(1000 + (long)(Math.random() * 2000));

            String result = String.format("КОМАНДА ВЫПОЛНЕНА: %s (Автор: %s)",
                    command.getDescription(), command.getAuthor());

            command.setStatus(CommandStatus.COMPLETED);
            command.setExecutedAt(LocalDateTime.now());
            command.setExecutionResult(result);

            completedCommandsCounter.increment();

            log.info("Команда выполнена: {}", command.getId());

        } catch (Exception e) {
            command.setStatus(CommandStatus.FAILED);
            command.setExecutionResult(String.format("ОШИБКА: %s", e.getMessage()));
            failedCommandsCounter.increment();

            log.error("Ошибка выполнения команды {}: {}", command.getId(), e.getMessage());

        } finally {
            queueSize.decrementAndGet();
        }
    }

    @WeylandWatchingYou(description = "Получение информации о команде")
    public Command getCommand(String commandId) {
        return commandHistory.get(commandId);
    }

    @WeylandWatchingYou(description = "Получение статистики команд по авторам")
    public Map<String, Integer> getCommandStatsByAuthor() {
        Map<String, Integer> stats = new ConcurrentHashMap<>();
        authorCommandCount.forEach((author, count) -> stats.put(author, count.get()));
        return stats;
    }

    @WeylandWatchingYou(description = "Получение списка активных команд")
    public long getActiveCommandsCount() {
        return commandHistory.values().stream()
                .filter(cmd -> cmd.getStatus() == CommandStatus.EXECUTING ||
                        cmd.getStatus() == CommandStatus.PENDING)
                .count();
    }

    public int getCurrentQueueSize() {
        return queueSize.get();
    }

    public int getTotalCommandsCount() {
        return commandHistory.size();
    }
}
