package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.List;

@Service
@Slf4j
public class UserService extends AbstractService<User> {

    private final FriendshipStorage friendshipStorage;

    @Autowired
    public UserService(Storage<User> storage, FriendshipStorage friendshipStorage) {
        super(storage);
        this.friendshipStorage = friendshipStorage;
    }

    @Override
    public User create(User user) {
        checkUserName(user);
        return storage.create(user);
    }

    @Override
    public User update(long id, User user) {
        User updateUser = findById(id);
        updateUser.setEmail(user.getEmail());
        updateUser.setLogin(user.getLogin());
        checkUserName(user);
        updateUser.setName(user.getName());
        updateUser.setBirthday(user.getBirthday());
        return storage.update(id, updateUser);
    }

    public List<User> addFriend(long id, long friendId) {
        findById(id);
        findById(friendId);
        return friendshipStorage.addFriend(id, friendId);

    }

    public List<User> delFriend(long id, long friendID) {
        findById(id);
        findById(friendID);
        return friendshipStorage.delFriend(id, friendID);
    }

    public List<User> findFriendsById(long id) {
        findById(id);
        return friendshipStorage.findFriendsByUserId(id);
    }

    public List<User> findCommonFriends(long id, long otherId) {
        return friendshipStorage.findCommonUsers(id, otherId);
    }

    private void checkUserName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
