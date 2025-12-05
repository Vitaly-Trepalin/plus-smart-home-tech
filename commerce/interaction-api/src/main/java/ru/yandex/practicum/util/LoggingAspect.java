package ru.yandex.practicum.util;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class LoggingAspect {
    @Around("@annotation(ru.yandex.practicum.util.Loggable)")
    public Object logExecutorTime(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("Запуск метода {}", joinPoint.getSignature());
        Object[] args = joinPoint.getArgs();
        log.info("Параметры запроса {}", args);

        Object result = joinPoint.proceed();
        log.info("Завершение выполнения метода {} - Ответ: {}", joinPoint.getSignature(), result);
        return result;
    }
}
