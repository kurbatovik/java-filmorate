DROP ALL OBJECTS;
CREATE TABLE IF NOT EXISTS mpa
(
    id   INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar(16)
);

CREATE TABLE IF NOT EXISTS users
(
    id       INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email    varchar(256) NOT NULL UNIQUE,
    login    varchar(32)  NOT NULL,
    name     varchar(32),
    birthday date
);
CREATE UNIQUE INDEX IF NOT EXISTS user_email_uindex ON users (email);
CREATE UNIQUE INDEX IF NOT EXISTS user_login_uindex ON users (login);

CREATE TABLE IF NOT EXISTS friends
(
    id        INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    user_id   int,
    friend_id int,
    accepted  boolean,
    CONSTRAINT friends_user_id_fk FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT friends_friend_id_fk FOREIGN KEY (friend_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS films
(
    id           INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name         varchar(64)  NOT NULL,
    description  varchar(200) NOT NULL,
    release_date date,
    duration     int,
    mpa_id       int,
    CONSTRAINT films_mpa_id_fk FOREIGN KEY (mpa_id) REFERENCES mpa (id)
);

CREATE TABLE IF NOT EXISTS likes
(
    id      INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    film_id int,
    user_id int,
    CONSTRAINT likes_film_id_fk FOREIGN KEY (film_id) REFERENCES films (id),
    CONSTRAINT likes_user_id_fk FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS genres
(
    id   INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar(32) NOT NULL
);

CREATE TABLE IF NOT EXISTS films_genres
(
    id       INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    film_id  int,
    genre_id int,
    CONSTRAINT fg_film_id_fk FOREIGN KEY (film_id) REFERENCES films (id),
    CONSTRAINT fg_genre_id_fk FOREIGN KEY (genre_id) REFERENCES genres (id)
);