package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserBookDto;
import com.edu.ulab.app.mapper.UserBookMapper;
import com.edu.ulab.app.service.UserBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.util.List;

@Slf4j
@Service
public class UserBookServiceImplTemplate implements UserBookService {

    private final UserBookMapper userBookMapper;

    private final JdbcTemplate jdbcTemplate;

    public UserBookServiceImplTemplate(UserBookMapper userBookMapper, JdbcTemplate jdbcTemplate) {
        this.userBookMapper = userBookMapper;
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public void savePersonBook(UserBookDto userBookDto) {
        final String INSERT_SQL = "INSERT INTO PERSON_BOOK(PERSON_ID, BOOK_ID) VALUES (?,?)";

        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(INSERT_SQL);
                    ps.setLong(1, userBookDto.getPersonId());
                    ps.setLong(2, userBookDto.getBookId());
                    return ps;
                }
        );

        log.info("Saved UserBook: {}", userBookMapper.userBookDtoToPersonBook(userBookDto));
    }

    @Override
    public List<Long> getBooksIdListByUserId(Long id) {
        final String SELECT_SQL_BIds = "SELECT BOOK_ID FROM PERSON_BOOK WHERE PERSON_ID = ?";

        List<Long> booksIdList = jdbcTemplate.query(SELECT_SQL_BIds,
                (rs, rowNum) -> rs.getLong("BOOK_ID"),
                id);

        log.info("BooksId List got from DB: {}", booksIdList);

        return booksIdList;
    }

    @Override
    public UserBookDto getUserBookDto(Long userId) {
        final String SELECT_SQL = "SELECT * FROM PERSON_BOOK WHERE PERSON_ID = ?";

        UserBookDto userBookDtoResult = jdbcTemplate.queryForObject(
                SELECT_SQL,
                (rs, rowNum) -> {
                    UserBookDto userBookDto = new UserBookDto();
                    userBookDto.setPersonId(rs.getLong("PERSON_ID"));
                    userBookDto.setBookId(rs.getLong("BOOK_ID"));
                    return userBookDto;
                },
                userId);
        log.info("UserBookDto got from DB: {}", userBookDtoResult);

        return userBookDtoResult;
    }

    @Override
    public void deletePersonBookByUserId(Long userId) {
        final String DELETE_SQL = "DELETE FROM PERSON_BOOK WHERE PERSON_ID = ?";
        jdbcTemplate.update(DELETE_SQL, userId);
        log.info("UserBook with user id: {} deleted from DB", userId);
    }

    @Override
    public boolean existBookByPersonId(Long userId) {
        final String SELECT_PERSON_BOOK_SQL = "SELECT count(*) FROM PERSON_BOOK WHERE PERSON_ID = ?";
        Long counter = jdbcTemplate.queryForObject(SELECT_PERSON_BOOK_SQL, Long.class, userId);

        boolean userBookExist = counter != null && counter != 0;
        log.info("UserBook with user id:{} exist - {}", userId, userBookExist);

        return userBookExist;
    }
}
