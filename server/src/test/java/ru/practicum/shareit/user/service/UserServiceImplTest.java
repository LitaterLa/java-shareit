package ru.practicum.shareit.user.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.UtilTestDataClass;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.NewUserRequest;
import ru.practicum.shareit.user.dto.UpdateUserRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplTest {
    private final EntityManager em;
    private final UserService service;

    private final NewUserRequest newUserRequest = UtilTestDataClass.TestUser.newParis();
    private final UpdateUserRequest updateUser = UtilTestDataClass.TestUser.updateParis();

    @Test
    void create() {
        service.create(newUserRequest);

        TypedQuery<User> query = em.createQuery("select u from User u where u.email = :email", User.class);
        User user1 = query.setParameter("email", newUserRequest.getEmail()).getSingleResult();

        assertThat(user1.getId(), notNullValue());
        assertThat(user1.getName(), equalTo(newUserRequest.getName()));
        assertThat(user1.getEmail(), equalTo(newUserRequest.getEmail()));
    }

    @Test
    void update() {
        UserDto dto = service.create(newUserRequest);
        service.update(dto.getId(), updateUser);

        TypedQuery<User> query = em.createQuery("select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", updateUser.getEmail()).getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(updateUser.getName()));
        assertThat(user.getEmail(), equalTo(updateUser.getEmail()));

    }

    @Test
    void updateNotExistingUser() {
        assertThrows(NotFoundException.class,
                () -> service.update(999, updateUser));
    }

    @Test
    void getNotExistingUser() {
        assertThrows(NotFoundException.class,
                () -> service.get(999));
    }

    @Test
    void get() {
        UserDto userDto1 = service.create(newUserRequest);
        int id = userDto1.getId();
        service.get(id);

        TypedQuery<User> query = em.createQuery("select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto1.getEmail()).getSingleResult();

        assertThat(user.getId(), equalTo(userDto1.getId()));
        assertThat(user.getName(), equalTo(userDto1.getName()));
        assertThat(user.getEmail(), equalTo(userDto1.getEmail()));
    }

    @Test
    void delete() {
        UserDto dto = service.create(newUserRequest);
        service.delete(dto.getId());

        assertThrows(NotFoundException.class, () -> service.get(dto.getId()));
    }

}