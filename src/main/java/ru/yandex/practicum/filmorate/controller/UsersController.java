package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;


@Slf4j
@RequestMapping("/users")
@RestController
public class UsersController extends AbstractController<User> {

    @Autowired
    public UsersController(UserService service) {
        this.service = service;
    }

    @PutMapping(value = "/{id}/friends/{friendId}")
    public List<User> addFriend(@PathVariable(value = "id") long id, @PathVariable(value = "friendId") long friendId) {
        UserService userService = getUserService();
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping(value = "/{id}/friends/{friendId}")
    public List<User> delFriend(@PathVariable(value = "id") long id, @PathVariable(value = "friendId") long friendID) {
        return getUserService().delFriend(id, friendID);
    }

    @GetMapping(value = "/{id}/friends")
    public List<User> findFriends(@PathVariable(value = "id") long id) {
        return getUserService().findFriendById(id);
    }

    @GetMapping(value = "/{id}/friends/common/{otherId}")
    public List<User> findCommonFriends(@PathVariable(value = "id") long id,
                                        @PathVariable(value = "otherId") long otherId) {
        return getUserService().findCommonFriends(id, otherId);
    }

    private UserService getUserService() {
        return (UserService) service;
    }

}
