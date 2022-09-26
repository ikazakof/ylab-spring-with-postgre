package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.repository.BookRepository;
import com.edu.ulab.app.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BookServiceImpl implements BookService {
    //Todo выбрасывать ли NoSuchElementException при условии того, что он не может появиться?
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
    public boolean bookIdExist(Long bookId) {
        boolean bookExist = bookRepository.existsById(bookId);
        log.info("Book with id:{} exist - {}", bookId, bookExist);
        return bookExist;
    }

    @Override
    public BookDto updateBook(BookDto bookDto) throws NotFoundException {
        Book bookFromDB = bookRepository.findById(bookDto.getId()).orElse(new Book());
        if (bookFromDB.getId() == null) {
            log.info("User with id: {} does not exist ", bookDto.getId());
            throw new NotFoundException("User with input id does not exist");
        }

        Book updatedBook = bookMapper.getBookUpdatedByDto(bookDto, bookFromDB);
        log.info("Mapped book for update: {}", updatedBook);

        bookRepository.save(updatedBook);
        log.info("Updated book: {}", updatedBook);

        return bookMapper.bookToBookDto(updatedBook);
    }

    @Override
    public BookDto getBookById(Long id) throws NotFoundException {
        BookDto bookDto = bookMapper.bookToBookDto(bookRepository.findById(id).orElse(new Book()));
        if (bookDto.getId() == null) {
            log.info("User with id: {} does not exist ", bookDto.getId());
            throw new NotFoundException("User with input id does not exist");
        }

        log.info("Book got from DB: {}", bookDto);
        return bookDto;
    }


    @Override
    public void deleteBookById(Long id) {
        bookRepository.deleteById(id);
        log.info("Book with ID: {} deleted from DB", id);
    }
}
