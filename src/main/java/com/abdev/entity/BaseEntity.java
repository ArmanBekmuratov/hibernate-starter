package com.abdev.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;



public  interface BaseEntity<T extends Serializable> {

    void setId(T id);

    T getId();
}
