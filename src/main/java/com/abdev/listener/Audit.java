package com.abdev.listener;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Serializable entityId;

    private String entityName;

    private String entityContent; //JSON in real projects.

    @Enumerated(value = EnumType.STRING)
    private Operation operation;

    public enum Operation {
        SAVE, UPDATE, DELETE, INSERT;
    }
}
