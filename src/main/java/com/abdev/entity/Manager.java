package com.abdev.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@PrimaryKeyJoinColumn(name = "id")
public class Manager extends User{
    private String projectName;

    @Builder
    public Manager(Long id, String username, PersonalInfo personalInfo, Role role, Company company, Profile profile, List<UsersChat> userChats, String projectName) {
        super(id, username, personalInfo, role, company, profile, userChats);
        this.projectName = projectName;
    }
}


