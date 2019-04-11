package com.lyricsphotos.controller;

import com.lyricsphotos.data.LineWithTag;
import com.lyricsphotos.data.Song;
import com.lyricsphotos.data.SongWithTags;
import com.lyricsphotos.service.NounsService;
import com.lyricsphotos.service.SongsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.sound.sampled.Line;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

@Controller
public class SongsController {
    @Autowired
    private SongsService songsService;

    @Autowired
    private NounsService nounsService;

    @GetMapping(value = "/songs")
    public String getAllSongs(Model model) {
        model.addAttribute("songs", songsService.findAll());
        return "/songs";
    }

    @GetMapping(value = "/songs/lyrics/{id}")
    public String getSongDetails(@PathVariable(value = "id") int id, Model model) throws IOException {
        Song song = songsService.findSongById(id);
        ArrayList<String> tags = nounsService.extractNouns(song.getLyrics());
        ArrayList<String> lyrics = song.getLyrics();
        ArrayList<LineWithTag> linesWithTags = new ArrayList<>();
        for(int i = 0; i < tags.size(); i++) {
            LineWithTag lineWithTag = new LineWithTag(lyrics.get(i), tags.get(i));
            linesWithTags.add(lineWithTag);
        }

        SongWithTags songWithTags = new SongWithTags(song, linesWithTags);
        model.addAttribute("songWithTags", songWithTags);
        return "/lyrics";
    }

    @GetMapping(value="/songs/lyricsWithImages/{id}")
    public String getSongDetailsWithImages(@PathVariable(value = "id") int id, Model model) {
        model.addAttribute("song", songsService.findSongById(id));
        return "/lyricsWithImages";
    }
}
