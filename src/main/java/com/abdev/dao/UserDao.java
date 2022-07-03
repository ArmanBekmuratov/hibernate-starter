package com.abdev.dao;

import com.abdev.entity.Payment;
import com.abdev.entity.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.Session;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserDao {

    private static final UserDao INSTANCE = new UserDao();

    /**
     * Returns all employees
     */
    public List<User> findAll(Session session) {
        return session.createQuery("SELECT u from User u", User.class)
                .list();
    }

    /**
     *Returns all employees with the given name
     */
    public List<User> findAllByFirstName(Session session, String firstName ) {

        return session.createQuery("select u from User u " +
                "where u.personalInfo.firstname = :firstname", User.class)
                .setParameter("firstname", firstName)
                .list();
    }

    /**
     Returns the first {limit} employees sorted by date of birth (in ascending order)
     */
    public List<User> findLimitedUsersOrderedByBirthday(Session session, Integer limit) {
        return session.createQuery("select u from User  u " +
                "order by u.personalInfo.birthdate", User.class)
                .setMaxResults(limit)
                .list();
    }

    /**
     * Returns all employees of the company with the specified company name
     */
    public List<User> findAllByCompanyName(Session session, String companyName) {
        return session.createQuery("select u from Company c " +
                        "join c.users u " +
                        "where c.name = :companyName", User.class)
                .setParameter("companyName", companyName)
                .list();
    }

    /**
     Returns all payments received by employees of the company with the specified name,
     * sorted by employee name and then by payout amount
     */
    public List<Payment> findAllPaymentsByCompanyName(Session session, String companyName) {
        return session.createQuery("select u from Payment p " +
                        "join p.receiver u " +
                        "join u.company c " +
                        "where  c.name = :companyName " +
                        "order by u.personalInfo.firstname, p.amount", Payment.class)
                .setParameter("companyName", companyName)
                .list();
    }

    /**
     * Returns the average salary of an employee with the given first and last name
     */
    public Double findAveragePaymentAmountByFirstAndLastNames(Session session, String firstName, String lastName) {
        return session.createQuery("select avg(p.amount) from Payment p " +
                        "join p.receiver u " +
                        "where u.personalInfo.firstname = :firstName " +
                        "   and u.personalInfo.lastname = :lastName", Double.class)
                .setParameter("firstName", firstName)
                .setParameter("lastName", lastName)
                .uniqueResult();
    }

    /**
     * Returns for each company: name, average salary of all its employees. Companies are ordered by name.
     */
    public List<Object[]> findCompanyNamesWithAvgUserPaymentsOrderedByCompanyName(Session session) {
        return session.createQuery("select c.name, avg(p.amount) from Company c " +
                        "join c.users u " +
                        "join u.payments p " +
                        "group by c.name " +
                        "order by c.name" , Object[].class)
                .list();
    }

    /**
     *      Returns a list of: employee (User object), average payout,
     *      but only for those employees whose average payout is greater than the average
     *      payout of all employees Order by employee name
     */
    public List<Object[]> isItPossible(Session session) {
        return session.createQuery("select u, avg(p.amount) from User u " +
                    "join u.payments p " +
                    "group by  u " +
                    "having avg(p.amount) > (select avg(p.amount) from Payment p)", Object[].class)
                .list();
    }

    public static UserDao getInstance() {
        return INSTANCE;
    }
}
