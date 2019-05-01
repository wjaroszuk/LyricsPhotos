package com.lyricsphotos.service;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.auth.Auth;
import com.flickr4java.flickr.auth.AuthInterface;
import com.flickr4java.flickr.auth.Permission;
import com.flickr4java.flickr.people.User;
import com.flickr4java.flickr.photos.*;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.lyricsphotos.data.Song;
import com.lyricsphotos.data.Stanza;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Service
@PropertySource("classpath:secret.properties")
public class FlickrService {
    private Flickr flickr;

    @Value("${weka.directory}")
    private String mainDirectory;

    public FlickrService(@Value("${flickr.apikey}") String API_KEY, @Value("${flickr.sharedsecret}") String SHARED_SECRET) {
        flickr = new Flickr(API_KEY, SHARED_SECRET, new REST());
        final AuthInterface authInterface = flickr.getAuthInterface();
        OAuth1RequestToken requestToken = authInterface.getRequestToken();
        Auth auth = new Auth();
        auth.setToken(requestToken.getToken());
        auth.setTokenSecret(requestToken.getTokenSecret());
        auth.setUser(new User());
        auth.setPermission(Permission.READ);
        flickr.setAuth(auth);
    }

    public void searchAndSave(Song song) throws IOException {
        PhotosInterface photosInterface = flickr.getPhotosInterface();
        SearchParameters params = new SearchParameters();
        String path = mainDirectory + "\\" + song.getArtist() + "-" + song.getTitle();
        Files.deleteIfExists(Paths.get(path));
        Files.createDirectories(Paths.get(path));
        ArrayList<Stanza> stanzas = song.getStanzas();
        for (int i = 0; i < song.getStanzas().size(); i++) {
            params.setTags(stanzas.get(i).getTags().stream().toArray(String[]::new));
            Set<String> extras = new HashSet<>(Extras.ALL_EXTRAS);
            extras.remove(Extras.MACHINE_TAGS);
            params.setExtras(extras);
            params.setSort(SearchParameters.INTERESTINGNESS_DESC);
            params.setSafeSearch("on"); // not sure if works

            try {
                PhotoList<Photo> searchResult = photosInterface.search(params, 1, 1);
                File file = new File(path + "/" + "image" + i + ".jpg");
                ImageIO.write(photosInterface.getImage(searchResult.get(0), Size.MEDIUM_640), "jpg", file);
            } catch (FlickrException | IOException e) {
                e.printStackTrace();
            }
        }
    }
}
