package com.edu.ulab.app.mapper;

import com.edu.ulab.app.dto.UserBookDto;
import com.edu.ulab.app.entity.PersonBook;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserBookMapper {

    PersonBook userBookDtoToPersonBook(UserBookDto userBookDto);

    UserBookDto personBookToUserBookDto(PersonBook personBook);

}
