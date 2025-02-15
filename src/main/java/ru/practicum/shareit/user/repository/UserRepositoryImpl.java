package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final Map<Integer, User> inMemoryUsers = new HashMap<>();
    private Integer counter = 0;

    @Override
    public User create(User user) {
        if (inMemoryUsers.values().stream().anyMatch(user1 -> user1.getEmail().equals(user.getEmail()))) {
            throw new RuntimeException("User with the email already exists");
        }
        user.setId(generateId());
        inMemoryUsers.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        if (inMemoryUsers.values().stream().anyMatch(user1 -> user1.getEmail().equals(user.getEmail()))) {
            throw new RuntimeException("User with the email already exists");
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

    @Override
    public Optional<User> get(Integer id) {
        return Optional.ofNullable(inMemoryUsers.get(id));
    }

    @Override
    public void delete(Integer userId) {
        inMemoryUsers.remove(userId);
    }

    private int generateId() {
        return ++counter;
    }
}
