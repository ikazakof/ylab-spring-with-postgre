package com.edu.ulab.app.service;


import com.edu.ulab.app.dto.BookDto;

public interface BookService {
    BookDto createBook(BookDto bookDto);

    boolean bookIdExist(Long bookId);

    BookDto updateBook(BookDto bookDto);

    BookDto getBookById(Long id);

    void deleteBookById(Long id);
}
