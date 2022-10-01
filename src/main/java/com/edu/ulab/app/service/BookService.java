package com.edu.ulab.app.service;


import com.edu.ulab.app.dto.BookDto;

import java.util.List;

public interface BookService {
    BookDto createBook(BookDto bookDto);

    boolean bookIdExist(long bookId);

    BookDto updateBook(BookDto bookDto);

    BookDto getBookById(long id);

    List<Long> getBooksIdsByUserId(long userId);

    void deleteBookById(long id);

    void deleteBooksByUserId(long userId);

    boolean anyBooksWithUserIdExist(long userId);
}
