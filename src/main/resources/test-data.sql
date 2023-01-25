INSERT INTO public.genres (name)
VALUES ('Комедия');

INSERT INTO public.genres (name)
VALUES ('Драма');

INSERT INTO public.genres (name)
VALUES ('Мультфильм');

INSERT INTO public.genres (name)
VALUES ('Триллер');

INSERT INTO public.genres (name)
VALUES ('Документальный');

INSERT INTO public.genres (name)
VALUES ('Боевик');

INSERT INTO public.mpa (name)
VALUES ('G');

INSERT INTO public.mpa (name)
VALUES ('PG');

INSERT INTO public.mpa (name)
VALUES ('PG-13');

INSERT INTO public.mpa (name)
VALUES ('R');

INSERT INTO public.mpa (name)
VALUES ('NC-17');

INSERT INTO public.films (name, description, release_date, duration, mpa_id)
VALUES ('Film', 'New film update decription', '1989-04-17', 190, 3);
INSERT INTO public.films (name, description, release_date, duration, mpa_id)
VALUES ('New film', 'New film about friends', '1999-04-30', 120, 3);
INSERT INTO public.films (name, description, release_date, duration, mpa_id)
VALUES ('nisi eiusmod', 'adipisicing', '1967-03-25', 200, 1);

INSERT INTO public.users (email, login, name, birthday)
VALUES ('mail@yandex.ru', 'doloreUpdate', 'est adipisicing', '1976-09-20');
INSERT INTO public.users (email, login, name, birthday)
VALUES ('friend@mail.ru', 'friend', 'friend adipisicing', '1976-08-20');
INSERT INTO public.users (email, login, name, birthday)
VALUES ('friend@common.ru', 'common', 'common', '2000-08-20');
