package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.repository.UserRepository;
import com.edu.ulab.app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository,
                           UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        Person user = userMapper.userDtoToPerson(userDto);
        log.info("Mapped user: {}", user);
        Person savedUser = userRepository.save(user);
        log.info("Saved user: {}", savedUser);
        return userMapper.personToUserDto(savedUser);
    }

    @Override
    public boolean userIdExist(Long userId) {
        boolean userExist = userRepository.existsById(userId);
        log.info("User with id:{} exist - {}", userId, userExist);
        return userExist;
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        Person personFromDB = userRepository.findById(userDto.getId()).orElse(new Person());
        log.info("Mapped user: {}", personFromDB);

        Person updatedPerson = userMapper.getPersonUpdatedByDto(userDto, personFromDB);
        log.info("Mapped user for update: {}", updatedPerson);

        userRepository.save(updatedPerson);
        log.info("Updated user: {}", updatedPerson);

        return userMapper.personToUserDto(updatedPerson);
    }

    @Override
    public UserDto getUserById(Long id) {
        UserDto userDto = userMapper.personToUserDto(userRepository.findById(id).orElse(new Person()));
        log.info("User got from DB: {}", userDto);
        return userDto;
    }

    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
        log.info("User with ID: {} deleted from DB", id);
    }
}
