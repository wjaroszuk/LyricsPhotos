package com.lyricsphotos.service;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

@Service
public class NounsService {
    public ArrayList<String> extractNouns(ArrayList<String> lyrics) throws IOException {
        ArrayList<String> tags = new ArrayList<>();
        for (String line : lyrics) {
            POSSample lineSample = generatePOSSample(line);
            int lineSampleLength = lineSample.getSentence().length;

            ArrayList<String> nounList = new ArrayList<>();
            for (int i = 0; i < lineSampleLength; i++) { // select only nouns from the line
                if (lineSample.getTags()[i].startsWith("N")) {
                    nounList.add(lineSample.getSentence()[i]);
                }
            }

            if (nounList.isEmpty()) { // if there are no nouns, return the longest word and go to next line
                tags.add(getLongestWord(lineSample.getSentence()));
            } else if (nounList.size() == 1) { // if there is one noun, return it
                tags.add(nounList.get(0));
            } else { // if there are multiple nouns, return the longest one
                Object[] objectArray = nounList.toArray();
                String[] nounArray = Arrays.copyOf(objectArray, objectArray.length, String[].class);
                tags.add(getLongestWord(nounArray));
            }
        }
        return tags;
    }

    private POSSample generatePOSSample(String line) throws IOException {
        InputStream inputStream = new FileInputStream("D:\\Projects\\LyricsPhotos\\en-pos-maxent.bin");
        POSModel model = new POSModel(inputStream);
        POSTaggerME tagger = new POSTaggerME(model);
        WhitespaceTokenizer whitespaceTokenizer = WhitespaceTokenizer.INSTANCE;
        String[] tokens = whitespaceTokenizer.tokenize(line);
        String[] tags = tagger.tag(tokens);
        POSSample sample = new POSSample(tokens, tags);
        return sample;
    }

    // Get longest word in the array (the last one, taking order into account)
    private String getLongestWord(String[] stringArray) {
        int index = 0;
        int elementLength = stringArray[0].length();
        for (int i = 1; i < stringArray.length; i++) {
            if (stringArray[i].length() >= elementLength) {
                index = i;
                elementLength = stringArray[i].length();
            }
        }
        return stringArray[index];
    }
}