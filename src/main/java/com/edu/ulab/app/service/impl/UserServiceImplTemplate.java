package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Objects;

@Slf4j
@Service
public class UserServiceImplTemplate implements UserService {
    private final JdbcTemplate jdbcTemplate;
    private final UserMapper userMapper;

    public UserServiceImplTemplate(JdbcTemplate jdbcTemplate, UserMapper userMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        final String INSERT_SQL = "INSERT INTO ulab_edu.PERSON(ID, FULL_NAME, TITLE, AGE) VALUES (nextval('ulab_edu.sequence'),?,?,?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(INSERT_SQL, new String[] { "id" });
                    ps.setString(1, userDto.getFullName());
                    ps.setString(2, userDto.getTitle());
                    ps.setInt(3, userDto.getAge());
                    return ps;
                }, keyHolder);

        userDto.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        log.info("Saved user: {}", userDto);
        return userDto;
    }

    @Override
    public boolean userIdExist(Long userId) {
        final String SELECT_USER_SQL = "SELECT count(*) FROM ulab_edu.PERSON WHERE id = ?";
        Long counter = jdbcTemplate.queryForObject(SELECT_USER_SQL, Long.class, userId);

        boolean userExist = counter != null && counter != 0;
        log.info("User with id:{} exist - {}", userId, userExist);

        return userExist;
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        Person personFromDB = userMapper.userDtoToPerson(getUserById(userDto.getId()));
        log.info("Mapped user: {}", personFromDB);

        Person personAfterUpdateByDto = userMapper.getPersonUpdatedByDto(userDto, personFromDB);
        log.info("Mapped user for update: {}", personAfterUpdateByDto);

        final String UPDATE_SQL = "UPDATE ulab_edu.PERSON SET full_name = ?, title = ?, age = ? WHERE id = ?";

        jdbcTemplate.update(UPDATE_SQL, personAfterUpdateByDto.getFullName(), personAfterUpdateByDto.getTitle(),
                personAfterUpdateByDto.getAge(), userDto.getId());
        log.info("Updated user : {}", personAfterUpdateByDto);

        return userMapper.personToUserDto(personAfterUpdateByDto);
    }


    @Override
    public UserDto getUserById(Long id) {
        final String SELECT_SQL = "SELECT * FROM ulab_edu.PERSON WHERE id = ?";

        UserDto userDtoResult = jdbcTemplate.queryForObject(
                SELECT_SQL,
                (rs, rowNum) -> {
                    UserDto userDto = new UserDto();
                    userDto.setId(rs.getLong("id"));
                    userDto.setFullName(rs.getString("full_name"));
                    userDto.setTitle(rs.getString("title"));
                    userDto.setAge(rs.getInt("age"));
                    return userDto;
                },
                id);

        log.info("User got from DB: {}", userDtoResult);

        return userDtoResult;
    }

    @Override
    public void deleteUserById(Long id) {
        final String DELETE_SQL = "DELETE FROM ulab_edu.PERSON WHERE id = ?";
        jdbcTemplate.update(DELETE_SQL, id);
        log.info("User with ID: {} deleted from DB", id);
    }
}
