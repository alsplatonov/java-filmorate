package ru.yandex.practicum.filmorate.dao.likes;

public interface LikesStorage {
    void addLike(long filmId, long userId);

    void removeLike(long filmId, long userId);

    int getLikesCount(long filmId);

    boolean isLiked(long filmId, long userId);

    boolean hasLikes(Long userId);
}
