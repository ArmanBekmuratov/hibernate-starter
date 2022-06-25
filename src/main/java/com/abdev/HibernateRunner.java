package com.abdev;

import com.abdev.entity.User;
import com.abdev.util.HibernateUtil;
import org.hibernate.Session;

import java.sql.SQLException;

public class HibernateRunner {
    public static void main(String[] args) throws SQLException {
        User user = User.builder()
                .username("john12@gmail.com")
                .firstname("John")
                .lastname("Trump")
                .build();

        try (var sessionFactory = HibernateUtil.buildSessionFactory()) {
            try (Session session1 = sessionFactory.openSession()) {
                session1.beginTransaction();

                session1.saveOrUpdate(user);

                session1.getTransaction().commit();
            }

            try (Session session2 = sessionFactory.openSession()) {
                session2.beginTransaction();



                session2.getTransaction().commit();
            }

        }
    }
}
