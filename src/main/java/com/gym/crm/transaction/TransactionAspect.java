package com.gym.crm.transaction;

import com.gym.crm.config.TransactionManager;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class TransactionAspect {

    private final TransactionManager transactionManager;

    @Around("@annotation(Transaction)")
    public Object manageTransaction(ProceedingJoinPoint joinPoint, Transactional transactional) throws Throwable {
        return transactionManager.performReturningWithinTx(entityManager -> {
            try {
                return joinPoint.proceed();
            } catch (RuntimeException e) {
                throw e;
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });
    }
}
