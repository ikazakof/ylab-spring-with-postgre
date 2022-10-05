package com.edu.ulab.app.service;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.repository.UserRepository;
import com.edu.ulab.app.service.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.shouldHaveThrown;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Тестирование функционала {@link UserServiceImpl}.
 */
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DisplayName("Testing user functionality.")
public class UserServiceImplTest {
    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    @Mock
    UserMapper userMapper;

    @Test
    @DisplayName("Создание пользователя. Должно пройти успешно.")
    void savePerson_Test() {
        //given

        UserDto userDto = new UserDto();
        userDto.setAge(11);
        userDto.setFullName("test name");
        userDto.setTitle("test title");

        Person person = new Person();
        person.setFullName("test name");
        person.setAge(11);
        person.setTitle("test title");

        Person savedPerson = new Person();
        savedPerson.setId(1L);
        savedPerson.setFullName("test name");
        savedPerson.setAge(11);
        savedPerson.setTitle("test title");

        UserDto result = new UserDto();
        result.setId(1L);
        result.setAge(11);
        result.setFullName("test name");
        result.setTitle("test title");


        //when

        when(userMapper.userDtoToPerson(userDto)).thenReturn(person);
        when(userRepository.save(person)).thenReturn(savedPerson);
        when(userMapper.personToUserDto(savedPerson)).thenReturn(result);


        //then

        UserDto resultFromService = userService.createUser(userDto);
        assertNotNull(resultFromService);
        assertEquals(result.getId(), resultFromService.getId());
        assertEquals(result.getFullName(), resultFromService.getFullName());
        assertEquals(result.getAge(), resultFromService.getAge());
        assertEquals(result.getTitle(), resultFromService.getTitle());

    }

    @DisplayName("Пользователю не присвоено имя. Должно выбросить ошибку")
    @Test
    @Rollback
    void personTitleNotSet_thenFail() {
        //Given
        UserDto userDto = new UserDto();
        userDto.setAge(11);
        userDto.setFullName("test name");

        Person person = new Person();
        person.setFullName("test name");
        person.setAge(11);


        //When
        when(userMapper.userDtoToPerson(userDto)).thenReturn(person);
        doThrow(DataIntegrityViolationException.class).when(userRepository).save(person);

        //Then

        assertThatThrownBy(() -> shouldHaveThrown(DataIntegrityViolationException.class));
    }

    @Test
    @DisplayName("Проверка существования пользователя. Должно пройти успешно.")
    void checkPerson_Test() {
        //given

        Long userId = 1L;

        //when

        when(userRepository.existsById(userId)).thenReturn(true);

        //then

        boolean resultFromService = userService.userIdExist(userId);
        assertTrue(resultFromService);
    }

    @Test
    @DisplayName("Обновление пользователя. Должно пройти успешно.")
    void updatePerson_Test() {
        //given

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setAge(22);
        userDto.setFullName("test name updateDto");
        userDto.setTitle("test title updateDto");

        Person personFromDb = new Person();
        personFromDb.setId(1L);
        personFromDb.setFullName("test name");
        personFromDb.setAge(11);
        personFromDb.setTitle("test title");

        Person savedPerson = new Person();
        savedPerson.setId(1L);
        savedPerson.setFullName("test name updateDto");
        savedPerson.setAge(22);
        savedPerson.setTitle("test title updateDto");

        UserDto resultUserDto = new UserDto();
        resultUserDto.setId(savedPerson.getId());
        resultUserDto.setFullName(savedPerson.getFullName());
        resultUserDto.setTitle(savedPerson.getTitle());
        resultUserDto.setAge(savedPerson.getAge());

        //when

        when(userRepository.findById(userDto.getId())).thenReturn(Optional.of(personFromDb));
        when(userMapper.getPersonUpdatedByDto(userDto, personFromDb)).thenReturn(savedPerson);
        when(userRepository.save(savedPerson)).thenReturn(savedPerson);
        when(userMapper.personToUserDto(savedPerson)).thenReturn(resultUserDto);

        //then
        UserDto resultFromService = userService.updateUser(userDto);
        assertNotNull(resultFromService);
        assertEquals(resultUserDto.getId(), resultFromService.getId());
        assertEquals(resultUserDto.getFullName(), resultFromService.getFullName());
        assertEquals(resultUserDto.getAge(), resultFromService.getAge());
        assertEquals(resultUserDto.getTitle(), resultFromService.getTitle());
    }

    @Test
    @DisplayName("Получение пользователя. Должно пройти успешно.")
    void getPersonById_Test() {
        //given

        Long userId = 1L;

        Person personFromDb = new Person();
        personFromDb.setId(1L);
        personFromDb.setFullName("test name");
        personFromDb.setAge(22);
        personFromDb.setTitle("test title");

        UserDto resultUserDto = new UserDto();
        resultUserDto.setId(1L);
        resultUserDto.setAge(22);
        resultUserDto.setFullName("test name");
        resultUserDto.setTitle("test title");

        //when

        when(userRepository.findById(userId)).thenReturn(Optional.of(personFromDb));
        when(userMapper.personToUserDto(personFromDb)).thenReturn(resultUserDto);

        //then

        UserDto resultFromService = userService.getUserById(userId);
        assertNotNull(resultFromService);
        assertEquals(resultUserDto.getId(), resultFromService.getId());
        assertEquals(resultUserDto.getFullName(), resultFromService.getFullName());
        assertEquals(resultUserDto.getAge(), resultFromService.getAge());
        assertEquals(resultUserDto.getTitle(), resultFromService.getTitle());
    }

    @Test
    @DisplayName("Удаление пользователя. Должно пройти успешно")
    void deletePersonById_Test() {
        //given

        Long userId = 1L;

        //when

        userService.deleteUserById(userId);

        //then

        verify(userRepository, times(1)).deleteById(userId);

    }


    // update
    // get
    // get all
    // delete

    // * failed
    //         doThrow(dataInvalidException).when(testRepository)
    //                .save(same(test));
    // example failed
    //  assertThatThrownBy(() -> testeService.createTest(testRequest))
    //                .isInstanceOf(DataInvalidException.class)
    //                .hasMessage("Invalid data set");
}
