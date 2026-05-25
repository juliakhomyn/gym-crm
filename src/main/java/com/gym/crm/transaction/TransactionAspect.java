package com.gym.crm.transaction;

import com.gym.crm.config.TransactionManager;
import com.gym.crm.exception.EntityNotFoundException;
import com.gym.crm.exception.ValidationFailedException;
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

    @Around("@annotation(com.gym.crm.transaction.Transactional)")
    public Object manageTransaction(ProceedingJoinPoint joinPoint) {
        return transactionManager.performReturningWithinTx(entityManager -> {
            try {
                return joinPoint.proceed();
            } catch (EntityNotFoundException | ValidationFailedException e) {
                throw e;
            } catch (Throwable e) {
                throw new IllegalStateException("Transaction failed due to error", e);
            }
        });
    }
}
