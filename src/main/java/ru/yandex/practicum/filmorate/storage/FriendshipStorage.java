package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Friendship;

import java.util.Collection;
import java.util.List;

public interface FriendshipStorage extends Storage<Friendship> {

    List<Long> addFriend(long id, long friendId);

    List<Long> delFriend(long id, long friendId);

    Collection<Long> findFriendsByUserId(long id);

}
