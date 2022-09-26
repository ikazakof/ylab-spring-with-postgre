package com.edu.ulab.app.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@Data
@NoArgsConstructor
@IdClass(PersonBookId.class)
@Table(name = "person_book")
public class PersonBook {
    @Id
    private Long personId;
    @Id
    private Long bookId;
}
