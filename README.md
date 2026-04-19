# java-filmorate
Filmorate — это бэкенд-сервис для оценки фильмов и социальной активности пользователей: лайки, друзья и рекомендации 
    популярных фильмов.

# ER-диаграмма базы данных
![ER Diagram](ER-Diagram.png)

# Архитектура базы данных

База данных спроектирована в **третьей нормальной форме (3НФ)** и включает следующие сущности:

## Пользователи (users)
Хранят информацию о зарегистрированных пользователях:
- email
- login
- имя
- дата рождения

## Фильмы (films)
Основная сущность приложения:
- название
- описание
- дата релиза
- длительность
- рейтинг MPA

## Рейтинг MPA (mpa_ratings)
Справочник возрастных ограничений:
- G
- PG
- PG-13
- R
- NC-17

## Жанры (genres)
Справочник жанров фильмов:
- комедия
- драма
- боевик и др.

# Связи между сущностями

- Пользователь может ставить лайки фильмам (M:N)
- Фильмы могут иметь несколько жанров (M:N через film_genres)
- Фильм имеет один рейтинг MPA (M:1)
- Пользователи могут добавлять друг друга в друзья (self M:N с указанием статуса дружбы)

# Дружба пользователей

Система дружбы поддерживает два статуса:

- `UNCONFIRMED` — заявка отправлена
- `CONFIRMED` — дружба подтверждена

# Основные SQL-запросы

## Получить топ популярных фильмов

```sql
SELECT f.*, COUNT(l.user_id) AS likes_count
    FROM films f
LEFT JOIN likes l ON f.id = l.film_id
    GROUP BY f.id
    ORDER BY likes_count DESC
LIMIT 10;
```

## Получить фильмы по жанру
```sql
SELECT f.*
    FROM films f
JOIN film_genres fg ON f.id = fg.film_id
    WHERE fg.genre_id = ?;
```

## Получить общих друзей
```sql
SELECT f1.friend_id
    FROM friendships f1
JOIN friendships f2 ON f1.friend_id = f2.friend_id
    WHERE f1.user_id = ?
    AND f2.user_id = ?
    AND f1.status = 'CONFIRMED'
    AND f2.status = 'CONFIRMED';
```