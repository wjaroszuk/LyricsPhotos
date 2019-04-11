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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
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

    public void testFlickr() {
        try {
            PhotosInterface photosInterface = flickr.getPhotosInterface();
            Photo photo = photosInterface.getPhoto("6940286716");
            File outputFile = new File(mainDirectory + "test_photo");
            ImageIO.write(photosInterface.getImage(photo, Size.MEDIUM_800), "jpg", outputFile);
        } catch (FlickrException | IOException e) {
            e.printStackTrace();
        }
    }

    //    public List<Photo> search(String[] tags, Integer page, Integer perPage) {
    public void search(String[] tags, Integer page, Integer perPage) {
        PhotosInterface photosInterface = flickr.getPhotosInterface();
        SearchParameters params = new SearchParameters();
        params.setTags(tags);
        Set<String> extras = new HashSet<>(Extras.ALL_EXTRAS);
        extras.remove(Extras.MACHINE_TAGS);
        params.setExtras(extras);
        params.setSort(SearchParameters.INTERESTINGNESS_DESC);
        params.setSafeSearch("on"); // not sure if works

        try {
            long start = System.currentTimeMillis();
            PhotoList<Photo> searchResult = photosInterface.search(params, perPage, page);
            System.out.println("Reading "+ perPage +" photos over interface took {" + (System.currentTimeMillis() - start) + "} ms");
            for (int i = 0; i < searchResult.size(); i++) {
                File outputFile = new File(mainDirectory + "image_" + i + ".jpg");
                ImageIO.write(photosInterface.getImage(searchResult.get(i), Size.MEDIUM_800), "jpg", outputFile);
            }
        } catch (FlickrException | IOException e) {
            e.printStackTrace();
        }
    }
}
