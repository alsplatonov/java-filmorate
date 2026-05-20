package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.MpaRatingDto;
import ru.yandex.practicum.filmorate.model.MpaRating;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MpaRatingMapper {
    public static MpaRatingDto mapToRatingDto(MpaRating mpaRating) {
        if (mpaRating == null) {
            return null;
        }
        MpaRatingDto mpaRatingDto = new MpaRatingDto();
        mpaRatingDto.setId(mpaRating.getId());
        mpaRatingDto.setName(mpaRating.getName());
        return mpaRatingDto;
    }

    public static MpaRating mapToRating(MpaRatingDto dto) {
        if (dto == null) {
            return null;
        }
        MpaRating mpaRating = new MpaRating();
        mpaRating.setId(dto.getId());
        mpaRating.setName(dto.getName());
        return mpaRating;
    }
}
