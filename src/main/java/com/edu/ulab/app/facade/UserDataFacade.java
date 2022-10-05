package com.edu.ulab.app.facade;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.exception.*;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.service.impl.BookServiceImpl;
import com.edu.ulab.app.service.impl.UserServiceImpl;
import com.edu.ulab.app.web.request.UserBookRequest;
import com.edu.ulab.app.web.request.UserBooksWithIdRequest;
import com.edu.ulab.app.web.response.UserBookResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class UserDataFacade {
    private final UserServiceImpl userService;
    private final BookServiceImpl bookService;

    private final UserMapper userMapper;
    private final BookMapper bookMapper;

    public UserDataFacade(UserServiceImpl userService,
                          BookServiceImpl bookService,
                          UserMapper userMapper,
                          BookMapper bookMapper) {
        this.userService = userService;
        this.bookService = bookService;
        this.userMapper = userMapper;
        this.bookMapper = bookMapper;
    }

    @Transactional
    public UserBookResponse createUserWithBooks(UserBookRequest userBookRequest) throws UserAgeNotSpecifyException {
        log.info("Got user book create request: {}", userBookRequest);
        UserDto userDto = userMapper.userRequestToUserDto(userBookRequest.getUserRequest());

        if (userDto.getAge() == 0) {
            log.info("User age not specify in request - {}", userDto.getAge());
            throw new UserAgeNotSpecifyException("User age not specify in request");
        }
        log.info("Mapped user request: {}", userDto);

        UserDto createdUser = userService.createUser(userDto);
        log.info("Created user: {}", createdUser);

        List<Long> bookIdList = userBookRequest.getBookRequests()
                .stream()
                .filter(Objects::nonNull)
                .map(bookMapper::bookRequestToBookDto)
                .peek(bookDto -> bookDto.setUserId(createdUser.getId()))
                .peek(mappedBookDto -> log.info("mapped book: {}", mappedBookDto))
                .map(bookService::createBook)
                .peek(createdBook -> log.info("Created book: {}", createdBook))
                .map(BookDto::getId)
                .toList();
        log.info("Collected book ids: {}", bookIdList);


        return UserBookResponse.builder()
                .userId(createdUser.getId())
                .booksIdList(bookIdList)
                .build();
    }

    @Transactional
    public UserBookResponse updateUserWithBooks(Long userId, UserBooksWithIdRequest userBooksWithIdRequest) throws NotFoundException {
        if (!userService.userIdExist(userId)) {
            log.info("User does not exist with input id: {}", userId);
            throw new NotFoundException("User with input id does not exist");
        }

        log.info("Got user book update request: {}", userBooksWithIdRequest);
        UserDto userDto = userMapper.userRequestToUserDtoWithId(userId, userBooksWithIdRequest.getUserRequest());
        log.info("Mapped user request: {}", userDto);

        UserDto updatedUser = userService.updateUser(userDto);
        log.info("Updated user: {}", updatedUser);

        List<Long> updatedBookIdList = userBooksWithIdRequest.getBookWithIdRequests()
                .stream()
                .filter(Objects::nonNull)
                .map(bookMapper::bookWithIdRequestToBookDto)
                .peek(bookDto -> bookDto.setUserId(userId))
                .peek(bookDto -> {
                    if (bookDto.getId() == null) {
                        bookDto.setId(bookService.createBook(bookDto).getId());
                    } else {
                        bookService.updateBook(bookDto);
                    }
                })
                .peek(updatedBook -> log.info("Updated book: {}", updatedBook))
                .map(BookDto::getId)
                .toList();

        log.info("Updated book ids: {}", updatedBookIdList);


        return UserBookResponse.builder()
                .userId(updatedUser.getId())
                .booksIdList(updatedBookIdList)
                .build();
    }

    @Transactional
    public UserBookResponse getUserWithBooks(Long userId) throws NotFoundException {
        if (!userService.userIdExist(userId)) {
            log.info("User does not exist with input id: {}", userId);
            throw new NotFoundException("User with input id does not exist");
        }

        UserDto searchUser = userService.getUserById(userId);
        log.info("Searched user: {}", searchUser);

        List<Long> booksIds = bookService.getBooksIdsByUserId(userId);
        log.info("Searched booksIds: {}", booksIds);

        return UserBookResponse.builder()
                .userId(searchUser.getId())
                .booksIdList(booksIds)
                .build();
    }

    @Transactional
    public void deleteUserWithBooks(Long userId) throws NotFoundException, UserDoesNotDeletedException, BooksDoesNotDeletedException, UserBooksDoesNotDeletedException {
        if (!userService.userIdExist(userId)) {
            log.info("User does not exist with input id: {}", userId);
            throw new NotFoundException("User with input id does not exist");
        }

        bookService.deleteBooksByUserId(userId);
        if (bookService.anyBooksWithUserIdExist(userId)) {
            log.info("Books by User id does not deleted");
        } else {
            log.info("All books deleted successfully");
        }

        userService.deleteUserById(userId);

        if (userService.userIdExist(userId)) {
            log.info("User does not deleted: {}", userId);
            throw new UserDoesNotDeletedException("User with input id does not deleted");
        } else {
            log.info("User with id: {} - deleted ", userId);
        }
    }
}
