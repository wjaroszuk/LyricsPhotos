package com.lyricsphotos.controller;

import com.lyricsphotos.data.Song;
import com.lyricsphotos.service.FlickrService;
import com.lyricsphotos.service.NounsService;
import com.lyricsphotos.service.SongsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
public class SongsController {
    @Autowired
    private SongsService songsService;

    @Autowired
    private NounsService nounsService;

    @Autowired
    private FlickrService flickrService;

    @GetMapping(value = "/songs")
    public String getAllSongs(Model model) {
        model.addAttribute("songs", songsService.findAll());
        return "/songs";
    }

    @GetMapping(value = "/songs/lyrics/{id}")
    public String getSongDetails(@PathVariable(value = "id") int id, Model model) throws IOException {
        Song song = songsService.findSongById(id);
        if (!song.isAreTagsGenerated()) { // generate if tags are not generated
            System.out.println(" getSongDetails >>> TAGS: " + song.isAreTagsGenerated() + "IMAGES: " + song.isAreImagesGenerated());
            song = nounsService.extractNounsAndSetTags(song);
            song.setAreTagsGenerated(true);
            System.out.println(" getSongDetails >>> TAGS: " + song.isAreTagsGenerated() + "IMAGES: " + song.isAreImagesGenerated());
        }
        songsService.updateSong(song);
        model.addAttribute("song", songsService.findSongById(id));
//        model.addAttribute("song", song);
        return "/lyrics";
    }

    @GetMapping(value = "/songs/lyricsWithImages/{id}")
    public String getSongDetailsWithImages(@PathVariable(value = "id") int id, Model model) throws IOException {
        Song song = songsService.findSongById(id);
        if (!song.isAreTagsGenerated()) { // if tags are not generated, then generate tags+images
            System.out.println(" getSongDetailsWithImages #1 >>> TAGS: " + song.isAreTagsGenerated() + "IMAGES: " + song.isAreImagesGenerated());
            song = nounsService.extractNounsAndSetTags(song);
            song.setAreTagsGenerated(true);
            System.out.println(" getSongDetailsWithImages #1 >>> TAGS: " + song.isAreTagsGenerated() + "IMAGES: " + song.isAreImagesGenerated());
            flickrService.searchAndSave(song);
            song.setAreImagesGenerated(true);
            System.out.println(" getSongDetailsWithImages #1 >>> TAGS: " + song.isAreTagsGenerated() + "IMAGES: " + song.isAreImagesGenerated());
        }
        if (!song.isAreImagesGenerated()) { // if images are not generated (but tags are generated), then generate images
            System.out.println(" getSongDetailsWithImages #2 >>> TAGS: " + song.isAreTagsGenerated() + "IMAGES: " + song.isAreImagesGenerated());
            flickrService.searchAndSave(song);
            song.setAreImagesGenerated(true);
            System.out.println(" getSongDetailsWithImages #2 >>> TAGS: " + song.isAreTagsGenerated() + "IMAGES: " + song.isAreImagesGenerated());
        }
        songsService.updateSong(song);
        model.addAttribute("song", songsService.findSongById(id));
//        model.addAttribute("song", song);
        return "/lyricsWithImages";
    }
}
