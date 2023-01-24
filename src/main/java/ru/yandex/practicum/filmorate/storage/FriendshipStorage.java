package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendshipStorage extends Storage<Friendship> {

    List<User> addFriend(long id, long friendId);

    List<User> delFriend(long id, long friendId);

    List<User> findFriendsByUserId(long id);

    List<User> findCommonUsers(long user_id, long other_id);

}
