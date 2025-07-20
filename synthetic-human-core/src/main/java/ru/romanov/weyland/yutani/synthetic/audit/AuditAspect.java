package ru.romanov.weyland.yutani.synthetic.audit;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import ru.romanov.weyland.yutani.synthetic.dto.AuditDTO;
import ru.romanov.weyland.yutani.synthetic.service.AuditService;

import java.time.LocalDateTime;
import java.util.Arrays;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Aspect
@Component
public class AuditAspect {

    AuditService auditService;
    ObjectMapper objectMapper;

    @Around("@annotation(weylandWatchingYou)")
    public Object auditMethod(ProceedingJoinPoint joinPoint, WeylandWatchingYou weylandWatchingYou) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        String parameters = null;
        if (weylandWatchingYou.includeParameters()) {
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0) {
                parameters = objectMapper.writeValueAsString(Arrays.asList(args));
            }
        }

        long startTime = System.currentTimeMillis();
        Object result;
        String status = null;
        String resultStr = null;
        String errorMessage = null;

        try {
            result = joinPoint.proceed();
            status = "SUCCESS";

            if (weylandWatchingYou.includeResult() && result != null) {
                resultStr = objectMapper.writeValueAsString(result);
            }

            return result;

        } catch (Exception e) {
            status = "FAILED";
            errorMessage = e.getMessage();
            throw e;

        } finally {
            long executionTime = System.currentTimeMillis() - startTime;

            AuditDTO auditDto = AuditDTO.builder()
                    .timestamp(LocalDateTime.now())
                    .methodName(String.format("%s.%s", className, methodName))
                    .description(weylandWatchingYou.description().isEmpty() ?
                            String.format("Выполнение метода %s", methodName) : weylandWatchingYou.description())
                    .parameters(parameters)
                    .result(resultStr)
                    .status(status)
                    .errorMessage(errorMessage)
                    .executionTimeMs(executionTime)
                    .level(weylandWatchingYou.level())
                    .build();

            auditService.sendAuditRecord(auditDto);

            log.info("SYNTHETIC AUDIT: {} - {} в {} мс", methodName, auditDto.status(), executionTime);
        }
    }
}
