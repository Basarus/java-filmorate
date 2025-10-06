DELETE FROM film_likes;
DELETE FROM film_genre;
DELETE FROM films;
DELETE FROM genres;
DELETE FROM mpa;

INSERT INTO mpa (id, name) VALUES (1, 'G'), (2, 'PG');
INSERT INTO genres (id, name) VALUES (1, 'Comedy'), (2, 'Drama');