package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.List;
import java.util.stream.Collectors;

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
        List<Long> friendsIds = friendshipStorage.addFriend(id, friendId);
        return getList(friendsIds);

    }

    public List<User> delFriend(long id, long friendID) {
        findById(id);
        findById(friendID);
        return getList(friendshipStorage.delFriend(id, friendID));
    }

    public List<User> findFriendsById(long id) {
        findById(id);
        return getList(friendshipStorage.findFriendsByUserId(id));
    }

    public List<User> findCommonFriends(long id, long otherId) {
        return getList(friendshipStorage.findFriendsByUserId(id).stream()
                .filter(userId -> friendshipStorage.findFriendsByUserId(otherId).contains(userId))
                .collect(Collectors.toSet()));
    }
}
