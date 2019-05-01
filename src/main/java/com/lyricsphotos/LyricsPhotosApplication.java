package com.lyricsphotos;

import com.lyricsphotos.data.Song;
import com.lyricsphotos.data.Stanza;
import com.lyricsphotos.repository.SongsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.context.annotation.Bean;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
public class LyricsPhotosApplication {
    @Autowired
    SongsRepository songsRepository;

    @Autowired
    private ThymeleafProperties thymeleafProperties;

    @Value("${spring.thymeleaf.templates-root}")
    private String templatesRoot;

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(LyricsPhotosApplication.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }

    @PostConstruct
    void init() throws IOException {
        readAllTestData();
    }

    private void readAllTestData() throws IOException {
        List<File> files = Files.walk(Paths.get("D:\\lyrics\\testset")).filter(Files::isRegularFile).map(Path::toFile).collect(Collectors.toList());

        for (File file : files) {
//            System.out.println("Reading file: " + file.getPath());
            List<String> lyricsLines;
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                lyricsLines = br.lines().collect(Collectors.toList());
            }
            ArrayList<Stanza> stanzas = new ArrayList<>();
            ArrayList<String> currentLines = new ArrayList<>();
            Stanza stanza = new Stanza();

            for (String currentLine : lyricsLines) {
                if (currentLine != null) {
                    if (currentLine.length() == 0) {
                        stanza.setLines(currentLines);
                        stanzas.add(stanza);
                        stanza = new Stanza();
                        currentLines = new ArrayList<>();
                    } else {
                        currentLines.add(currentLine);
                    }
                }
            }
            String[] fileNameSplit = file.getName().substring(0, file.getName().lastIndexOf(".")).split("-");
            Song song = new Song(fileNameSplit[0], fileNameSplit[1], stanzas, false, false);
            songsRepository.save(song);
        }
    }

    @Bean
    public ITemplateResolver defaultTemplateResolver() {
        FileTemplateResolver resolver = new FileTemplateResolver();
        resolver.setSuffix(thymeleafProperties.getSuffix());
        resolver.setPrefix(templatesRoot);
        resolver.setTemplateMode(thymeleafProperties.getMode());
        resolver.setCacheable(thymeleafProperties.isCache());
        return resolver;
    }
}
