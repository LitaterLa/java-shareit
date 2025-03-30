package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public final class MyClassForUserMapperImpl {
    private MyClassForUserMapperImpl() {
    }

    public static UserDto mapToUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        return userDto;
    }

    public static User mapToUser(NewUserRequest newUserRequest) {
        return User.builder()
                .id(null)
                .name(newUserRequest.getName())
                .email(newUserRequest.getEmail())
                .build();
    }

    public static User updateUserFields(User user, UpdateUserRequest request) {
        return User.builder()
                .id(user.getId())
                .name(request.getName() != null ? request.getName() : user.getName())
                .email(request.getEmail() != null ? request.getEmail() : user.getEmail())
                .build();
    }


}
