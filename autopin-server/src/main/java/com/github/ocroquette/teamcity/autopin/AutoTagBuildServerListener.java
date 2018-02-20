package com.github.ocroquette.teamcity.autopin;

import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.messages.Status;
import jetbrains.buildServer.serverSide.*;
import jetbrains.buildServer.users.User;
import jetbrains.buildServer.util.EventDispatcher;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class AutoTagBuildServerListener extends BuildServerAdapter {

    private final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(Loggers.SERVER_CATEGORY);
    private final BuildHistory buildHistory;


    public AutoTagBuildServerListener(@NotNull EventDispatcher<BuildServerListener> events,
                                      @NotNull BuildHistory buildHistory) {
        events.addListener(this);
        this.buildHistory = buildHistory;
    }

    @Override
    public void buildStarted(@NotNull SRunningBuild runningBuild) {
        User triggeringUser = runningBuild.getTriggeredBy().getUser();
        for (SBuildFeatureDescriptor bfd : runningBuild.getBuildFeaturesOfType(AutoTagBuildFeature.TYPE)) {
            String tag = bfd.getParameters().get(AutoTagBuildFeature.PARAM_TAG);

            if (areTaggingConditionsMet(bfd.getParameters(), runningBuild)) {
                BuildTagHelper.addTag(runningBuild, tag);

                if (StringUtils.isTrue(bfd.getParameters().get(AutoTagBuildFeature.PARAM_TAG_DEPENDENCIES))) {
                    for (BuildPromotion bp : runningBuild.getBuildPromotion().getAllDependencies()) {
                        BuildTagHelper.addTag(buildHistory.findEntry(bp.getAssociatedBuild().getBuildId()), tag);
                    }
                }
            }
        }
    }

    private boolean areTaggingConditionsMet(Map<String, String> parameters, SBuild build) {
        boolean matching = true;

        String requestedBranchPattern = parameters.get(AutoTagBuildFeature.PARAM_BRANCH_PATTERN);
        if (requestedBranchPattern != null && !requestedBranchPattern.isEmpty()  && build.getBranch() != null) {
            matching = matching && build.getBranch().getDisplayName().matches(requestedBranchPattern);
        }

        return matching;
    }
}
