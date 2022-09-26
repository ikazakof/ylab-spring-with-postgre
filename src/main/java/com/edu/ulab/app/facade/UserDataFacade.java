package com.edu.ulab.app.facade;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.dto.UserBookDto;
import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.exception.*;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.service.impl.BookServiceImplTemplate;
import com.edu.ulab.app.service.impl.UserBookServiceImplTemplate;
import com.edu.ulab.app.service.impl.UserServiceImpl;
import com.edu.ulab.app.service.impl.UserServiceImplTemplate;
import com.edu.ulab.app.web.request.UserBookRequest;
import com.edu.ulab.app.web.request.UserBooksWithIdRequest;
import com.edu.ulab.app.web.response.UserBookResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Slf4j
@Component
public class UserDataFacade {
    private final UserServiceImpl userService;
    private final BookServiceImplTemplate bookService;
    private final UserBookServiceImplTemplate userBookService;
    private final UserMapper userMapper;
    private final BookMapper bookMapper;

    public UserDataFacade(UserServiceImpl userService,
                          BookServiceImplTemplate bookService,
                          UserBookServiceImplTemplate userBookService,
                          UserMapper userMapper,
                          BookMapper bookMapper) {
        this.userService = userService;
        this.bookService = bookService;
        this.userBookService = userBookService;
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
                .peek(bookId -> userBookService.savePersonBook(new UserBookDto(createdUser.getId(), bookId)))
                .toList();
        log.info("Collected book ids: {}", bookIdList);

        log.info("Created PersonBooks links");

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
                        userBookService.savePersonBook(new UserBookDto(updatedUser.getId(), bookDto.getId()));
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

        List<Long> booksIds = userBookService.getBooksIdListByUserId(searchUser.getId());
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

        userService.deleteUserById(userId);

        if (userService.userIdExist(userId)) {
            log.info("User does not deleted: {}", userId);
            throw new UserDoesNotDeletedException("User with input id does not deleted");
        } else {
            log.info("User with id: {} - deleted ", userId);
        }

        userBookService.getBooksIdListByUserId(userId)
                .forEach(bookId -> {
                    bookService.deleteBookById(bookId);
                    if (bookService.bookIdExist(bookId)) {
                        log.info("Book does not deleted: {}", bookId);
                        throw new UserDoesNotDeletedException("Book does not deleted");
                    }
                });

        log.info("All books deleted successfully");

        userBookService.deletePersonBookByUserId(userId);

        if (userBookService.existBookByPersonId(userId)) {
            log.info("UserBooks does not deleted by userId: {}", userId);
            throw new UserBooksDoesNotDeletedException("UserBooks does not deleted by userId");
        }

    }
}
