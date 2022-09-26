package com.edu.ulab.app.mapper;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.web.request.BookRequest;
import com.edu.ulab.app.web.request.BookWithIdRequest;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface BookMapper {

    BookDto bookRequestToBookDto(BookRequest bookRequest);

    BookRequest bookDtoToBookRequest(BookDto bookDto);

    Book bookDtoToBook(BookDto bookDto);

    BookDto bookToBookDto(Book book);

    @Mapping(source = "bookId", target = "id")
    Book bookDtoToBookWithId(Long bookId, BookDto bookDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Book getBookUpdatedByDto(BookDto bookDto, @MappingTarget Book book);

    BookDto bookWithIdRequestToBookDto(BookWithIdRequest bookWithIdRequest);

    BookDto booToBookDto(Book book);
}
