package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto create(@RequestBody NewUserRequest user) {
        log.info("Создание пользователя имя {}", user.getName());
        return userService.create(user);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable Integer userId,
                          @RequestBody UpdateUserRequest user) {
        log.info("Обновление пользователя из сервиса ид {}", userId);
        return userService.update(userId, user);
    }

    @GetMapping("/{userId}")
    public UserDto get(@PathVariable Integer userId) {
        log.info("Получение пользователя id {}", userId);
        return userService.get(userId);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Integer userId) {
        log.info("удаление пользователя id {}", userId);
        userService.delete(userId);
    }
}
