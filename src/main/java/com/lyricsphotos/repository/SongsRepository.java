package com.lyricsphotos.repository;

import com.lyricsphotos.data.Song;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SongsRepository extends CrudRepository<Song, Integer> {
    Song findSongById(int id);
    Song findByArtistAndTitleAllIgnoreCase(String artist, String lyrics);
}
