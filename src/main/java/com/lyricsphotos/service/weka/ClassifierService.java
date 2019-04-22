package com.lyricsphotos.service.weka;

import org.springframework.stereotype.Service;
import weka.classifiers.meta.FilteredClassifier;
import weka.core.Instances;

@Service
public class ClassifierService {
    // create 20 songs files
    // read them all
    // use filters
    // use attribute selection (probably not in my case)
    // build classifier
    // evaluate
    /*
    * ADMIN MODULE:
    * manage dataset (tick which files you want to use in learning process?)
    * display evaluation results
    * build classifier (it's build on startup by default, but can be re-built
    * update classifier (other classifier?)
    *
    * USER MODULE:
    * like already, but FORCE GENERATE TAGS/IMAGES has to be written
    *       to let user regenerate tags/images for new classifier
    * */


    private Instances trainingSet;
    private FilteredClassifier classifier;


    public ClassifierService() {
    }

    public ClassifierService(String classifierName) {
        // TODO write switch statement to choose classifier and read training set (necessary of course)
    }

    public Instances getTrainingSet() {
        return trainingSet;
    }

    public void setTrainingSet(Instances trainingSet) {
        this.trainingSet = trainingSet;
    }

    public FilteredClassifier getClassifier() {
        return classifier;
    }

    public void setClassifier(FilteredClassifier classifier) {
        this.classifier = classifier;
    }
}