package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@Service
@Slf4j
public class UserService extends AbstractService<User> {


    @Autowired
    public UserService(UserDbStorage storage) {
        super(storage);
    }

    @Override
    public User create(User user) {
        String userEmail = user.getEmail();
        if (((UserDbStorage) storage).getByEmail(userEmail) != null) {
            String error = String.format("User with email: %s already exists", userEmail);
            log.info(error);
            throw new ResponseStatusException(UNPROCESSABLE_ENTITY,
                    error);
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return storage.create(user);
    }

    @Override
    public User update(long id, User user) {
        User updateUser = findById(id);
        updateUser.setEmail(user.getEmail());
        updateUser.setLogin(user.getLogin());
        if (user.getName() != null) {
            updateUser.setName(user.getName());
        }
        updateUser.setBirthday(user.getBirthday());
        return storage.update(id, updateUser);
    }

    public List<User> addFriend(long id, long friendId) {
        findById(id);
        findById(friendId);
        return ((UserDbStorage)storage).addFriend(id, friendId);

    }

    public List<User> delFriend(long id, long friendID) {
        findById(id);
        findById(friendID);
        return getList(((UserDbStorage)storage).delFriend(id, friendID));
    }

    public List<User> findFriendsById(long id) {
        return getList(findById(id).getFriends());
    }

    public List<User> findCommonFriends(long id, long otherId) {
        return getList(findById(id).getFriends().stream()
                .filter(userId -> findById(otherId).getFriends().contains(userId))
                .collect(Collectors.toSet()));
    }
}
