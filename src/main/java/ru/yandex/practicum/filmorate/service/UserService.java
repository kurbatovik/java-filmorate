package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@Service
@Slf4j
public class UserService extends AbstractService<User> {

    private final Set<String> userEmails;

    @Autowired
    public UserService(InMemoryStorage<User> storage) {
        super(storage);
        userEmails = new HashSet<>();
    }

    @Override
    public User create(User user) {
        String userEmail = user.getEmail();
        if (userEmails.contains(userEmail)) {
            String error = String.format("User with email: %s already exists", userEmail);
            log.info(error);
            throw new ResponseStatusException(UNPROCESSABLE_ENTITY,
                    error);
        }
        userEmails.add(userEmail);
        return storage.create(user);
    }

    @Override
    public User update(long id, User user) {
        User updateUser = findById(id);

        if (!user.getEmail().equals(updateUser.getEmail())) {
            userEmails.remove(user.getEmail());
            userEmails.add(updateUser.getEmail());
        }

        updateUser.setEmail(user.getEmail());
        updateUser.setLogin(user.getLogin());
        if (user.getName() != null) {
            updateUser.setName(user.getName());
        }
        updateUser.setBirthday(user.getBirthday());
        return storage.update(id, updateUser);
    }

    public List<User> addFriend(long id, long friendId) {
        Set<Long> friendsIds = findById(id).getFriends();
        findById(friendId).getFriends().add(id);
        friendsIds.add(friendId);
        return getList(friendsIds);

    }

    public List<User> delFriend(long id, long friendID) {
        Set<Long> friendsIds = findById(id).getFriends();
        findById(friendID).getFriends().remove(id);
        friendsIds.remove(friendID);
        return getList(friendsIds);
    }

    public List<User> findFriendById(long id) {
        return getList(findById(id).getFriends());
    }

    public List<User> findCommonFriends(long id, long otherId) {
        return getList(findById(id).getFriends().stream()
                .filter(userId -> findById(otherId).getFriends().contains(userId))
                .collect(Collectors.toSet()));
    }
}
