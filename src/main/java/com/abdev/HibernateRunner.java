package com.abdev;

import com.abdev.entity.Payment;
import com.abdev.util.HibernateUtil;
import com.abdev.util.TestDataImporter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;

import javax.persistence.LockModeType;
import javax.transaction.Transactional;
import java.sql.SQLException;
import java.util.Map;

@Slf4j
public class HibernateRunner {

    @Transactional
    public static void main(String[] args) throws SQLException {
        try (var sessionFactory = HibernateUtil.buildSessionFactory();
             Session session = sessionFactory.openSession()) {
            TestDataImporter.importData(sessionFactory);
            session.beginTransaction();

            var payment = session.find(Payment.class, 1L, LockModeType.OPTIMISTIC);

            session.getTransaction().commit();
        }
    }
    
}
