package com.github.ocroquette.teamcity.autopin;

import jetbrains.buildServer.serverSide.SBuild;

import java.util.ArrayList;
import java.util.List;

public class BuildTagHelper {
    public static void addTag(SBuild build, String tag) {
        List<String> originalTags = build.getTags();
        List<String> newTags = new ArrayList<String>(originalTags);
        newTags.add(tag);
        build.setTags(newTags);
    }

    public static void removeTag(SBuild build, String tag) {
        List<String> originalTags = build.getTags();
        List<String> newTags = new ArrayList<String>(originalTags);
        newTags.remove(tag);
        build.setTags(newTags);
    }
}
