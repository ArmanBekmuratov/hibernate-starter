package com.abdev.dao;

import com.abdev.entity.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.Session;

import javax.persistence.criteria.*;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserDao {

    private static final UserDao INSTANCE = new UserDao();

    /**
     * Returns all employees
     */
    public List<User> findAll(Session session) {

//  ----------  HQL ----------------
//        return session.createQuery("SELECT u from User u", User.class)
//                .list();

//  ----------  Criteria API ----------------
        var cb = session.getCriteriaBuilder();

        var criteria = cb.createQuery(User.class);
        var user = criteria.from(User.class);
        criteria.select(user);

        return session.createQuery(criteria)
                .list();
    }

    /**
     *Returns all employees with the given name
     */
    public List<User> findAllByFirstName(Session session, String firstName ) {
//  ----------  HQL ----------------
//        return session.createQuery("select u from User u " +
//                "where u.personalInfo.firstname = :firstname", User.class)
//                .setParameter("firstname", firstName)
//                .list();
//  ----------  Criteria API ----------------
        var cb = session.getCriteriaBuilder();

        var criteria = cb.createQuery(User.class);

        var user = criteria.from(User.class);

        criteria.select(user).where(
                cb.equal(user.get("personalInfo").get("firstname"), firstName));

        return session.createQuery(criteria).list();
    }

    /**
     Returns the first {limit} employees sorted by date of birth (in ascending order)
     */
    public List<User> findLimitedUsersOrderedByBirthday(Session session, Integer limit) {
        //  ----------  HQL ----------------
//        return session.createQuery("select u from User  u " +
//                "order by u.personalInfo.birthdate", User.class)
//                .setMaxResults(limit)
//                .list();

        //  ----------  Criteria API ----------------
        var cb = session.getCriteriaBuilder();
        var criteria = cb.createQuery(User.class);
        var user = criteria.from(User.class);

        criteria.select(user).orderBy(cb.asc(user.get("personalInfo").get("birthdate")));

        return session.createQuery(criteria)
                .setMaxResults(limit)
                .list();
    }

    /**
     * Returns all employees of the company with the specified company name
     */
    public List<User> findAllByCompanyName(Session session, String companyName) {
                        //  ----------  HQL ----------------
//        return session.createQuery("select u from Company c " +
//                        "join c.users u " +
//                        "where c.name = :companyName", User.class)
//                .setParameter("companyName", companyName)
//                .list();

        //  ----------  Criteria API ----------------
        var cb = session.getCriteriaBuilder();
        var criteria = cb.createQuery(User.class);
        var company = criteria.from(Company.class);

        var users = company.join(Company_.users);

        criteria.select(users).where(
                cb.equal(company.get(Company_.name), companyName)
        );

        return session.createQuery(criteria).list();
    }

    /**
     Returns all payments received by employees of the company with the specified name,
     * sorted by employee name and then by payout amount
     */
    public List<Payment> findAllPaymentsByCompanyName(Session session, String companyName) {
        //  ----------  HQL ----------------
//        return session.createQuery("select p from Payment p " +
//                        "join p.receiver u " +
//                        "join u.company c " +
//                        "where  c.name = :companyName " +
//                        "order by u.personalInfo.firstname, p.amount", Payment.class)
//                .setParameter("companyName", companyName)
//                .list();
        //  ----------  Criteria API ----------------
        var cb = session.getCriteriaBuilder();

        var criteria = cb.createQuery(Payment.class);
        var payment = criteria.from(Payment.class);
        var user = payment.join(Payment_.receiver);
        var company = user.join(User_.company);

        criteria.select(payment).where(
                        cb.equal(company.get(Company_.name), companyName)
                )
                .orderBy(
                        cb.asc(user.get(User_.personalInfo).get(PersonalInfo_.firstname)),
                        cb.asc(payment.get(Payment_.amount))
                );

        return session.createQuery(criteria)
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
                        "having avg(p.amount) > (select avg(p.amount) from Payment p)" +
                        "order by u.personalInfo.firstname", Object[].class)
                .list();
    }

    public static UserDao getInstance() {
        return INSTANCE;
    }
}
