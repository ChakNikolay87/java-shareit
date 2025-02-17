package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.model.ConflictException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.exception.model.ValidationException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserUpdateRequest;

import java.util.*;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private long id = 1;

    @Override
    public User getUser(Long userId) {
        isUserExist(userId);
        return users.get(userId);
    }

    @Override
    public List<User> findAll() {
        List<User> userList = new ArrayList<>();
        users.forEach((key, value) -> userList.add(value));
        return Collections.unmodifiableList(userList);
    }

    @Override
    public User userCreate(User user) {
        if (!isNotConflictEmail(user.getEmail())) {
            throw new ConflictException("Пользователь с таким email уже существует: " + user.getEmail());
        }

        User newUser = createUser(user);
        users.put(newUser.getId(), newUser);
        return newUser;
    }


    @Override
    public User addUpdatingUser(long userId, User user) {
        users.put(userId, user);
        return users.get(userId);
    }

    @Override
    public User updateUser(long userId, UserUpdateRequest userUpdateRequest) {
        isUserExist(userId);
        User updateUser = users.get(userId);

        String newName = userUpdateRequest.getName();
        String newEmail = userUpdateRequest.getEmail();

        if ((newName == null || newName.isBlank()) && (newEmail == null || newEmail.isBlank())) {
            throw new ValidationException("At least one field for update should be provided.");
        }

        if (newName != null && !newName.isBlank()) {
            updateUser.setName(newName);
        }

        if (newEmail != null && !newEmail.isBlank()) {
            if (isNotConflictEmail(newEmail, userId)) {
                updateUser.setEmail(newEmail);
            }
        }

        return updateUser;
    }


    @Override
    public boolean removeUser(long userId) {
        users.remove(userId);
        return !users.containsKey(userId);
    }

    private User createUser(User user) {
        return new User(id++, user.getName(), user.getEmail());
    }

    private void isUserExist(long userId) throws NotFoundException {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("User ID=" + userId + " not found ");
        }
    }

    private boolean isNotConflictEmail(String email) {
        for (Map.Entry<Long, User> entry : users.entrySet()) {
            User user = entry.getValue();
            if (user.getEmail().equals(email)) {
                throw new ConflictException("User: " + entry.getKey() + " with email: " + email + " is already exist");
            }
        }
        return true;
    }

    private boolean isNotConflictEmail(String email, long userId) {
        for (Map.Entry<Long, User> entry : users.entrySet()) {
            User user = entry.getValue();
            if (user.getEmail().equals(email) && user.getId() != userId) {
                throw new ConflictException("User: " + entry.getKey() + " with email: " + email + " is already exist");
            }
        }
        return true;
    }
}
