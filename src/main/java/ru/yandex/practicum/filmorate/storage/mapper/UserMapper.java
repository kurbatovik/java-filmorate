package ru.yandex.practicum.filmorate.storage.mapper;

import org.springframework.jdbc.support.rowset.SqlRowSet;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserMapper {

    public static List<User> extractorUser(SqlRowSet rs){
        Map<Long, User> usersById = new HashMap<>();
        while (rs.next()) {
            long id = rs.getLong("id");
            User user = usersById.get(id);
            if (user == null) {
                user = buildUser(rs, id);
                usersById.put(user.getId(), user);
            }
        }
        return new ArrayList<>(usersById.values());
    }

    private static User buildUser(SqlRowSet rs, long id){
        return User.builder()
                .id(id)
                .email(rs.getString("email"))
                .name(rs.getString("name"))
                .login(rs.getString("login"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();
    }
}
