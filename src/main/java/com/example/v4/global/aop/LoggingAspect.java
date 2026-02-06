package com.example.v4.global.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Controller, Service 계층의 메서드 실행을 로깅하는 AOP Aspect.
 *
 * <p>
 * 적용 대상:
 * <ul>
 * <li>com.example.v4..controller 패키지의 모든 메서드</li>
 * <li>com.example.v4..service 패키지의 모든 메서드</li>
 * <li>com.example.v4..repository 패키지의 모든 메서드</li>
 * </ul>
 *
 * <p>
 * 로깅 내용: 메서드명, 인자, 반환값, 실행 시간, 예외
 */
@Aspect
@Component
public class LoggingAspect {

    @Around("execution(* com.example.v4..controller..*(..)) || execution(* com.example.v4..service..*(..)) || execution(* com.example.v4..repository..*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Logger log = LoggerFactory.getLogger(joinPoint.getTarget().getClass());
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = signature.getName();
        Object[] args = joinPoint.getArgs();

        log.info("[AOP] {}#{} 호출 - args: {}", className, methodName, maskSensitiveArgs(args));

        long start = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long elapsed = System.currentTimeMillis() - start;
            log.info("[AOP] {}#{} 완료 ({}ms) - return: {}", className, methodName, elapsed, maskReturnValue(result));
            return result;
        } catch (Throwable ex) {
            long elapsed = System.currentTimeMillis() - start;
            log.error("[AOP] {}#{} 예외 ({}ms) - {}", className, methodName, elapsed, ex.getMessage(), ex);
            throw ex;
        }
    }

    private Object[] maskSensitiveArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return args;
        }
        Object[] masked = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg != null && isSensitiveType(arg.getClass())) {
                masked[i] = "***";
            } else {
                masked[i] = arg;
            }
        }
        return masked;
    }

    private boolean isSensitiveType(Class<?> clazz) {
        String name = clazz.getName();
        return name.contains("SessionUser") || name.contains("HttpSession")
                || name.contains("Password") || name.contains("password");
    }

    private Object maskReturnValue(Object result) {
        if (result == null) {
            return null;
        }
        if (isSensitiveType(result.getClass())) {
            return "***";
        }
        return result;
    }
}
