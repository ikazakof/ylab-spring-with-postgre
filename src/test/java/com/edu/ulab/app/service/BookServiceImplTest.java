package com.edu.ulab.app.service;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.repository.BookRepository;
import com.edu.ulab.app.service.impl.BookServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Тестирование функционала {@link BookServiceImpl}.
 */
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DisplayName("Testing book functionality.")
public class BookServiceImplTest {
    @InjectMocks
    BookServiceImpl bookService;

    @Mock
    BookRepository bookRepository;

    @Mock
    BookMapper bookMapper;

    @Test
    @DisplayName("Создание книги. Должно пройти успешно.")
    void saveBook_Test() {
        //given

        Person person  = new Person();
        person.setId(1L);

        BookDto bookDto = new BookDto();
        bookDto.setUserId(1L);
        bookDto.setAuthor("test author");
        bookDto.setTitle("test title");
        bookDto.setPageCount(1000);

        BookDto result = new BookDto();
        result.setId(1L);
        result.setUserId(1L);
        result.setAuthor("test author");
        result.setTitle("test title");
        result.setPageCount(1000);

        Book book = new Book();
        book.setPageCount(1000);
        book.setTitle("test title");
        book.setAuthor("test author");
        book.setPerson(person);

        Book savedBook = new Book();
        savedBook.setId(1L);
        savedBook.setPageCount(1000);
        savedBook.setTitle("test title");
        savedBook.setAuthor("test author");
        savedBook.setPerson(person);

        //when

        when(bookMapper.bookDtoToBook(bookDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(savedBook);
        when(bookMapper.bookToBookDto(savedBook)).thenReturn(result);


        //then
        BookDto bookDtoResult = bookService.createBook(bookDto);
        assertNotNull(bookDtoResult);
        assertEquals(result.getId(), bookDtoResult.getId());
        assertEquals(result.getUserId(), bookDtoResult.getUserId());
        assertEquals(result.getAuthor(), bookDtoResult.getAuthor());
        assertEquals(result.getTitle(), bookDtoResult.getTitle());
        assertEquals(result.getPageCount(), bookDtoResult.getPageCount());
    }


    @Test
    @DisplayName("Проверка существования книги. Должно пройти успешно.")
    void checkBook_Test() {
        //given

        long bookId = 1L;

        //when

        when(bookRepository.existsById(bookId)).thenReturn(true);

        //then

        boolean resultFromService = bookService.bookIdExist(bookId);
        assertTrue(resultFromService);
    }

    @Test
    @DisplayName("Проверка существования книги по Id пользователя. Должно пройти успешно.")
    void checkBookByUserId_Test() {
        //given

        long userId = 1L;

        //when

        when(bookRepository.existsBookByPerson_Id(userId)).thenReturn(true);

        //then

        boolean resultFromService = bookService.anyBooksWithUserIdExist(userId);
        assertTrue(resultFromService);
    }

    @Test
    @DisplayName("Обновление книги. Должно пройти успешно.")
    void updateBook_Test() {
        //given


        BookDto bookDto = new BookDto();
        bookDto.setId(1L);
        bookDto.setUserId(2L);
        bookDto.setAuthor("test author update");
        bookDto.setTitle("test title update");
        bookDto.setPageCount(2000);

        Person person  = new Person();
        person.setId(2L);

        Book bookFindById = new Book();
        bookFindById.setId(1L);
        bookFindById.setAuthor("test author");
        bookFindById.setTitle("test title");
        bookFindById.setPageCount(1000);
        bookFindById.setPerson(person);

        Book updatedBook = new Book();
        updatedBook.setId(1L);
        updatedBook.setAuthor("test author update");
        updatedBook.setTitle("test title update");
        updatedBook.setPageCount(2000);
        updatedBook.setPerson(person);

        BookDto resultBookDto = new BookDto();
        resultBookDto.setId(1L);
        resultBookDto.setUserId(2L);
        resultBookDto.setAuthor("test author update");
        resultBookDto.setTitle("test title update");
        resultBookDto.setPageCount(2000);

        //when

        when(bookRepository.findById(bookDto.getId())).thenReturn(Optional.of(bookFindById));
        when(bookMapper.getBookUpdatedByDto(bookDto, bookFindById)).thenReturn(updatedBook);
        when(bookRepository.save(updatedBook)).thenReturn(updatedBook);
        when(bookMapper.bookToBookDto(updatedBook)).thenReturn(resultBookDto);

        //then

        BookDto resultBookFromService = bookService.updateBook(bookDto);
        assertNotNull(resultBookFromService);
        assertEquals(resultBookDto.getId(), resultBookFromService.getId());
        assertEquals(resultBookDto.getUserId(), resultBookFromService.getUserId());
        assertEquals(resultBookDto.getAuthor(), resultBookFromService.getAuthor());
        assertEquals(resultBookDto.getTitle(), resultBookFromService.getTitle());
        assertEquals(resultBookDto.getPageCount(), resultBookFromService.getPageCount());

    }

    @Test
    @DisplayName("Получение книги. Должно пройти успешно.")
    void getBookById_Test() {
        //given

        long bookId = 1L;

        Person person  = new Person();
        person.setId(2L);

        Book bookFindById = new Book();
        bookFindById.setId(1L);
        bookFindById.setAuthor("test author");
        bookFindById.setTitle("test title");
        bookFindById.setPageCount(1000);
        bookFindById.setPerson(person);


        BookDto resultBookDto = new BookDto();
        resultBookDto.setId(1L);
        resultBookDto.setUserId(2L);
        resultBookDto.setAuthor("test author");
        resultBookDto.setTitle("test title");
        resultBookDto.setPageCount(1000);

        //when

        when(bookRepository.findById(bookId)).thenReturn(Optional.of(bookFindById));
        when(bookMapper.bookToBookDto(bookFindById)).thenReturn(resultBookDto);

        //then

        BookDto resultBookFromService = bookService.getBookById(bookId);
        assertNotNull(resultBookFromService);
        assertEquals(resultBookDto.getId(), resultBookFromService.getId());
        assertEquals(resultBookDto.getUserId(), resultBookFromService.getUserId());
        assertEquals(resultBookDto.getAuthor(), resultBookFromService.getAuthor());
        assertEquals(resultBookDto.getTitle(), resultBookFromService.getTitle());
        assertEquals(resultBookDto.getPageCount(), resultBookFromService.getPageCount());

    }

    @Test
    @DisplayName("Получение списка id книг по id пользователя. Должно пройти успешно.")
    void getBooksIdsByUserId_Test() {
        //given

        long userId = 1L;

        Person person  = new Person();
        person.setId(1L);

        Book resultBookDto = new Book();
        resultBookDto.setId(1L);
        resultBookDto.setPerson(person);
        resultBookDto.setAuthor("test author");
        resultBookDto.setTitle("test title");
        resultBookDto.setPageCount(1000);

        Book resultBookDto2 = new Book();
        resultBookDto.setId(3L);
        resultBookDto.setPerson(person);
        resultBookDto.setAuthor("test author2");
        resultBookDto.setTitle("test title2");
        resultBookDto.setPageCount(2000);

        Book resultBookDto3 = new Book();
        resultBookDto.setId(4L);
        resultBookDto.setPerson(person);
        resultBookDto.setAuthor("test author3");
        resultBookDto.setTitle("test title3");
        resultBookDto.setPageCount(2000);

        List<Book> resultBooks = new ArrayList<>();
        resultBooks.add(resultBookDto);
        resultBooks.add(resultBookDto2);
        resultBooks.add(resultBookDto3);

        List<Long> booksId = resultBooks
                .stream()
                .filter(Objects::nonNull)
                .map(Book::getId)
                .toList();

        //when

        when(bookRepository.findBooksByPerson_Id(userId)).thenReturn(resultBooks);

        //then

        List<Long> booksIdFromService = bookService.getBooksIdsByUserId(userId);
        assertNotNull(booksIdFromService);
        assertEquals(booksId, booksIdFromService);
    }

    @Test
    @DisplayName("Удаление всех книг по id пользователя. Должно пройти успешно.")
    void deleteAllBooksByUserId_Test() {
        //given

        long userId = 1L;

        //when

        bookService.deleteBooksByUserId(userId);

        //then

        verify(bookRepository, times(1)).deleteAllByPerson_Id(userId);
    }

    @Test
    @DisplayName("Удаление книги по id. Должно пройти успешно.")
    void deleteBookById_Test() {
        //given

        long bookId = 1L;

        //when

        bookService.deleteBookById(bookId);

        //then

        verify(bookRepository, times(1)).deleteById(bookId);
    }

    // update
    // get
    // get all
    // delete

    // * failed
}
