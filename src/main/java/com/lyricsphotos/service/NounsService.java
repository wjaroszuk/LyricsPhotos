package com.lyricsphotos.service;

import com.lyricsphotos.data.Song;
import com.lyricsphotos.data.Stanza;
import com.lyricsphotos.service.weka.ClassifierService;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NounsService {
    @Autowired
    ClassifierService classifierService;

    private static final String WORD_REGEX = "[^A-Za-z]";

    public Song extractNounsAndSetTags(Song song) throws Exception {
        for (Stanza stanza : song.getStanzas()) {
            POSSample stanzaSampled = generatePOSSample(stanza);
            int lineSampleLength = stanzaSampled.getSentence().length;

            List<String> nounList = new ArrayList<>();
            for (int i = 0; i < lineSampleLength; i++) { // select only nouns from the line
                if (stanzaSampled.getTags()[i].startsWith("N")) {
                    nounList.add(stanzaSampled.getSentence()[i]);
                }
            }

            nounList = nounList.stream().distinct().collect(Collectors.toList()); // remove occurrences like "You'll" - output is "You"
            ArrayList<String> tags = new ArrayList<>();

            if (nounList.isEmpty()) { // if there are no nouns, return the longest word and go to next line
                String longestWord = "";
                for (int i = 0; i < lineSampleLength; i++) {
                    String currentWord = stanzaSampled.getSentence()[i];
                    if (currentWord.length() >= longestWord.length()) {
                        longestWord = currentWord;
                    }
                }
                tags.add(longestWord.split(WORD_REGEX)[0]);
            } else if (nounList.size() <= 3) { // if there are 1-3 nouns, return them all
                nounList.forEach(word -> word = word.split(WORD_REGEX)[0]);
                tags.addAll(nounList);
            } else { // if there are 4+ nouns, return three longest nouns
                String tag1 = Collections.max(nounList, Comparator.comparing(String::length));
                nounList.remove(tag1);
                String tag2 = Collections.max(nounList, Comparator.comparing(String::length));
                nounList.remove(tag2);
                String tag3 = Collections.max(nounList, Comparator.comparing(String::length));
                nounList.remove(tag3);
                tags.add(tag1.split(WORD_REGEX)[0]);
                tags.add(tag2.split(WORD_REGEX)[0]);
                tags.add(tag3.split(WORD_REGEX)[0]);
            }

            String[] tagsArray = tags.stream().distinct().toArray(String[]::new);
            tags.add(classifierService.evaluateInstance(tagsArray));
            stanza.setTags(tags);
        }
        return song;
    }

    private POSSample generatePOSSample(Stanza stanza) throws IOException {
        InputStream inputStream = new FileInputStream("D:\\Projects\\LyricsPhotos\\en-pos-maxent.bin");
        POSModel model = new POSModel(inputStream);
        POSTaggerME tagger = new POSTaggerME(model);
        WhitespaceTokenizer whitespaceTokenizer = WhitespaceTokenizer.INSTANCE;
        StringBuilder lineText = new StringBuilder();
        for (String line : stanza.getLines()) {
            lineText.append(line).append(" ");
        }
        String[] tokens = whitespaceTokenizer.tokenize(String.valueOf(lineText));
        String[] tags = tagger.tag(tokens);
        return new POSSample(tokens, tags);
    }
}