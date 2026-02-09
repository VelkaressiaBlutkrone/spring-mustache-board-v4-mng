package com.example.v4.aop;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import com.example.v4.global.annotation.ValidateOnError;
import com.example.v4.global.exception.ValidationException;

import jakarta.validation.Valid;

@Aspect
@Component
public class ValidationHandler {

    @Before("@annotation(com.example.v4.global.annotation.ValidateOnError)")
    public void validationCheck(JoinPoint jp) {
        MethodSignature signature = (MethodSignature) jp.getSignature();
        Method method = signature.getMethod();
        Object[] args = jp.getArgs();

        ValidateOnError annotation = method.getAnnotation(ValidateOnError.class);
        if (annotation == null) {
            return;
        }

        BindingResult br = null;
        Object dto = null;

        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            if (args[i] instanceof BindingResult bindingResult) {
                br = bindingResult;
            }
            if (parameters[i].isAnnotationPresent(Valid.class)) {
                dto = args[i];
            }
        }

        if (br == null || !br.hasErrors()) {
            return;
        }

        String pathVariableValue = null;
        String pathVarName = annotation.pathVariable();
        if (!pathVarName.isEmpty()) {
            for (int i = 0; i < parameters.length; i++) {
                if (pathVarName.equals(parameters[i].getName())) {
                    Object val = args[i];
                    pathVariableValue = val != null ? val.toString() : null;
                    break;
                }
            }
        }

        throw new ValidationException(br, dto, annotation.viewName(), pathVariableValue);
    }
}
