package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.exception.ResourceConflictException;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ManualUserRepository {
    private final Map<Integer, User> inMemoryUsers = new HashMap<>();
    private Integer counter = 0;


    public User create(User user) {
        if (inMemoryUsers.values().stream().anyMatch(user1 -> user1.getEmail().equals(user.getEmail()))) {
            throw new ResourceConflictException("User with the email already exists");
        }
        user.setId(generateId());
        inMemoryUsers.put(user.getId(), user);
        return user;
    }


    public User update(User user) {
        boolean emailExists = inMemoryUsers.values().stream()
                .anyMatch(u -> u.getEmail().equals(user.getEmail()) && !u.getId().equals(user.getId()));

        if (emailExists) {
            throw new ResourceConflictException("Duplicate email");
        }
        User newUser = inMemoryUsers.get(user.getId());
        if (user.getEmail() != null) {
            newUser.setEmail(user.getEmail());
        }

        if (user.getName() != null) {
            newUser.setName(user.getName());
        }
        return newUser;
    }


    public Optional<User> get(Integer id) {
        return Optional.ofNullable(inMemoryUsers.get(id));
    }


    public void delete(Integer userId) {
        inMemoryUsers.remove(userId);
    }

    private int generateId() {
        return ++counter;
    }
}
