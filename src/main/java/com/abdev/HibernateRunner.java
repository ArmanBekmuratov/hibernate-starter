package com.abdev;

import com.abdev.entity.User;
import com.abdev.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class HibernateRunner {

    private static final Logger log =  LoggerFactory.getLogger(HibernateRunner.class);

    public static void main(String[] args) throws SQLException {
        User user = User.builder()
                .username("john12@gmail.com")
                .firstname("John")
                .lastname("Trump")
                .build();

        log.info("User entity is transient state, object: {}", user);

        try (var sessionFactory = HibernateUtil.buildSessionFactory()) {
            Session session1 = sessionFactory.openSession();
            try (session1) {

                Transaction transaction = session1.beginTransaction();
                log.info("Transaction is created: {}", transaction);

                session1.saveOrUpdate(user);
                log.info("User is in persistent state: {}, session {} ", user, session1);

                session1.getTransaction().commit();
            }
            log.warn("Session is in detached state: {}, session {} ", user, session1);
        } catch (Exception e) {
            log.error("Exception: ", e);
            throw e;
        }
    }
}
