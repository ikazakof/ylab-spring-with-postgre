package com.edu.ulab.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserBookDto {
    private Long personId;
    private Long bookId;
}
