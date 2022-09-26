package com.edu.ulab.app.service;

import com.edu.ulab.app.dto.UserDto;
import org.springframework.stereotype.Repository;

import java.util.NoSuchElementException;

@Repository
public interface UserService {
    UserDto createUser(UserDto userDto);

    boolean userIdExist(Long userId);

    UserDto updateUser(UserDto userDto);

    UserDto getUserById(Long id);

    void deleteUserById(Long id);
}
