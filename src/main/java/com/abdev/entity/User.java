package com.abdev.entity;

import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"company", "profile", "userChats", "payments"})
@Entity
@Builder
@Table(name = "users")
public  class User  implements Comparable<User>, BaseEntity<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    @Embedded
    private PersonalInfo personalInfo;

    @Type(type = "jsonb")
    private String info;

    @Enumerated(EnumType.STRING)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @OneToOne(mappedBy = "user",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    private Profile profile;

    @Builder.Default
    @OneToMany(mappedBy = "user")
    private List<UsersChat> userChats = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "receiver")
    private List<Payment> payments = new ArrayList<>();

    @Override
    public int compareTo(User o) {
        return username.compareTo(o.username);
    }

    public String fullName() {
        return getPersonalInfo().getFirstname() + " " + getPersonalInfo().getLastname();
    }

}
