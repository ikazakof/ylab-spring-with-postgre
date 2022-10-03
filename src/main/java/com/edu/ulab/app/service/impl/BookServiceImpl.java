package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.repository.BookRepository;
import com.edu.ulab.app.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;

    private final BookMapper bookMapper;

    public BookServiceImpl(BookRepository bookRepository,
                           BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    @Override
    public BookDto createBook(BookDto bookDto) {
        Book book = bookMapper.bookDtoToBook(bookDto);
        log.info("Mapped book: {}", book);
        Book savedBook = bookRepository.save(book);
        log.info("Saved book: {}", savedBook);
        return bookMapper.bookToBookDto(savedBook);
    }

    @Override
    public boolean bookIdExist(long bookId) {
        boolean bookExist = bookRepository.existsById(bookId);
        log.info("Book with id:{} exist - {}", bookId, bookExist);
        return bookExist;
    }

    @Override
    public boolean anyBooksWithUserIdExist(long userId) {
        return bookRepository.existsBookByPerson_Id(userId);
    }

    @Override
    public BookDto updateBook(BookDto bookDto) {
        Book bookFromDB = bookRepository.findById(bookDto.getId()).orElse(new Book());

        Book updatedBook = bookMapper.getBookUpdatedByDto(bookDto, bookFromDB);
        log.info("Mapped book for update: {}", updatedBook);

        bookRepository.save(updatedBook);
        log.info("Updated book: {}", updatedBook);

        return bookMapper.bookToBookDto(updatedBook);
    }

    @Override
    public BookDto getBookById(long id) {
        BookDto bookDto = bookMapper.bookToBookDto(bookRepository.findById(id).orElse(new Book()));
        log.info("Book got from DB: {}", bookDto);
        return bookDto;
    }

    @Override
    public List<Long> getBooksIdsByUserId(long userId) {
        return bookRepository.findBooksByPerson_Id(userId)
                .stream()
                .filter(Objects::nonNull)
                .map(Book::getId)
                .toList();
    }

    @Override
    public void deleteBooksByUserId(long userId) {
        bookRepository.deleteAllByPerson_Id(userId);
    }

    @Override
    public void deleteBookById(long bookId) {
        bookRepository.deleteById(bookId);
        log.info("Book with ID: {} deleted from DB", bookId);
    }
}
