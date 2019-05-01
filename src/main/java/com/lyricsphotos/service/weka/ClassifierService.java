package com.lyricsphotos.service.weka;

import com.lyricsphotos.repository.SongsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NumericToNominal;
import weka.filters.unsupervised.attribute.StringToWordVector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
@PropertySource("classpath:secret.properties")
public class ClassifierService {
    @Autowired
    private SongsRepository songsRepository;

    private final String trainingDataPath;
    private Instances trainingSet;
    private FilteredClassifier classifier;

    public ClassifierService(@Value("${trainingfiles.directory}") String trainingDataPath) throws Exception { // default configuration
        this.trainingDataPath = trainingDataPath;
        setTrainingSet(null); // read ALL training instances
        this.classifier = new FilteredClassifier();
        setFilter();
        setFilter2();
        this.trainingSet.setClassIndex(this.trainingSet.attribute("weatherAttribute").index());
        J48 j48 = new J48();
        j48.setUnpruned(true);
        this.classifier.setClassifier(j48);
        this.classifier.buildClassifier(trainingSet);
    }

    public void recreateClassifier(String[] trainingFilenames, String classifierName) throws Exception {
        songsRepository.findAll().forEach(song -> {
            song.setAreTagsGenerated(false);
            song.setAreImagesGenerated(false);
        });

        ArrayList<String> trainingList = new ArrayList<>(Arrays.asList(trainingFilenames));
        setTrainingSet(trainingList);
        setFilter();
        setFilter2();
        this.trainingSet.setClassIndex(this.trainingSet.attribute("weatherAttribute").index());
        setClassifier(classifierName);
        this.classifier.buildClassifier(trainingSet);
    }

    private void setTrainingSet(ArrayList<String> trainingFilenames) throws IOException {
        if (trainingFilenames == null) {
            readAllTrainingData();
        } else {
            this.trainingSet = null;
            this.trainingSet = new Instances(new FileReader("D:\\lyrics\\learningset\\The Weather Girls - It's Raining Men.arff"));
            for (String trainingFile : trainingFilenames) {
                BufferedReader br = new BufferedReader(new FileReader(trainingDataPath + trainingFile));
                this.trainingSet.addAll(new Instances(br));
            }
            if (this.trainingSet.isEmpty()) {
                readAllTrainingData();
            }
        }
    }

    private void setFilter() throws Exception {
        StringToWordVector stringToWordVector = new StringToWordVector();
        stringToWordVector.setAttributeIndices("first");
        stringToWordVector.setIDFTransform(true);
        stringToWordVector.setTFTransform(true);
        stringToWordVector.setInputFormat(this.trainingSet);
        this.trainingSet = Filter.useFilter(this.trainingSet, stringToWordVector);
    }

    private void setFilter2() throws Exception {
        NumericToNominal numericToNominal = new NumericToNominal();
        numericToNominal.setAttributeIndices("first");
        numericToNominal.setInvertSelection(true);
        numericToNominal.setInputFormat(this.trainingSet);
        this.trainingSet = Filter.useFilter(this.trainingSet, numericToNominal);
    }

    private void readAllTrainingData() throws IOException {
        List<File> files = null;
        try {
            files = Files.walk(Paths.get(trainingDataPath)).filter(Files::isRegularFile).map(Path::toFile).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.trainingSet = new Instances(new FileReader("D:\\lyrics\\learningset\\The Weather Girls - It's Raining Men.arff"));
        for (File file : files) {
            BufferedReader br;
            br = new BufferedReader(new FileReader(file));
            try {
                this.trainingSet.addAll(new Instances(br));
            } catch (IOException e) {
                e.printStackTrace();
            }
            br.close();
        }
    }

    private void setClassifier(String classifierName) {
        this.classifier = new FilteredClassifier();
        switch (classifierName) {
            case "J48":
                J48 tree = new J48();
                tree.setUnpruned(true);
                classifier.setClassifier(tree);
                break;
            case "IBk":
                IBk iBkClassifier = new IBk();
                iBkClassifier.setKNN(5);
                classifier.setClassifier(iBkClassifier);
                break;
        }
    }

    public FilteredClassifier getClassifier() {
        return classifier;
    }

    public String evaluateInstance(String[] tags) throws Exception {
        Enumeration<Attribute> attributeEnumeration = trainingSet.get(0).enumerateAttributes();
        ArrayList<Attribute> attributes = new ArrayList<>(); // new attributes
        double[] values = new double[trainingSet.numAttributes()];
        int i = 1;
        attributes.add(trainingSet.classAttribute());
        while (attributeEnumeration.hasMoreElements()) {
            Attribute attribute = attributeEnumeration.nextElement();
            attributes.add(attribute);
            for (String tag : tags) {
                if (tag.equalsIgnoreCase(attribute.name())) {
                    values[i] = Double.parseDouble(attribute.value(1));
                    break;
                } else {
                    values[i] = 0.0;
                }
            }
            i++;
        }

        Instances testInstances = new Instances("TestSet", attributes, 0);
        testInstances.setClassIndex(0);
        DenseInstance newInstance = new DenseInstance(1.0, values);
        newInstance.setDataset(testInstances);
        double result = classifier.classifyInstance(newInstance);
        return trainingSet.classAttribute().value((int) result);
    }

    public List<String> getTrainingSetFilenames() {
        List<String> files = null;
        try {
            files = Files.walk(Paths.get(trainingDataPath)).filter(Files::isRegularFile).map(e -> e.getFileName().toString()).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return files;
    }
}