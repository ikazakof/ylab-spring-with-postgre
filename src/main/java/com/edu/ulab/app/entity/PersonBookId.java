package com.edu.ulab.app.entity;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@EqualsAndHashCode
public class PersonBookId implements Serializable {
    private Long personId;
    private Long bookId;
}
