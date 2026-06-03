package ru.yandex.practicum.filmorate.dao.review;

public interface ReviewLikesStorage {
    void addLike(Long id, Long userId);

    void addDislike(Long id, Long userId);

    void removeLike(Long id, Long userId);

    void removeDislike(Long id, Long userId);
}
