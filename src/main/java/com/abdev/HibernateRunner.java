package com.abdev;

import com.abdev.entity.BirthDay;
import com.abdev.entity.Role;
import com.abdev.entity.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.sql.SQLException;
import java.time.LocalDate;

public class HibernateRunner {
    public static void main(String[] args) throws SQLException {

        var configuration = new Configuration();
//      configuration.addAnnotatedClass(User.class);
        configuration.configure();

        try (var sessionFactory = configuration.buildSessionFactory();
             var session = sessionFactory.openSession()) {

            session.beginTransaction();
           User user = User.builder()
                   .username("John12")
                   .firstname("John")
                   .lastname("Biden")
                   .birthdate(new BirthDay(LocalDate.of(1996,5,21)))
                   .role(Role.ADMIN)
                   .build();

           session.persist(user);

           session.getTransaction().commit();
        }
    }
}
