package com.gym.crm.config;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;
import java.util.function.Function;

@Component
public class TransactionManager {

    private final SessionFactory sessionFactory;

    public TransactionManager(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void performWithinTx(Consumer<Session> action) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        try {
            action.accept(session);
            tx.commit();
        } catch (Exception e) {
            tx.rollback();

            throw e;
        } finally {
            session.close();
        }
    }

    public <T> T performReturningWithinTx(Function<Session, T> action) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();

        try {
            T result = action.apply(session);
            tx.commit();

            return result;
        } catch (Exception e) {
            tx.rollback();

            throw e;
        } finally {
            session.close();
        }
    }
}
