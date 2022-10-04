package com.edu.ulab.app.repository;

import com.edu.ulab.app.config.SystemJpaTest;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.Person;
import com.vladmihalcea.sql.SQLStatementCountValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.List;

import static com.vladmihalcea.sql.SQLStatementCountValidator.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Тесты репозитория {@link BookRepository}.
 */
@SystemJpaTest
public class BookRepositoryTest {
    @Autowired
    BookRepository bookRepository;
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        SQLStatementCountValidator.reset();
    }

    @DisplayName("Сохранить книгу и автора. Число select должно равняться 2, параметры созданной и сохраненной книги должны совпадать")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql"
    })
    void findAllBadges_thenAssertDmlCount() {
        //Given
        Person person = new Person();
        person.setAge(111);
        person.setTitle("reader");
        person.setFullName("Test Test");

        Person savedPerson = userRepository.save(person);

        Book book = new Book();
        book.setAuthor("Test Author");
        book.setTitle("test");
        book.setPageCount(1000);
        book.setPerson(savedPerson);

        //When
        Book result = bookRepository.save(book);

        //Then
        assertThat(result).isNotNull();
        assertThat(result.getAuthor()).isEqualTo("Test Author");
        assertThat(result.getPageCount()).isEqualTo(1000);
        assertThat(result.getTitle()).isEqualTo("test");
        assertThat(result.getPerson().getId()).isEqualTo(savedPerson.getId());
        assertSelectCount(2);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }


    @DisplayName("Проверить наличие книги. Число select должно равняться 1, в результате проверки наличия в бд возвращается true")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void checkBookById_thenAssertDmlCount() {
        //Given
        Long bookId = 3003L;

        //When
        boolean bookExistInDB = bookRepository.existsById(bookId);

        //Then
        assertThat(bookExistInDB).isTrue();
        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Проверить наличие книги по ID пользователя. Число select должно равняться 1, в результате проверки наличия в бд возвращается true")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void checkBookByUserId_thenAssertDmlCount() {
        //Given
        Long userId = 1001L;

        //When
        boolean bookExistInDB = bookRepository.existsBookByPerson_Id(userId);

        //Then
        assertThat(bookExistInDB).isTrue();
        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Обновить книгу. Число select должно равняться 2, параметры созданной и сохраненной книги должны совпадать id книги не меняется.")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void updateBook_thenAssertDmlCount() {
        //Given
        Person person = new Person();
        person.setAge(111);
        person.setTitle("reader");
        person.setFullName("Test Test");

        Person savedPerson = userRepository.save(person);

        Book book = new Book();
        book.setId(3003L);
        book.setAuthor("on more author updated");
        book.setTitle("more default book updated");
        book.setPageCount(1000);
        book.setPerson(savedPerson);

        //When
        Book result = bookRepository.save(book);

        //Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(3003L);
        assertThat(result.getAuthor()).isEqualTo("on more author updated");
        assertThat(result.getPageCount()).isEqualTo(1000);
        assertThat(result.getTitle()).isEqualTo("more default book updated");
        assertThat(result.getPerson().getId()).isEqualTo(savedPerson.getId());
        assertSelectCount(2);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Получить книгу. Число select должно равняться 2, параметры созданной и сохраненной книги должны совпадать id книги не меняется.")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void getBookById_thenAssertDmlCount() {
        //Given
        Person person = new Person();
        person.setId(1001L);

        Book book = new Book();
        book.setId(3003L);
        book.setAuthor("on more author");
        book.setTitle("more default book");
        book.setPageCount(6655);
        book.setPerson(person);

        //When
        Book result = bookRepository.findById(book.getId()).orElse(new Book());

        //Then
        assertThat(result.getId()).isEqualTo(3003L);
        assertThat(result.getAuthor()).isEqualTo("on more author");
        assertThat(result.getPageCount()).isEqualTo(6655);
        assertThat(result.getTitle()).isEqualTo("more default book");
        assertThat(result.getPerson().getId()).isEqualTo(person.getId());
        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Получить книгу. Число select должно равняться 2, параметры созданной и сохраненной книги должны совпадать id книги не меняется.")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void getBookByPersonId_thenAssertDmlCount() {
        //Given
        Person person = new Person();
        person.setId(1001L);

        Book book = new Book();
        book.setId(3003L);
        book.setAuthor("on more author");
        book.setTitle("more default book");
        book.setPageCount(6655);
        book.setPerson(person);

        Book book2 = new Book();
        book2.setId(2002L);
        book2.setAuthor("author");
        book2.setTitle("default book");
        book2.setPageCount(5500);
        book2.setPerson(person);

        List<Book> expected = new ArrayList<>();
        expected.add(book);
        expected.add(book2);


        //When
        List<Book> result = bookRepository.findBooksByPerson_Id(person.getId());


        //Then
        assertThat(result).isNotNull();
        assertThat(result).isNotEmpty();
        assertThat(result).hasSize(expected.size());

        assertThat(result.get(0).getId()).isEqualTo(2002L);
        assertThat(result.get(0).getAuthor()).isEqualTo("author");
        assertThat(result.get(0).getPageCount()).isEqualTo(5500);
        assertThat(result.get(0).getTitle()).isEqualTo("default book");
        assertThat(result.get(0).getPerson().getId()).isEqualTo(person.getId());

        assertThat(result.get(1).getId()).isEqualTo(3003L);
        assertThat(result.get(1).getAuthor()).isEqualTo("on more author");
        assertThat(result.get(1).getPageCount()).isEqualTo(6655);
        assertThat(result.get(1).getTitle()).isEqualTo("more default book");
        assertThat(result.get(1).getPerson().getId()).isEqualTo(person.getId());
        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Проверить наличие книги. Число select должно равняться 2, delete 1, в результате проверки наличия в бд возвращается true")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void deleteBookById_thenAssertDmlCount() {
        //Given
        Long bookId = 3003L;

        //When
        bookRepository.deleteById(bookId);
        boolean bookWithIdExist = bookRepository.existsById(bookId);

        //Then
        assertThat(bookWithIdExist).isFalse();
        assertSelectCount(2);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(1);

    }

    @DisplayName("Проверить наличие книги. Число select должно равняться 2, результирующий список пустой")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void deleteBooksByUserId_thenAssertDmlCount() {
        //Given
        Long userId = 3003L;

        //When
        bookRepository.deleteAllByPerson_Id(userId);
        List<Book> result = bookRepository.findBooksByPerson_Id(userId);

        //Then
        assertThat(result).isEmpty();
        assertSelectCount(2);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);

    }


    // update
    // get
    // get all
    // delete

    // * failed


    // example failed test
}
