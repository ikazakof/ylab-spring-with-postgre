package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserBookDto;
import com.edu.ulab.app.entity.PersonBook;
import com.edu.ulab.app.mapper.UserBookMapper;
import com.edu.ulab.app.repository.UserBookRepository;
import com.edu.ulab.app.service.UserBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class UserBookServiceImpl implements UserBookService {

    private final UserBookMapper userBookMapper;

    private final UserBookRepository userBookRepository;

    public UserBookServiceImpl(UserBookMapper userBookMapper, UserBookRepository userBookRepository) {
        this.userBookMapper = userBookMapper;
        this.userBookRepository = userBookRepository;
    }

    @Override
    public void savePersonBook(UserBookDto userBookDto) {
        PersonBook personBook = userBookMapper.userBookDtoToPersonBook(userBookDto);
        userBookRepository.save(personBook);
        log.info("Saved UserBook: {}", personBook);
    }

    @Override
    public List<Long> getBooksIdListByUserId(Long id) {
        List<Long> booksIdList = userBookRepository.findPersonBookByPersonId(id)
                .stream()
                .filter(Objects::nonNull)
                .map(PersonBook::getBookId)
                .toList();

        log.info("BooksId List got from DB: {}", booksIdList);

        return booksIdList;
    }

    @Override
    public UserBookDto getUserBookDto(Long userId) {
        UserBookDto userBookDto = userBookMapper.personBookToUserBookDto(userBookRepository.getPersonBookByPersonId(userId));
        log.info("UserBookDto got from DB: {}", userBookDto);

        return userBookDto;
    }

    @Override
    public void deletePersonBookByUserId(Long userId) {
        userBookRepository.deletePersonBookByPersonId(userId);
        log.info("UserBook with user id: {} deleted from DB", userId);
    }

    @Override
    public boolean existBookByPersonId(Long userId) {
        boolean userBookExist = userBookRepository.existsByPersonId(userId);
        log.info("UserBook with user id:{} exist - {}", userId, userBookExist);
        return userBookExist;
    }
}
