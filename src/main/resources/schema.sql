-- USERS
CREATE TABLE IF NOT EXISTS users (
                                     id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                     email VARCHAR(255) NOT NULL,
                                     login VARCHAR(255) NOT NULL,
                                     name VARCHAR(255),
                                     birthday DATE
);

-- MPA RATINGS
CREATE TABLE IF NOT EXISTS mpa_ratings (
                                           id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                           name VARCHAR(10) NOT NULL UNIQUE
);

-- FILMS
CREATE TABLE IF NOT EXISTS films (
                                     id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                     name VARCHAR(255) NOT NULL,
                                     description VARCHAR(1000),
                                     release_date DATE NOT NULL,
                                     duration INTEGER NOT NULL,
                                     mpa_id BIGINT NOT NULL,
                                     CONSTRAINT fk_films_mpa FOREIGN KEY (mpa_id) REFERENCES mpa_ratings(id)
);

-- GENRES
CREATE TABLE IF NOT EXISTS genres (
                                      id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                      name VARCHAR(100) NOT NULL UNIQUE
);

-- FILM_GENRES (many-to-many)
CREATE TABLE IF NOT EXISTS film_genres (
                                           film_id BIGINT NOT NULL,
                                           genre_id BIGINT NOT NULL,
                                           PRIMARY KEY (film_id, genre_id),
                                           CONSTRAINT fk_fg_film FOREIGN KEY (film_id) REFERENCES films(id),
                                           CONSTRAINT fk_fg_genre FOREIGN KEY (genre_id) REFERENCES genres(id)
);

-- LIKES (many-to-many)
CREATE TABLE IF NOT EXISTS likes (
                                     film_id BIGINT NOT NULL,
                                     user_id BIGINT NOT NULL,
                                     PRIMARY KEY (film_id, user_id),
                                     CONSTRAINT fk_likes_film FOREIGN KEY (film_id) REFERENCES films(id),
                                     CONSTRAINT fk_likes_user FOREIGN KEY (user_id) REFERENCES users(id)
);

-- FRIENDSHIPS
CREATE TABLE IF NOT EXISTS friendships (
                                           user_id BIGINT NOT NULL,
                                           friend_id BIGINT NOT NULL,
                                           PRIMARY KEY (user_id, friend_id),
                                           CONSTRAINT fk_friend_user FOREIGN KEY (user_id) REFERENCES users(id),
                                           CONSTRAINT fk_friend_friend FOREIGN KEY (friend_id) REFERENCES users(id)
);