package com.lyricsphotos.service;

import com.lyricsphotos.data.Song;
import com.lyricsphotos.data.SongWithTags;
import com.lyricsphotos.repository.SongsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SongsService {
    private final SongsRepository songsRepository;

    public SongsService(SongsRepository songsRepository) {
        this.songsRepository = songsRepository;
    }

    public List<Song> findAll() {
        return (List<Song>) songsRepository.findAll();
    }

    public Song findSongByArtistAndTitle(String artist, String title) {
        return songsRepository.findByArtistAndTitleAllIgnoreCase(artist, title);
    }

    public Song findSongById(int id) {
        return songsRepository.findSongById(id);
    }
}
