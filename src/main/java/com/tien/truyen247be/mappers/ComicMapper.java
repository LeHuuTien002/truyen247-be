package com.tien.truyen247be.mappers;

import com.tien.truyen247be.models.Comic;
import com.tien.truyen247be.payload.request.ComicRequest;
import com.tien.truyen247be.payload.response.ComicResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ComicMapper {
    Comic toComic(ComicRequest request);

    List<ComicResponse> toComicResponses(List<Comic> comics);
}
