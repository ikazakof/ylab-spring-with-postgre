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
        final String INSERT_SQL = "INSERT INTO BOOK(TITLE, AUTHOR, PAGE_COUNT, USER_ID) VALUES (?,?,?,?)";
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
    public boolean bookIdExist(Long bookId) {
        final String SELECT_BOOK_SQL = "SELECT count(*) FROM BOOK WHERE id = ?";
        Long counter = jdbcTemplate.queryForObject(SELECT_BOOK_SQL, Long.class, bookId);

        boolean bookExist = counter != null && counter != 0;
        log.info("Book with id:{} exist - {}", bookId, bookExist);

        return bookExist;
    }

    @Override
    public BookDto updateBook(BookDto bookDto) {
        Book bookFromDB = bookMapper.bookDtoToBook(getBookById(bookDto.getId()));
        log.info("Mapped book : {}", bookFromDB);

        Book bookAfterUpdateByDto = bookMapper.getBookUpdatedByDto(bookDto, bookFromDB);
        log.info("Mapped book for update: {}", bookAfterUpdateByDto);

        final String UPDATE_SQL = "UPDATE BOOK SET title = ?, author = ?, page_count = ?, user_id = ? WHERE id = ?";

        jdbcTemplate.update(UPDATE_SQL, bookAfterUpdateByDto.getTitle(), bookAfterUpdateByDto.getAuthor(),
                bookAfterUpdateByDto.getPageCount(), bookAfterUpdateByDto.getUserId(), bookAfterUpdateByDto.getId());

        log.info("Updated book : {}", bookAfterUpdateByDto);

        return bookMapper.bookToBookDto(bookAfterUpdateByDto);
    }

    @Override
    public BookDto getBookById(Long id) {
        final String SELECT_SQL = "SELECT * FROM BOOK WHERE id = ?";

        BookDto bookDtoResult = jdbcTemplate.queryForObject(
                SELECT_SQL,
                (rs, rowNum) -> {
                    BookDto bookDto = new BookDto();
                    bookDto.setId(rs.getLong("id"));
                    bookDto.setTitle(rs.getString("title"));
                    bookDto.setAuthor(rs.getString("author"));
                    bookDto.setPageCount(rs.getInt("page_count"));
                    bookDto.setUserId(rs.getLong("user_id"));
                    return bookDto;
                }, id);

        log.info("Book got from DB: {}", bookDtoResult);

        return bookDtoResult;
    }

    @Override
    public void deleteBookById(Long id) {
        final String DELETE_SQL = "DELETE FROM BOOK WHERE id = ?";
        jdbcTemplate.update(DELETE_SQL, id);
        log.info("Book with ID: {} deleted from DB", id);
    }
}
