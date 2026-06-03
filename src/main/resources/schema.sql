-- Сначала удаляем таблицы в порядке от зависимых к родительским
DROP TABLE IF EXISTS film_genres CASCADE;
DROP TABLE IF EXISTS likes CASCADE;
DROP TABLE IF EXISTS friendships CASCADE;
DROP TABLE IF EXISTS film_directors CASCADE;
DROP TABLE IF EXISTS review_likes CASCADE;
DROP TABLE IF EXISTS reviews CASCADE;
DROP TABLE IF EXISTS events CASCADE;
DROP TABLE IF EXISTS films CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS genres CASCADE;
DROP TABLE IF EXISTS mpa_ratings CASCADE;
DROP TABLE IF EXISTS directors CASCADE;

-- Теперь создаём таблицы в правильном порядке: сначала родительские, затем зависимые

-- MPA RATINGS (родительская таблица)
CREATE TABLE IF NOT EXISTS mpa_ratings (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(10) NOT NULL UNIQUE
    );

-- GENRES (родительская таблица)
CREATE TABLE IF NOT EXISTS genres (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
    );

-- DIRECTORS (родительская таблица)
CREATE TABLE IF NOT EXISTS directors (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL
    );

-- USERS (родительская таблица)
CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    login VARCHAR(255) NOT NULL,
    name VARCHAR(255),
    birthday DATE
    );

-- FILMS (зависит от mpa_ratings)
CREATE TABLE IF NOT EXISTS films (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    release_date DATE NOT NULL,
    duration INTEGER NOT NULL,
    mpa_id BIGINT NOT NULL,
    CONSTRAINT fk_films_mpa FOREIGN KEY (mpa_id)
    REFERENCES mpa_ratings(id)
    );

-- FILM_GENRES (зависит от films и genres)
CREATE TABLE IF NOT EXISTS film_genres (
    film_id BIGINT NOT NULL,
    genre_id BIGINT NOT NULL,
    PRIMARY KEY (film_id, genre_id),
    CONSTRAINT fk_fg_film FOREIGN KEY (film_id)
    REFERENCES films(id) ON DELETE CASCADE,
    CONSTRAINT fk_fg_genre FOREIGN KEY (genre_id)
    REFERENCES genres(id) ON DELETE CASCADE
    );

-- LIKES (зависит от films и users)
CREATE TABLE IF NOT EXISTS likes (
    film_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (film_id, user_id),
    CONSTRAINT fk_likes_film FOREIGN KEY (film_id)
    REFERENCES films(id) ON DELETE CASCADE,
    CONSTRAINT fk_likes_user FOREIGN KEY (user_id)
    REFERENCES users(id) ON DELETE CASCADE
    );

-- FRIENDSHIPS (зависит от users)
CREATE TABLE IF NOT EXISTS friendships (
    user_id BIGINT NOT NULL,
    friend_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, friend_id),
    CONSTRAINT fk_friend_user FOREIGN KEY (user_id)
    REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_friend_friend FOREIGN KEY (friend_id)
    REFERENCES users(id) ON DELETE CASCADE
    );

-- FILM_DIRECTORS (зависит от films и directors)
CREATE TABLE IF NOT EXISTS film_directors (
    film_id BIGINT NOT NULL,
    director_id BIGINT NOT NULL,
    PRIMARY KEY (film_id, director_id),
    CONSTRAINT fk_fd_film FOREIGN KEY (film_id)
    REFERENCES films(id) ON DELETE CASCADE,
    CONSTRAINT fk_fd_director FOREIGN KEY (director_id)
    REFERENCES directors(id) ON DELETE CASCADE
    );

-- REVIEWS (зависит от users и films)
CREATE TABLE IF NOT EXISTS reviews (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    content TEXT NOT NULL,
    is_positive BOOLEAN NOT NULL,
    user_id BIGINT NOT NULL,
    film_id BIGINT NOT NULL,
    useful BIGINT NOT NULL,
    CONSTRAINT fk_review_user FOREIGN KEY (user_id)
    REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_review_film FOREIGN KEY (film_id)
    REFERENCES films(id) ON DELETE CASCADE
    );

-- REVIEW_LIKES (зависит от reviews и users)
CREATE TABLE IF NOT EXISTS review_likes (
    review_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    is_like BOOLEAN NOT NULL,
    PRIMARY KEY (review_id, user_id),
    CONSTRAINT fk_rl_review FOREIGN KEY (review_id)
    REFERENCES reviews(id) ON DELETE CASCADE,
    CONSTRAINT fk_rl_user FOREIGN KEY (user_id)
    REFERENCES users(id) ON DELETE CASCADE
    );

-- EVENTS (зависит от users)
CREATE TABLE IF NOT EXISTS events (
    event_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL,
    event_type VARCHAR(10) NOT NULL,  -- LIKE / REVIEW / FRIEND
    operation VARCHAR(10) NOT NULL,    -- ADD / REMOVE / UPDATE
    entity_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_event_user FOREIGN KEY (user_id)
    REFERENCES users(id) ON DELETE CASCADE
    );