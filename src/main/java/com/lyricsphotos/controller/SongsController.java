package com.lyricsphotos.controller;

import com.lyricsphotos.data.Song;
import com.lyricsphotos.data.Stanza;
import com.lyricsphotos.service.FlickrService;
import com.lyricsphotos.service.NounsService;
import com.lyricsphotos.service.SongsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.nio.file.Files;
import java.nio.file.Paths;

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
    public String getSongDetails(@PathVariable(value = "id") int id, Model model) throws Exception {
        Song song = songsService.findSongById(id);
        if (!song.isAreTagsGenerated()) { // generate if tags are not generated
            System.out.println(" getSongDetails >>> TAGS: " + song.isAreTagsGenerated() + "IMAGES: " + song.isAreImagesGenerated());
            song = nounsService.extractNounsAndSetTags(song);
            song.setAreTagsGenerated(true);
            System.out.println(" getSongDetails >>> TAGS: " + song.isAreTagsGenerated() + "IMAGES: " + song.isAreImagesGenerated());
        }
        songsService.updateSong(song);
        model.addAttribute("song", songsService.findSongById(id));
        return "/lyrics";
    }

    @GetMapping(value = "/songs/lyricsWithImages/{id}")
    public String getSongDetailsWithImages(@PathVariable(value = "id") int id, Model model) throws Exception {
        Song song = songsService.findSongById(id);
        if (!song.isAreTagsGenerated()) { // if tags are not generated, then generate tags+images
            song = nounsService.extractNounsAndSetTags(song);
            song.setAreTagsGenerated(true);
            flickrService.searchAndSave(song);
            song.setAreImagesGenerated(true);
        }
        if (!song.isAreImagesGenerated()) { // if images are not generated (but tags are generated), then generate images
            flickrService.searchAndSave(song);
            song.setAreImagesGenerated(true);
        }
        songsService.updateSong(song);
        model.addAttribute("song", songsService.findSongById(id));
        return "/lyricsWithImages";
    }

    @GetMapping(value = "/songs/forceGenerateTags/{id}")
    public String forceGenerateTags(@PathVariable(value = "id") int id, Model model) throws Exception {
        Song song = songsService.findSongById(id);
        for (Stanza stanza : song.getStanzas()) {
            stanza.setTags(null);
        }
        song = nounsService.extractNounsAndSetTags(song);
        song.setAreTagsGenerated(true);
        song.setAreImagesGenerated(false);
        songsService.updateSong(song);
        model.addAttribute("song", songsService.findSongById(id));
        return "/lyrics";
    }

    @GetMapping(value = "/songs/forceGenerateImages/{id}")
    public String forceGenerateImages(@PathVariable(value = "id") int id, Model model) throws Exception {
        Song song = songsService.findSongById(id);
        if (!song.isAreTagsGenerated()) {
            for (Stanza stanza : song.getStanzas()) {
                stanza.setTags(null);
            }
            song = nounsService.extractNounsAndSetTags(song);
            song.setAreTagsGenerated(true);
        }
        flickrService.searchAndSave(song);
        song.setAreImagesGenerated(true);
        songsService.updateSong(song);
        model.addAttribute("song", songsService.findSongById(id));
        return "/lyricsWithImages";
    }
}
