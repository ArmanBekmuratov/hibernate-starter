package com.abdev;

import com.abdev.entity.*;
import com.abdev.util.HibernateTestUtil;
import com.abdev.util.HibernateUtil;
import lombok.Cleanup;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.jpa.QueryHints;
import org.junit.jupiter.api.Test;

import javax.persistence.Column;
import javax.persistence.FlushModeType;
import javax.persistence.Table;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

class HibernateRunnerTest {

    @Test
    void checkHql() {
        try (SessionFactory sessionFactory = HibernateUtil.buildSessionFactory();
             Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            // HQL/JPQL
            String firstName = "Ivan";
            String company = "google";
            var result = session.createQuery(
                            "select  u from User u join u.company c " +
                                    "where u.personalInfo.firstname = :firstname and c.name = :company ", User.class)
                    .setParameter("firstname", firstName)
                    .setParameter("company", company)
                    .setFlushMode(FlushModeType.COMMIT)
                    .setHint(QueryHints.HINT_FETCH_SIZE, "value")
                    .list();

            var countRows = session.createQuery("update User u set u.role = 'ADMIN'")
                    .executeUpdate();

            session.getTransaction().commit();
        }
    }

    @Test
    void checkH2() {
        try (SessionFactory sessionFactory = HibernateTestUtil.buildSessionFactory();
             Session session = sessionFactory.openSession()) {
            session.beginTransaction();

           Company google = Company.builder()
                   .name("Google")
                   .build();

           session.save(google);

            session.getTransaction().commit();

        }
    }

    @Test
    void localeInfo() {
        try (SessionFactory sessionFactory = HibernateUtil.buildSessionFactory();
             Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            Company company = session.get(Company.class, 7);
//            company.getLocales().add(LocaleInfo.of("us", "Description in english"));
//            company.getLocales().add(LocaleInfo.of("ru", "???????????????? ???? ??????????????"));
            company.getUsers().forEach((k,v) -> System.out.println(v));

            session.getTransaction().commit();

        }
    }

    @Test
    void checkManyToMany() {
        try (SessionFactory sessionFactory = HibernateUtil.buildSessionFactory();
             Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            User user = session.get(User.class, 3L);
            Chat chat = session.get(Chat.class, 1L);

            UsersChat usersChat = UsersChat.builder()
                    .build();
            usersChat.setUser(user);
            usersChat.setChat(chat);

            session.save(usersChat);

//            Chat chat = Chat.builder()
//                    .name("usa")
//                    .build();
//
//
//            session.save(chat);

            session.getTransaction().commit();
        }
    }

    @Test
    void checkOneToOne() {
        try (SessionFactory sessionFactory = HibernateUtil.buildSessionFactory();
             Session session = sessionFactory.openSession()) {
            session.beginTransaction();

//            User user = User.builder()
//                    .username("john12@gmail.com")
//                    .build();

            Profile profile = Profile.builder()
                    .language("en")
                    .street("1st")
                    .build();

//            session.save(user);
//            profile.setUser(user);
//            session.save(profile);

            session.getTransaction().commit();
        }

    }

    @Test
    void checkOrphanRemoval() {
        try (SessionFactory sessionFactory = HibernateUtil.buildSessionFactory();
             Session session = sessionFactory.openSession()) {
            session.beginTransaction();

          // Company company = session.getReference(Company.class, 4);
          // company.getUsers().removeIf(user -> user.getId().equals(1L));

            session.getTransaction().commit();
        }

    }

    @Test
    void checkLazyInitialization() {

        Company company = null;

        try (SessionFactory sessionFactory = HibernateUtil.buildSessionFactory();
             Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            company = session.getReference(Company.class, 1);

            session.getTransaction().commit();
        }
        //Set<User> users = company.getUsers();
        //System.out.println(users.size());
    }

    @Test
    void deleteCompany() {
        @Cleanup SessionFactory sessionFactory = HibernateUtil.buildSessionFactory();
        @Cleanup Session session = sessionFactory.openSession();
        session.beginTransaction();

        Company company = session.get(Company.class, 6);
        session.delete(company);

        session.getTransaction().commit();
    }

    @Test
    void addUserToCompany() {
        @Cleanup SessionFactory sessionFactory = HibernateUtil.buildSessionFactory();
        @Cleanup Session session = sessionFactory.openSession();
        session.beginTransaction();

        Company company = Company.builder()
                .name("Epam")
                .build();

//        User user = User.builder()
//                .username("ivan12@gmail.com")
//                .build();
//        user.setCompany(company);
//        company.getUsers().add(user);

//        company.addUser(user);

        session.save(company);

        session.getTransaction().commit();

    }

    @Test
    void oneToMany() {
        @Cleanup SessionFactory sessionFactory = HibernateUtil.buildSessionFactory();
        @Cleanup Session session = sessionFactory.openSession();
        session.beginTransaction();

        Company company = session.get(Company.class, 1);
        System.out.println();

        session.getTransaction().commit();
    }

    @Test
    void checkGetReflectionApi() throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        PreparedStatement preparedStatement = null;
        var resultSet = preparedStatement.executeQuery();
        resultSet.getString("username");
        resultSet.getString("firstname");
        resultSet.getString("lastname");

        Class<User> clazz = User.class;
        var constructor = clazz.getConstructor();
        User user = constructor.newInstance();
        Field usernameField = clazz.getDeclaredField("username");
        usernameField.setAccessible(true);
        usernameField.set(user, resultSet.getString("username"));
    }

    @Test
    void checkReflectionApi() throws SQLException, IllegalAccessException {
        User user = null;

        String sql = """
                insert
                into
                %s
                (%s)
                values
                (%s)
                """;

        var tableName = Optional.ofNullable(user.getClass().getAnnotation(Table.class))
                .map(tableAnnotation -> tableAnnotation.schema() + "." + tableAnnotation.name())
                .orElse(user.getClass().getName());

        var declaredFields = user.getClass().getDeclaredFields();

        var columnNames = Arrays.stream(declaredFields)
                .map(field -> Optional.ofNullable(field.getAnnotation(Column.class))
                        .map(Column::name)
                        .orElse(field.getName()))
                .collect(Collectors.joining(", "));

        var columnValues = Arrays.stream(declaredFields)
                .map(field -> "?")
                .collect(Collectors.joining(", "));

        System.out.println(sql.formatted(tableName, columnNames, columnValues));

        Connection connection = null;
        var preparedStatement = connection.prepareStatement(sql.formatted(tableName, columnNames, columnValues));

        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
            preparedStatement.setObject(1, declaredField.get(user));
        }


    }

}