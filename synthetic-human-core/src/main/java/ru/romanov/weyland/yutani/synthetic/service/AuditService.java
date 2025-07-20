package ru.romanov.weyland.yutani.synthetic.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.romanov.weyland.yutani.synthetic.dto.AuditDTO;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Service
public class AuditService {

    @NonFinal
    @Value("${spring.weyland.audit.mode}")
    String auditMode;

    @NonFinal
    @Value("${spring.weyland.audit.kafka.topic}")
    String topic;

    KafkaTemplate<String, String> kafkaTemplate;
    ObjectMapper objectMapper;

    public void sendAuditRecord(AuditDTO auditDto) {
        try {
            switch (auditMode.toUpperCase()) {
                case "KAFKA" -> sendToKafka(auditDto);
                case "CONSOLE" -> sendToConsole(auditDto);
                default -> {
                    log.warn("Неизвестный режим аудита: '{}'. Используйте консольный вывод.", auditMode);
                    sendToConsole(auditDto);
                }
            }
        } catch (Exception e) {
            log.error("Ошибка при отправке аудита: {}", e.getMessage());
            sendToConsole(auditDto);
        }
    }

    private void sendToKafka(AuditDTO auditDto) {
        if (kafkaTemplate == null) {
            log.warn("Kafka недоступна. Переключение на консольный вывод.");
            sendToConsole(auditDto);
            return;
        }

        try {
            String jsonDto = objectMapper.writeValueAsString(auditDto);
            kafkaTemplate.send(topic, jsonDto);
            log.debug("Аудит отправлен в Kafka Topic: {}", topic);
        } catch (Exception e) {
            log.error("Ошибка отправки в Kafka: {}", e.getMessage());
            sendToConsole(auditDto);
        }
    }

    private void sendToConsole(AuditDTO auditDto) {
        StringBuilder sb = new StringBuilder(
                String.format("""
                               %n----------------------------------------------------------
                               Время: %s
                               Метод: %s
                               Описание: %s
                               Статус: %s
                               Время выполнения: %d мс
                               Уровень: %s
                               """,
                        auditDto.timestamp().toString(),
                        auditDto.methodName(),
                        auditDto.description(),
                        auditDto.status(),
                        auditDto.executionTimeMs(),
                        auditDto.level()
                )
        );

        if (auditDto.parameters() != null) {
            sb.append(String.format("%nПараметры: %s", auditDto.parameters()));
        }

        if (auditDto.result() != null) {
            sb.append(String.format("%nРезультат: %s", auditDto.result()));
        }

        if (auditDto.errorMessage() != null) {
            sb.append(String.format("%nОшибка: %s", auditDto.errorMessage()));
        }

        sb.append("\n----------------------------------------------------------");
        log.info(sb.toString());
    }
}
