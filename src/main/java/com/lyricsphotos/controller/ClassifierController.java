package com.lyricsphotos.controller;

import com.lyricsphotos.service.weka.ClassifierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ClassifierController {
    @Autowired
    private ClassifierService classifierService;

    @GetMapping(value = "/classifierDetails")
    public String getClassifierDetails(Model model) {
        model.addAttribute("classifier", classifierService.getClassifier());
        return "/classifierDetails";
    }

    @GetMapping(value = "/rebuildClassifier")
    public String rebuildClassifier(Model model) {
        model.addAttribute("trainingFilenames", classifierService.getTrainingSetFilenames());
        return "/rebuildClassifier";
    }

    @PostMapping(value = "/rebuildClassifier")
    public String rebuildClassifier(@RequestParam String trainingSet, @RequestParam String classifierName, Model model) throws Exception {
        if (trainingSet.length() == 0) {
            model.addAttribute("trainingFilenames", classifierService.getTrainingSetFilenames());
            return "/rebuildClassifier";
        }
        String[] trainingIDs = trainingSet.split(";");
        classifierService.recreateClassifier(trainingIDs, classifierName);
        model.addAttribute("classifier", classifierService.getClassifier());
        return "/classifierDetails";
    }
}
