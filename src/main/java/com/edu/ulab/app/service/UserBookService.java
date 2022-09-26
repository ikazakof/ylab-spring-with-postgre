package com.edu.ulab.app.service;

import com.edu.ulab.app.dto.UserBookDto;

import java.util.List;

public interface UserBookService {

    void savePersonBook(UserBookDto userBookDto);

    List<Long> getBooksIdListByUserId(Long id);
    UserBookDto getUserBookDto(Long userId);

    void deletePersonBookByUserId(Long userId);

    boolean existBookByPersonId(Long userId);

}