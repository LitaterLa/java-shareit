package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto create(NewUserRequest request) {
        User user = UserMapper.mapToUser(request);
        user = userRepository.create(user);
        return UserMapper.mapToUserDto(user);
    }

    @Override
    public UserDto update(Integer id, UpdateUserRequest user) {
        User updated = userRepository.get(id).map(user1 -> UserMapper.updateUserFields(user1, user))
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        userRepository.update(updated);
        return UserMapper.mapToUserDto(updated);
    }

    @Override
    public UserDto get(Integer id) {
        return userRepository.get(id)
                .map(UserMapper::mapToUserDto)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    @Override
    public void delete(Integer userId) {
        if (userRepository.get(userId) == null) {
            throw new NotFoundException("Пользователь не найден");
        }
        userRepository.delete(userId);
    }
}
