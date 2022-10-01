package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class BookServiceImplTemplate implements BookService {

    private final JdbcTemplate jdbcTemplate;

    private final BookMapper bookMapper;

    public BookServiceImplTemplate(JdbcTemplate jdbcTemplate, BookMapper bookMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.bookMapper = bookMapper;
    }

    @Override
    public BookDto createBook(BookDto bookDto) {
        final String INSERT_SQL = "INSERT INTO ulab_edu.BOOK(TITLE, AUTHOR, PAGE_COUNT, USER_ID) VALUES (?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(
                new PreparedStatementCreator() {
                    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                        PreparedStatement ps =
                                connection.prepareStatement(INSERT_SQL, new String[]{"id"});
                        ps.setString(1, bookDto.getTitle());
                        ps.setString(2, bookDto.getAuthor());
                        ps.setLong(3, bookDto.getPageCount());
                        ps.setLong(4, bookDto.getUserId());
                        return ps;
                    }
                },
                keyHolder);

        bookDto.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        log.info("Saved book: {}", bookDto);
        return bookDto;
    }

    @Override
    public boolean bookIdExist(long bookId) {
        final String SELECT_BOOK_SQL = "SELECT count(*) FROM ulab_edu.BOOK WHERE id = ?";
        Long counter = jdbcTemplate.queryForObject(SELECT_BOOK_SQL, Long.class, bookId);

        boolean bookExist = counter != null && counter != 0;
        log.info("Book with id:{} exist - {}", bookId, bookExist);

        return bookExist;
    }

    @Override
    public boolean anyBooksWithUserIdExist(long userId) {
        final String SELECT_AND_COUNT_BOOK_SQL = "SELECT count(*) FROM ulab_edu.BOOK WHERE user_id = ?";
        Long counter = jdbcTemplate.queryForObject(SELECT_AND_COUNT_BOOK_SQL, Long.class, userId);

        boolean bookExist = counter != null && counter != 0;
        log.info("Book with UserId:{} exist - {}, and its count = {}", userId, bookExist, counter);

        return bookExist;
    }

    @Override
    public BookDto updateBook(BookDto bookDto) {
        Book bookFromDB = bookMapper.bookDtoToBook(getBookById(bookDto.getId()));
        log.info("Mapped book : {}", bookFromDB);

        Book bookAfterUpdateByDto = bookMapper.getBookUpdatedByDto(bookDto, bookFromDB);
        log.info("Mapped book for update: {}", bookAfterUpdateByDto);

        final String UPDATE_SQL = "UPDATE ulab_edu.BOOK SET title = ?, author = ?, page_count = ?, user_id = ? WHERE id = ?";

        jdbcTemplate.update(UPDATE_SQL, bookAfterUpdateByDto.getTitle(), bookAfterUpdateByDto.getAuthor(),
                bookAfterUpdateByDto.getPageCount(), bookAfterUpdateByDto.getUserId(), bookAfterUpdateByDto.getId());

        log.info("Updated book : {}", bookAfterUpdateByDto);

        return bookMapper.bookToBookDto(bookAfterUpdateByDto);
    }

    @Override
    public BookDto getBookById(long id) {
        final String SELECT_SQL = "SELECT * FROM ulab_edu.BOOK WHERE id = ?";
        Book bookResult = jdbcTemplate.queryForObject(
                SELECT_SQL,
                (rs, rowNum) -> {
                    Book book = new Book();
                    book.setId(rs.getLong("id"));
                    book.setTitle(rs.getString("title"));
                    book.setAuthor(rs.getString("author"));
                    book.setPageCount(rs.getInt("page_count"));
                    book.setUserId(rs.getLong("user_id"));
                    return book;
                }, id);
        BookDto bookDtoResult = bookMapper.bookToBookDto(bookResult);
        log.info("Book got from DB: {}", bookDtoResult);

        return bookDtoResult;
    }

    @Override
    public List<Long> getBooksIdsByUserId(long userId) {
        final String SELECT_BOOKS_BY_USERID_SQL = "SELECT id FROM ulab_edu.BOOK WHERE user_id = ?";
        List<Long> booksIds = jdbcTemplate.queryForList(SELECT_BOOKS_BY_USERID_SQL, Long.class, userId);
        return booksIds;
    }

    @Override
    public void deleteBooksByUserId(long userId) {
        final String DELETE_BY_USERID_SQL = "DELETE FROM ulab_edu.BOOK WHERE user_id = ?";
        jdbcTemplate.update(DELETE_BY_USERID_SQL, userId);
    }

    @Override
    public void deleteBookById(long id) {
        final String DELETE_SQL = "DELETE FROM ulab_edu.BOOK WHERE id = ?";
        jdbcTemplate.update(DELETE_SQL, id);
        log.info("Book with ID: {} deleted from DB", id);
    }
}
