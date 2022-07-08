package com.abdev;

import com.abdev.entity.User;
import com.abdev.entity.UsersChat;
import com.abdev.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.graph.GraphSemantic;
import org.hibernate.graph.RootGraph;
import org.hibernate.graph.SubGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Map;

public class HibernateRunner {

    private static final Logger log =  LoggerFactory.getLogger(HibernateRunner.class);

    public static void main(String[] args) throws SQLException {

        try (var sessionFactory = HibernateUtil.buildSessionFactory();
             Session session = sessionFactory.openSession()) {
           session.beginTransaction();
          // session.enableFetchProfile("withCompanyAndPayment");
            var userGraph = session.createEntityGraph(User.class);
            userGraph.addAttributeNodes("company", "userChats");
            var usersChatSubGraph = userGraph.addSubgraph("userChats", UsersChat.class);
            usersChatSubGraph.addAttributeNodes("chat");

            Map<String, Object> properties = Map.of(
//                    GraphSemantic.LOAD.getJpaHintName(), session.getEntityGraph("withCompanyAndChat")
                    GraphSemantic.LOAD.getJpaHintName(),userGraph
            );
            var user = session.find(User.class, 1L, properties);
            System.out.println(user.getCompany().getName());
            System.out.println(user.getUserChats().size());



            var users = session.createQuery(
                    "select u from User u ", User.class)
                    .setHint(GraphSemantic.LOAD.getJpaHintName(), userGraph )
                    .list();
            users.forEach(it -> System.out.println(it.getUserChats().size()));
            users.forEach(it -> System.out.println(it.getCompany().getName()));

            session.getTransaction().commit();
        }
    }
}
