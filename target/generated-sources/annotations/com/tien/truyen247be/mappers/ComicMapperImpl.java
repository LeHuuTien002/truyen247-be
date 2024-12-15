package com.tien.truyen247be.mappers;

import com.tien.truyen247be.models.Comic;
import com.tien.truyen247be.models.Genre;
import com.tien.truyen247be.payload.request.ComicRequest;
import com.tien.truyen247be.payload.response.ComicResponse;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-14T18:57:16+0700",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.11 (Oracle Corporation)"
)
@Component
public class ComicMapperImpl implements ComicMapper {

    @Override
    public Comic toComic(ComicRequest request) {
        if ( request == null ) {
            return null;
        }

        Comic comic = new Comic();

        comic.setName( request.getName() );
        comic.setOtherName( request.getOtherName() );
        comic.setStatus( request.getStatus() );
        comic.setContent( request.getContent() );
        comic.setAuthor( request.getAuthor() );
        comic.setActivate( request.isActivate() );
        comic.setThumbnail( request.getThumbnail() );

        return comic;
    }

    @Override
    public List<ComicResponse> toComicResponses(List<Comic> comics) {
        if ( comics == null ) {
            return null;
        }

        List<ComicResponse> list = new ArrayList<ComicResponse>( comics.size() );
        for ( Comic comic : comics ) {
            list.add( comicToComicResponse( comic ) );
        }

        return list;
    }

    protected ComicResponse.GenreListByComicIdResponse genreToGenreListByComicIdResponse(Genre genre) {
        if ( genre == null ) {
            return null;
        }

        ComicResponse.GenreListByComicIdResponse genreListByComicIdResponse = new ComicResponse.GenreListByComicIdResponse();

        genreListByComicIdResponse.setId( genre.getId() );
        genreListByComicIdResponse.setName( genre.getName() );

        return genreListByComicIdResponse;
    }

    protected Set<ComicResponse.GenreListByComicIdResponse> genreSetToGenreListByComicIdResponseSet(Set<Genre> set) {
        if ( set == null ) {
            return null;
        }

        Set<ComicResponse.GenreListByComicIdResponse> set1 = new LinkedHashSet<ComicResponse.GenreListByComicIdResponse>( Math.max( (int) ( set.size() / .75f ) + 1, 16 ) );
        for ( Genre genre : set ) {
            set1.add( genreToGenreListByComicIdResponse( genre ) );
        }

        return set1;
    }

    protected ComicResponse comicToComicResponse(Comic comic) {
        if ( comic == null ) {
            return null;
        }

        ComicResponse comicResponse = new ComicResponse();

        comicResponse.setId( comic.getId() );
        comicResponse.setName( comic.getName() );
        comicResponse.setOtherName( comic.getOtherName() );
        comicResponse.setStatus( comic.getStatus() );
        comicResponse.setContent( comic.getContent() );
        comicResponse.setAuthor( comic.getAuthor() );
        comicResponse.setActivate( comic.isActivate() );
        comicResponse.setThumbnail( comic.getThumbnail() );
        comicResponse.setCreateAt( comic.getCreateAt() );
        comicResponse.setUpdateAt( comic.getUpdateAt() );
        comicResponse.setGenres( genreSetToGenreListByComicIdResponseSet( comic.getGenres() ) );

        return comicResponse;
    }
}
