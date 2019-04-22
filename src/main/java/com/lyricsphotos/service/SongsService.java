package com.lyricsphotos.service;

import com.lyricsphotos.data.Song;
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

    public Song findSongById(int id) {
        return songsRepository.findSongById(id);
    }

    public void updateSong(Song song) {
        songsRepository.save(song);
    }
}
