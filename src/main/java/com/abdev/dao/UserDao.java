package com.abdev.dao;

import com.abdev.entity.*;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.Session;

import java.util.Collections;
import java.util.List;

import static com.abdev.entity.QCompany.*;
import static com.abdev.entity.QPayment.payment;
import static com.abdev.entity.QUser.*;

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
        //--------Querydsl---------------------
        return new JPAQuery<User>(session)
                .select(user)
                .from(user)
                .fetch();
    }

    /**
     *Returns all employees with the given first name
     */
    public List<User> findAllByFirstName(Session session, String firstName ) {
//  ---------- Querydsl ----------------
        return new JPAQuery<User>(session)
                .select(user)
                .from(user)
                .where(user.personalInfo.firstname.eq(firstName))
                .fetch();
//  ----------  HQL ----------------
//        return session.createQuery("select u from User u " +
//                "where u.personalInfo.firstname = :firstname", User.class)
//                .setParameter("firstname", firstName)
//                .list();
//  ----------  Criteria API ----------------
//        var cb = session.getCriteriaBuilder();
//
//        var criteria = cb.createQuery(User.class);
//
//        var user = criteria.from(User.class);
//
//        criteria.select(user).where(
//                cb.equal(user.get("personalInfo").get("firstname"), firstName));
//
//        return session.createQuery(criteria).list();
    }

    /**
     Returns the first {limit} employees sorted by date of birth (in ascending order)
     */
    public List<User> findLimitedUsersOrderedByBirthday(Session session, Integer limit) {
        //  ---------- Querydsl ----------------
        return new JPAQuery<User>(session)
                .select(user)
                .from(user)
                .orderBy(user.personalInfo.birthdate.asc())
                .limit(limit)
                .fetch();
        //  ----------  HQL ----------------
//        return session.createQuery("select u from User  u " +
//                "order by u.personalInfo.birthdate", User.class)
//                .setMaxResults(limit)
//                .list();

        //  ----------  Criteria API ----------------
//        var cb = session.getCriteriaBuilder();
//        var criteria = cb.createQuery(User.class);
//        var user = criteria.from(User.class);
//
//        criteria.select(user).orderBy(cb.asc(user.get("personalInfo").get("birthdate")));
//
//        return session.createQuery(criteria)
//                .setMaxResults(limit)
//                .list();
    }

    /**
     * Returns all employees of the company with the specified company name
     */
    public List<User> findAllByCompanyName(Session session, String companyName) {
        //  ---------- Querydsl ----------------
        return new JPAQuery<User>(session)
                .select(user)
                .from(company)
                .join(company.users)
                .where(company.name.eq(companyName))
                .fetch();

        //  ----------  HQL ----------------
//        return session.createQuery("select u from Company c " +
//                        "join c.users u " +
//                        "where c.name = :companyName", User.class)
//                .setParameter("companyName", companyName)
//                .list();

        //  ----------  Criteria API ----------------
//        var cb = session.getCriteriaBuilder();
//        var criteria = cb.createQuery(User.class);
//        var company = criteria.from(Company.class);
//
//        var users = company.join(Company_.users);
//
//        criteria.select(users).where(
//                cb.equal(company.get(Company_.name), companyName)
//        );
//
//        return session.createQuery(criteria).list();
    }

    /**
     Returns all payments received by employees of the company with the specified name,
     * sorted by employee name and then by payout amount
     */
    public List<Payment> findAllPaymentsByCompanyName(Session session, String companyName) {
        //  ---------- Querydsl ----------------
        return new JPAQuery<Payment>(session)
                .select(payment)
                .from(payment)
                .join(payment.receiver, user)
                .join(user.company, company)
                .where(company.name.eq(companyName))
                .orderBy(user.personalInfo.firstname.asc(), payment.amount.asc())
                .fetch();

        //  ----------  HQL ----------------
//        return session.createQuery("select p from Payment p " +
//                        "join p.receiver u " +
//                        "join u.company c " +
//                        "where  c.name = :companyName " +
//                        "order by u.personalInfo.firstname, p.amount", Payment.class)
//                .setParameter("companyName", companyName)
//                .list();
        //  ----------  Criteria API ----------------
//        var cb = session.getCriteriaBuilder();
//
//        var criteria = cb.createQuery(Payment.class);
//        var payment = criteria.from(Payment.class);
//        var user = payment.join(Payment_.receiver);
//        var company = user.join(User_.company);
//
//        criteria.select(payment).where(
//                        cb.equal(company.get(Company_.name), companyName)
//                )
//                .orderBy(
//                        cb.asc(user.get(User_.personalInfo).get(PersonalInfo_.firstname)),
//                        cb.asc(payment.get(Payment_.amount))
//                );
//
//        return session.createQuery(criteria)
//                .list();
    }

    /**
     * Returns the average salary of an employee with the given first and last name
     */
    public Double findAveragePaymentAmountByFirstAndLastNames(Session session, String firstName, String lastName) {
        //  ---------- Querydsl ----------------
        return new JPAQuery<Double>(session)
                .select(payment.amount.avg())
                .from(payment)
                .join(payment.receiver, user)
                .where(user.personalInfo.firstname.eq(firstName)
                                .and(user.personalInfo.lastname.eq(lastName)))
                .fetchFirst();
        /*  ----------  HQL ----------------
        return session.createQuery("select avg(p.amount) from Payment p " +
                        "join p.receiver u " +
                        "where u.personalInfo.firstname = :firstName " +
                        "   and u.personalInfo.lastname = :lastName", Double.class)
                .setParameter("firstName", firstName)
                .setParameter("lastName", lastName)
                .uniqueResult();*/
        //----------  Criteria API ----------------
//        var cb = session.getCriteriaBuilder();
//
//        var criteria = cb.createQuery(Double.class);
//        var payment = criteria.from(Payment.class);
//        var user = payment.join(Payment_.receiver);
//
//        List<Predicate> predicates = new ArrayList<>();
//
//        if( firstName != null) {
//            predicates.add(cb.equal(user.get(User_.personalInfo).get(PersonalInfo_.firstname), firstName));
//        }
//
//        if( lastName != null) {
//            predicates.add(cb.equal(user.get(User_.personalInfo).get(PersonalInfo_.lastname), lastName));
//        }
//
//        criteria.select(cb.avg(payment.get(Payment_.amount))).where(
//                predicates.toArray(Predicate[]::new)
//        );
//
//
//        return session.createQuery(criteria)
//                .uniqueResult();

    }


    /**
     * Returns for each company: name, average salary of all its employees. Companies are ordered by name.
     */
    public List<Tuple> findCompanyNamesWithAvgUserPaymentsOrderedByCompanyName(Session session) {
        //  ---------- Querydsl ----------------
        return new JPAQuery<com.querydsl.core.Tuple>(session)
                .select(company.name, payment.amount.avg())
                .from(company)
                .join(company.users, user)
                .join(user.payments, payment)
                .groupBy(company.name)
                .orderBy(company.name.asc())
                .fetch();
        //  ----------  HQL ----------------
//        return session.createQuery("select c.name, avg(p.amount) from Company c " +
//                        "join c.users u " +
//                        "join u.payments p " +
//                        "group by c.name " +
//                        "order by c.name" , Object[].class)
//                .list();

        //  ----------  Criteria API ----------------
//        var cb = session.getCriteriaBuilder();
//        var criteria = cb.createQuery(CompanyDto.class);
//
//        var company = criteria.from(Company.class);
//        var user = company.join(Company_.users);
//        var payment = user.join(User_.payments);
//
//        criteria.select(
//                cb.construct(CompanyDto.class,
//                company.get(Company_.name),
//                cb.avg(payment.get(Payment_.amount)))
//        )
//                .groupBy(company.get(Company_.name))
//                .orderBy(cb.asc(company.get(Company_.name))
//                );
//
//        return session.createQuery(criteria)
//                .list();
    }

    /**
     *      Returns a list of: employee (User object), average payout,
     *      but only for those employees whose average payout is greater than the average
     *      payout of all employees Order by employee name
     */
    public List<Tuple> isItPossible(Session session) {
        //  ---------- Querydsl ----------------
        return new JPAQuery<Tuple>(session)
                .select(user, payment.amount.avg())
                .from(user)
                .join(user.payments, payment)
                .groupBy(user.id)
                .having(payment.amount.avg().gt(
                        new JPAQuery<Double>(session).select(payment.amount.avg())
                        .from(payment)))
                .orderBy(user.personalInfo.firstname.asc())
                .fetch();
        //  ----------  HQL ----------------
//        return session.createQuery("select u, avg(p.amount) from User u " +
//                        "join u.payments p " +
//                        "group by  u " +
//                        "having avg(p.amount) > (select avg(p.amount) from Payment p)" +
//                        "order by u.personalInfo.firstname", Object[].class)
//                .list();

        //  ----------  Criteria API ----------------
//        var cb = session.getCriteriaBuilder();
//
//        var criteria = cb.createQuery(Tuple.class);
//        var user = criteria.from(User.class);
//        var payment = user.join(User_.payments);
//
//        var subquery = criteria.subquery(Double.class);
//        var paymentSubquery = subquery.from(Payment.class);
//
//
//
//        criteria.select(
//                cb.tuple(user,cb.avg(payment.get(Payment_.amount))
//                )
//        )
//                .groupBy(user.get(User_.id))
//                .having(cb.gt(
//                        cb.avg(payment.get(Payment_.amount)),
//                        subquery.select(cb.avg(paymentSubquery.get(Payment_.amount)))))
//                .orderBy(cb.asc(user.get(User_.personalInfo).get(PersonalInfo_.firstname)));
//
//        return session.createQuery(criteria).list();
    }

    public static UserDao getInstance() {
        return INSTANCE;
    }
}
