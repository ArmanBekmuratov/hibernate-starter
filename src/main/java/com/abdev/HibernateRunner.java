package com.abdev;

import com.abdev.entity.BirthDay;
import com.abdev.entity.Company;
import com.abdev.entity.PersonalInfo;
import com.abdev.entity.User;
import com.abdev.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.SQLException;
import java.time.LocalDate;

public class HibernateRunner {

    private static final Logger log =  LoggerFactory.getLogger(HibernateRunner.class);

    public static void main(String[] args) throws SQLException {
        Company company = Company.builder()
                .name("Google")
                .build();
        User user = null;
//                .username("Joe@gmail.com")
//                .personalInfo(PersonalInfo.builder()
//                        .firstname("Joe")
//                        .lastname("Biden")
//                        .birthdate(new BirthDay(LocalDate.of(2000, 5, 23)))
//                        .build())
//                .company(company)
//                .build();

        try (var sessionFactory = HibernateUtil.buildSessionFactory()) {
            Session session = sessionFactory.openSession();
            try (session) {

                Transaction transaction = session.beginTransaction();

                User user1 = session.get(User.class, 1L);
                Company company1 = user1.getCompany();
                String name = company1.getName();
//                session.save(company);
//                session.save(user);

                session.getTransaction().commit();
            }
        }
    }
}
