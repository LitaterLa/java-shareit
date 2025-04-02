package ru.practicum.shareit.user.service;


import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {
    UserDto create(NewUserRequest user);

    UserDto update(Integer id, UpdateUserRequest user);

    UserDto get(Integer id);

    void delete(Integer userId);
}
