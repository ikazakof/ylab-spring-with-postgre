package com.edu.ulab.app.mapper;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.web.request.UserRequest;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto userRequestToUserDto(UserRequest userRequest);

    @Mapping(source = "userId", target = "id")
    UserDto userRequestToUserDtoWithId(Long userId, UserRequest userRequest);

    UserRequest userDtoToUserRequest(UserDto userDto);

    Person userDtoToPerson(UserDto userDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Person getPersonUpdatedByDto(UserDto userDto, @MappingTarget Person person);

    UserDto personToUserDto(Person person);
}
